package com.createlt.agreement.server;

import com.createlt.agreement.base.BaseServer;
import com.createlt.agreement.base.Server;
import com.createlt.agreement.codec.CFTPDecoder;
import com.createlt.agreement.codec.CFTPEncoder;
import com.createlt.agreement.codec.CFTPMessage;
import com.createlt.agreement.codec.DataHead;
import com.createlt.agreement.headler.HeartBeatHandler;
import com.createlt.agreement.headler.ServerHandler;
import com.createlt.sys.entity.SysUser;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * FTP实现类
 */
public class CftpServer implements BaseServer {


    // 能处理的最大长度
    public static final int MAX_FRAME_LENGTH = Integer.MAX_VALUE;
    public static final int LENGTH_FIELD_OFFSET = 0;
    public static final int LENGTH_FIELD_LENGTH = 4;
    public static final int LENGTH_ADJUSTMENT = 0;
    public static final int INITIAL_BYTES_TO_STRIP = 4;

    // 用户事件隔多少秒
    private static final int READER_IDLE_TIME = 30;
    private static final int WRITER_IDLE_TIME = 0;
    private static final int ALL_IDLE_TIME = 0;
    private ServerHandler serverHandler;
    Server ser = new Server();


    /**
     * 所有在线用户
     * @Key 用户名
     * @value 连接处理器实例
     */
    private Map<String, ServerHandler> clients = new HashMap<>();
    EventLoopGroup bossGroup = new NioEventLoopGroup();
    EventLoopGroup workerGroup  = new NioEventLoopGroup();
    /**
     * 启动服务
     * @param port 监听端口
     * @param serverId 服务实例ID
     */
    @Override
    public void start(int port,String serverId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        SysUser user = new SysUser();
        if(auth != null){
            user = (SysUser) auth.getPrincipal();
        } else {
            user.setId("start");
        }
        String userId = user.getId();
        ChannelInitializer<SocketChannel> channelInitializer = new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                socketChannel.pipeline().addLast(
                        //设置读超时为40秒，配合心跳机制处理器使用
                        new IdleStateHandler(READER_IDLE_TIME,WRITER_IDLE_TIME,ALL_IDLE_TIME, TimeUnit.SECONDS),
                        //固定帧长解码器
                        new LengthFieldBasedFrameDecoder(MAX_FRAME_LENGTH,LENGTH_FIELD_OFFSET,LENGTH_FIELD_LENGTH,LENGTH_ADJUSTMENT,INITIAL_BYTES_TO_STRIP),
                        //自定义协议解码器
                        new CFTPDecoder(),
                        //自定义协议编码器
                        new CFTPEncoder(),
                        //代理客户端连接代理服务器处理器
                        serverHandler = new ServerHandler(serverId, clients, userId),
                        //服务器心跳机制处理器
                        new HeartBeatHandler()
                );
            }
        };
        ser.start(bossGroup,workerGroup,"localhost",port,channelInitializer);
    }

    /**
     * 关闭服务
     */
    @Override
    public void stop() {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
        ser.close();
    }

    /**
     * 发送信息
     * @param user 发给谁
     * @param files 发啥玩意
     */
    @Override
    public void send(String user, String files) {
        ServerHandler to =  clients.get(user);
        Thread send = new Thread(() -> {
            CFTPMessage HEART_BEAT;
            // String files = ConfigParser.getServerConfigFile() + "A.text";
            try (FileInputStream in = new FileInputStream(files)) {
                // 创建文件流通道
                FileChannel inChannel = in.getChannel();
                // 单次读最大数据（字节）
                int readMax = 20480;
                // 取文件质量
                long fileSize = inChannel.size();
                // 创建字节接收区
                ByteBuffer buffer = ByteBuffer.allocate(readMax);
                // 读取次数
                long total  = (fileSize / readMax) + 1;
                // 初始化请求头
                HashMap<String, Object> metaData;
                Date start = new Date();
                HEART_BEAT = new CFTPMessage(CFTPMessage.TYPE_DATA);
                metaData = new HashMap<>();
                metaData.put("fileName", "b.text");
                metaData.put("url", files);
                metaData.put("startDate", start);
                metaData.put("clientKey", to.clientKey);
                HEART_BEAT.setMetaData(metaData);
                DataHead data = new DataHead();
                data.setId(UUID.randomUUID().toString());
                data.setIndex(0);
                data.setFileName("b.text");
                data.setFileSize((int) fileSize);
                data.setTotal((int) total);
                data.setUrl(files);
                HEART_BEAT.setDataHead(data);
                to.processData(to.getCtx(),HEART_BEAT,true);

            } catch (FileNotFoundException e) {
                System.out.println(e.toString());
            } catch (IOException e) {
                System.out.println(e.toString());
            }
        });
        send.start();
    }

    /**
     * 获取在线用户
     */
    @Override
    public List<String> getUserList() {
        List<String> userList = new ArrayList<>();
        clients.forEach((key, value) -> {
            userList.add(key);
        });
        return userList;
    }
}
