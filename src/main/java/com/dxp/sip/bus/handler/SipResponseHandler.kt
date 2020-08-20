package com.dxp.sip.bus.handler

import com.dxp.sip.codec.sip.FullSipResponse
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.channel.socket.nio.NioDatagramChannel
import io.netty.util.internal.logging.InternalLoggerFactory

/**
 * 处理sip请求
 *
 * @author carzy
 * @date 2020/8/11
 */
class SipResponseHandler : SimpleChannelInboundHandler<FullSipResponse>() {

    companion object {
        private val LOGGER = InternalLoggerFactory.getInstance(SipResponseHandler::class.java)
    }

    @Throws(Exception::class)
    override fun channelRead0(ctx: ChannelHandlerContext, msg: FullSipResponse) {
        val channel = ctx.channel()

        // 启动的时候已经声明了. TCP为NioSocketChannel, UDP为NioDatagramChannel
        if (channel is NioDatagramChannel) {
            LOGGER.info("[{}-{}] rec udp response msg", channel.id().asShortText(), msg.recipient().toString())
        } else {
            LOGGER.info("[{}-{}] rec tcp response msg", channel.id().asShortText(), msg.recipient().toString())
        }
    }

}