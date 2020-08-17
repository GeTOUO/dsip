package com.dxp.sip.codec.sip;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.MessageAggregator;
import io.netty.handler.codec.TooLongFrameException;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

import java.net.InetSocketAddress;

import static com.dxp.sip.codec.sip.SipHeaderNames.CONTENT_LENGTH;
import static com.dxp.sip.codec.sip.SipHeaderNames.CONNECTION;

/**
 * 统一转发为 FullSipRequest,FullSipResponse.
 *
 * @author carzy
 * @see FullSipRequest
 * @see FullSipResponse
 */
public class SipObjectAggregator
        extends MessageAggregator<SipObject, SipMessage, SipContent, FullSipMessage> {

    private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(SipObjectAggregator.class);

    private static final FullSipResponse TOO_LARGE_CLOSE = new DefaultFullSipResponse(
            SipVersion.SIP_2_0, SipResponseStatus.REQUEST_ENTITY_TOO_LARGE, Unpooled.EMPTY_BUFFER);

    private static final FullSipResponse TOO_LARGE = new DefaultFullSipResponse(
            SipVersion.SIP_2_0, SipResponseStatus.REQUEST_ENTITY_TOO_LARGE, Unpooled.EMPTY_BUFFER);

    static {
        TOO_LARGE.headers().set(CONTENT_LENGTH, 0);

        TOO_LARGE_CLOSE.headers().set(CONTENT_LENGTH, 0);
        TOO_LARGE_CLOSE.headers().set(CONNECTION, SipHeaderValues.CLOSE);
    }

    private final boolean closeOnExpectationFailed;

    /**
     * Creates a new instance.
     *
     * @param maxContentLength the maximum length of the aggregated content in bytes.
     *                         If the length of the aggregated content exceeds this value,
     *                         {@link #handleOversizedMessage(ChannelHandlerContext, SipMessage)} will be called.
     */
    public SipObjectAggregator(int maxContentLength) {
        this(maxContentLength, false);
    }

    /**
     * Creates a new instance.
     *
     * @param maxContentLength         the maximum length of the aggregated content in bytes.
     *                                 If the length of the aggregated content exceeds this value,
     *                                 {@link #handleOversizedMessage(ChannelHandlerContext, SipMessage)} will be called.
     * @param closeOnExpectationFailed If a 100-continue response is detected but the content length is too large
     *                                 then {@code true} means close the connection. otherwise the connection will remain open and data will be
     *                                 consumed and discarded until the next request is received.
     */
    public SipObjectAggregator(int maxContentLength, boolean closeOnExpectationFailed) {
        super(maxContentLength);
        this.closeOnExpectationFailed = closeOnExpectationFailed;
    }

    @Override
    protected boolean isStartMessage(SipObject msg) throws Exception {
        return msg instanceof SipMessage;
    }

    @Override
    protected boolean isContentMessage(SipObject msg) throws Exception {
        return msg instanceof SipContent;
    }

    @Override
    protected boolean isLastContentMessage(SipContent msg) throws Exception {
        return msg instanceof LastSipContent;
    }

    @Override
    protected boolean isAggregated(SipObject msg) throws Exception {
        return msg instanceof FullSipMessage;
    }

    @Override
    protected boolean isContentLengthInvalid(SipMessage start, int maxContentLength) {
        try {
            return SipUtil.getContentLength(start, -1L) > maxContentLength;
        } catch (final NumberFormatException e) {
            return false;
        }
    }

    private static Object continueResponse(SipMessage start, int maxContentLength, ChannelPipeline pipeline) {
        return null;
    }

    @Override
    protected Object newContinueResponse(SipMessage start, int maxContentLength, ChannelPipeline pipeline) {
        return null;
    }

    @Override
    protected boolean closeAfterContinueResponse(Object msg) {
        return closeOnExpectationFailed && ignoreContentAfterContinueResponse(msg);
    }

    @Override
    protected boolean ignoreContentAfterContinueResponse(Object msg) {
        return false;
    }

    @Override
    protected FullSipMessage beginAggregation(SipMessage start, ByteBuf content) throws Exception {
        assert !(start instanceof FullSipMessage);

        AbstractAggregatedFullSipMessage ret;
        if (start instanceof SipRequest) {
            ret = new AbstractAggregatedFullSipRequest((SipRequest) start, content);
        } else if (start instanceof SipResponse) {
            ret = new AbstractAggregatedFullSipResponse((SipResponse) start, content);
        } else {
            throw new Error();
        }
        return ret;
    }

    @Override
    protected void aggregate(FullSipMessage aggregated, SipContent content) throws Exception {
    }

    @Override
    protected void finishAggregation(FullSipMessage aggregated) throws Exception {
        if (!SipUtil.isContentLengthSet(aggregated)) {
            aggregated.headers().set(
                    CONTENT_LENGTH,
                    String.valueOf(aggregated.content().readableBytes()));
        }
    }

    @Override
    protected void handleOversizedMessage(final ChannelHandlerContext ctx, SipMessage oversized) throws Exception {
        if (oversized instanceof SipRequest) {
            ctx.writeAndFlush(TOO_LARGE.retainedDuplicate()).addListener((ChannelFutureListener) future -> {
                if (!future.isSuccess()) {
                    LOGGER.debug("Failed to send a 413 Request Entity Too Large.", future.cause());
                    ctx.close();
                }
            });
        } else if (oversized instanceof SipResponse) {
            ctx.close();
            throw new TooLongFrameException("Response entity too large: " + oversized);
        } else {
            throw new IllegalStateException();
        }
    }

    public abstract static class AbstractAggregatedFullSipMessage implements FullSipMessage {
        protected final SipMessage message;
        private final ByteBuf content;
        private InetSocketAddress recipient;

        AbstractAggregatedFullSipMessage(SipMessage message, ByteBuf content) {
            this.message = message;
            this.content = content;
            this.recipient = message.recipient();
        }

        @Override
        public AbstractAggregatedFullSipMessage setRecipient(InetSocketAddress recipient) {
            this.recipient = recipient;
            return this;
        }

        @Override
        public InetSocketAddress recipient() {
            return recipient;
        }

        @Override
        public SipVersion protocolVersion() {
            return message.protocolVersion();
        }

        @Override
        public FullSipMessage setProtocolVersion(SipVersion version) {
            message.setProtocolVersion(version);
            return this;
        }

        @Override
        public AbstractSipHeaders headers() {
            return message.headers();
        }

        @Override
        public DecoderResult decoderResult() {
            return message.decoderResult();
        }

        @Override
        public void setDecoderResult(DecoderResult result) {
            message.setDecoderResult(result);
        }

        @Override
        public ByteBuf content() {
            return content;
        }

        @Override
        public int refCnt() {
            return content.refCnt();
        }

        @Override
        public FullSipMessage retain() {
            content.retain();
            return this;
        }

        @Override
        public FullSipMessage retain(int increment) {
            content.retain(increment);
            return this;
        }

        @Override
        public FullSipMessage touch(Object hint) {
            content.touch(hint);
            return this;
        }

        @Override
        public FullSipMessage touch() {
            content.touch();
            return this;
        }

        @Override
        public boolean release() {
            return content.release();
        }

        @Override
        public boolean release(int decrement) {
            return content.release(decrement);
        }

        @Override
        public abstract FullSipMessage copy();

        @Override
        public abstract FullSipMessage duplicate();

        @Override
        public abstract FullSipMessage retainedDuplicate();

        public abstract boolean isRequest();

        public SipMessage getMessage() {
            return message;
        }
    }

    public static final class AbstractAggregatedFullSipRequest extends AbstractAggregatedFullSipMessage implements FullSipRequest {

        public AbstractAggregatedFullSipRequest(SipRequest request, ByteBuf content) {
            super(request, content);
        }

        @Override
        public FullSipRequest copy() {
            return replace(content().copy());
        }

        @Override
        public FullSipRequest duplicate() {
            return replace(content().duplicate());
        }

        @Override
        public FullSipRequest retainedDuplicate() {
            return replace(content().retainedDuplicate());
        }

        @Override
        public boolean isRequest() {
            return true;
        }

        @Override
        public FullSipRequest replace(ByteBuf content) {
            DefaultFullSipRequest dup = new DefaultFullSipRequest(protocolVersion(), method(), uri(), content,
                    headers().copy());
            dup.setDecoderResult(decoderResult());
            return dup;
        }

        @Override
        public FullSipRequest retain(int increment) {
            super.retain(increment);
            return this;
        }

        @Override
        public FullSipRequest retain() {
            super.retain();
            return this;
        }

        @Override
        public FullSipRequest touch() {
            super.touch();
            return this;
        }

        @Override
        public FullSipRequest touch(Object hint) {
            super.touch(hint);
            return this;
        }

        @Override
        public FullSipRequest setMethod(SipMethod method) {
            ((SipRequest) message).setMethod(method);
            return this;
        }

        @Override
        public FullSipRequest setUri(String uri) {
            ((SipRequest) message).setUri(uri);
            return this;
        }

        @Override
        public SipMethod method() {
            return ((SipRequest) message).method();
        }

        @Override
        public String uri() {
            return ((SipRequest) message).uri();
        }

        @Override
        public FullSipRequest setProtocolVersion(SipVersion version) {
            super.setProtocolVersion(version);
            return this;
        }

        @Override
        public String toString() {
            return SipMessageUtil.appendFullRequest(new StringBuilder(256), this).toString();
        }
    }

    public static final class AbstractAggregatedFullSipResponse extends AbstractAggregatedFullSipMessage
            implements FullSipResponse {

        public AbstractAggregatedFullSipResponse(SipResponse message, ByteBuf content) {
            super(message, content);
        }

        @Override
        public FullSipResponse copy() {
            return replace(content().copy());
        }

        @Override
        public FullSipResponse duplicate() {
            return replace(content().duplicate());
        }

        @Override
        public FullSipResponse retainedDuplicate() {
            return replace(content().retainedDuplicate());
        }

        @Override
        public boolean isRequest() {
            return false;
        }

        @Override
        public FullSipResponse replace(ByteBuf content) {
            DefaultFullSipResponse dup = new DefaultFullSipResponse(protocolVersion(), status(), content,
                    headers().copy());
            dup.setDecoderResult(decoderResult());
            return dup;
        }

        @Override
        public FullSipResponse setStatus(SipResponseStatus status) {
            ((SipResponse) message).setStatus(status);
            return this;
        }

        @Override
        public SipResponseStatus status() {
            return ((SipResponse) message).status();
        }

        @Override
        public FullSipResponse setProtocolVersion(SipVersion version) {
            super.setProtocolVersion(version);
            return this;
        }

        @Override
        public FullSipResponse retain(int increment) {
            super.retain(increment);
            return this;
        }

        @Override
        public FullSipResponse retain() {
            super.retain();
            return this;
        }

        @Override
        public FullSipResponse touch(Object hint) {
            super.touch(hint);
            return this;
        }

        @Override
        public FullSipResponse touch() {
            super.touch();
            return this;
        }

        @Override
        public String toString() {
            return SipMessageUtil.appendFullResponse(new StringBuilder(256), this).toString();
        }
    }
}
