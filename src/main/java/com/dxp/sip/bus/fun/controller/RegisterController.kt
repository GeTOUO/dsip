package com.dxp.sip.bus.`fun`.controller

import com.dxp.sip.bus.`fun`.HandlerController
import com.dxp.sip.codec.sip.*
import io.netty.channel.Channel

/**
 * @author carzy
 * @date 2020/8/14
 */
class RegisterController : HandlerController {

    override fun method(): SipMethod {
        return SipMethod.REGISTER
    }

    override fun handler(request: FullSipRequest, channel: Channel) {
        val headers = request.headers()
        val response = DefaultFullSipResponse(SipResponseStatus.UNAUTHORIZED)
        response.setRecipient(request.recipient())
        val h = response.headers()
        if (!headers.contains(SipHeaderNames.AUTHORIZATION)) {
            val wwwAuth = "Digest realm=\"31011000002001234567\", nonce=\"" +
                    "b700dc7cb094478503a21148184a3731\", opaque=\"5b279c2efd18d123d1f4a2182527a281\", algorithm=MD5"
            h.set(SipHeaderNames.FROM, headers[SipHeaderNames.FROM])
                    .set(SipHeaderNames.TO, headers[SipHeaderNames.TO])
                    .set(SipHeaderNames.CSEQ, headers[SipHeaderNames.CSEQ])
                    .set(SipHeaderNames.CALL_ID, headers[SipHeaderNames.CALL_ID])
                    .set(SipHeaderNames.USER_AGENT, SipHeaderValues.USER_AGENT)
                    .set(SipHeaderNames.WWW_AUTHENTICATE, wwwAuth)[SipHeaderNames.CONTENT_LENGTH] = SipHeaderValues.EMPTY_CONTENT_LENGTH
        } else {
            h.set(SipHeaderNames.FROM, headers[SipHeaderNames.FROM])
                    .set(SipHeaderNames.TO, headers[SipHeaderNames.TO] + ";tag=" + System.currentTimeMillis())
                    .set(SipHeaderNames.CSEQ, headers[SipHeaderNames.CSEQ])
                    .set(SipHeaderNames.CALL_ID, headers[SipHeaderNames.CALL_ID])
                    .set(SipHeaderNames.USER_AGENT, SipHeaderValues.USER_AGENT)[SipHeaderNames.CONTENT_LENGTH] = SipHeaderValues.EMPTY_CONTENT_LENGTH
            response.setStatus(SipResponseStatus.OK)
        }
        channel.writeAndFlush(response)
    }
}