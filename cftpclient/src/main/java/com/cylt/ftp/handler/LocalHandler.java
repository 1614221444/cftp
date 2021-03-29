package com.cylt.ftp.handler;

import com.cylt.ftp.config.ConfigParser;
import com.cylt.ftp.protocol.CFTPMessage;
import com.cylt.ftp.protocol.DataHead;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;

/**
 * @Description 实际内网服务器channel连接处理器
 * @Author zhangjun
 * @Data 2020/5/26
 * @Time 21:15
 */

public class LocalHandler extends ChannelInboundHandlerAdapter {

    private ClientHandler clientHandler = null;
    private CFTPMessage message = null;
    private ChannelHandlerContext localCtx;

    public LocalHandler(ClientHandler clientHandler, CFTPMessage msg) {
        this.clientHandler = clientHandler;
        this.message = msg;
    }

    public ChannelHandlerContext getLocalCtx() {
        return localCtx;
    }

    //连接建立初始化
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        this.localCtx = ctx;
        System.out.println(this.getClass()+"\r\n 与本地端口建立连接成功："+ctx.channel().remoteAddress());
        System.out.println("开始发送");
        send(ctx, message);
    }

    //读取内网服务器请求和数据
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        CFTPMessage cftpMessage = (CFTPMessage) msg;
        switch (cftpMessage.getType()){
            // 准备接收
            case DataHead.READY_RECEIVE :
                readyReceive(cftpMessage);
                break;
            // 准备完成
            case DataHead.READY_COMPLETE :
                send(ctx,cftpMessage);
                break;
            // 数据
            case DataHead.SEND :
                receive(cftpMessage.getDataHead());
                break;
        }
        System.out.println("收到本地"+ctx.channel().remoteAddress()+"的数据，" +
                "数据量为"+cftpMessage.getDataHead().getData().length+"字节");
    }

    //连接断开
    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        CFTPMessage message = new CFTPMessage();
        message.setType(CFTPMessage.TYPE_DISCONNECTED);
        HashMap<String,Object> metaData = new HashMap<>();
        metaData.put("channelId", ctx.channel().id().asLongText());
        message.setMetaData(metaData);
        this.clientHandler.getCtx().writeAndFlush(message);
        System.out.println(this.getClass()+"\r\n 与本地连接断开："+ctx.channel().remoteAddress());
    }

    //连接异常
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.channel().close();
        System.out.println(this.getClass()+"\r\n 连接中断");
        cause.printStackTrace();
    }


    private void send(ChannelHandlerContext ctx, CFTPMessage message) {

        DataHead dataHead;
        String files = "/Users/wuyh/Desktop/FTP/A/b.text";
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
            int i = 0;
            message.getDataHead().setType(DataHead.SEND);
            while (i < total) {
                i++;
                if(i == total) {
                    buffer = ByteBuffer.allocate((int) fileSize % readMax);
                }
                inChannel.read(buffer);
                buffer.flip();
                dataHead = message.getDataHead();
                dataHead.setIndex(i);
                message.setData(buffer.array());
                System.out.println(i);
                message.setDataHead(dataHead);
                ctx.writeAndFlush(message);
                buffer.clear();
            }
        } catch (IOException e) {
            System.out.println(e.toString());
        }
    }

    /**
     * 接收
     * @param message
     */
    private void receive(DataHead message) {
        System.out.println(message.getIndex());
        long size = 0;
        try (FileOutputStream in = new FileOutputStream(ConfigParser.get("path") + File.separator + message.getId(), true)) {

            FileChannel inChannel = in.getChannel();
            inChannel.write(ByteBuffer.wrap(message.getData()));
            size = inChannel.size();
        } catch (IOException e) {
            System.out.println(e.toString());
        }
    }

    private void readyReceive(CFTPMessage message) {
        File file = new File((String) ConfigParser.get("path"));
        if (!file.exists()) {//如果文件夹不存在
            file.mkdir();//创建文件夹
        }

    }
}
