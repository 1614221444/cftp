package com.cylt.ftp.hendler;

import com.alibaba.fastjson.JSONArray;
import com.cylt.ftp.App;
import com.cylt.ftp.codec.CFTPDecoder;
import com.cylt.ftp.codec.CFTPEncoder;
import com.cylt.ftp.config.ConfigParser;
import com.cylt.ftp.protocol.CFTPMessage;
import com.cylt.ftp.server.Server;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.ChannelMatcher;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.HashMap;
import java.util.Map;

/**
 * 处理服务器接收到的客户端连接
 */
public class ServerHandler extends ChannelInboundHandlerAdapter {

    //在所有ServerHandler中共享当前在线的授权信息
    private static Map<String, Integer> clients = new HashMap<>();

    //统一管理客户端channel和remote channel
    private static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    //所有remote channel共享的线程池，减少线程创建
    //bossGroup指的是所有指定端口的监听处理线程
    //workerGroup指的是所有端口所收到连接的处理线程
    private EventLoopGroup bossGroup = new NioEventLoopGroup();
    private EventLoopGroup workerGroup = new NioEventLoopGroup();

    private Server remoteHelper = new Server();

    //客户端标识clientKey
    private String clientKey;
    //代理客户端的ChannelHandlerContext
    private ChannelHandlerContext ctx;
    //判断代理客户端是否已注册授权
    private boolean isRegister = false;

    public ChannelHandlerContext getCtx() {
        return ctx;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
        System.out.println(this.getClass() + "\r\n 有客户端建立连接，客户端地址为：" + ctx.channel().remoteAddress());
    }

    //数据读取与转发
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        CFTPMessage message = (CFTPMessage) msg;
        if (message.getType() == CFTPMessage.TYPE_REGISTER) {
            //处理客户端注册请求
            processRegister(message);
        } else if (isRegister) {
            switch (message.getType()) {
                //客户端请求断开连接
                case CFTPMessage.TYPE_DISCONNECTED:
                    processDisconnect(message);
                    break;
                //心跳，不做处理
                case CFTPMessage.TYPE_KEEPALIVE:
                    break;
                //处理数据
                case CFTPMessage.TYPE_DATA:
                    processData(message);
                    break;
                default:
                    System.out.printf("非法请求");
            }
        } else {
            System.out.println(this.getClass() + "\r\n 有未授权的客户端尝试发送消息，断开连接");
            ctx.close();
        }
    }

    //连接中断
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        channels.remove(ctx.channel());
        ctx.channel().close();
        //移除断开客户端的授权码
        clients.remove(clientKey);
        //取消正在监听的端口，否则第二次连接时无法再次绑定端口
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
        System.out.println(this.getClass() + "\r\n 客户端连接中断：" + ctx.channel().remoteAddress());
    }

    //连接异常处理
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.channel().close();
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
        System.out.println(this.getClass() + "\r\n 连接异常，已中断");
        cause.printStackTrace();
    }

    /**
     * @return boolean
     * @Description 判断客户端是否有授权
     * @Param [clientKey]
     **/
    public synchronized boolean isLegal(String clientKey) {
        boolean flag = ConfigParser.user.stream().filter(m ->
                m.get("key").toString().equals(clientKey)).findAny().isPresent();

        if (flag) {
            //一个client-key只允许一个代理客户端使用
            if (isExist(clientKey)) {
                System.out.println("不允许同一授权码重复登录\n");
                return false;
            }
            clients.put(clientKey, 1);
            this.clientKey = clientKey;
            return true;
        }
        return false;
    }

    /**
     * 判断授权码是否已存在连接
     *
     * @return
     * @Description
     * @Param
     */
    public boolean isExist(String clientKey) {
        if (clients.get(clientKey) != null) {
            return true;
        }
        return false;
    }

    /**
     * @return void
     * @Description 处理客户端注册请求
     * @Param [message]
     **/
    public void processRegister(CFTPMessage message) throws Exception {

        HashMap<String, Object> metaData = new HashMap<>();


        String clientKey = message.getMetaData().get("clientKey").toString();
        //客户端合法性判断
        if (isLegal(clientKey)) {
            //指定服务器需要开启的对外访问端口
            Object[] ports = ((JSONArray) message.getMetaData().get("ports")).toArray();
            try {
                for (Object port : ports) {
                    metaData.put("remotePort", port);
                }
                metaData.put("channelId", "123456789");
                metaData.put("isSuccess", true);
                clients.put(clientKey, 1);
                isRegister = true;
                System.out.println(this.getClass() + "\r\n 客户端注册成功，clientKey为：" + clientKey);
            } catch (Exception e) {
                metaData.put("isSuccess", false);
                metaData.put("reason", e.getMessage());
                System.out.println(this.getClass() + "\r\n processRegister()中出现错误");
                System.out.println(this.getClass() + "\r\n 启动器出错，客户端注册失败，clientKey为：" + clientKey);
                e.printStackTrace();
            }
        } else {
            metaData.put("isSuccess", false);
            metaData.put("reason", "client-key不合法，请两分钟后重试");
            System.out.println(this.getClass() + "\r\n 客户端注册失败，使用了不合法的clientKey，clientKey为：" + clientKey);
        }

        CFTPMessage res = new CFTPMessage();
        res.setType(CFTPMessage.TYPE_AUTH);
        res.setMetaData(metaData);
        ctx.writeAndFlush(res);
    }

    /**
     * @return void
     * @Description 处理客户端断开请求
     * @Param [message]
     **/
    public void processDisconnect(CFTPMessage message) {
        channels.close(new ChannelMatcher() {
            @Override
            public boolean matches(Channel channel) {
                return channel.id().asLongText().equals(message.getMetaData().get("channelId"));
            }
        });
        System.out.println(this.getClass() + "\r\n 有客户端请求断开，clientKey为：" + clientKey);
    }

    /**
     * @return void
     * @Description 处理客户端发送的数据
     * @Param [message]
     **/
    public void processData(CFTPMessage message) {
        if (!isExist(message.getMetaData().get("clientKey").toString())
        ) {
            return;
        }
        // 创建文件传输端口
        ServerHandler serverHandler = this;
        int port = 6565;
        ChannelInitializer channelInitializer = new ChannelInitializer() {
            @Override
            protected void initChannel(Channel channel) {
                RemoteHandler remoteHandler = new RemoteHandler(serverHandler, port, message);
                channel.pipeline().addLast(
                        //固定帧长解码器
                        new LengthFieldBasedFrameDecoder(App.MAX_FRAME_LENGTH, App.LENGTH_FIELD_OFFSET, App.LENGTH_FIELD_LENGTH, App.LENGTH_ADJUSTMENT, App.INITIAL_BYTES_TO_STRIP),
                        //自定义协议解码器
                        new CFTPDecoder(),
                        //自定义协议编码器
                        new CFTPEncoder(),
                        remoteHandler
                );
                //向channelGroup注册remote channel
                channels.add(channel);

            }
        };
        remoteHelper.start(bossGroup, workerGroup, (String) ConfigParser.get("server-host"), port, channelInitializer);
        Map<String, Object> map = message.getMetaData();
        map.put("remotePort", port);
        message.setMetaData(map);
        ctx.writeAndFlush(message);
        System.out.println("数据端口 " + port + " 创建成功 ，等待连接");
    }

}
