package com.createlt.agreement.headler;

import com.createlt.agreement.codec.CFTPMessage;
import com.createlt.agreement.codec.DataHead;
import com.createlt.business.model.BaseModel;
import com.createlt.cis.entity.CisController;
import com.createlt.cis.entity.CisModel;
import com.createlt.cis.entity.CisSendLog;
import com.createlt.cis.service.ICisControllerService;
import com.createlt.cis.service.ICisModelService;
import com.createlt.cis.service.ICisSendLogService;
import com.createlt.common.BaseController;
import com.createlt.common.ToolSpring;
import com.createlt.mapping.utils.RunJavaUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;

/**
 * 客户端数据处理器
 */
public class LocalHandler extends ChannelInboundHandlerAdapter {

    private CFTPMessage message = null;
    private ChannelHandlerContext localCtx;

    private boolean isSend;
    private int i = 0;
    private ClientHandler client;
    // 单次读最大数据（字节）
    private int readMax = 20480;
    private int weight = 1;
    //数据通道
    private FileChannel inChannel;
    /**
     * log
     */
    private ICisSendLogService cisSendLogService;

    private ICisControllerService cisControllerService;

    private ICisModelService cisModelService;

    /**
     * 映射器实体
     */
    private CisModel model;

    private double progress;
    CisSendLog log = new CisSendLog();

    public LocalHandler(ClientHandler client,CFTPMessage msg, boolean isSend) {
        this(client,msg,isSend,0);
    }


    private void initLog(CFTPMessage message) {
        log.setFileId(message.getDataHead().getUrl());
        if (this.isSend) {
            log.setSendId(client.clientId);
            log.setReceiverId(client.getController().getIp() + ":" + client.getController().getPort());
        } else {
            log.setSendId(client.getController().getIp() + ":" + client.getController().getPort());
            log.setReceiverId(client.clientId);
        }
        log.setFileSize((double) message.getDataHead().getFileSize() / 1000);
        log.setProgress(0);
        // 记录发送状态
        cisSendLogService.save(log);
    }

    public LocalHandler(ClientHandler client,CFTPMessage msg, boolean isSend, int i) {
        cisSendLogService = (ICisSendLogService) ToolSpring.getBean("cisSendLogServiceImpl");
        cisControllerService = (ICisControllerService) ToolSpring.getBean("cisControllerServiceImpl");
        CisController controller = cisControllerService.getById(client.clientId);
        cisModelService = (ICisModelService) ToolSpring.getBean("cisModelServiceImpl");
        this.model = cisModelService.getById(controller.getModelId());
        this.message = msg;
        this.isSend = isSend;
        File file = new File("/Users/wuyh/Desktop/DATA" + File.separator + this.message.getDataHead().getId());
        try {
            inChannel = new RandomAccessFile(file, "rw").getChannel();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        this.i = i;
        this.client = client;
        initLog(msg);
    }

    public ChannelHandlerContext getLocalCtx() {
        return localCtx;
    }

    //连接建立初始化
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        this.localCtx = ctx;
        if (isSend) {
            // 准备发送
            send(ctx, message);
        } else {//准备接收
            readyReceive(message);
        }
    }

