package com.miniplat.http;

import java.util.List;

public abstract class ListHandler<T> extends BaseHandler {
    public abstract Class<T> getClazz();
    public abstract void success(List<T> res);
}
