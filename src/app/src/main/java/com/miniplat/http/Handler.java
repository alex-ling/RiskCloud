package com.miniplat.http;

public abstract class Handler<T> extends BaseHandler {
    public abstract Class<T> getClazz();
    public abstract void success(T res);
}
