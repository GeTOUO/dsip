package com.dxp.sip.codec.sip;

/**
 * sip响应
 *
 * @author carzy
 * @date 2020/8/11
 */
public interface SipResponse extends SipMessage {

    SipResponseStatus status();

    SipResponse setStatus(SipResponseStatus status);

    @Override
    SipResponse setProtocolVersion(SipVersion version);
}
