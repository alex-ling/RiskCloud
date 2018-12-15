package com.miniplat.core;

import java.util.List;

public interface IListHandler<T> {
    void callback(List<T> results);
}
