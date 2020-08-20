package com.dxp.sip.bus.`fun`.controller

import com.dxp.sip.bus.`fun`.HandlerController
import com.dxp.sip.codec.sip.FullSipRequest
import com.dxp.sip.codec.sip.SipHeaderNames
import com.dxp.sip.codec.sip.SipHeaderValues
import com.dxp.sip.codec.sip.SipMethod
import com.dxp.sip.util.CharsetUtils
import com.dxp.sip.util.SendErrorResponseUtil.err400
import io.netty.channel.Channel
import io.netty.util.internal.logging.InternalLoggerFactory

/**
 * @author carzy
 * @date 2020/8/14
 */
class InviteController : HandlerController {

    override fun method(): SipMethod {
        return SipMethod.INVITE
    }

    override fun handler(request: FullSipRequest, channel: Channel) {
        val headers = request.headers()
        val type = headers[SipHeaderNames.CONTENT_TYPE]
        if (SipHeaderValues.APPLICATION_SDP.contentEqualsIgnoreCase(type)) {
            //todo.. sdp解析。
            LOGGER.info("sdp: {}", request.content().toString(CharsetUtils.US_ASCII))
        } else {
            err400(request, channel, "message content_type must be Application/MANSCDP+xml")
        }
    }

    companion object {
        private val LOGGER = InternalLoggerFactory.getInstance(InviteController::class.java)
    }
}