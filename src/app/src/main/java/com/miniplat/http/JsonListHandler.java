package com.miniplat.http;

import com.alibaba.fastjson.JSONArray;

public abstract class JsonListHandler extends BaseHandler {
    public abstract void success(JSONArray res);
}
