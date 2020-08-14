package com.dxp.sip.bus.fun;

import com.dxp.sip.codec.sip.*;
import io.netty.channel.Channel;


/**
 * @author carzy
 * @date 2020/8/14
 */
public interface HandlerController {

    SipMethod method();

    /**
     * 处理具体的 sip 请求
     *
     * @param request 请求携带的信息.
     * @param channel 通道
     */
    void handler(FullSipRequest request, Channel channel);

    default void err_400(CharSequence reason, FullSipRequest msg, Channel channel) {
        final AbstractSipHeaders headers = msg.headers();
        DefaultFullSipResponse response = new DefaultFullSipResponse(SipResponseStatus.METHOD_NOT_ALLOWED);
        response.setRecipient(msg.recipient());
        final AbstractSipHeaders h = response.headers();
        h.set(SipHeaderNames.FROM, headers.get(SipHeaderNames.FROM))
                .set(SipHeaderNames.TO, headers.get(SipHeaderNames.TO))
                .set(SipHeaderNames.CONTENT_LENGTH, headers.get(SipHeaderNames.CONTENT_LENGTH))
                .set(SipHeaderNames.CSEQ, headers.get(SipHeaderNames.CSEQ))
                .set(SipHeaderNames.CALL_ID, headers.get(SipHeaderNames.CALL_ID))
                .set(SipHeaderNames.REASON, reason)
                .set(SipHeaderNames.USER_AGENT, "d-sip");
        channel.writeAndFlush(response);
    }

}
