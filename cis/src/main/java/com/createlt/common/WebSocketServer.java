package com.createlt.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;

@ServerEndpoint(value = "/websocket/{username}")
@Service
public class WebSocketServer {

    private static Logger log = LoggerFactory.getLogger(WebSocketServer.class);

    /**
     * concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
     */
    private static CopyOnWriteArraySet<WebSocketServer> govWebSocketSet = new CopyOnWriteArraySet<>();
    private Session govSession;

    public static Map<String, List<WebSocketServer>> govUserSessions = new HashMap<>();
    public String pointsUrl;

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session,@PathParam("username") String username) {
        this.govSession = session;
        List<WebSocketServer> list = new ArrayList<>();
        // 一个用户不同地方登陆所做的处理
        if (govUserSessions.containsKey(username)) {
            list = govUserSessions.get(username);
        }
        list.add(this);
        govUserSessions.put(username, list);
        //加入set中
        govWebSocketSet.add(this);
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        //从set中删除
        closeRemove(govWebSocketSet, govUserSessions);
    }

    private void closeRemove(CopyOnWriteArraySet<WebSocketServer> webSocketSet, Map<String, List<WebSocketServer>> userSessions) {
        webSocketSet.remove(this);
        Set<Map.Entry<String, List<WebSocketServer>>> entrySet = userSessions.entrySet();
        for (Map.Entry<String, List<WebSocketServer>> entry : entrySet) {
            String key = entry.getKey();
            boolean isFind = findRemove(key);
            if (isFind) {
                return;
            }
        }
    }

    /**
     * 查找移除
     *
     * @param key
     * @return
     */
    private boolean findRemove(String key) {
        return backOrFrontFind(key, govUserSessions);
    }

    private boolean backOrFrontFind(String key, Map<String, List<WebSocketServer>> govUserSessions) {
        List<WebSocketServer> list = govUserSessions.get(key);
        for (int i = 0; i < list.size(); i++) {
            if (this.equals(list.get(i))) {
                list.remove(i);
                if (list.size() > 0) {
                    govUserSessions.put(key, list);
                } else {
                    govUserSessions.remove(key);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * @param error
     */
    @OnError
    public void onError(Throwable error) {
        log.error("发生错误");
        error.printStackTrace();
    }

    /**
     * 实现服务器主动推送
     */
    public synchronized void sendMessage(String message) {
        try {
            this.govSession.getBasicRemote().sendText(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 群发自定义消息
     */
    public static void sendInfo(String message,@PathParam("username") String username) {
        log.info("推送消息到窗口推送内容:" + message);
        backOrFrontSend(message,username,govWebSocketSet);
    }

    private static void backOrFrontSend(String message, @PathParam("username") String username,CopyOnWriteArraySet<WebSocketServer> govWebSocketSet) {
        List<WebSocketServer> list = govUserSessions.get(username);
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                list.get(i).sendMessage(message);
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WebSocketServer that = (WebSocketServer) o;
        return
                Objects.equals(govSession, that.govSession) &&
                Objects.equals(pointsUrl, that.pointsUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(govSession,pointsUrl);
    }
}
