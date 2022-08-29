package com.cylt.ftp.codec;

import com.alibaba.fastjson.JSON;
import com.cylt.ftp.protocol.CFTPMessage;
import com.cylt.ftp.protocol.DataHead;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.util.CharsetUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ProxyMessage解码器，需要先经过LengthFieldBasedFrameDecoder解码
 * 实例：1|8|{id:111}|[00,00,00,00],[00,00,00,00],[00,00,00,00],[00,00,00,00]
 */

public class CFTPDecoder extends MessageToMessageDecoder<ByteBuf> {

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) {
        try {
            // 读命令类型
            int type = byteBuf.readInt();
            //读json串长度
            int metaDataLength = byteBuf.readInt();
            //按长度取json字节
            CharSequence metaDataString = byteBuf.readCharSequence(metaDataLength, CharsetUtil.UTF_8);
            // 将json字符串转换成map对象
            Map<String, Object> metaData = JSON.parseObject(metaDataString.toString(), HashMap.class);

            //判断是否有字节数组
            byte[] data = null;
            if (byteBuf.isReadable()) {
                data = ByteBufUtil.getBytes(byteBuf);
            }

            //封装对象
            CFTPMessage ftp = new CFTPMessage();
            ftp.setType(type);
            ftp.setMetaData(metaData);
            ftp.setDataHead(new DataHead(metaData));
            ftp.setData(data);
            list.add(ftp);
        } catch (Exception e) {
            System.out.println("数据解码错误" + e.getMessage());
        }
    }
}
