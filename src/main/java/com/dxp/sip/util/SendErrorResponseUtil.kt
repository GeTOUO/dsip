package com.dxp.sip.util

import com.dxp.sip.bus.`fun`.DispatchHandlerContext
import com.dxp.sip.codec.sip.*
import io.netty.channel.Channel

/**
 * @author carzy
 * @date 2020/8/17
 */
object SendErrorResponseUtil {
    @JvmStatic
    fun err400(request: FullSipRequest, channel: Channel, reason: String) {
        error(request, SipResponseStatus.BAD_REQUEST, channel, reason)
    }

    @JvmStatic
    fun err500(request: FullSipRequest, channel: Channel, reason: String) {
        error(request, SipResponseStatus.INTERNAL_SERVER_ERROR, channel, reason)
    }

    @JvmStatic
    fun err405(request: FullSipRequest, channel: Channel) {
        error(request, SipResponseStatus.METHOD_NOT_ALLOWED, channel, request.method().asciiName().toString() + " not allowed")
    }

    /**
     * @param request 请求
     * @param channel 通道
     * @param reason  错误信息.
     */
    private fun error(request: FullSipRequest, status: SipResponseStatus, channel: Channel, reason: String) {
        val headers = request.headers()
        val response = DefaultFullSipResponse(status)
        response.setRecipient(request.recipient())
        val h = response.headers()
        h.set(SipHeaderNames.FROM, headers[SipHeaderNames.FROM])
                .set(SipHeaderNames.TO, headers[SipHeaderNames.TO])
                .set(SipHeaderNames.ALLOW, DispatchHandlerContext.allowMethod())
                .set(SipHeaderNames.CONTENT_LENGTH, headers[SipHeaderNames.CONTENT_LENGTH])
                .set(SipHeaderNames.CSEQ, headers[SipHeaderNames.CSEQ])
                .set(SipHeaderNames.CALL_ID, headers[SipHeaderNames.CALL_ID])
                .set(SipHeaderNames.REASON, reason)[SipHeaderNames.USER_AGENT] = SipHeaderValues.USER_AGENT
        channel.writeAndFlush(response)
    }
}