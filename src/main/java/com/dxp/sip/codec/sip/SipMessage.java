package com.dxp.sip.codec.sip;

import java.net.InetSocketAddress;

/**
 * sip消息对象
 *
 * @author carzy
 * @date 2020/8/10
 */
public interface SipMessage extends SipObject {

    /**
     * Returns the protocol version of this {@link SipMessage}
     *
     * @return SipVersion
     */
    SipVersion protocolVersion();

    /**
     * Set the protocol version of this {@link SipMessage}
     *
     * @return SipMessage
     */
    SipMessage setProtocolVersion(SipVersion version);

    /**
     * Returns the headers of this message.
     *
     * @return HttpHeaders
     */
    AbstractSipHeaders headers();

    /**
     * 设置消息来源地址
     */
    SipMessage setRecipient(InetSocketAddress recipient);

    /**
     * 获取消息来源地址
     */
    InetSocketAddress recipient();

}
