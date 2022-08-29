package com.cylt.ftp.codec;

import com.alibaba.fastjson.JSON;
import com.cylt.ftp.protocol.CFTPMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;

/**
 * CFTPEncoder编码器
 * 实例：1|8|{id:111}|[00,00,00,00],[00,00,00,00],[00,00,00,00],[00,00,00,00]
 */
public class CFTPEncoder extends MessageToByteEncoder<CFTPMessage> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, CFTPMessage cftpMessage, ByteBuf byteBuf) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             DataOutputStream dos = new DataOutputStream(baos)){
            // 写命令类型
            dos.writeInt(cftpMessage.getType());
            if(cftpMessage.getMetaData() == null){
                cftpMessage.setMetaData(new HashMap<>());
            }
            if(cftpMessage.getDataHead() != null){
                cftpMessage.getDataHead().toMap(cftpMessage.getMetaData());
            }
            // 将map转换成json结构并且序列化成字节数组
            byte[] metaData = JSON.toJSONString(cftpMessage.getMetaData()).getBytes();
            // 记录字符数组长度
            dos.writeInt(metaData.length);
            // 导入数据参数字节
            dos.write(metaData);
            if (cftpMessage.getData() != null && cftpMessage.getData().length > 0) {
                dos.write(cftpMessage.getData());
            }
            byte[] data = baos.toByteArray();
            // 记录主数据字节数组长度
            byteBuf.writeInt(data.length);
            // 导入主数据字节
            byteBuf.writeBytes(data);
        } catch (IOException e) {
            System.out.println("数据编码错误 :IOException");
        } catch (Exception e) {
            System.out.println("数据编码错误 :Exception");
        }
    }
}
