package cn.ai4wms.client.utils;

import java.util.List;

public class ShellResult {
    public static final int SUCCESS = 0;

    public static final int ERROR = 1;

    public static final int TIMEOUT = 13;

    private int errorCode;

    private List<String> description;

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public List<String> getDescription() {
        return description;
    }

    public void setDescription(List<String> description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "ShellResult{" +
                "errorCode=" + errorCode +
                ", description=" + description +
                '}';
    }
}
