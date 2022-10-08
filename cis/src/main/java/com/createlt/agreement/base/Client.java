package com.createlt.agreement.base;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioSocketChannel;

public class Client {

    public Channel channel = null;
    private Bootstrap bootstrap = new Bootstrap();
    public synchronized void start(EventLoopGroup workerGroup, ChannelInitializer channelInitializer,
                                   String host, int port) throws Exception {

        if (host == null || port == 0) {
            System.out.println("配置信息有误");
            return;
        }

        try {
            bootstrap.group(workerGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(channelInitializer);
            channel = bootstrap.connect(host, port).sync().channel();
            channel.closeFuture().addListener((ChannelFutureListener) future -> {
                workerGroup.shutdownGracefully();
                close();
            });
            System.out.println(channel);
        } catch (Exception e) {
            close();
            workerGroup.shutdownGracefully();
            System.out.println(this.getClass() + "\r\n 关闭线程组内所有连接");
            throw new Exception(e.getMessage());
        }
    }

    public synchronized void close() {
        if (channel != null) {
            channel.deregister();
            channel.close();
            bootstrap.clone();
        }
    }
}
