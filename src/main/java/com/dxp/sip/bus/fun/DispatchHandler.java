package com.dxp.sip.bus.fun;

import com.dxp.sip.codec.sip.*;
import io.netty.channel.Channel;
import io.netty.channel.DefaultEventLoopGroup;
import io.netty.channel.EventLoopGroup;
import io.netty.util.concurrent.DefaultThreadFactory;

/**
 * @author carzy
 * @date 2020/8/14
 */
public class DispatchHandler {

    /**
     * 异步执行处理函数， 不阻塞work线程。
     */
    private final EventLoopGroup loopGroup = new DefaultEventLoopGroup(new DefaultThreadFactory("dis-han"));

    private DispatchHandler() {
    }

    public static final DispatchHandler INSTANCE = new DispatchHandler();

    public void handler(FullSipRequest request, Channel channel) {
        request.retain();
        loopGroup.submit(() -> {
            handler0(request, channel);
        });
    }

    private void handler0(FullSipRequest request, Channel channel) {
        try {
            final SipMethod method = request.method();
            final HandlerController controller = DispatchHandlerContext.method(method);
            if (controller == null) {
                err_405(request, channel);
            } else {
                controller.handler(request, channel);
            }
        } finally {
            request.release();
        }
    }

    private void err_405(FullSipRequest request, Channel channel) {
        final AbstractSipHeaders headers = request.headers();
        DefaultFullSipResponse response = new DefaultFullSipResponse(SipResponseStatus.METHOD_NOT_ALLOWED);
        response.setRecipient(request.recipient());
        final AbstractSipHeaders h = response.headers();
        h.set(SipHeaderNames.FROM, headers.get(SipHeaderNames.FROM))
                .set(SipHeaderNames.TO, headers.get(SipHeaderNames.TO))
                .set(SipHeaderNames.ALLOW, DispatchHandlerContext.allowMethod())
                .set(SipHeaderNames.CONTENT_LENGTH, headers.get(SipHeaderNames.CONTENT_LENGTH))
                .set(SipHeaderNames.CSEQ, headers.get(SipHeaderNames.CSEQ))
                .set(SipHeaderNames.CALL_ID, headers.get(SipHeaderNames.CALL_ID))
                .set(SipHeaderNames.REASON, request.method().asciiName())
                .set(SipHeaderNames.USER_AGENT, "d-sip");
        channel.writeAndFlush(response);
    }
}
