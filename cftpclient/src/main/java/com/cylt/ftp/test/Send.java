package com.cylt.ftp.test;

import com.cylt.ftp.App;
import com.cylt.ftp.config.ConfigParser;
import com.cylt.ftp.handler.ClientHandler;
import com.cylt.ftp.protocol.CFTPMessage;
import com.cylt.ftp.protocol.DataHead;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

public class Send extends Thread {

	@Override
	public void run() {
		CFTPMessage HEART_BEAT;
		DataHead dataHead;
		String file = "/Users/wuyh/work/资源/安装包/jre-8u202-windows-x64.exe";
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
			// 初始化请求头
			HashMap<String, Object> metaData;
			int i = 0;
			Date start = new Date();
			String id = UUID.randomUUID().toString();

			HEART_BEAT = new CFTPMessage(CFTPMessage.TYPE_DATA);
			metaData = new HashMap<>();
			metaData.put("clientKey", ConfigParser.get("client-key"));
			metaData.put("fileName", "b.text");
			metaData.put("fileSize", fileSize);
			metaData.put("total", total);
			metaData.put("url", files);
			metaData.put("id", id);
			metaData.put("startDate", start);
			HEART_BEAT.setMetaData(metaData);
			App.ser.channel.writeAndFlush(HEART_BEAT);
//			while (i < total) {
//				i++;
//				buffer.clear();
//				if(i == total) {
//					buffer = ByteBuffer.allocate((int) fileSize % readMax);
//				}
//				buffer.flip();
//				dataHead = new DataHead();
//				dataHead.setId(id);
//				inChannel.read(buffer);
//				dataHead.setIndex(i);
//				dataHead.setData(buffer.array());
//				ClientHandler.localHelper.channel.writeAndFlush(dataHead);
//
//
//			}
		} catch (IOException e) {
			System.out.println(e.toString());
		}
	}
}
