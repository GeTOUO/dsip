package com.dxp.sip.codec.sip;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.FileRegion;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.StringUtil;

import java.net.InetSocketAddress;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import static com.dxp.sip.codec.sip.SipConstants.CR;
import static com.dxp.sip.codec.sip.SipConstants.LF;
import static io.netty.buffer.Unpooled.directBuffer;
import static io.netty.buffer.Unpooled.unreleasableBuffer;

/**
 * 解析SIP请求与响应的公共类.
 *
 * @author carzy
 */
public abstract class AbstractSipObjectEncoder<H extends SipMessage> extends MessageToMessageEncoder<Object> {

    static final int CRLF_SHORT = (CR << 8) | LF;
    private static final int ZERO_CRLF_MEDIUM = ('0' << 16) | CRLF_SHORT;
    private static final byte[] ZERO_CRLF_CRLF = {'0', CR, LF, CR, LF};
    private static final ByteBuf CRLF_BUF = unreleasableBuffer(directBuffer(2).writeByte(CR).writeByte(LF));
    private static final ByteBuf ZERO_CRLF_CRLF_BUF = unreleasableBuffer(directBuffer(ZERO_CRLF_CRLF.length)
            .writeBytes(ZERO_CRLF_CRLF));
    private static final float HEADERS_WEIGHT_NEW = 1 / 5f;
    private static final float HEADERS_WEIGHT_HISTORICAL = 1 - HEADERS_WEIGHT_NEW;
    private static final float TRAILERS_WEIGHT_NEW = HEADERS_WEIGHT_NEW;
    private static final float TRAILERS_WEIGHT_HISTORICAL = HEADERS_WEIGHT_HISTORICAL;

    private static final int ST_INIT = 0;
    private static final int ST_CONTENT_NON_CHUNK = 1;
    private static final int ST_CONTENT_CHUNK = 2;
    private static final int ST_CONTENT_ALWAYS_EMPTY = 3;

    private int state = ST_INIT;

    /**
     * Used to calculate an exponential moving average of the encoded size of the initial line and the headers for
     * a guess for future buffer allocations.
     */
    private float headersEncodedSizeAccumulator = 2048;

