package com.dxp.sip.bus.handler;

import com.dxp.sip.codec.sip.*;
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
        final AbstractSipHeaders headers = msg.headers();

        logger.info("收到一个SIP请求");

        if (SipMethod.BAD == msg.method()) {
            logger.error("收到一个错误的SIP消息");
            StringBuilder builder = new StringBuilder();
            logger.error(SipMessageUtil.appendFullRequest(builder, msg).toString());
        }

        // todo..  test register
        DefaultFullSipResponse response = new DefaultFullSipResponse(SipResponseStatus.UNAUTHORIZED);
        final AbstractSipHeaders h = response.headers();
        h.set(SipHeaderNames.FROM, headers.get(SipHeaderNames.FROM))
                .set(SipHeaderNames.TO, headers.get(SipHeaderNames.TO))
                .set(SipHeaderNames.CONTENT_LENGTH, headers.get(SipHeaderNames.CONTENT_LENGTH))
                .set(SipHeaderNames.CSEQ, headers.get(SipHeaderNames.CSEQ))
                .set(SipHeaderNames.CALL_ID, headers.get(SipHeaderNames.CALL_ID))
                .set(SipHeaderNames.USER_AGENT, "d-sip");
        if (headers.contains(SipHeaderNames.AUTHORIZATION)) {
            String wwwAuth = "Digest realm=\"31011000002001234567\", nonce=\"" +
                    "b700dc7cb094478503a21148184a3731\", opaque=\"5b279c2efd18d123d1f4a2182527a281\", algorithm=MD5";
            h.set(SipHeaderNames.WWW_AUTHENTICATE, wwwAuth);
        } else {
            response.setStatus(SipResponseStatus.OK);
        }
        ctx.writeAndFlush(response);
    }
}
