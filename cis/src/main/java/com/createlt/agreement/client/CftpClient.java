package com.createlt.agreement.client;

import com.createlt.agreement.base.BaseClient;
import com.createlt.agreement.base.Client;
import com.createlt.agreement.codec.CFTPDecoder;
import com.createlt.agreement.codec.CFTPEncoder;
import com.createlt.agreement.codec.CFTPMessage;
import com.createlt.agreement.codec.DataHead;
import com.createlt.agreement.headler.ClientHandler;
import com.createlt.agreement.headler.HeartBeatClientHandler;
import com.createlt.agreement.headler.LocalHandler;
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
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class CftpClient implements BaseClient {


    // 能处理的最大长度
    public static final int MAX_FRAME_LENGTH = Integer.MAX_VALUE;
    public static final int LENGTH_FIELD_OFFSET = 0;
    public static final int LENGTH_FIELD_LENGTH = 4;
    public static final int LENGTH_ADJUSTMENT = 0;
    public static final int INITIAL_BYTES_TO_STRIP = 4;

    // 用户事件隔多少秒
    private static final int READER_IDLE_TIME = 20;
    private static final int WRITER_IDLE_TIME = 0;
    private static final int ALL_IDLE_TIME = 0;

    public Client client = new Client();

    public ClientHandler clientHandler;
    private EventLoopGroup workerGroup = new NioEventLoopGroup();
    @Override
    public void start(String ip, int port,String clientId) throws Exception {
        // 获取用户ID主动推送请求结果
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        SysUser user = new SysUser();
        if(auth != null){
            user = (SysUser) auth.getPrincipal();
        } else {
            user.setId("start");
        }
        clientHandler = new ClientHandler(clientId, user.getId());
        ChannelInitializer<SocketChannel> channelInitializer = new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                socketChannel.pipeline().addLast(
                        //设置读超时为40秒，配合心跳机制处理器使用
                        new IdleStateHandler(READER_IDLE_TIME, WRITER_IDLE_TIME, ALL_IDLE_TIME, TimeUnit.SECONDS),
                        //固定帧长解码器
                        new LengthFieldBasedFrameDecoder(MAX_FRAME_LENGTH, LENGTH_FIELD_OFFSET, LENGTH_FIELD_LENGTH, LENGTH_ADJUSTMENT, INITIAL_BYTES_TO_STRIP),
                        //自定义协议解码器
                        new CFTPDecoder(),
                        //自定义协议编码器
                        new CFTPEncoder(),
                        clientHandler,
                        //客户端心跳机制处理器
                        new HeartBeatClientHandler(workerGroup,clientId)
                );
            }
        };
        client.start(workerGroup, channelInitializer, ip, port);

    }

    @Override
    public void stop() {
        workerGroup.shutdownGracefully();
        client.close();
    }

    @Override
    public void send(String sendId, String file) {
        CFTPMessage HEART_BEAT;
        // String files = ConfigParser.getServerConfigFile() + "A.text";
        try (FileInputStream in = new FileInputStream(file)) {
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
            metaData.put("clientKey", clientHandler.getLoginId());
            metaData.put("fileName", "b.text");
            metaData.put("fileSize", fileSize);
            metaData.put("total", total);
            metaData.put("url", file);
            metaData.put("id", sendId);
            metaData.put("startDate", start);
            DataHead data = new DataHead();
            data.setId(sendId);
            data.setIndex(0);
            data.setSend(true);
            data.setFileName("b.text");
            data.setFileSize((int) fileSize);
            data.setTotal((int) total);
            data.setUrl(file);
            HEART_BEAT.setDataHead(data);
            HEART_BEAT.setMetaData(metaData);

            LocalHandler localHandler = new LocalHandler(clientHandler,HEART_BEAT, true, 0);
            clientHandler.localHandlerMap.put(sendId, localHandler);

            client.channel.writeAndFlush(HEART_BEAT);
        } catch (IOException e) {
            System.out.println(e.toString());
        }
    }
}
