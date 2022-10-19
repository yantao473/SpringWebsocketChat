package cn.ai4wms.server.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * @description: 返回数据
 * @author: yanqing
 * @date: 2022-08-01 20:59
 */
public class Result extends HashMap<String, Object> {
    private static final long serialVersionUID = 1L;
    public static final String CODE = "code";
    public static final String MSG = "msg";
    public static final String DATA = "data";

    public Result() {
        put(CODE, 0);
        put(MSG, "success");
        put(DATA, new HashMap<String, Object>());
    }

    public static Result error() {
        return error(500, "Unknown Error");
    }

    public static Result error(String msg) {
        return error(500, msg);
    }

    public static Result error(int code, String msg) {
        Result result = new Result();
        result.put(CODE, code);
        result.put(MSG, msg);
        result.put(DATA, "");
        return result;
    }

    public static Result ok(String msg) {
        Result result = new Result();
        result.put(MSG, msg);
        return result;
    }

    public static Result ok(Map<String, Object> map) {
        Result result = new Result();
        result.put(DATA, map);
        return result;
    }

    public static Result ok() {
        return new Result();
    }

    @Override
    public Result put(String key, Object value) {
        if (CODE.equals(key) || MSG.equals(key) || DATA.equals(key)) {
            super.put(key, value);
        } else {
            Map<String, Object> dataMap = (Map<String, Object>) this.getOrDefault(DATA, new HashMap<String, Object>());

            Map<String, Object> map = new HashMap<>(1);
            map.put(key, value);

            Map<String, Object> combineResultMap = new HashMap<>();
            combineResultMap.putAll(dataMap);
            combineResultMap.putAll(map);

            super.put(DATA, combineResultMap);
        }

        return this;
    }
}
