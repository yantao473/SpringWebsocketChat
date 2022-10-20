package cn.ai4wms.client.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class StreamGobbler extends Thread {
    private InputStream is;

    private List<String> output = new ArrayList<String>();

    public StreamGobbler(InputStream is) {
        this.is = is;
    }

    public List<String> getOutput() {
        return output;
    }

    @Override
    public void run() {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String line = "";
            while ((line = reader.readLine()) != null) {
                output.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            CommonUtils.closeStream(reader);
        }
    }
}
