package com.dxp.sip

import com.dxp.sip.bus.handler.GbLoggingHandler
import com.dxp.sip.bus.handler.SipRequestHandler
import com.dxp.sip.bus.handler.SipResponseHandler
import com.dxp.sip.codec.sip.*
import io.netty.bootstrap.Bootstrap
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
import io.netty.channel.EventLoopGroup
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioDatagramChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.handler.logging.LogLevel
import io.netty.util.concurrent.Future
import io.netty.util.internal.logging.InternalLoggerFactory

/**
 * 启动类
 *
 * @author carzy
 * @date 2020/8/10
 */
class Application {

    private fun startUdp() {
        val b = Bootstrap()
        b.group(UDP_GROUP)
                .channel(NioDatagramChannel::class.java) // 关闭广播
                .option(ChannelOption.SO_BROADCAST, false)
                .handler(object : ChannelInitializer<NioDatagramChannel>() {
                    @Throws(Exception::class)
                    override fun initChannel(ch: NioDatagramChannel) {
                        ch.pipeline()
                                .addLast(AbstractSipResponseEncoder())
                                .addLast(AbstractSipRequestEncoder())
                                .addLast(SipObjectUdpDecoder())
                                .addLast(SipObjectAggregator(8192))
                                .addLast(LOGGING_HANDLER)
                                .addLast(SipRequestHandler())
                                .addLast(SipResponseHandler())
                    }
                })
        try {
            val future = b.bind(port).sync()
            LOGGER.info("udp port $port is running.")
            future.channel().closeFuture().addListener { f: Future<in Void?> ->
                if (f.isSuccess) {
                    LOGGER.info("udp exit suc on port $port")
                } else {
                    LOGGER.error("udp exit err on port $port", f.cause())
                }
            }
        } catch (e: Exception) {
            LOGGER.error("udp run port $port err", e)
            UDP_GROUP.shutdownGracefully();
        }
    }

    private fun startTcp() {
        val b = ServerBootstrap()
        b.group(BOSS_GROUP, WORKER_GROUP)
                .channel(NioServerSocketChannel::class.java)
                .option(ChannelOption.SO_BACKLOG, 512)
                .childHandler(object : ChannelInitializer<NioSocketChannel>() {
                    @Throws(Exception::class)
                    override fun initChannel(ch: NioSocketChannel) {
                        ch.pipeline()
                                .addLast(AbstractSipResponseEncoder())
                                .addLast(AbstractSipRequestEncoder())
                                .addLast(SipObjectTcpDecoder())
                                .addLast(SipObjectAggregator(8192))
                                .addLast(LOGGING_HANDLER)
                                .addLast(SipRequestHandler())
                                .addLast(SipResponseHandler())
                    }
                })
        try {
            val future = b.bind(port).sync()
            LOGGER.info("tcp port $port is running.")
            future.channel().closeFuture().addListener { f: Future<in Void?> ->
                if (f.isSuccess) {
                    LOGGER.info("tcp exit suc on port $port")
                } else {
                    LOGGER.error("tcp exit err on port $port", f.cause())
                }
            }
        } catch (e: Exception) {
            LOGGER.error("tcp run port $port err", e)
            BOSS_GROUP.shutdownGracefully()
            WORKER_GROUP.shutdownGracefully()
        }
    }

    companion object {
        private const val port = 5060
        private val LOGGER = InternalLoggerFactory.getInstance(Application::class.java)
        private val LOGGING_HANDLER = GbLoggingHandler(LogLevel.DEBUG)

        // Configure the server.
        private val BOSS_GROUP: EventLoopGroup = NioEventLoopGroup(1)
        private val WORKER_GROUP: EventLoopGroup = NioEventLoopGroup()
        private val UDP_GROUP: EventLoopGroup = NioEventLoopGroup()

        @JvmStatic
        fun main(args: Array<String>) {
            val application = Application()
            Thread { application.startTcp() }.start()
            Thread { application.startUdp() }.start()
        }
    }
}