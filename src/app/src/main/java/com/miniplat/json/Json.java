package com.miniplat.json;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.TypeReference;
import com.miniplat.http.*;
import com.alibaba.fastjson.JSONObject;

import java.lang.reflect.Type;
import java.util.List;
import ikidou.reflect.TypeBuilder;

public final class Json {
    public static String toJson(Object o) {
        return FastJson.toJson(o);
    }

    public static JSONObject fromJson(String s) {
        return FastJson.fromJson(s);
    }

    public static JSONArray fromJsonList(String s) {
        return FastJson.fromJsonList(s);
    }

    public static <T> T fromJsonObject(String s, Class<T> c) {
        return FastJson.fromJson(s, c);
    }

    public static <T> List<T> fromJsonList(String s, Class<T> c) {
        return FastJson.fromJsonList(s, c);
    }

    public static <T> Response<T> fromJsonResponse(String s, Class<T> c) {
        Type type = TypeBuilder
                .newInstance(Response.class)
                .addTypeParam(c)
                .build();
        return FastJson.fromJson(s, type);
    }

    public static <T> Response<List<T>> fromJsonResponseList(String s, Class<T> c) {
        Type type = TypeBuilder
                .newInstance(Response.class)
                .beginSubType(List.class)
                .addTypeParam(c)
                .endSubType()
                .build();
        return FastJson.fromJson(s, type);
    }
}
