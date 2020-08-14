package com.dxp.sip;

import com.dxp.sip.bus.handler.GbLoggingHandler;
import com.dxp.sip.bus.handler.SipRequestHandler;
import com.dxp.sip.bus.handler.SipResponseHandler;
import com.dxp.sip.codec.sip.*;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

/**
 * 启动类
 *
 * @author carzy
 * @date 2020/8/10
 */
public class Application {

    private static int port = 5060;
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(Application.class);
    private static final GbLoggingHandler loggingHandler = new GbLoggingHandler(LogLevel.DEBUG);

    // Configure the server.
    private static final EventLoopGroup BOSS_GROUP = new NioEventLoopGroup(1);
    private static final EventLoopGroup WORKER_GROUP = new NioEventLoopGroup();

    public static void main(String[] args) {
        Application application = new Application();
        try {
            application.startUdp();
//            application.startTcp();
        } catch (Exception e) {
            BOSS_GROUP.shutdownGracefully();
            WORKER_GROUP.shutdownGracefully();
        }
    }

    private void startUdp() throws InterruptedException {
        Bootstrap b = new Bootstrap();
        b.group(WORKER_GROUP)
                .channel(NioDatagramChannel.class)
                // 关闭广播
                .option(ChannelOption.SO_BROADCAST, false)
                .handler(new ChannelInitializer<NioDatagramChannel>() {
                    @Override
                    protected void initChannel(NioDatagramChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast(new SipResponseEncoder())
                                .addLast(new SipRequestEncoder())
                                .addLast(new SipObjectUdpDecoder())
                                .addLast(new SipObjectAggregator(8192))
                                .addLast(loggingHandler)
                                .addLast(new SipRequestHandler())
                                .addLast(new SipResponseHandler());
                    }
                });

        ChannelFuture future = b.bind(port).sync();

        logger.info("udp port " + port + " is running.");

        future.channel().closeFuture().addListener(f -> {
            if (f.isSuccess()) {
                logger.info("udp exit suc on port " + port);
            } else {
                logger.error("udp exit err on port " + port, f.cause());
            }
        });
    }

    private void startTcp() throws InterruptedException {
        ServerBootstrap b = new ServerBootstrap();
        b.group(BOSS_GROUP, WORKER_GROUP)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 512)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast(new SipResponseEncoder())
                                .addLast(new SipRequestEncoder())
                                .addLast(new SipObjectDecoder())
                                .addLast(new SipObjectAggregator(8192))
                                .addLast(loggingHandler)
                                .addLast(new SipRequestHandler())
                                .addLast(new SipResponseHandler());
                    }
                });
        final ChannelFuture future = b.bind(port).sync();

        logger.info("tcp port " + port + " is running.");

        future.channel().closeFuture().addListener(f -> {
            if (f.isSuccess()) {
                logger.info("tcp exit suc on port " + port);
            } else {
                logger.error("tcp exit err on port " + port, f.cause());
            }
        });
    }
}
