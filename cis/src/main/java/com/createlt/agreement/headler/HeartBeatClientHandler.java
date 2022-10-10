package com.createlt.agreement.headler;

import com.createlt.agreement.codec.CFTPMessage;
import com.createlt.cis.entity.CisController;
import com.createlt.cis.service.ICisControllerService;
import com.createlt.common.ToolSpring;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.EventLoopGroup;

import java.util.HashMap;
import java.util.Map;

/**
 * 客户端心跳处理器
 */

public class HeartBeatClientHandler extends ChannelInboundHandlerAdapter {

    //服务器端读超时时间为40秒，因此客户端需要保证在40秒内一定要有数据发送给服务器
    //当空闲35秒时还没有发送数据，那么发送心跳包，因此设置客户端的写超时为35秒

    private EventLoopGroup workerGroup = null;

    /**
     * 客户端控制器实例
     */
    private String clientId = "";
    /**
     * 客户端实例
     */
    private ICisControllerService cisControllerService;

    public HeartBeatClientHandler() {
    }

    public HeartBeatClientHandler(EventLoopGroup workerGroup,String clientId) {
        this.workerGroup = workerGroup;
        this.clientId = clientId;
        cisControllerService = (ICisControllerService) ToolSpring.getBean("cisControllerServiceImpl");
    }

    //心跳包
    private static final CFTPMessage HEART_BEAT = new CFTPMessage(CFTPMessage.TYPE_KEEPALIVE);

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        // 连接被关闭 同步到实例关闭
        CisController controller = cisControllerService.getById(clientId);
        controller.setIsStart(false);
        cisControllerService.updateById(controller);
        // 关闭线程
        workerGroup.shutdownGracefully();
        ctx.channel().close();
        super.channelUnregistered(ctx);
    }

    //客户端写超时事件发生会默认调用该方法
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        Map<String,Object> map = new HashMap<>();
        map.put("channelId","666");
        map.put("clientKey","test1");
        HEART_BEAT.setMetaData(map);
        HEART_BEAT.setData(new byte[55]);
        ctx.writeAndFlush(HEART_BEAT);
        super.userEventTriggered(ctx,evt);
    }

    //异常处理，发生异常时直接关闭服务器连接(比如发送心跳失败，可能服务器下线了)
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println(this.getClass()+"\r\n 连接异常，已中断");
        workerGroup.shutdownGracefully();
        ctx.fireExceptionCaught(cause);
        ctx.channel().close();
    }
}
