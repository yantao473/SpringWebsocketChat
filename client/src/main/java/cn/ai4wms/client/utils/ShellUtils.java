package cn.ai4wms.client.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ShellUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShellUtils.class);

    private static final long THREAD_SLEEP_TIME = 10;

    private static final int DEFAULT_WAIT_TIME = 20 * 60 * 1000;


    public static void runShell(String cmd) {
        String[] command = new String[]{"/bin/sh", "-c", cmd};
        try {
            Process process = Runtime.getRuntime().exec(command);
            ShellResult result = getProcessResult(process, DEFAULT_WAIT_TIME);
            LOGGER.info("Command [{}] executed successfully.", cmd);
            LOGGER.info(result.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取命令执行结果
     * @param process 子进程
     * @param waitTime 指定超时时间
     * @return 命令执行输出结果
     */
    public static ShellResult getProcessResult(Process process, long waitTime) {
        ShellResult cmdResult = new ShellResult();
        boolean isTimeout = false;
        long loopNumber = waitTime / THREAD_SLEEP_TIME;
        long realLoopNumber = 0;
        int exitValue = -1;

        StreamGobbler errorGobbler = new StreamGobbler(process.getErrorStream());
        StreamGobbler outputGobbler = new StreamGobbler(process.getInputStream());

        errorGobbler.start();
        outputGobbler.start();

        try {
            while (true) {
                try {
                    Thread.sleep(THREAD_SLEEP_TIME);
                    exitValue = process.exitValue();
                    break;
                } catch (InterruptedException e) {
                    realLoopNumber++;
                    if (realLoopNumber >= loopNumber) {
                        isTimeout = true;
                        break;
                    }
                }
            }

            errorGobbler.join();
            outputGobbler.join();

            if (isTimeout) {
                cmdResult.setErrorCode(ShellResult.TIMEOUT);
                return cmdResult;
            }

            cmdResult.setErrorCode(exitValue);
            if (exitValue != ShellResult.SUCCESS) {
                cmdResult.setDescription(errorGobbler.getOutput());
            } else {
                cmdResult.setDescription(outputGobbler.getOutput());
            }
        } catch (InterruptedException e) {
            LOGGER.error("Get shell result error.");
            cmdResult.setErrorCode(ShellResult.ERROR);
        } finally {
            CommonUtils.closeStream(process.getErrorStream());
            CommonUtils.closeStream(process.getInputStream());
            CommonUtils.closeStream(process.getOutputStream());
        }

        return cmdResult;
    }
}
