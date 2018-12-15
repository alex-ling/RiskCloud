package com.miniplat.http;

public class Response<T> {
    public int status;
    public String error;
    public String error_description;
    public T value;
}
