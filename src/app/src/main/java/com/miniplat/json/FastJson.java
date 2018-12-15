package com.miniplat.json;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;

import java.lang.reflect.Type;
import java.util.List;

public class FastJson {
    public static String toJson(Object o) {
        return JSON.toJSONString(o);
    }

    public static JSONObject fromJson(String s) {
        return JSON.parseObject(s);
    }

    public static JSONArray fromJsonList(String s) {
        return JSON.parseArray(s);
    }

    public static <T> T fromJson(String s, Type t) {
        return JSON.parseObject(s, t);
    }

    public static <T> List<T> fromJsonList(String s, Class<T> t) {
        return JSON.parseArray(s, t);
    }
}
