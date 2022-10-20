package cn.ai4wms.client.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;


public class CommonUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommonUtils.class);

    /**
     * 提供统一关闭流的方法
     *
     * @param stream 待关闭的流
     */
    public static void closeStream(Closeable stream) {
        if (stream == null) {
            return;
        }

        try {
            stream.close();
        } catch (IOException e) {
            LOGGER.error("Close stream failed!");
        }
    }
}
