package com.createlt.agreement.base;

public interface BaseClient {

    /**
     * 启动本地服务
     * @param ip
     * @param port
     * @param clientId
     */
    void start(String ip, int port,String clientId) throws Exception;



    /**
     * 关闭服务
     */
    void stop();

    /**
     * 发送消息
     */
    void send(String sendId, String data);
}
