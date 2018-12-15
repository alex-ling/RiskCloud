package com.miniplat.http;

import com.alibaba.fastjson.JSONObject;

public abstract class JsonHandler extends BaseHandler {
    public abstract void success(JSONObject res);
}