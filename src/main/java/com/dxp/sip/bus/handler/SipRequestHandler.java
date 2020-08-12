package com.dxp.sip.bus.handler;

import com.dxp.sip.codec.sip.*;
import com.dxp.sip.util.CharsetUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

/**
 * 处理sip请求.
 * <p>
 * 该对象必须单例使用.
 *
 * @author carzy
 * @date 2020/8/11
 */
public class SipRequestHandler extends SimpleChannelInboundHandler<FullSipRequest> {

    private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(SipRequestHandler.class);
    private Channel channel;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullSipRequest msg) throws Exception {
        final AbstractSipHeaders headers = msg.headers();
        this.channel = ctx.channel();

        // 启动的时候已经声明了. TCP为NioSocketChannel, UDP为NioDatagramChannel
        if (channel instanceof NioDatagramChannel) {
            LOGGER.warn("rec udp msg");
        } else {
            LOGGER.warn("rec tcp msg");
        }

        if (SipMethod.BAD == msg.method()) {
            LOGGER.error("收到一个错误的SIP消息");
            StringBuilder builder = new StringBuilder();
            LOGGER.error(SipMessageUtil.appendFullRequest(builder, msg).toString());
        }

        final SipMethod method = msg.method();
        if (SipMethod.REGISTER == method) {
            register(msg);
        } else if (SipMethod.MESSAGE == method) {
            message(msg);
        } else if (SipMethod.INVITE == method) {
            invite(msg);
        } else {
            err_405(method.asciiName(), msg);
        }
    }

    private void invite(FullSipRequest msg) {
        final AbstractSipHeaders headers = msg.headers();
        if (!checkContentLength(msg)) {
            return;
        }

        final String type = headers.get(SipHeaderNames.CONTENT_TYPE);
        if (SipHeaderValues.APPLICATION_SDP.contentEqualsIgnoreCase(type)) {
            //todo.. sdp解析。
            LOGGER.info("sdp: {}", msg.content().toString(CharsetUtils.US_ASCII));
        } else {
            err_405("message content_type must be Application/MANSCDP+xml", msg);
        }
    }

    private void message(FullSipRequest msg) {
        final AbstractSipHeaders headers = msg.headers();
        if (!checkContentLength(msg)) {
            return;
        }

        final String type = headers.get(SipHeaderNames.CONTENT_TYPE);
        if (SipHeaderValues.APPLICATION_MANSCDP_XML.contentEqualsIgnoreCase(type)) {
            //todo.. XML解析。 国标使用的是 gb2313 字符集
            LOGGER.info("xml: {}", msg.content().toString(CharsetUtils.GB_2313));
        } else {
            err_405("message content_type must be Application/MANSCDP+xml", msg);
        }
    }

    private void register(FullSipRequest msg) {
        final AbstractSipHeaders headers = msg.headers();
        DefaultFullSipResponse response = new DefaultFullSipResponse(SipResponseStatus.UNAUTHORIZED);
        response.setRecipient(msg.recipient());
        final AbstractSipHeaders h = response.headers();
        h.set(SipHeaderNames.FROM, headers.get(SipHeaderNames.FROM))
                .set(SipHeaderNames.TO, headers.get(SipHeaderNames.TO))
                .set(SipHeaderNames.CONTENT_LENGTH, headers.get(SipHeaderNames.CONTENT_LENGTH))
                .set(SipHeaderNames.CSEQ, headers.get(SipHeaderNames.CSEQ))
                .set(SipHeaderNames.CALL_ID, headers.get(SipHeaderNames.CALL_ID))
                .set(SipHeaderNames.USER_AGENT, "d-sip");
        if (!headers.contains(SipHeaderNames.AUTHORIZATION)) {
            String wwwAuth = "Digest realm=\"31011000002001234567\", nonce=\"" +
                    "b700dc7cb094478503a21148184a3731\", opaque=\"5b279c2efd18d123d1f4a2182527a281\", algorithm=MD5";
            h.set(SipHeaderNames.WWW_AUTHENTICATE, wwwAuth);
        } else {
            response.setStatus(SipResponseStatus.OK);
        }
        channel.writeAndFlush(response);
    }

    private void err_405(CharSequence reason, FullSipRequest msg) {
        final AbstractSipHeaders headers = msg.headers();
        DefaultFullSipResponse response = new DefaultFullSipResponse(SipResponseStatus.METHOD_NOT_ALLOWED);
        response.setRecipient(msg.recipient());
        final AbstractSipHeaders h = response.headers();
        h.set(SipHeaderNames.FROM, headers.get(SipHeaderNames.FROM))
                .set(SipHeaderNames.TO, headers.get(SipHeaderNames.TO))
                .set(SipHeaderNames.CONTENT_LENGTH, headers.get(SipHeaderNames.CONTENT_LENGTH))
                .set(SipHeaderNames.CSEQ, headers.get(SipHeaderNames.CSEQ))
                .set(SipHeaderNames.CALL_ID, headers.get(SipHeaderNames.CALL_ID))
                .set(SipHeaderNames.REASON, reason)
                .set(SipHeaderNames.USER_AGENT, "d-sip");
        channel.writeAndFlush(response);
    }

    private boolean checkContentLength(FullSipRequest msg) {
        final AbstractSipHeaders headers = msg.headers();
        final Integer contentLength = headers.getInt(SipHeaderNames.CONTENT_LENGTH);
        if (contentLength == null || contentLength < 1) {
            err_405("contentLength must > 0", msg);
            return false;
        } else {
            return true;
        }
    }
}
