package com.dxp.sip;

import com.dxp.sip.bus.handler.SipRequestHandler;
import com.dxp.sip.bus.handler.SipResponseHandler;
import com.dxp.sip.codec.sip.SipObjectAggregator;
import com.dxp.sip.codec.sip.SipObjectDecoder;
import com.dxp.sip.codec.sip.SipRequestEncoder;
import com.dxp.sip.codec.sip.SipResponseEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpResponseEncoder;
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

    public static void main(String[] args) throws InterruptedException {

        ServerBootstrap b = new ServerBootstrap();
        // Configure the server.
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        LoggingHandler loggingHandler = new LoggingHandler(LogLevel.DEBUG);

        b.group(bossGroup, workerGroup)
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
