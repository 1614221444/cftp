package com.createlt.agreement.headler;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.createlt.agreement.base.Server;
import com.createlt.agreement.codec.CFTPDecoder;
import com.createlt.agreement.codec.CFTPEncoder;
import com.createlt.agreement.codec.CFTPMessage;
import com.createlt.agreement.codec.DataHead;
import com.createlt.cis.entity.CisAuthentication;
import com.createlt.cis.service.ICisAuthenticationService;
import com.createlt.common.ToolSpring;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.ChannelMatcher;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.createlt.agreement.server.CftpServer.*;

/**
 * 处理服务器接收到的客户端连接
 */
public class ServerHandler extends ChannelInboundHandlerAdapter {

    //在所有ServerHandler中共享当前在线的授权信息
    private Map<String, Integer> clients;

    //统一管理客户端channel和remote channel
    private static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    //记录客户端与数据端口关系(发送中的数据)
    public static Map<String, List<CFTPMessage>> clientDataIds = new HashMap<>();

    //bossGroup指的是所有指定端口的监听处理线程
    //workerGroup指的是所有端口所收到连接的处理线程
    private Map<String, EventLoopGroup> bossGroup = new HashMap<>();
    private Map<String, EventLoopGroup> workerGroup = new HashMap<>();
    private Server remoteHelper = new Server();

    //客户端标识clientKey
    public String clientKey;
    //代理客户端的ChannelHandlerContext
    private ChannelHandlerContext ctx;
    //判断代理客户端是否已注册授权
    private boolean isRegister = false;

    public ChannelHandlerContext getCtx() {
        return ctx;
    }
    public Map<String, List<CFTPMessage>> getClientDataIds() {
        return clientDataIds;
    }

    public ChannelGroup getChannels() {
        return channels;
    }
    public Map<String, Integer> getClients() {
        return clients;
    }

    private String serverId = "";

    private String userId = "";

    /**
     * 服务认证service
     */
    private ICisAuthenticationService cisAuthenticationService;

    public ServerHandler(String serverId,Map<String, Integer> clients, String userId) {
        // 手动注入Spring认证类
        cisAuthenticationService = (ICisAuthenticationService) ToolSpring.getBean("cisAuthenticationServiceImpl");
        this.serverId = serverId;
        this.clients = clients;
        this.userId = userId;
    }

