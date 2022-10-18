package com.createlt.agreement.headler;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.createlt.agreement.base.Client;
import com.createlt.agreement.client.CftpClient;
import com.createlt.agreement.codec.CFTPDecoder;
import com.createlt.agreement.codec.CFTPEncoder;
import com.createlt.agreement.codec.CFTPMessage;
import com.createlt.agreement.codec.DataHead;
import com.createlt.cis.entity.CisAuthentication;
import com.createlt.cis.entity.CisController;
import com.createlt.cis.service.ICisAuthenticationService;
import com.createlt.cis.service.ICisControllerService;
import com.createlt.common.BaseController;
import com.createlt.common.ToolSpring;
import com.createlt.common.WebSocketServer;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.*;
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
    private Map<String, EventLoopGroup> localGroup = new HashMap<>();
    //每个外部请求channelId与其处理器handler的映射关系
    public ConcurrentHashMap<String, LocalHandler> localHandlerMap = new ConcurrentHashMap<>();

    private ChannelHandlerContext ctx = null;

    public ChannelHandlerContext getCtx() {
        return ctx;
    }


    private static Map<String, Client> localHelperMap = new HashMap<>();

    private WebSocketServer webSocketServer;

    /**
     * 服务认证service
     */
    private ICisAuthenticationService cisAuthenticationService;

    /**
     * 客户端实例
     */
    private ICisControllerService cisControllerService;

    /**
     * 控制器客户端实例
     */
    private CisController controller;


    /**
     * 当前客户端实例ID
     */
    String clientId = "";

    /**
     * 连接状态主动推送人
     */
    String userId = "";

    /**
     * 当前协议登录用户
     */
    String loginId = "";

    public CisController getController() {
        return controller;
    }

    public WebSocketServer getWebSocketServer() {
        return webSocketServer;
    }


    public String getLoginId() {
        return loginId;
    }

    public String getClientId() {
        return clientId;
    }

    public String getUserId() {
        return userId;
    }

    public ClientHandler(String clientId, String userId) {
        cisAuthenticationService = (ICisAuthenticationService) ToolSpring.getBean("cisAuthenticationServiceImpl");
        cisControllerService = (ICisControllerService) ToolSpring.getBean("cisControllerServiceImpl");
        webSocketServer = (WebSocketServer) ToolSpring.getBean("webSocketServer");
        this.clientId = clientId;
        this.userId = userId;
    }

    //连接建立，初始化
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
        //连接建立成功，发送注册请求
        CFTPMessage message = new CFTPMessage();
        message.setType(CFTPMessage.TYPE_REGISTER);
        HashMap<String, Object> metaData = new HashMap<>();
        List<CisAuthentication> authList = cisAuthenticationService.list(new LambdaQueryWrapper<CisAuthentication>()
                .eq(CisAuthentication::getControllerId, clientId));
        loginId = authList.get(0).getLoginName();
        metaData.put("clientKey", loginId);
        //获取配置中指定的服务器端口
        ArrayList<Integer> serverPortArr = new ArrayList<>();
        serverPortArr.add(9124);
        metaData.put("ports", serverPortArr);
        message.setMetaData(metaData);
        ctx.writeAndFlush(message);
        System.out.println("与服务器连接建立成功，正在进行注册...");
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
        for (String id : localGroup.keySet()) {
            dataPortClose(id);
        }
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
        BaseController base = new BaseController();
        if ((Boolean) message.getMetaData().get("isSuccess")) {
            //System.out.println(this.getClass() + "\r\n 注册成功");
            // 推送消息到发起人
            webSocketServer.sendInfo(base.responseSuccess("客户端连接成功"), userId);
            // 更新实例启动状态
            controller = cisControllerService.getById(clientId);
            controller.setIsStart(true);
            controller.setStartTime(new Date());
            cisControllerService.updateById(controller);
        } else {
            //ctx.fireExceptionCaught(new Throwable());
            ctx.channel().close();
            //System.out.println(this.getClass() + "\r\n 连接失败，原因：" + message.getMetaData().get("reason"));
            webSocketServer.sendInfo(base.responseFail("连接失败，原因："+ message.getMetaData().get("reason")), userId);
        }
    }

    /**
     * @return void
     * @Description 服务器通知客户端与本地服务建立连接
     * @Date 11:38 2020/2/22
     * @Param [message]
     **/
    public void processConnected(CFTPMessage message) throws Exception {
        ClientHandler client = this;
        ChannelInitializer channelInitializer = new ChannelInitializer() {
            @Override
            protected void initChannel(Channel channel) throws Exception {
                LocalHandler localHandler = localHandlerMap.get(message.getDataHead().getId());
                // 判断有没有创建处理器实例 如果创建了说明是当前端发送的请求 (需要发送文件端)
                if (localHandler == null) {
                    localHandler = new LocalHandler(client, message, false);
                }
                channel.pipeline().addLast(
                        //固定帧长解码器ClientHandler
                        new LengthFieldBasedFrameDecoder(CftpClient.MAX_FRAME_LENGTH, CftpClient.LENGTH_FIELD_OFFSET, CftpClient.LENGTH_FIELD_LENGTH, CftpClient.LENGTH_ADJUSTMENT, CftpClient.INITIAL_BYTES_TO_STRIP),
                        //自定义协议解码器
                        new CFTPDecoder(),
                        //自定义协议编码器
                        new CFTPEncoder(),
                        localHandler
                );
                channels.add(channel);
            }
        };
        String server = "localhost";
        //这里根据portMap将远程服务器端口作为key获取对应的本地端口
        int remotePort = (Integer) message.getMetaData().get("remotePort");
        Client localHelper = new Client();

        EventLoopGroup local = new NioEventLoopGroup();
        localGroup.put(message.getDataHead().getId(), local);
        localHelper.start(local, channelInitializer, server, remotePort);
        localHelperMap.put(message.getDataHead().getId(), localHelper);
        System.out.println("服务器" + remotePort + "端口进入连接，正在向本地端口建立连接, 数据id：" + message.getDataHead().getId());
    }


    /**
     * 关闭数据通道
     *
     * @param id 通道id
     */
    public void dataPortClose(String id) {
        //取消正在监听的端口，否则第二次连接时无法再次绑定端口
        if (localGroup.get(id) != null) {
            localGroup.get(id).shutdownGracefully();
        }
        localGroup.remove(id);
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
    public void processData(CFTPMessage message) throws Exception {
        switch (message.getDataHead().getType()) {
            // 准备连接
            case DataHead.READY_COMPLETE:
                processConnected(message);
                break;
            // 外部请求进入，开始与内网建立连接
            case DataHead.RECEIVE_END:
                String key = message.getDataHead().getId();
                localHandlerMap.get(message.getDataHead().getId()).progress(100);
                localHandlerMap.remove(message.getDataHead().getId());
                System.out.println("\n数据传输完成 通道已关闭 数据通道id：" + key);
                localHelperMap.get(key).close();
                localHelperMap.remove(key);
                break;
            // 补发数据确认
            case DataHead.REISSUE:
                //这里没有确认动作 自动补发
                /*System.out.println("数据补发 通道开启 数据通道id：" + message.getMetaData().get("id")
                        + "\n补发初始化上传进度：" + message.getMetaData().get("index"));
                Send send = new Send((String) message.getMetaData().get("id"),
                        (String) message.getMetaData().get("url"),this, (int) message.getMetaData().get("index"), true);
                send.run();*/
                break;
                // 补发数据确认
            case DataHead.COLLECTION:
                //这里没有确认动作 自动补收
                /*System.out.println("数据补收 通道开启 数据通道id：" + message.getMetaData().get("id")
                        + "\n断点续传进度：" + message.getMetaData().get("index"));
                Send collection = new Send((String) message.getMetaData().get("id"),
                        (String) message.getMetaData().get("url"),this, message.getDataHead().getIndex(), false);
                collection.run();*/
                break;
        }
    }

}
