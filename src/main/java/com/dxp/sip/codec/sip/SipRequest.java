package com.dxp.sip.codec.sip;

/**
 * sip响应
 *
 * @author carzy
 * @date 2020/8/11
 */
public interface SipRequest extends SipMessage {

    SipMethod method();

    SipRequest setMethod(SipMethod method);

    String uri();

    SipRequest setUri(String uri);

    @Override
    SipRequest setProtocolVersion(SipVersion version);
}
