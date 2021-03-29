package com.cylt.ftp.handler;

import com.cylt.ftp.App;
import com.cylt.ftp.client.Client;
import com.cylt.ftp.codec.CFTPDecoder;
import com.cylt.ftp.codec.CFTPEncoder;
import com.cylt.ftp.config.ConfigParser;
import com.cylt.ftp.protocol.CFTPMessage;
import com.cylt.ftp.protocol.DataHead;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description 服务器连接处理器
 * @Author zhangjun
 * @Data 2020/5/26
 * @Time 21:15
 */

public class ClientHandler extends ChannelInboundHandlerAdapter {

    /**
     * 全局管理channels
     */
    public static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    //serverPort与localPort映射map
    private ConcurrentHashMap<Integer, Integer> portMap = new ConcurrentHashMap<>();
    //所有localChannel共享，减少线程上下文切换
    private EventLoopGroup localGroup = new NioEventLoopGroup();
    //每个外部请求channelId与其处理器handler的映射关系
    private ConcurrentHashMap<String, LocalHandler> localHandlerMap = new ConcurrentHashMap<>();

    private ChannelHandlerContext ctx = null;

    public ChannelHandlerContext getCtx() {
        return ctx;
    }


    public static Client localHelper = new Client();

    //连接建立，初始化
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
        //连接建立成功，发送注册请求
        CFTPMessage message = new CFTPMessage();
        message.setType(CFTPMessage.TYPE_REGISTER);
        HashMap<String, Object> metaData = new HashMap<>();
        metaData.put("clientKey", ConfigParser.get("client-key"));
        //获取配置中指定的服务器端口
        ArrayList<Integer> serverPortArr = new ArrayList<>();
        serverPortArr.add((Integer) ConfigParser.get("data-local-port"));
        metaData.put("ports", serverPortArr);
        message.setMetaData(metaData);
        ctx.writeAndFlush(message);
        System.out.println(this.getClass() + "\r\n 与服务器连接建立成功，正在进行注册...");
    }

    //读取数据
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        CFTPMessage message = (CFTPMessage) msg;
        switch (message.getType()) {
            // 授权
            case CFTPMessage.TYPE_AUTH:
                processAuth(message);
                break;
            // 外部请求进入，开始与内网建立连接
            case CFTPMessage.TYPE_CONNECTED:
                processConnected(message);
                break;
            // 断开连接
            case CFTPMessage.TYPE_DISCONNECTED:
                processDisConnected(message);
                break;
            // 心跳请求
            case CFTPMessage.TYPE_KEEPALIVE:
                //心跳，不做处理
                break;
            // 数据传输
            case CFTPMessage.TYPE_DATA:
                processData(message);
                break;
        }
    }

    //连接中断
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        channels.close();
        System.out.println(this.getClass() + "\r\n 与服务器连接断开");
        localGroup.shutdownGracefully();
    }

    //异常处理
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println(this.getClass() + "\r\n 连接异常");
        cause.printStackTrace();
        //传递异常
        ctx.fireExceptionCaught(cause);
        ctx.channel().close();
    }

    /**
     * @return void
     * @Description 授权结果处理
     * @Date 11:37 2020/2/22
     * @Param [message]
     **/
    public void processAuth(CFTPMessage message) {
        if ((Boolean) message.getMetaData().get("isSuccess")) {
            System.out.println(this.getClass() + "\r\n 注册成功");
        } else {
            ctx.fireExceptionCaught(new Throwable());
            ctx.channel().close();
            System.out.println(this.getClass() + "\r\n 注册失败，原因：" + message.getMetaData().get("reason"));
        }
    }

    /**
     * @return void
     * @Description 服务器通知客户端与本地服务建立连接
     * @Date 11:38 2020/2/22
     * @Param [message]
     **/
    public void processConnected(CFTPMessage message) {
        ClientHandler clientHandler = this;
        message.getDataHead().setType(DataHead.READY_COMPLETE);
        ChannelInitializer channelInitializer = new ChannelInitializer() {
            @Override
            protected void initChannel(Channel channel) throws Exception {
                LocalHandler localHandler = new LocalHandler(clientHandler, message);
                channel.pipeline().addLast(
                        //固定帧长解码器ClientHandler
                        new LengthFieldBasedFrameDecoder(App.MAX_FRAME_LENGTH, App.LENGTH_FIELD_OFFSET, App.LENGTH_FIELD_LENGTH, App.LENGTH_ADJUSTMENT, App.INITIAL_BYTES_TO_STRIP),
                        //自定义协议解码器
                        new CFTPDecoder(),
                        //自定义协议编码器
                        new CFTPEncoder(),
                        localHandler
                );
                channels.add(channel);
                localHandlerMap.put(message.getDataHead().getId(), localHandler);
            }
        };
        String server = (String) ConfigParser.get("server-host");
        //这里根据portMap将远程服务器端口作为key获取对应的本地端口
        int remotePort = (Integer) message.getMetaData().get("remotePort");
        int localPort = (Integer) ConfigParser.get("data-local-port");
        localHelper.start(localGroup, channelInitializer, server, remotePort);
        System.out.println(this.getClass() + "\r\n 服务器" + remotePort + "端口进入连接，正在向本地" + localPort + "端口建立连接");
    }

    /**
     * @return void
     * @Description 处理外部请求与代理服务器断开连接通知
     * @Date 11:40 2020/2/22
     * @Param [message]
     **/
    public void processDisConnected(CFTPMessage message) {
        String channelId = message.getMetaData().get("channelId").toString();
        LocalHandler handler = localHandlerMap.get(channelId);
        if (handler != null) {
            handler.getLocalCtx().close();
            localHandlerMap.remove(channelId);
        }
    }

    /**
     * @return void
     * @Description 处理服务器传输的请求数据
     * @Date 11:40 2020/2/22
     * @Param [message]
     **/
    public void processData(CFTPMessage message) {
        processConnected(message);
    }

}
