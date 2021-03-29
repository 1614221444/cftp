package com.cylt.ftp.codec;

import com.alibaba.fastjson.JSON;
import com.cylt.ftp.protocol.CFTPMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * CFTPEncoder编码器
 */
public class CFTPEncoder extends MessageToByteEncoder<CFTPMessage> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, CFTPMessage cftpMessage, ByteBuf byteBuf) {

        int type = cftpMessage.getType();
        byte[] data;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             DataOutputStream dos = new DataOutputStream(baos)){
            dos.writeInt(type);
            if(cftpMessage.getDataHead() != null){
                cftpMessage.getDataHead().toMap(cftpMessage.getMetaData());
            }
            byte[] metaData = JSON.toJSONString(cftpMessage.getMetaData()).getBytes();
            dos.writeInt(metaData.length);
            dos.write(metaData);
            if (cftpMessage.getData() != null && cftpMessage.getData().length > 0) {
                dos.write(cftpMessage.getData());
            }
            data = baos.toByteArray();
            byteBuf.writeInt(data.length);
            byteBuf.writeBytes(data);
        } catch (IOException e) {
            System.out.println("数据编码错误 :IOException");
        } catch (Exception e) {
            System.out.println("数据编码错误 :Exception");
        }
    }
}
