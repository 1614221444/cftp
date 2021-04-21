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
 */

public class CFTPDecoder extends MessageToMessageDecoder<ByteBuf> {

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) {
        try {
            int type = byteBuf.readInt();
            int metaDataLength = byteBuf.readInt();
            CharSequence metaDataString = byteBuf.readCharSequence(metaDataLength, CharsetUtil.UTF_8);
            Map<String, Object> metaData = JSON.parseObject(metaDataString.toString(), HashMap.class);

            byte[] data = null;
            if (byteBuf.isReadable()) {
                data = ByteBufUtil.getBytes(byteBuf);
            }

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
