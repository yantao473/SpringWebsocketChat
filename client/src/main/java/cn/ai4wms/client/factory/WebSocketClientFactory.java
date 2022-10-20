package cn.ai4wms.client.factory;

import cn.ai4wms.client.utils.MacUtil;
import cn.ai4wms.client.utils.ShellUtils;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Pattern;

@Component
@Slf4j
public class WebSocketClientFactory {
    public static final String outCallWebSockertUrl = "ws://192.168.18.8:9000/server";

    private WebSocketClient outCallWebSocketClientHolder;


    /**
     * 创建websocket对象
     *
     * @return WebSocketClient
     * @throws URISyntaxException
     */
    private WebSocketClient createNewWebSocketClient() throws URISyntaxException {
        WebSocketClient webSocketClient = new WebSocketClient(new URI(outCallWebSockertUrl)) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
            }

            @Override
            public void onMessage(String msg) {
                if (isValidIPAddress(msg)) {
                    String rtspStream = String.format("rtsp://admin:a12345678@%s:554/cam/realmonitor?channel=1&subtype=0", msg);
                    String suffix = msg.replace(".", "-");
                    String rtmpStream = String.format("rtmp://192.168.18.242:1935/live/%s", suffix);
                    String cmd = String.format("nohup ffmpeg -loglevel quiet -i '%s' -vcodec copy -acodec copy -f flv %s> /dev/null 2>&1 &", rtspStream, rtmpStream);
                    log.info(cmd);
                    ShellUtils.runShell(cmd);
                }
                log.info("接收信息为：{}", msg);
            }

            @Override
            public void onClose(int i, String s, boolean b) {
                log.info("关闭连接");
                retryOutCallWebSocketClient();
            }

            @Override
            public void onError(Exception e) {
                log.error("连接异常");
                retryOutCallWebSocketClient();
            }
        };
        webSocketClient.connect();
        return webSocketClient;
    }


    /**
     * 项目启动或连接失败的时候打开新链接,进行连接认证
     * 需要加同步，不然会创建多个连接
     */
    public synchronized WebSocketClient retryOutCallWebSocketClient() {
        try {
            // 关闭旧的websocket连接, 避免占用资源
            WebSocketClient oldOutCallWebSocketClientHolder = getOutCallWebSocketClientHolder();
            if (null != oldOutCallWebSocketClientHolder) {
                log.info("关闭旧的websocket连接");
                oldOutCallWebSocketClientHolder.close();
            }

            log.info("打开新的websocket连接，并进行认证");
            WebSocketClient webSocketClient = createNewWebSocketClient();
            String mac = MacUtil.getCurrentIpLocalMac();
            String sendOpenJsonStr = "{\"event\":\"connect\",\"mac\":\"" + mac + "\",\"token\":\"df59eba89\"}";
            sendMsg(webSocketClient, sendOpenJsonStr);

            // 每次创建新的就放进去
            setOutCallWebSocketClientHolder(webSocketClient);
            return webSocketClient;
        } catch (URISyntaxException e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return null;
    }


    /**
     * 发送消息
     * 注意： 要加超时设置，避免很多个都在同时超时占用资源
     *
     * @param webSocketClient 指定的webSocketClient
     * @param message         消息
     */
    public void sendMsg(WebSocketClient webSocketClient, String message) {
        log.info("websocket向服务端发送消息，消息为：{}", message);
        long startOpenTimeMillis = System.currentTimeMillis();
        while (!webSocketClient.getReadyState().equals(WebSocket.READYSTATE.OPEN)) {
            log.debug("正在建立通道，请稍等");
            long currentTimeMillis = System.currentTimeMillis();
            if (currentTimeMillis - startOpenTimeMillis >= 5000) {
                log.error("超过5秒钟还未打开连接，超时，不再等待");
                return;
            }
        }
        webSocketClient.send(message);
    }


    @Async
    @Scheduled(fixedRate = 10000)
    public void sendHeartBeat() {
        log.info("定时发送websocket心跳");
        try {
            WebSocketClient outCallWebSocketClientHolder = getOutCallWebSocketClientHolder();

            if (null == outCallWebSocketClientHolder) {
                log.info("当前连接还未建立，暂不发送心跳消息");
                return;
            }

            // 心跳的请求串，根据服务端来定
            String mac = MacUtil.getCurrentIpLocalMac();
            String heartBeatMsg = "{\"event\":\"heartbeat\",\"mac\":\"" + mac + "\"}";
            sendMsg(outCallWebSocketClientHolder, heartBeatMsg);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("发送心跳异常");
            retryOutCallWebSocketClient();
        }
    }

    private boolean isValidIPAddress(String ipAddress) {
        if ((ipAddress != null) && (!ipAddress.isEmpty())) {
            return Pattern.matches("^([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}$", ipAddress);
        }
        return false;
    }

    private WebSocketClient getOutCallWebSocketClientHolder() {
        return outCallWebSocketClientHolder;
    }

    private void setOutCallWebSocketClientHolder(WebSocketClient webSocketClient) {
        outCallWebSocketClientHolder = webSocketClient;
    }
}