    /**
     * 通道准备就绪
     * @param ctx
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
        System.out.println("有客户端建立连接，客户端地址为：" + ctx.channel().remoteAddress());
    }

    /**
     * 接收数据
     * @param ctx
     * @param msg 发送内容
     * @throws Exception
     */
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
                    processData(ctx,message, !message.getDataHead().isSend());
                    break;
                default:
                    System.out.printf("非法请求");
            }
        } else {
            System.out.println(this.getClass() + "\r\n 有未授权的客户端尝试发送消息，断开连接");
            ctx.close();
        }
    }

    /**
     * TCP连接通道中断
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        channels.remove(ctx.channel());
        //移除断开客户端的授权码
        clients.remove(clientKey);
        //取消正在监听的端口，否则第二次连接时无法再次绑定端口
        ctx.channel().close();
        if (clientDataIds.get(clientKey)!= null) {
            clientDataIds.get(clientKey).forEach(
                    (message) ->  dataPortClose(message.getDataHead().getId())
            );
        }
        System.out.println(this.getClass() + "\r\n 客户端连接中断：" + ctx.channel().remoteAddress());
    }

    //连接异常处理
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //取消正在监听的端口，否则第二次连接时无法再次绑定端口
        ctx.channel().close();
        if (clientDataIds.get(clientKey)!= null) {
            clientDataIds.get(clientKey).forEach(
                    (message) -> dataPortClose(message.getDataHead().getId())

            );
        }
        System.out.println(this.getClass() + "\r\n 连接异常，已中断");
        cause.printStackTrace();
    }

    /**
     * 验证权限
     * @param clientKey 登录人
     * @return 是否验证通过
     */
    public synchronized boolean isLegal(String clientKey) {
        //授权模块
        boolean flag = false;
        List<CisAuthentication> authList = cisAuthenticationService.list(new LambdaQueryWrapper<CisAuthentication>()
                .eq(CisAuthentication::getControllerId, serverId));
        // 验证权限
        for(CisAuthentication auth : authList) {
            if(auth.getLoginName().equals(clientKey)) {
                flag = true;
                break;
            }
        }
        if (flag) {
            //一个client-key只允许一个代理客户端使用
            if (isExist(clientKey)) {
                // System.out.println("不允许同一授权码重复登录\n");
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
            try {
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

            if (isExist(clientKey)) {
                metaData.put("reason", "不允许同一授权码重复登录");
            } else {
                metaData.put("reason", "client-key不合法，请两分钟后重试");
            }
            System.out.println(this.getClass() + "\r\n 客户端注册失败，使用了不合法的clientKey，clientKey为：" + clientKey);
        }

        CFTPMessage res = new CFTPMessage();
        res.setType(CFTPMessage.TYPE_AUTH);
        res.setMetaData(metaData);
        ctx.writeAndFlush(res);

        if ((boolean) metaData.get("isSuccess")){
            checkDataWreckage();
        }
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
    public synchronized void processData(ChannelHandlerContext ctx,CFTPMessage message,boolean isSend) {
        if (!isExist(message.getMetaData().get("clientKey").toString())
        ) {
            return;
        }
        if (DataHead.RECEIVE_END == message.getDataHead().getType()){
            String key = message.getDataHead().getId();
            System.out.println("\n数据传输完成 通道已关闭 数据通道id：" + key);
            dataPortClose(key);

            List<CFTPMessage> messages = clientDataIds.get(clientKey);
            CFTPMessage remove = new CFTPMessage();
            for (CFTPMessage mes : messages) {
                if (message.getDataHead().getId().equals(mes.getDataHead().getId())) {
                    remove = mes;
                    break;
                }
            }
            messages.remove(remove);
            ServerHandler server = this;
            /*Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Send send = new Send(server,"/Users/wuyh/Desktop/FTP/A/a.text");
                    send.run();
                }
            }, 5000);*/
            return;
        }
        // 创建文件传输端口
        ServerHandler serverHandler = this;
        //初始化线程组
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();

        int port = getPort();
        ChannelInitializer channelInitializer = new ChannelInitializer() {
            @Override
            protected void initChannel(Channel channel) {
                RemoteHandler remoteHandler = new RemoteHandler(serverHandler, port, message, isSend);
                channel.pipeline().addLast(
                        //设置按天触发事件(如果连接一天未关闭则强制关闭)
                        new IdleStateHandler(1,0,0, TimeUnit.DAYS),
                        //固定帧长解码器
                        new LengthFieldBasedFrameDecoder(MAX_FRAME_LENGTH, LENGTH_FIELD_OFFSET, LENGTH_FIELD_LENGTH, LENGTH_ADJUSTMENT, INITIAL_BYTES_TO_STRIP),
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
        remoteHelper.start(boss, worker, "localhost", port, channelInitializer);
        bossGroup.put(message.getDataHead().getId(),boss);
        workerGroup.put(message.getDataHead().getId(),worker);

        //记录客户端与数据端口的关系
        List<CFTPMessage> messages = clientDataIds.get(clientKey);
        if(messages == null){
            messages = new ArrayList<>();
        }
        messages.add(message);
        clientDataIds.put(clientKey, messages);
        Map<String, Object> map = message.getMetaData();
        message.getDataHead().setType(DataHead.READY_COMPLETE);
        map.put("remotePort", port);
        message.getDataHead().toMap(map);
        message.setMetaData(map);
        ctx.writeAndFlush(message);
        System.out.println("数据端口 " + port + " 创建成功 ，等待连接");
    }

    /**
     * 关闭数据通道
     * @param id 通道id
     */
    public  void dataPortClose(String id){
        //取消正在监听的端口，否则第二次连接时无法再次绑定端口
        if (bossGroup.get(id) != null) {
            bossGroup.get(id).shutdownGracefully();
            workerGroup.get(id).shutdownGracefully();
            bossGroup.remove(id);
            workerGroup.remove(id);
        }
    }

    /**
     * 检查未传输结束的数据
     */
    private void checkDataWreckage(){
        List<CFTPMessage> messageList = clientDataIds.get(this.clientKey);
        if (messageList != null && messageList.size() != 0){
            for (CFTPMessage message : messageList){
                ctx.writeAndFlush(message);
            }
            messageList.clear();
        }
    }


    private synchronized int getPort() {
        Socket Skt;
        String host = "localhost";
        int i  = 2000;
        int max  = 3000;
        for (; i < max; i++) {
            try {
                Skt = new Socket(host, i);
            }
            catch (UnknownHostException e) {
                System.out.println("Exception occured"+ e);
                break;
            }
            catch (IOException e) {
                return i;
            }
        }
        try {
            Thread.sleep(1000);
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
        return getPort();
    }
}
