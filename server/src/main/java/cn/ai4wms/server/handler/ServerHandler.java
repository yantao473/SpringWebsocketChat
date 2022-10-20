package cn.ai4wms.server.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ServerHandler extends TextWebSocketHandler {
    private Logger logger = LoggerFactory.getLogger(ServerHandler.class);
    private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final Map<String, WebSocketSession> SESSIONS = new ConcurrentHashMap<>();

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String msg = String.format("-------  Receive message: %s at %s -------", message.getPayload(), df.format(new Date()));
        logger.info(msg);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String msg = String.format("------- Connect at: %s -------", df.format(new Date()));
        logger.info(msg);
        TextMessage message = new TextMessage("Connection OK");
        SESSIONS.put("xxx", session);
        session.sendMessage(message);
    }

    public void sendMessage(String msg) {
        SESSIONS.forEach((k, v) -> {
            if (v != null) {
                try {
                    TextMessage message = new TextMessage(msg);
                    v.sendMessage(message);
                } catch (Exception e) {
                    // TODO
                }
            }
        });
    }
}
