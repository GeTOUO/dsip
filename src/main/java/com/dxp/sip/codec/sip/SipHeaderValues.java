package com.dxp.sip.codec.sip;

import io.netty.util.AsciiString;

/**
 * content-type值
 *
 * @author carzy
 * @date 2020/8/11
 */
public final class SipHeaderValues {

    private SipHeaderValues(){}

    public static final AsciiString APPLICATION_SDP = AsciiString.cached("Application/sdp");

    public static final AsciiString APPLICATION_MANSCDP_XML = AsciiString.cached("Application/MANSCDP+xml");

    public static final AsciiString TEXT_PLAIN = AsciiString.cached("text/plain");

    public static final AsciiString CLOSE = AsciiString.cached("close");

    public static final AsciiString CONTINUE = AsciiString.cached("100-continue");

    public static final AsciiString EMPTY_CONTENT_LENGTH = AsciiString.cached("0");

    public static final AsciiString USER_AGENT = AsciiString.cached("d-sip");

}