    /**
     * Used to calculate an exponential moving average of the encoded size of the trailers for
     * a guess for future buffer allocations.
     */
    private float trailersEncodedSizeAccumulator = 256;


    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, List<Object> out) throws Exception {
        ByteBuf buf = null;
        InetSocketAddress sender = null;
        boolean isTcp = true;
        if (msg instanceof SipMessage) {
            if (ctx.channel() instanceof DatagramChannel){
                isTcp = false;
                sender = ((SipMessage) msg).recipient();
            }
            if (state != ST_INIT) {
                throw new IllegalStateException("unexpected message type: " + StringUtil.simpleClassName(msg)
                        + ", state: " + state);
            }

            @SuppressWarnings({"unchecked", "CastConflictsWithInstanceof"})
            H m = (H) msg;

            buf = ctx.alloc().buffer((int) headersEncodedSizeAccumulator);
            // Encode the message.
            encodeInitialLine(buf, m);
//            state = isContentAlwaysEmpty(m) ? ST_CONTENT_ALWAYS_EMPTY :
//                    SipUtil.isTransferEncodingChunked(m) ? ST_CONTENT_CHUNK : ST_CONTENT_NON_CHUNK;
            state = ST_CONTENT_NON_CHUNK;

            //sanitizeHeadersBeforeEncode(m, state == ST_CONTENT_ALWAYS_EMPTY);

            encodeHeaders(m.headers(), buf);
            ByteBufUtil.writeShortBE(buf, CRLF_SHORT);

            //headersEncodedSizeAccumulator = HEADERS_WEIGHT_NEW * padSizeForAccumulation(buf.readableBytes()) +
            //                                HEADERS_WEIGHT_HISTORICAL * headersEncodedSizeAccumulator;
        }

        if (msg instanceof ByteBuf) {
            final ByteBuf potentialEmptyBuf = (ByteBuf) msg;
            if (!potentialEmptyBuf.isReadable()) {
                if (isTcp){
                    out.add(potentialEmptyBuf.retain());
                }else {
                    out.add(new DatagramPacket(potentialEmptyBuf.retain(), sender));
                }
                return;
            }
        }

        if (msg instanceof SipContent || msg instanceof ByteBuf || msg instanceof FileRegion) {
            switch (state) {
                case ST_INIT:
                    throw new IllegalStateException("unexpected message type: " + StringUtil.simpleClassName(msg));
                case ST_CONTENT_NON_CHUNK:
                    final long contentLength = contentLength(msg);
                    if (contentLength > 0) {
                        if (buf != null && buf.writableBytes() >= contentLength && msg instanceof SipContent) {
                            // merge into other buffer for performance reasons
                            buf.writeBytes(((SipContent) msg).content());
                            if (isTcp){
                                out.add(buf);
                            }else {
                                out.add(new DatagramPacket(buf, sender));
                            }
                        } else {
                            if (buf != null) {
                                if (isTcp){
                                    out.add(buf);
                                }else {
                                    out.add(new DatagramPacket(buf, sender));
                                }
                            }
                            out.add(encodeAndRetain(msg));
                        }

                        if (msg instanceof LastSipContent) {
                            state = ST_INIT;
                        }

                        break;
                    }

                    // fall-through!
                case ST_CONTENT_ALWAYS_EMPTY:

                    if (buf != null) {
                        // We allocated a buffer so add it now.
                        if (isTcp){
                            out.add(buf);
                        }else {
                            out.add(new DatagramPacket(buf, sender));
                        }
                    } else {
                        // Need to produce some output otherwise an
                        // IllegalStateException will be thrown as we did not write anything
                        // Its ok to just write an EMPTY_BUFFER as if there are reference count issues these will be
                        // propagated as the caller of the encode(...) method will release the original
                        // buffer.
                        // Writing an empty buffer will not actually write anything on the wire, so if there is a user
                        // error with msg it will not be visible externally
                        ;
                        if (isTcp){
                            out.add(out.add(Unpooled.EMPTY_BUFFER));
                        }else {
                            out.add(new DatagramPacket(Unpooled.EMPTY_BUFFER, sender));
                        }
                    }

                    break;
                case ST_CONTENT_CHUNK:
                    if (buf != null) {
                        // We allocated a buffer so add it now.
                        if (isTcp){
                            out.add(buf);
                        }else {
                            out.add(new DatagramPacket(buf, sender));
                        }
                    }
                    encodeChunkedContent(ctx, msg, contentLength(msg), out);

                    break;
                default:
                    throw new Error();
            }

            if (msg instanceof LastSipContent) {
                state = ST_INIT;
            }
        } else if (buf != null) {
            if (isTcp) {
                out.add(buf);
            } else {
                out.add(new DatagramPacket(buf, sender));
            }
        }
    }

    /**
     * Encode the {@link AbstractSipHeaders} into a {@link ByteBuf}.
     */
    protected void encodeHeaders(AbstractSipHeaders headers, ByteBuf buf) {
        Iterator<Entry<CharSequence, CharSequence>> iter = headers.iteratorCharSequence();
        while (iter.hasNext()) {
            Entry<CharSequence, CharSequence> header = iter.next();
            SipHeadersEncoder.encoderHeader(header.getKey(), header.getValue(), buf);
        }
    }

    private void encodeChunkedContent(ChannelHandlerContext ctx, Object msg, long contentLength, List<Object> out) {
        if (contentLength > 0) {
            String lengthHex = Long.toHexString(contentLength);
            ByteBuf buf = ctx.alloc().buffer(lengthHex.length() + 2);
            buf.writeCharSequence(lengthHex, CharsetUtil.US_ASCII);
            ByteBufUtil.writeShortBE(buf, CRLF_SHORT);
            out.add(buf);
            out.add(encodeAndRetain(msg));
            out.add(CRLF_BUF.duplicate());
        }

        if (msg instanceof LastSipContent) {
            out.add(ZERO_CRLF_CRLF_BUF.duplicate());
        } else if (contentLength == 0) {
            out.add(encodeAndRetain(msg));
        }
    }

    /**
     * Determine whether a message has a content or not. Some message may have headers indicating
     * a content without having an actual content, e.g the response to an HEAD or CONNECT request.
     *
     * @param msg the message to test
     * @return {@code true} to signal the message has no content
     */
    protected boolean isContentAlwaysEmpty(@SuppressWarnings("unused") H msg) {
        return false;
    }

    @Override
    public boolean acceptOutboundMessage(Object msg) throws Exception {
        return msg instanceof SipObject || msg instanceof ByteBuf || msg instanceof FileRegion;
    }

    private static Object encodeAndRetain(Object msg) {
        if (msg instanceof ByteBuf) {
            return ((ByteBuf) msg).retain();
        }
        if (msg instanceof SipContent) {
            return ((SipContent) msg).content().retain();
        }
        if (msg instanceof FileRegion) {
            return ((FileRegion) msg).retain();
        }
        throw new IllegalStateException("unexpected message type: " + StringUtil.simpleClassName(msg));
    }

    private static long contentLength(Object msg) {
        if (msg instanceof SipContent) {
            return ((SipContent) msg).content().readableBytes();
        }
        if (msg instanceof ByteBuf) {
            return ((ByteBuf) msg).readableBytes();
        }
        if (msg instanceof FileRegion) {
            return ((FileRegion) msg).count();
        }
        throw new IllegalStateException("unexpected message type: " + StringUtil.simpleClassName(msg));
    }

    /**
     * Add some additional overhead to the buffer. The rational is that it is better to slightly over allocate and waste
     * some memory, rather than under allocate and require a resize/copy.
     *
     * @param readableBytes The readable bytes in the buffer.
     * @return The {@code readableBytes} with some additional padding.
     */
    private static int padSizeForAccumulation(int readableBytes) {
        return (readableBytes << 2) / 3;
    }

    protected abstract void encodeInitialLine(ByteBuf buf, H message) throws Exception;

}
