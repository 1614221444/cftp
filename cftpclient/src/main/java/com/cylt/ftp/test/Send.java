package com.cylt.ftp.test;

import com.cylt.ftp.App;
import com.cylt.ftp.config.ConfigParser;
import com.cylt.ftp.handler.ClientHandler;
import com.cylt.ftp.handler.LocalHandler;
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

	/**
	 * 文件路径
	 */
	private String files;

	/**
	 * 当前传输
	 */
	private String id;

	/**
	 * 当前传输索引
	 */
	private int i;

	public Send(String files) {
		this(UUID.randomUUID().toString(), files, 0);
	}
	public Send(String id, String files, int i) {
		this.files = files;
		this.id = id;
		this.i = i;
	}

	@Override
	public void run() {
		CFTPMessage HEART_BEAT;
		DataHead dataHead;
		// String files = ConfigParser.getServerConfigFile() + "A.text";
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
			Date start = new Date();
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

			LocalHandler localHandler = new LocalHandler(HEART_BEAT, true, i);
			ClientHandler.localHandlerMap.put(id, localHandler);
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
