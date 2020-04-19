package com.popcloud.util;

import com.alibaba.fastjson.JSONObject;
import java.util.Map;

public class CommonUtil {

    public static int ENTITY_NEWS = 1;    //EntityType=1代表给新闻评论

    public static String TOUTIAO_DOMAIN = "http://127.0.0.1:8080/";
    public static String IMAGE_DIR = "D:/upload/";
    public static String[] IMAGE_FILE_EXTD = new String[] {"png", "bmp", "jpg", "jpeg","jfif"};

    public static boolean isFileAllowed(String fileName) {
        for (String ext : IMAGE_FILE_EXTD) {
            if (ext.equals(fileName)) {
                return true;
            }
        }
        return false;
    }

    public static String getJSONString(int code) {
        JSONObject json = new JSONObject();
        json.put("code", code);
        return json.toJSONString();
    }

    public static String getJSONString(int code, String msg) {
        JSONObject json = new JSONObject();
        json.put("code", code);
        json.put("msg", msg);
        return json.toJSONString();
    }

    public static String getJSONString(int code, Map<String, Object> map) {
        JSONObject json = new JSONObject();
        json.put("code", code);
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            json.put(entry.getKey(), entry.getValue());
        }
        return json.toJSONString();
    }
}
