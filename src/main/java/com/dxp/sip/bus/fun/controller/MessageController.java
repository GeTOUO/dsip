package com.dxp.sip.bus.fun.controller;

import com.dxp.sip.bus.fun.HandlerController;
import com.dxp.sip.codec.sip.*;
import com.dxp.sip.util.CharsetUtils;
import io.netty.channel.Channel;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;


/**
 * @author carzy
 * @date 2020/8/14
 */
public class MessageController implements HandlerController {

    private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(MessageController.class);

    @Override
    public SipMethod method() {
        return SipMethod.MESSAGE;
    }

    @Override
    public void handler(FullSipRequest request, Channel channel) throws DocumentException {
        final AbstractSipHeaders headers = request.headers();

        final String type = headers.get(SipHeaderNames.CONTENT_TYPE);
        if (SipHeaderValues.APPLICATION_MANSCDP_XML.contentEqualsIgnoreCase(type)) {
            final String xml = request.content().toString(CharsetUtils.GB_2313);
            final Document document = DocumentHelper.parseText(xml);
            final String cmdType = document.getRootElement().element("CmdType").getTextTrim();
            if ("Keepalive".equalsIgnoreCase(cmdType)) {
                keepalive(request, channel);
            } else {
                err_405("cmdType not allowed.", request, channel);
            }
        } else {
            err_400("message content_type must be Application/MANSCDP+xml", request, channel);
        }
    }

    private void keepalive(FullSipRequest msg, Channel channel) {
        final AbstractSipHeaders headers = msg.headers();
        DefaultFullSipResponse response = new DefaultFullSipResponse(SipResponseStatus.OK);
        response.setRecipient(msg.recipient());
        final AbstractSipHeaders h = response.headers();
        h.set(SipHeaderNames.FROM, headers.get(SipHeaderNames.FROM))
                .set(SipHeaderNames.TO, headers.get(SipHeaderNames.TO) + ";tag=" + System.currentTimeMillis())
                .set(SipHeaderNames.CSEQ, headers.get(SipHeaderNames.CSEQ))
                .set(SipHeaderNames.CALL_ID, headers.get(SipHeaderNames.CALL_ID))
                .set(SipHeaderNames.USER_AGENT, "d-sip")
                .set(SipHeaderNames.CONTENT_LENGTH, SipHeaderValues.EMPTY_CONTENT_LENGTH);
        channel.writeAndFlush(response);
    }

}
