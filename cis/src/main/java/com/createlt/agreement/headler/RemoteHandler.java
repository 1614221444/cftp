package com.createlt.agreement.headler;

import com.createlt.agreement.codec.CFTPMessage;
import com.createlt.agreement.codec.DataHead;
import com.createlt.cis.entity.CisSendLog;
import com.createlt.cis.service.ICisSendLogService;
import com.createlt.common.BaseController;
import com.createlt.common.ToolSpring;
import com.createlt.common.WebSocketServer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;

/**
 * 数据传输处理器
 *
 */
public class RemoteHandler extends ChannelInboundHandlerAdapter {

    private ServerHandler serverHandler;
    private int remotePort;
    private CFTPMessage message;
    private boolean isSend;
    private int i;

    //数据通道
    private FileChannel inChannel;

    // 单次读最大数据（字节）
    private int readMax = 20480;

    // 百分比进度权值
    private int weight = 0;

    /**
     * log
     */
    private ICisSendLogService cisSendLogService;

    /**
     * 进度
     */
    public double progress = 1;
    private WebSocketServer webSocketServer;
    CisSendLog log = new CisSendLog();

    public RemoteHandler(ServerHandler serverHandler, int remotePort, CFTPMessage message, boolean isSend,WebSocketServer webSocketServer) {
        cisSendLogService = (ICisSendLogService) ToolSpring.getBean("cisSendLogServiceImpl");
        this.webSocketServer = webSocketServer;
        this.serverHandler = serverHandler;
        this.remotePort = remotePort;
        this.message = message;
        this.isSend = isSend;
        this.i = message.getDataHead().getIndex();
        initLog(message);

        File file = new File("/Users/wuyh/Desktop/DATA" + File.separator + this.message.getDataHead().getId());
        try {
            inChannel = new RandomAccessFile(file, "rw").getChannel();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void initLog(CFTPMessage message) {

        if (this.isSend) {
            log.setSendId(serverHandler.getServerId());
            log.setReceiverId(serverHandler.getClientKey());
        } else {
            log.setSendId(serverHandler.getClientKey());
            log.setReceiverId(serverHandler.getServerId());
        }
        log.setFileId(message.getDataHead().getUrl());
        log.setFileSize((double) message.getDataHead().getFileSize() / 1000);
        log.setProgress((int) progress);
        // 记录发送状态
        cisSendLogService.save(log);
    }


    //连接一天内未关闭则直接关闭
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        exceptionCaught(ctx, new Throwable("数据端口连接超时，已强制关闭"));
    }

    //连接初始化，建立连接
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        if (isSend) {
            // 准备发送
            CFTPMessage mes = new CFTPMessage(CFTPMessage.TYPE_DATA);
            DataHead dataHead = new DataHead(message.getMetaData());
            mes.setDataHead(dataHead);
            send(ctx, mes);
        } else {//准备接收
            readyReceive(message);
        }
    }

