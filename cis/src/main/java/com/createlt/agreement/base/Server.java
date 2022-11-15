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

    /**
     * 通道绑定
     */
    ServerBootstrap serverBootstrap = new ServerBootstrap();

    /**
     * 通道实例
     */
    Channel channel;


    /**
     * netty服务启动
     * @param bossGroup 请求头部线程组
     * @param workerGroup 请求消费线程组
     * @param serverHost 监听IP
     * @param serverPort 监听端口
     * @param channelInitializer TCP通道规则
     */
    public synchronized void start(EventLoopGroup bossGroup, EventLoopGroup workerGroup,
                                   String serverHost, int serverPort, ChannelInitializer channelInitializer) {
        try {
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(channelInitializer)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
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
