package com.dxp.sip.bus.fun.controller;

import com.dxp.sip.bus.fun.HandlerController;
import com.dxp.sip.codec.sip.*;
import com.dxp.sip.util.CharsetUtils;
import io.netty.channel.Channel;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

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
    public void handler(FullSipRequest request, Channel channel) {
        final AbstractSipHeaders headers = request.headers();

        final String type = headers.get(SipHeaderNames.CONTENT_TYPE);
        if (SipHeaderValues.APPLICATION_MANSCDP_XML.contentEqualsIgnoreCase(type)) {
            //todo.. XML解析。 国标使用的是 gb2313 字符集
            LOGGER.info("xml: {}", request.content().toString(CharsetUtils.GB_2313));
        } else {
            err_400("message content_type must be Application/MANSCDP+xml", request, channel);
        }
    }
}
