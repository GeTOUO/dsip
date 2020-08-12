package com.dxp.sip.codec.sip;


import io.netty.handler.codec.http.*;
import io.netty.util.internal.ObjectUtil;


import java.net.InetSocketAddress;

import static io.netty.util.internal.ObjectUtil.checkNotNull;

/**
 * @author carzy
 * @date 2020/8/11
 */
public class DefaultSipMessage extends DefaultSipObject implements SipMessage {

    private static final int HASH_CODE_PRIME = 31;
    private SipVersion version;
    private final AbstractSipHeaders headers;
    private InetSocketAddress recipient;

    /**
     * Creates a new instance.
     */
    protected DefaultSipMessage(final SipVersion version) {
        this(version, true);
    }

    /**
     * Creates a new instance.
     */
    protected DefaultSipMessage(final SipVersion version, boolean validateHeaders) {
        this(version, new DefaultSipHeaders(validateHeaders));
    }

    /**
     * Creates a new instance.
     */
    protected DefaultSipMessage(final SipVersion version, AbstractSipHeaders headers) {
        this.version = checkNotNull(version, "version");
        this.headers = checkNotNull(headers, "headers");
    }

    @Override
    public SipVersion protocolVersion() {
        return version;
    }

    @Override
    public SipMessage setProtocolVersion(SipVersion version) {
        this.version = ObjectUtil.checkNotNull(version, "version");
        return this;
    }

    @Override
    public AbstractSipHeaders headers() {
        return headers;
    }

    @Override
    public DefaultSipMessage setRecipient(InetSocketAddress recipient) {
        this.recipient = recipient;
        return this;
    }

    @Override
    public InetSocketAddress recipient() {
        return this.recipient;
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = HASH_CODE_PRIME * result + headers.hashCode();
        result = HASH_CODE_PRIME * result + version.hashCode();
        result = HASH_CODE_PRIME * result + super.hashCode();
        return result;
    }

    @SuppressWarnings("EqualsBetweenInconvertibleTypes")
    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }

        if (o == this) {
            return true;
        }

        if (!(o instanceof DefaultHttpMessage)) {
            return false;
        }

        DefaultHttpMessage other = (DefaultHttpMessage) o;

        return headers().equals(other.headers()) &&
                protocolVersion().equals(other.protocolVersion()) &&
                super.equals(o);
    }
}
