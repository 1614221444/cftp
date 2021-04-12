package com.cylt.ftp.protocol;


import java.util.HashMap;
import java.util.Map;

public class DataHead {

    //类型
    private int type;
    //传输数据路径
    private String url;
    //传输id
    private String id;
    //顺序
    private int index;
    //次数
    private int total;
    //文件质量
    private int fileSize;
    //文件名称
    private String fileName;
    //消息内容
    private byte[] data;


    //准备接收
    public static final int READY_RECEIVE = 7;
    //准备完成
    public static final int READY_COMPLETE = 8;
    //补发
    public static final int SEND = 9;
    //接收完成
    public static final int RECEIVE_END = 10;
    //补发确认
    public static final int REISSUE = 11;

    public DataHead() {
    }

    public DataHead(Map<String, Object> map) {
        setType((int) (map.get("type") == null ? 0 : map.get("type")));
        setIndex((int) (map.get("index") == null ? 0 : map.get("index")));
        setFileSize((int) (map.get("fileSize") == null ? 0 : map.get("fileSize")));
        setUrl((String) map.get("url"));
        setId((String) map.get("id"));
        setTotal((int) (map.get("total") == null ? 0 : map.get("total")));
        setFileName((String) map.get("fileName"));
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }


    public int getFileSize() {
        return fileSize;
    }

    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        toMap(map);
        return map;
    }

    public void toMap(Map<String, Object> map) {
        map.put("type", getType());
        map.put("url", getUrl());
        map.put("id", getId());
        map.put("index", getIndex());
        map.put("fileSize", getFileSize());
        map.put("fileName", getFileName());
    }
}
