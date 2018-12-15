package com.miniplat.cache;

public interface ICache {
    <T> T getObject(String key, Class<T> c);
    <T> void setObject(String key, T value);
    String getString(String key, String defaultValue);
    void setString(String key, String value);
    int getInt(String key, int defaultValue);
    void setInt(String key, int value);
    boolean getBoolean(String key, boolean defaultValue);
    void setBoolean(String key, boolean value);
    long getLong(String key, long defaultValue);
    void setLong(String key, long value);
    float getFloat(String key, float defaultValue);
    void setFloat(String key, float value);
    void remove(String key);
}