    //读取内网服务器请求和数据
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            CFTPMessage cftpMessage = (CFTPMessage) msg;
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
                    cftpMessage.getDataHead().setData(cftpMessage.getData());
                    receive(cftpMessage.getDataHead());
                    break;
                // 进度
                case DataHead.PROGRESS:
                    progress(cftpMessage.getDataHead());
                    break;
            }
        } catch (Exception e) {
            System.out.println("数据处理异常");
            e.printStackTrace();
        }
    }

    //连接断开
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        CFTPMessage message = new CFTPMessage();
        message.setType(CFTPMessage.TYPE_DISCONNECTED);
        HashMap<String, Object> metaData = new HashMap<>();
        metaData.put("channelId", ctx.channel().id().asLongText());
        message.setMetaData(metaData);
        //TODO 这里原来是客户端的通信结构
        localCtx.channel().writeAndFlush(message);
        if (inChannel != null) {
            inChannel.close();
        }
        System.out.println(this.getClass() + "\r\n 与本地连接断开：" + ctx.channel().remoteAddress());
    }

    //连接异常
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.channel().close();
        System.out.println(this.getClass() + "\r\n 连接中断");
        cause.printStackTrace();
        if (inChannel != null) {
            inChannel.close();
        }
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
                inChannel.read(buffer);
                buffer.flip();
                dataHead = message.getDataHead();
                dataHead.setIndex(i);
                message.setData(buffer.array());
                message.setDataHead(dataHead);
                ctx.writeAndFlush(message);
                buffer.clear();

                //进度每过百分之一回传一次进度
                if (weight < (float) i / (float) total * 100) {
                    progress(message.getDataHead());
                    weight++;
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
    private void receive(DataHead message) throws IOException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        // 按位置插入数据
        MappedByteBuffer buffer = inChannel.map(FileChannel.MapMode.READ_WRITE,
                (long) message.getIndex() * readMax, message.getData().length);
        buffer.put(message.getData());

        //计算进度
        float progress = (float) message.getIndex() / (float) this.message.getDataHead().getTotal() * 100;
        //进度每过百分之一回传一次进度
        if(weight < progress) {
            CFTPMessage retn = new CFTPMessage();
            retn.setType(CFTPMessage.TYPE_DATA);
            DataHead dataHead = new DataHead();
            dataHead.setType(DataHead.PROGRESS);
            dataHead.setIndex(message.getIndex());
            dataHead.setIndex(message.getIndex());
            retn.setDataHead(dataHead);
            localCtx.writeAndFlush(retn);
            weight++;
        }
        if (weight == 30) {
           //System.exit(1);
        }
        // System.out.println(message.getDataHead().getIndex());
        this.message.getDataHead().setIndex(message.getIndex());
        if (message.getIndex() == this.message.getDataHead().getTotal()) {
            CFTPMessage retn = new CFTPMessage();
            retn.setMetaData(this.message.getMetaData());
            retn.setType(CFTPMessage.TYPE_DATA);
            message.setType(DataHead.RECEIVE_END);
            message.setId(this.message.getDataHead().getId());
            retn.setDataHead(message);
            client.getCtx().writeAndFlush(retn);
            System.out.println("\n数据传输完成 \n 数据id："
                    + this.message.getDataHead().getId() + "\n文件大小：" + inChannel.size() / 1000000.00 + "M");
            localCtx.channel().close();
            client.dataPortClose(this.message.getDataHead().getId());
            // 2、创建buffer
            ByteBuffer byteBuffer = ByteBuffer.allocate(2048);
            // 2、通过channel读文件到buffer中
            int byteNum = inChannel.read(byteBuffer);
            // -1 表示读取到最后一行了
            while (byteNum!=-1){
                // buffer中是否有内容
                while (byteBuffer.hasRemaining()){
                    // 读写切换（内部游标操作）
                    byteBuffer.flip();
                    byteBuffer.get();
                }
                // 清空缓冲区
                byteBuffer.clear();
                // 清空缓冲区后继续读文件
                byteNum = inChannel.read(byteBuffer);
            }
            if (inChannel != null) {
                inChannel.close();
            }
            RunJavaUtils javaUtils = new RunJavaUtils();
            //获取动态classloader脚本
            Class<BaseModel> iserviceClass = (Class<BaseModel>) javaUtils.getScript(this.model.getId());
            //反射创建动态类
            Object modelImpl = iserviceClass.newInstance();
            // 记录解析开始
            log.setProgress(1);
            cisSendLogService.updateById(log);

            Method method = iserviceClass.getMethod("push", String.class, byte[].class);
            Object data = method.invoke(modelImpl, log.getId(), byteBuffer.array());
            System.out.println(data);

            // 记录解析结束
            log.setProgress(2);
            cisSendLogService.updateById(log);
            // TODO 映射层代码
            // modelImpl.push(log.getId(), byteBuffer.array());
            /*BaseModel ss = BaseModelImpl.class.newInstance();
            // 视图层入口
            //实现了接口的业务类
            BaseView viewImpl = new BaseViewImpl();
            // 创建代理类对象
            BaseView view = (BaseView) Proxy.newProxyInstance(viewImpl.getClass().getClassLoader(),
                    viewImpl.getClass().getInterfaces(), new ViewProxy(viewImpl));
            view.setData(log.getId(), data);*/
        }
    }

    /**
     * 回执进度
     * @param message 进度参数
     */
    private void progress(DataHead message) {
        this.message.getDataHead().setIndex(message.getIndex());
        progress += 0.5;
        log.setProgress((int) progress);
        // 推送消息到发起人
        BaseController base = new BaseController();
        client.getWebSocketServer().sendInfo(base.getJson(log));

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
        client.getWebSocketServer().sendInfo(base.getJson(log));

    }

    private void readyReceive(CFTPMessage message) {
        File file = new File("/Users/wuyh/Desktop/");
        if (!file.exists()) {//如果文件夹不存在
            file.mkdir();//创建文件夹
        }

    }
}
