package com.cylt.ftp;

import com.cylt.ftp.client.Client;
import com.cylt.ftp.codec.CFTPDecoder;
import com.cylt.ftp.codec.CFTPEncoder;
import com.cylt.ftp.config.ConfigParser;
import com.cylt.ftp.handler.ClientHandler;
import com.cylt.ftp.handler.HeartBeatHandler;
import com.cylt.ftp.test.Send;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 启动入口
 */
public class App {

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

    public static Client ser = new Client();

    private static final EventLoopGroup workerGroup = new NioEventLoopGroup();

    public static void main(String[] args) {
        String serverHost = (String) ConfigParser.get("server-host");
        int serverPort = (Integer) ConfigParser.get("server-port");
        ClientHandler client = new ClientHandler();
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
                        client,
                        //客户端心跳机制处理器
                        new HeartBeatHandler(workerGroup)
                );
            }
        };
        ser.start(workerGroup, channelInitializer, serverHost, serverPort);

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Send send = new Send("/Users/wuyh/Desktop/FTP/A/b.text", client, true);
                send.run();
            }
        }, 5000);
    }
}
