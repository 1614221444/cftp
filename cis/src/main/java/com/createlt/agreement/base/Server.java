package com.createlt.agreement.base;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.Timer;
import java.util.TimerTask;

/**
 * ftp server服务
 */
public class Server {

    ServerBootstrap serverBootstrap = new ServerBootstrap();
    Channel channel;
    public synchronized void start(EventLoopGroup bossGroup, EventLoopGroup workerGroup,
                                   String serverHost, int serverPort, ChannelInitializer channelInitializer) {
        try {
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(channelInitializer)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            //连接超时时间，连接公网服务器时可能会超时导致连接失败，最好不要设置
            //.option(ChannelOption.CONNECT_TIMEOUT_MILLIS,10000);
            channel = serverBootstrap.bind(serverHost, serverPort).sync().channel();
            //Channel channel = serverBootstrap.bind(serverHost,serverPort).awaitUninterruptibly().channel();
            channel.closeFuture().addListener((ChannelFutureListener) future -> {
                //channel关闭，将channel从workerGroup取消注册
                channel.deregister();
                channel.close();
            });
        } catch (InterruptedException e) {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            System.err.println(this.toString() + "\r\n bootStart(" + serverPort + ")中出现错误");
            e.printStackTrace();

            System.out.println("数据端口创建失败 正在尝试重新创建");
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    start(bossGroup, workerGroup,
                            serverHost, serverPort, channelInitializer);
                }
            }, 2000);
        }
    }

    /**
     * 关闭服务
     */
    public synchronized void close() {
        channel.deregister();
        channel.close();
    }
}
