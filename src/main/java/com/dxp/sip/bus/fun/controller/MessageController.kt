package com.dxp.sip.bus.`fun`.controller

import com.dxp.sip.bus.`fun`.HandlerController
import com.dxp.sip.codec.sip.*
import com.dxp.sip.util.CharsetUtils
import com.dxp.sip.util.SendErrorResponseUtil.err400
import io.netty.channel.Channel
import io.netty.util.internal.logging.InternalLoggerFactory
import org.dom4j.DocumentException
import org.dom4j.DocumentHelper

/**
 * @author carzy
 * @date 2020/8/14
 */
class MessageController : HandlerController {

    override fun method(): SipMethod {
        return SipMethod.MESSAGE
    }

    @Throws(DocumentException::class)
    override fun handler(request: FullSipRequest, channel: Channel) {
        val headers = request.headers()
        val type = headers[SipHeaderNames.CONTENT_TYPE]
        if (SipHeaderValues.APPLICATION_MANSCDP_XML.contentEqualsIgnoreCase(type)) {
            val xml = request.content().toString(CharsetUtils.GB_2313)
            val document = DocumentHelper.parseText(xml)
            val cmdType = document.rootElement.element("CmdType").textTrim
            if ("Keepalive".equals(cmdType, ignoreCase = true)) {
                keepalive(request, channel)
            } else {
                err400(request, channel, "cmdType not allowed.")
            }
        } else {
            err400(request, channel, "message content_type must be Application/MANSCDP+xml")
        }
    }

    private fun keepalive(msg: FullSipRequest, channel: Channel) {
        val headers = msg.headers()
        val response = DefaultFullSipResponse(SipResponseStatus.OK)
        response.setRecipient(msg.recipient())
        val h = response.headers()
        h.set(SipHeaderNames.FROM, headers[SipHeaderNames.FROM])
                .set(SipHeaderNames.TO, headers[SipHeaderNames.TO] + ";tag=" + System.currentTimeMillis())
                .set(SipHeaderNames.CSEQ, headers[SipHeaderNames.CSEQ])
                .set(SipHeaderNames.CALL_ID, headers[SipHeaderNames.CALL_ID])
                .set(SipHeaderNames.USER_AGENT, SipHeaderValues.USER_AGENT)[SipHeaderNames.CONTENT_LENGTH] = SipHeaderValues.EMPTY_CONTENT_LENGTH
        channel.writeAndFlush(response)
    }

    companion object {
        private val LOGGER = InternalLoggerFactory.getInstance(MessageController::class.java)
    }
}