package com.dxp.sip.codec.sip;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.IllegalReferenceCountException;

import java.net.InetSocketAddress;

import static io.netty.util.internal.ObjectUtil.checkNotNull;

/**
 * @author carzy
 * @see io.netty.handler.codec.http.DefaultHttpResponse
 */
public class DefaultFullSipResponse extends DefaultSipResponse implements FullSipResponse {

    private final ByteBuf content;

    /**
     * Used to cache the value of the hash code and avoid.
     */
    private int hash;

    public DefaultFullSipResponse(SipResponseStatus status) {
        this(SipVersion.SIP_2_0, status);
    }

    public DefaultFullSipResponse(SipVersion version, SipResponseStatus status) {
        this(version, status, Unpooled.buffer(0));
    }

    public DefaultFullSipResponse(SipVersion version, SipResponseStatus status, ByteBuf content) {
        this(version, status, content, true);
    }

    public DefaultFullSipResponse(SipVersion version, SipResponseStatus status, boolean validateHeaders) {
        this(version, status, Unpooled.buffer(0), validateHeaders);
    }

    public DefaultFullSipResponse(SipVersion version, SipResponseStatus status,
                                  boolean validateHeaders, InetSocketAddress recipient) {
        this(version, status, Unpooled.buffer(0), validateHeaders);
        this.setRecipient(recipient);
    }

    public DefaultFullSipResponse(SipVersion version, SipResponseStatus status,
                                  ByteBuf content, boolean validateHeaders) {
        super(version, status, validateHeaders);
        this.content = checkNotNull(content, "content");
    }

    public DefaultFullSipResponse(SipVersion version, SipResponseStatus status,
                                  ByteBuf content, AbstractSipHeaders headers) {
        super(version, status, headers);
        this.content = checkNotNull(content, "content");
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
    public FullSipResponse retain() {
        content.retain();
        return this;
    }

    @Override
    public FullSipResponse retain(int increment) {
        content.retain(increment);
        return this;
    }

    @Override
    public FullSipResponse touch() {
        content.touch();
        return this;
    }

    @Override
    public FullSipResponse touch(Object hint) {
        content.touch(hint);
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
    public DefaultFullSipResponse setProtocolVersion(SipVersion version) {
        super.setProtocolVersion(version);
        return this;
    }

    @Override
    public FullSipResponse setStatus(SipResponseStatus status) {
        super.setStatus(status);
        return this;
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
    public FullSipResponse replace(ByteBuf content) {
        FullSipResponse response = new DefaultFullSipResponse(protocolVersion(), status(), content,
                headers().copy());
        response.setDecoderResult(decoderResult());
        return response;
    }

    @Override
    public int hashCode() {
        int hash = this.hash;
        if (hash == 0) {
            if (content().refCnt() != 0) {
                try {
                    hash = 31 + content().hashCode();
                } catch (IllegalReferenceCountException ignored) {
                    // Handle race condition between checking refCnt() == 0 and using the object.
                    hash = 31;
                }
            } else {
                hash = 31;
            }
            hash = 31 * hash + super.hashCode();
            this.hash = hash;
        }
        return hash;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof DefaultFullSipResponse)) {
            return false;
        }

        DefaultFullSipResponse other = (DefaultFullSipResponse) o;

        return super.equals(other) &&
                content().equals(other.content());
    }

    @Override
    public String toString() {
        return SipMessageUtil.appendFullResponse(new StringBuilder(256), this).toString();
    }
}