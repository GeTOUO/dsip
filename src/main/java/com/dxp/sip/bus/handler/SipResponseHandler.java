package com.dxp.sip.bus.handler;

import com.dxp.sip.codec.sip.FullSipResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

import java.nio.charset.StandardCharsets;

/**
 * 处理sip请求
 *
 * @author carzy
 * @date 2020/8/11
 */
public class SipResponseHandler extends SimpleChannelInboundHandler<FullSipResponse> {

    private static final InternalLogger logger = InternalLoggerFactory.getInstance(SipResponseHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullSipResponse msg) throws Exception {
        logger.info("收到一个SIP响应");
    }
}
