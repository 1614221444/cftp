package com.cylt.ftp.hendler;

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

public class RemoteHandler extends ChannelInboundHandlerAdapter {

    private ServerHandler serverHandler = null;
    private int remotePort;
    private CFTPMessage message;

    /**
     * @return void
     * @Description 不采用构造器赋值，因为SpringBoot会对构造器进行自动注入
     * @Date 14:16 2020/4/16
     * @Param [serverHandler, remotePort, clientKey]
     **/
    public RemoteHandler(ServerHandler serverHandler, int remotePort, CFTPMessage message) {
        this.serverHandler = serverHandler;
        this.remotePort = remotePort;
        this.message = message;
    }

    //连接初始化，建立连接
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        readyReceive(message);
        System.out.println(remotePort + "端口有请求进入，channelId为：" + ctx.channel().id().asLongText());
    }

    //读取外部连接数据
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {

        CFTPMessage cftpMessage = (CFTPMessage) msg;
        if(cftpMessage.getDataHead() != null){
            switch (cftpMessage.getDataHead().getType()){
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
                    receive(cftpMessage);
                    break;
            }
        } else {
            System.out.println("数据异常");
        }
    }

    //连接中断
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        send(CFTPMessage.TYPE_DISCONNECTED, ctx.channel().id().asLongText(), null);
        System.out.println(this.getClass() + "\r\n" + remotePort + "端口有请求离开，channelId为：" + ctx.channel().id().asLongText());
    }

    /**
     * @return void
     * @Description 发送数据到内网客户端流程封装
     * @Param [type, channelId]
     **/
    public void send(int type, String channelId, byte[] data) {
        if (serverHandler == null) {
            System.out.println(this.getClass() + "\r\n 客户端channel不存在");
            return;
        }
        CFTPMessage message = new CFTPMessage();
        message.setType(type);
        HashMap<String, Object> metaData = new HashMap<>();
        //每个请求都是一个channel，每个channel有唯一id，
        // 将该id发送至客户端，客户端返回响应时携带此id便可知道响应需要返回给哪个请求
        metaData.put("channelId", channelId);
        metaData.put("remotePort", remotePort);
        message.setMetaData(metaData);
        if (data != null) {
            message.setData(data);
        }
        this.serverHandler.getCtx().writeAndFlush(message);
        //记录日志，只记录数据传输请求
//        if (serverHandler!=null && CFTPMessage.TYPE_DATA==type){  //防止外部请求断开造成remoteHandler空指针异常
//            serverHandler.logService.addLog(clientKey,((InetSocketAddress)serverHandler.getCtx().channel().localAddress()).getPort(),(double) message.getData().length/1024);
//        }
    }

    private void send(ChannelHandlerContext ctx, CFTPMessage message) {

        DataHead dataHead;
        String files = "/Users/wuyh/Desktop/FTP/A/b.text";
        try (FileInputStream in = new FileInputStream(message.getDataHead().getUrl())) {
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
            while (i < total) {
                i++;
                buffer.clear();
                if(i == total) {
                    buffer = ByteBuffer.allocate((int) fileSize % readMax);
                }
                buffer.flip();
                dataHead = message.getDataHead();
                inChannel.read(buffer);
                dataHead.setIndex(i);
                dataHead.setData(buffer.array());
                message.setDataHead(dataHead);
                ctx.writeAndFlush(message);
            }
        } catch (IOException e) {
            System.out.println(e.toString());
        }
    }

    /**
     * 接收
     * @param message
     */
    private void receive(CFTPMessage message) {
        System.out.println(message.getDataHead().getIndex());
        long size = 0;
        try (FileOutputStream in = new FileOutputStream(ConfigParser.get("path") + File.separator + message.getDataHead().getId(), true)) {

            FileChannel inChannel = in.getChannel();
            inChannel.write(ByteBuffer.wrap(message.getData()));
            System.out.println(message.getDataHead().getId());
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
