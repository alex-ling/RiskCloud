package com.miniplat.cache;

import android.content.Context;
import android.content.SharedPreferences;

import com.miniplat.json.Json;
import com.miniplat.app.AppCtx;

public class SharedCache implements ICache {
    private static final String KEY_Cache = "key_cache";
    private SharedPreferences store;

    public SharedCache() {
        store = AppCtx.getAppContext().getSharedPreferences(KEY_Cache, Context.MODE_PRIVATE);
    }

    public <T> T getObject(String key, Class<T> c) {
        String json = store.getString(key, null);
        if (json != null) {
            return Json.fromJsonObject(json, c);
        }
        return null;
    }

    public <T> void setObject(String key, T value) {
        setString(key, Json.toJson(value));
    }

    public String getString(String key, String defaultValue) {
        return store.getString(key, defaultValue);
    }

    public void setString(String key, String value) {
        SharedPreferences.Editor editor = store.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public int getInt(String key, int defaultValue) {
        return store.getInt(key, defaultValue);
    }

    public void setInt(String key, int value) {
        SharedPreferences.Editor editor = store.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return store.getBoolean(key, defaultValue);
    }

    public void setBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = store.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public long getLong(String key, long defaultValue) {
        return store.getLong(key, defaultValue);
    }

    public void setLong(String key, long value) {
        SharedPreferences.Editor editor = store.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    public float getFloat(String key, float defaultValue) {
        return store.getFloat(key, defaultValue);
    }

    public void setFloat(String key, float value) {
        SharedPreferences.Editor editor = store.edit();
        editor.putFloat(key, value);
        editor.apply();
    }

    public void remove(String key) {
        SharedPreferences.Editor editor = store.edit();
        editor.remove(key);
        editor.apply();
    }
}
