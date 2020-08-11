package com.dxp.sip.bus.handler;

import com.dxp.sip.codec.sip.FullSipRequest;
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
public class SipRequestHandler extends SimpleChannelInboundHandler<FullSipRequest> {

    private static final InternalLogger logger = InternalLoggerFactory.getInstance(SipRequestHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullSipRequest msg) throws Exception {
        logger.info("收到一个SIP请求");
        logger.info("URL ===> " + msg.uri());
        logger.info("method ===> " + msg.method().name());
        logger.info("headers ===> " + msg.headers().toString());
        logger.info("content ===> " + msg.content().readCharSequence(msg.content().readableBytes(), StandardCharsets.UTF_8));
    }
}
