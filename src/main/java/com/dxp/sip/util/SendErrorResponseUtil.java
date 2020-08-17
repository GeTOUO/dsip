package com.dxp.sip.util;

import com.dxp.sip.bus.fun.DispatchHandlerContext;
import com.dxp.sip.codec.sip.*;
import io.netty.channel.Channel;

/**
 * @author carzy
 * @date 2020/8/17
 */
public class SendErrorResponseUtil {

    public static void err_400(FullSipRequest request, Channel channel, String reason) {
        err(request, SipResponseStatus.BAD_REQUEST, channel, reason);
    }

    public static void err_500(FullSipRequest request, Channel channel, String reason) {
        err(request, SipResponseStatus.INTERNAL_SERVER_ERROR, channel, reason);
    }

    public static void err_405(FullSipRequest request, Channel channel, String reason) {
        err(request, SipResponseStatus.METHOD_NOT_ALLOWED, channel, reason);
    }

    /**
     * @param request 请求
     * @param channel 通道
     * @param reason  错误信息.
     */
    private static void err(FullSipRequest request, SipResponseStatus status, Channel channel, String reason) {
        final AbstractSipHeaders headers = request.headers();
        DefaultFullSipResponse response = new DefaultFullSipResponse(status);
        response.setRecipient(request.recipient());
        final AbstractSipHeaders h = response.headers();
        h.set(SipHeaderNames.FROM, headers.get(SipHeaderNames.FROM))
                .set(SipHeaderNames.TO, headers.get(SipHeaderNames.TO))
                .set(SipHeaderNames.ALLOW, DispatchHandlerContext.allowMethod())
                .set(SipHeaderNames.CONTENT_LENGTH, headers.get(SipHeaderNames.CONTENT_LENGTH))
                .set(SipHeaderNames.CSEQ, headers.get(SipHeaderNames.CSEQ))
                .set(SipHeaderNames.CALL_ID, headers.get(SipHeaderNames.CALL_ID))
                .set(SipHeaderNames.REASON, reason)
                .set(SipHeaderNames.USER_AGENT, SipHeaderValues.USER_AGENT);
        channel.writeAndFlush(response);
    }

    private SendErrorResponseUtil(){}

}
