package cn.ai4wms.server.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class ServerHandler extends TextWebSocketHandler {
    private Logger logger = LoggerFactory.getLogger(ServerHandler.class);
    private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

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
        session.sendMessage(message);
    }
}
