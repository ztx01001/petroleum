package com.university.springboot_petroleum.websocket;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@ServerEndpoint("/test/oneToOne")
@Component
public class WebSocketServer {
	private static Logger log = LoggerFactory.getLogger(WebSocketServer.class);

	/*静态变量，用来记录当前在线连接数.应该把它设计成线程安全的
	 */
	private static AtomicInteger onlineCount = new AtomicInteger(0);
	private static ConcurrentHashMap<String,Session> clients = new ConcurrentHashMap<>();
	/*
	 *连接建立成功调用的方法 
	 **/
	@OnOpen
	public void onOpen(Session session) {
		log.info("WebSocketServer onOpen ");
		if(clients.containsKey(session.getId())) {
			clients.remove(session.getId());
			clients.put(session.getId(), session);
		}else {
			onlineCount.incrementAndGet();
			clients.put(session.getId(), session);
		}
	}
	/*
	 * 连接关闭调用方法
	 **/
	@OnClose
	public void onClose(Session session) {
		log.info("WebSocketServer onClose ");
		if(clients.containsKey(session.getId())) {
			onlineCount.decrementAndGet();
			clients.remove(session.getId());
		}
	}
	 /**
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session,Throwable error) {
    	log.info("WebSocketServer onError "+error.getLocalizedMessage());
    	
    }
    /*
     * 接收到消息
     * */
    @OnMessage
    public void onMessage(String message,Session session) {
    	log.info("WebSocketServer onMessage ="+message);
    }
    
    /**
     *服务端发送消息到某一个客户端 
     */
    public static void sendMessage(String message,Session toSession) {
    	log.info("WebSocketServer sendMessage= "+message);
    	try {
    		toSession.getBasicRemote().sendText(message);
    	}catch(Exception e) {
    		log.error("WebSocketServer sendMessage exception="+e.getLocalizedMessage());
    	}
    }
    /*服务端广播消息
     **/
    public static void sendAll(String message) {
    	log.info("WebSocketServer sendAll ");
    	try {
    		for(Map.Entry<String, Session> entry:clients.entrySet()) {
    			Session session = entry.getValue();
    			session.getBasicRemote().sendText(message);
    		}
    	}catch(Exception e) {
    		log.error("WebSocketServer sendAll exception="+e.getLocalizedMessage());
    	}
    }
}
