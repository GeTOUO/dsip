package com.dxp.sip.codec.sip;

import io.netty.util.internal.ObjectUtil;

import java.net.InetSocketAddress;

import static io.netty.util.internal.ObjectUtil.checkNotNull;

/**
 * 响应
 *
 * @author carzy
 * @date 2020/8/11
 */
public class DefaultSipResponse extends DefaultSipMessage implements SipResponse {

    private SipResponseStatus status;

    public DefaultSipResponse(SipVersion version, SipResponseStatus status) {
        this(version, status, true);
    }

    public DefaultSipResponse(SipVersion version, SipResponseStatus status, boolean validateHeaders) {
        super(version, validateHeaders);
        this.status = checkNotNull(status, "status");
    }

    public DefaultSipResponse(SipVersion version, SipResponseStatus status, boolean validateHeaders, InetSocketAddress recipient) {
        super(version, validateHeaders);
        this.status = checkNotNull(status, "status");
        this.setRecipient(recipient);
    }

    public DefaultSipResponse(SipVersion version, SipResponseStatus status, AbstractSipHeaders headers) {
        super(version, headers);
        this.status = checkNotNull(status, "status");
    }

    @Override
    public SipResponseStatus status() {
        return status;
    }

    @Override
    public SipResponse setStatus(SipResponseStatus status) {
        this.status = ObjectUtil.checkNotNull(status, "status");
        return this;
    }

    @Override
    public DefaultSipResponse setProtocolVersion(SipVersion version) {
        super.setProtocolVersion(version);
        return this;
    }

    @Override
    public String toString() {
        return SipMessageUtil.appendResponse(new StringBuilder(256), this).toString();
    }


    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + status.hashCode();
        result = 31 * result + super.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof DefaultSipResponse)) {
            return false;
        }

        DefaultSipResponse other = (DefaultSipResponse) o;

        return status.equals(other.status()) && super.equals(o);
    }

}
