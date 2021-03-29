package com.cylt.ftp.protocol;

import java.util.Map;

public class CFTPMessage {

    //消息类型
    private int type;
    //元数据
    private Map<String,Object> metaData;

    //元数据
    private DataHead dataHead;
    //消息内容
    private byte[] data;

    //注册
    public static final int TYPE_REGISTER = 1;
    //授权
    public static final int TYPE_AUTH = 2;
    //建立连接
    public static final int TYPE_CONNECTED = 3;
    //断开连接
    public static final int TYPE_DISCONNECTED = 4;
    //心跳
    public static final int TYPE_KEEPALIVE = 5;
    //数据传输
    public static final int TYPE_DATA = 6;

    public CFTPMessage() {
    }

    public CFTPMessage(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Map<String, Object> getMetaData() {
        return metaData;
    }

    public void setMetaData(Map<String, Object> metaData) {
        this.metaData = metaData;
    }

    public DataHead getDataHead() {
        return dataHead;
    }

    public void setDataHead(DataHead dataHead) {
        this.dataHead = dataHead;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
