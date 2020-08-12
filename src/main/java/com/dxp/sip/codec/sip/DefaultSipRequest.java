package com.dxp.sip.codec.sip;

import java.net.InetSocketAddress;

import static io.netty.util.internal.ObjectUtil.checkNotNull;

/**
 * @author carzy
 */
public class DefaultSipRequest extends DefaultSipMessage implements SipRequest {
    private static final int HASH_CODE_PRIME = 31;
    private SipMethod method;
    private String uri;

    /**
     * Creates a new instance.
     *
     * @param httpVersion the HTTP version of the request
     * @param method      the HTTP method of the request
     * @param uri         the URI or path of the request
     */
    public DefaultSipRequest(SipVersion httpVersion, SipMethod method, String uri) {
        this(httpVersion, method, uri, true);
    }

    /**
     * Creates a new instance.
     *
     * @param httpVersion       the HTTP version of the request
     * @param method            the HTTP method of the request
     * @param uri               the URI or path of the request
     * @param validateHeaders   validate the header names and values when adding them to the {@link AbstractSipHeaders}
     */
    public DefaultSipRequest(SipVersion httpVersion, SipMethod method, String uri, boolean validateHeaders) {
        super(httpVersion, validateHeaders);
        this.method = checkNotNull(method, "method");
        this.uri = checkNotNull(uri, "uri");
    }

    public DefaultSipRequest(SipVersion httpVersion, SipMethod method, String uri, boolean validateHeaders, InetSocketAddress recipient) {
        super(httpVersion, validateHeaders);
        this.method = checkNotNull(method, "method");
        this.uri = checkNotNull(uri, "uri");
        this.setRecipient(recipient);
    }

    /**
     * Creates a new instance.
     *
     * @param httpVersion       the HTTP version of the request
     * @param method            the HTTP method of the request
     * @param uri               the URI or path of the request
     * @param headers           the Headers for this Request
     */
    public DefaultSipRequest(SipVersion httpVersion, SipMethod method, String uri, AbstractSipHeaders headers) {
        super(httpVersion, headers);
        this.method = checkNotNull(method, "method");
        this.uri = checkNotNull(uri, "uri");
    }

    public DefaultSipRequest(SipVersion httpVersion, SipMethod method, String uri, AbstractSipHeaders headers, InetSocketAddress recipient) {
        super(httpVersion, headers);
        this.method = checkNotNull(method, "method");
        this.uri = checkNotNull(uri, "uri");
        this.setRecipient(recipient);
    }

    @Override
    public SipMethod method() {
        return method;
    }

    @Override
    public String uri() {
        return uri;
    }

    @Override
    public SipRequest setMethod(SipMethod method) {
        this.method = checkNotNull(method, "method");
        return this;
    }

    @Override
    public SipRequest setUri(String uri) {
        this.uri = checkNotNull(uri, "uri");
        return this;
    }

    @Override
    public SipRequest setProtocolVersion(SipVersion version) {
        super.setProtocolVersion(version);
        return this;
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = HASH_CODE_PRIME * result + method.hashCode();
        result = HASH_CODE_PRIME * result + uri.hashCode();
        result = HASH_CODE_PRIME * result + super.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof DefaultSipRequest)) {
            return false;
        }

        DefaultSipRequest other = (DefaultSipRequest) o;

        return method().equals(other.method()) &&
               uri().equalsIgnoreCase(other.uri()) &&
               super.equals(o);
    }

    @Override
    public String toString() {
        return SipMessageUtil.appendRequest(new StringBuilder(256), this).toString();
    }
}