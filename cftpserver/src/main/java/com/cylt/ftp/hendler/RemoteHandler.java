package com.cylt.ftp.hendler;

import com.cylt.ftp.config.ConfigParser;
import com.cylt.ftp.protocol.CFTPMessage;
import com.cylt.ftp.protocol.DataHead;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.List;

/**
 * 数据传输处理器
 * TODO 1.当客户端连接时主动给客户端发送文件  2.做心跳处理器
 */
public class RemoteHandler extends ChannelInboundHandlerAdapter {

    private ServerHandler serverHandler;
    private int remotePort;
    private CFTPMessage message;
    private boolean isSend;
    private int i = 0;
    //数据传输内存缓冲区
    private ByteBuffer[] byteBuffers;

    private boolean isWrite = true;

    // 单次读最大数据（字节）
    private int readMax = 20480;

    public RemoteHandler(ServerHandler serverHandler, int remotePort, CFTPMessage message, boolean isSend, int i) {
        this.serverHandler = serverHandler;
        this.remotePort = remotePort;
        this.message = message;
        this.isSend = isSend;
        this.i = i;
        byteBuffers = new ByteBuffer[message.getDataHead().getTotal()];
    }

    //连接初始化，建立连接
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        if (isSend) {
            // 准备发送
            send(ctx, message);
        } else {//准备接收
            readyReceive(message);
        }
    }
    //连接中断
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        ctx.channel().close();
        if(this.isWrite){
            write();
        }
        // 将已接收的数据序列化到文件
        serverHandler.dataPortClose(this.message.getDataHead().getId());
        System.out.println(this.getClass() + "\r\n 客户端连接中断：" + ctx.channel().remoteAddress());
    }

    //读取外部连接数据
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {

        CFTPMessage cftpMessage = (CFTPMessage) msg;
        if (cftpMessage.getDataHead() != null) {
            switch (cftpMessage.getDataHead().getType()) {
                // 准备接收
                case DataHead.READY_RECEIVE:
                    readyReceive(cftpMessage);
                    break;
                // 准备完成
                case DataHead.READY_COMPLETE:
                    send(ctx, cftpMessage);
                    break;
                // 数据
                case DataHead.SEND:
                    receive(ctx, cftpMessage);
                    break;
            }
        } else {
            System.out.println("数据异常");
        }
    }

    //连接异常处理
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //取消正在监听的端口，否则第二次连接时无法再次绑定端口
        ctx.channel().close();
        // 将已接收的数据序列化到文件
//        if(this.isWrite){
//            write();
//        }
        serverHandler.dataPortClose(this.message.getDataHead().getId());
        System.out.println(this.getClass() + "\r\n 连接异常，已中断");
        cause.printStackTrace();
    }

    //服务器读超时事件发生时会默认调用该方法
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

    }


    /**
     * @return void
     * @Description 发送数据到内网客户端流程封装
     * @Param [type, channelId]
     **/
    public void send(int type, String channelId, byte[] data) {
        if (serverHandler == null) {
            System.out.println("客户端channel不存在");
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
        try (FileInputStream in = new FileInputStream((String) this.message.getMetaData().get("url"))) {
            // 创建文件流通道
            FileChannel inChannel = in.getChannel().position(i * readMax);
            // 取文件质量
            long fileSize = inChannel.size();
            // 创建字节接收区
            ByteBuffer buffer = ByteBuffer.allocate(readMax);
            // 读取次数
            long total = (fileSize / readMax) + 1;
            message.setDataHead(new DataHead());
            message.getDataHead().setType(DataHead.SEND);
            while (i < total) {
//                try {
//                    Robot r = new Robot();
//                    r.delay(1000);
//                }catch (AWTException s){
//
//                }

                i++;
                if (i == total) {
                    buffer = ByteBuffer.allocate((int) fileSize % readMax);
                }
                System.out.println(i);
                inChannel.read(buffer);
                buffer.flip();
                dataHead = message.getDataHead();
                dataHead.setIndex(i);
                message.setData(buffer.array());
                message.setDataHead(dataHead);
                ctx.writeAndFlush(message);
                buffer.clear();
            }
        } catch (IOException e) {
            System.out.println(e.toString());
        }
        System.out.println("发送完成");
    }

    /**
     * 接收
     *
     * @param message
     */
    private void receive(ChannelHandlerContext ctx, CFTPMessage message) {
        this.byteBuffers[message.getDataHead().getIndex() - 1] = ByteBuffer.wrap(message.getData());
        System.out.println(message.getDataHead().getIndex());
        this.message.getDataHead().setIndex(message.getDataHead().getIndex());
        if (message.getDataHead().getIndex() == this.message.getDataHead().getTotal()) {
            long size;
            size = write();
            List<CFTPMessage> messages = ServerHandler.clientDataIds.get(serverHandler.clientKey);
            messages.remove(this.message);
            System.out.println("数据传输完成 端口 " + remotePort + "已关闭\n 数据id："
                    + this.message.getDataHead().getId() + "\n文件大小：" + size / 1000000.00 + "M");
            message.getDataHead().setType(DataHead.RECEIVE_END);
            message.getDataHead().setId(this.message.getDataHead().getId());
            serverHandler.getCtx().writeAndFlush(message);
            ctx.channel().close();
            serverHandler.dataPortClose(this.message.getDataHead().getId());
        }
    }

    /**
     * 将缓冲区文件实例化到文件
     */
    private long write() {
        if(!this.isWrite){
            return 0;
        }
        this.isWrite = false;
        File file = new File(ConfigParser.get("path") + File.separator + this.message.getDataHead().getId());
        try (FileChannel inChannel = new RandomAccessFile(file, "rw").getChannel()) {
            MappedByteBuffer buffer;
            for (int i = 0; i< this.byteBuffers.length;i++){
                if (this.byteBuffers[i] != null) {
                    buffer = inChannel.map(FileChannel.MapMode.READ_WRITE, i * readMax, this.byteBuffers[i].array().length);
                    buffer.put(this.byteBuffers[i]);
                }
            }
            return inChannel.size();
        } catch (IOException e) {
            System.out.println(e.toString());
        }
        return -1;
    }

    private void readyReceive(CFTPMessage message) {
        File file = new File((String) ConfigParser.get("path"));
        if (!file.exists()) {//如果文件夹不存在
            file.mkdir();//创建文件夹
        }
    }
}
