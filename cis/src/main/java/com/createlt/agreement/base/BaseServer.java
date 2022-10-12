package com.createlt.agreement.base;

import java.util.List;

/**
 * 基础服务类
 */
public interface BaseServer {

    /**
     * 启动本地服务
     * @param port 端口
     * @param serverId 服务实例ID
     */
    void start(int port,String serverId);

    /**
     * 关闭服务
     */
    void stop();

    /**
     * 发送消息
     * @ to 发送人(查询用户返回的字符串)
     * @ file id 发送ID
     * @ data 发送数据
     */
    void send(String to,String fileId, String data);

    /**
     * 查询连接用户
     */
    List<String> getUserList();
}