    //连接中断
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        ctx.channel().close();
        // 如果是异常情况 记录下次连接类型是补发还是补收
        if (this.isSend) {
            this.message.getDataHead().setType(DataHead.COLLECTION);
        } else {
            this.message.getDataHead().setType(DataHead.REISSUE);
        }
        if(inChannel != null){
            inChannel.close();
        }
        // 将已接收的数据序列化到文件
        serverHandler.dataPortClose(this.message.getDataHead().getId());
        System.out.println(this.getClass() + "\r\n 数据连接中断：" + ctx.channel().remoteAddress());
    }

    //读取外部连接数据
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
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
                    // 进度
                    case DataHead.PROGRESS:
                        progress(cftpMessage.getDataHead());
                        break;
                }
            } else {
                System.out.println("数据异常");
            }
        } catch (Exception e) {
            System.out.println("数据处理异常");
            e.printStackTrace();
        }
    }

    //连接异常处理
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //取消正在监听的端口，否则第二次连接时无法再次绑定端口
        ctx.channel().close();
        // 如果是异常情况 记录下次连接类型是补发还是补收
        if (this.isSend) {
            this.message.getDataHead().setType(DataHead.COLLECTION);
        } else {
            this.message.getDataHead().setType(DataHead.REISSUE);
        }
        if(inChannel != null){
            inChannel.close();
        }
        serverHandler.dataPortClose(this.message.getDataHead().getId());
        System.out.println(this.getClass() + "\r\n 连接异常，已中断");
        cause.printStackTrace();
    }


    private void send(ChannelHandlerContext ctx, CFTPMessage mes) {

        DataHead dataHead;
        try (FileInputStream in = new FileInputStream(mes.getDataHead().getUrl())) {
            // 单次读最大数据（字节）
            int readMax = 20480;
            // 创建文件流通道
            FileChannel inChannel = in.getChannel().position(i * readMax);
            // 取文件质量
            long fileSize = inChannel.size();
            // 创建字节接收区
            ByteBuffer buffer = ByteBuffer.allocate(readMax);
            // 读取次数
            long total = (fileSize / readMax) + 1;
            mes.setDataHead(new DataHead());
            mes.getDataHead().setType(DataHead.SEND);
            while (i < total) {
                i++;
                if (i == total) {
                    buffer = ByteBuffer.allocate((int) fileSize % readMax);
                }
                inChannel.read(buffer);
                buffer.flip();
                dataHead = mes.getDataHead();
                dataHead.setIndex(i);
                mes.setData(buffer.array());
                mes.setDataHead(dataHead);
                ctx.writeAndFlush(mes);
                buffer.clear();
                float progress = (float) i / (float) this.message.getDataHead().getTotal() * 100;
                //进度每过百分之一回传一次进度
                if(weight < progress) {
                    weight++;
                    progress(dataHead);
                }
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
    private void receive(ChannelHandlerContext ctx, CFTPMessage message) throws IOException {
        // 按位置插入数据
        MappedByteBuffer buffer = inChannel.map(FileChannel.MapMode.READ_WRITE,
                message.getDataHead().getIndex() * readMax, message.getData().length);
        buffer.put(message.getData());
        //计算进度
        float progress = ((float) message.getDataHead().getIndex() / (float) this.message.getDataHead().getTotal()) * 100;
        //进度每过百分之一回传一次进度
        if (weight < progress) {
            CFTPMessage retn = new CFTPMessage();
            retn.setType(CFTPMessage.TYPE_DATA);
            DataHead dataHead = new DataHead();
            dataHead.setType(DataHead.PROGRESS);
            dataHead.setIndex((int) progress);
            weight++;
            retn.setDataHead(dataHead);
            ctx.writeAndFlush(retn);
            progress(message.getDataHead());
        }
        this.message.getDataHead().setIndex(message.getDataHead().getIndex());
        if (message.getDataHead().getIndex() == this.message.getDataHead().getTotal()) {
            List<CFTPMessage> messages = ServerHandler.clientDataIds.get(serverHandler.clientKey);
            messages.remove(this.message);
            message.getDataHead().setType(DataHead.RECEIVE_END);
            message.getDataHead().setId(this.message.getDataHead().getId());
            serverHandler.getCtx().writeAndFlush(message);
            System.out.println("\n数据传输完成 端口 " + remotePort + "已关闭\n 数据id："
                    + this.message.getDataHead().getId() + "\n文件大小：" + inChannel.size() / 1000000.00 + "M");
            serverHandler.dataPortClose(this.message.getDataHead().getId());

            List<CFTPMessage> me = serverHandler.getClientDataIds().get(serverHandler.clientKey);
            me.remove(this.message);
            if (inChannel != null) {
                inChannel.close();
            }
        }
    }

    /**
     * 回执进度
     *
     * @param message 进度参数
     */
    public void progress(DataHead message) {
        this.message.getDataHead().setIndex(message.getIndex());
        progress += 0.5;
        log.setProgress((int) progress);
        // System.out.print("|");
        // 推送消息到发起人
        BaseController base = new BaseController();
        webSocketServer.sendInfo(base.getJson(log));

    }

    /**
     * 回执进度 固定进度
     *
     * @param progress 进度参数
     */
    public void progress(int progress) {
        log.setProgress(progress);
        // 推送消息到发起人
        BaseController base = new BaseController();
        webSocketServer.sendInfo(base.getJson(log));

    }

    private void readyReceive(CFTPMessage message) {
        File file = new File("/Users/wuyh/Desktop/DATA");
        if (!file.exists()) {//如果文件夹不存在
            file.mkdir();//创建文件夹
        }
    }
}
