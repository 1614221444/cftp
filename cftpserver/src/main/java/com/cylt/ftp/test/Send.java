package com.cylt.ftp.test;

import com.cylt.ftp.config.ConfigParser;
import com.cylt.ftp.hendler.ServerHandler;
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

	private ServerHandler server;

	public Send(ServerHandler server,String files) {
		this(server,UUID.randomUUID().toString(), files, 0);
	}
	public Send(ServerHandler server,String id, String files, int i) {
		this.files = files;
		this.id = id;
		this.i = i;
		this.server = server;
	}

	@Override
	public void run() {
		CFTPMessage HEART_BEAT;
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
			metaData.put("fileName", "b.text");
			metaData.put("url", files);
			metaData.put("startDate", start);
			metaData.put("clientKey", server.clientKey);
			HEART_BEAT.setMetaData(metaData);
			DataHead data = new DataHead();
			data.setId(id);
			data.setIndex(i);
			data.setFileName("b.text");
			data.setFileSize((int) fileSize);
			data.setTotal((int) total);
			data.setUrl(files);
			HEART_BEAT.setDataHead(data);
			server.processData(server.getCtx(),HEART_BEAT,true);

		} catch (IOException e) {
			System.out.println(e.toString());
		}
	}
}
