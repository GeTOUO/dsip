package com.dxp.sip.bus.fun;

import com.dxp.sip.codec.sip.*;
import com.dxp.sip.util.SendErrorResponseUtil;
import io.netty.channel.Channel;
import io.netty.channel.DefaultEventLoopGroup;
import io.netty.channel.EventLoopGroup;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.dom4j.DocumentException;

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
                SendErrorResponseUtil.err_405(request, channel, request.method().asciiName() + " not allowed");
            } else {
                controller.handler(request, channel);
            }
        } catch (DocumentException e) {
            SendErrorResponseUtil.err_400(request, channel, "xml err");
        } catch (Exception e) {
            SendErrorResponseUtil.err_500(request, channel, e.getMessage());
        } finally {
            request.release();
        }
    }


}
