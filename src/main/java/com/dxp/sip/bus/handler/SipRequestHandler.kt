package com.dxp.sip.bus.handler

import com.dxp.sip.bus.`fun`.DispatchHandler
import com.dxp.sip.codec.sip.FullSipRequest
import com.dxp.sip.codec.sip.SipMessageUtil
import com.dxp.sip.codec.sip.SipMethod
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.channel.socket.nio.NioDatagramChannel
import io.netty.util.internal.logging.InternalLoggerFactory

/**
 * 处理sip请求.
 *
 *
 * 该对象必须单例使用.
 *
 * @author carzy
 * @date 2020/8/11
 */
class SipRequestHandler : SimpleChannelInboundHandler<FullSipRequest>() {

    @Throws(Exception::class)
    override fun channelRead0(ctx: ChannelHandlerContext, msg: FullSipRequest) {
        val headers = msg.headers()
        val channel = ctx.channel()

        // 启动的时候已经声明了. TCP为NioSocketChannel, UDP为NioDatagramChannel
        if (channel is NioDatagramChannel) {
            LOGGER.info("[{}{}] rec udp request msg", channel.id().asShortText(), msg.recipient().toString())
        } else {
            LOGGER.info("[{}{}] rec tcp request msg", channel.id().asShortText(), msg.recipient().toString())
        }
        if (SipMethod.BAD === msg.method()) {
            LOGGER.error("收到一个错误的SIP消息")
            val builder = StringBuilder()
            LOGGER.error(SipMessageUtil.appendFullRequest(builder, msg).toString())
        }

        // 异步执行
        DispatchHandler.INSTANCE.handler(msg, ctx.channel())
    }

    companion object {
        private val LOGGER = InternalLoggerFactory.getInstance(SipRequestHandler::class.java)
    }
}