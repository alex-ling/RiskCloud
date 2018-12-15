package com.miniplat.app;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class AceApplication extends Application {
    private static Context context;
    private static boolean debug = false;

    public void onCreate() {
        super.onCreate();

        // 设置全局的应用上下文
        context = getApplicationContext();

        // 设置当前是否调试版本
        debug = (getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;

        // 初始化图片加载器
        initImageLoader();
    }

    private void initImageLoader() {
        ImageLoaderConfiguration config =
            new ImageLoaderConfiguration.Builder(this).build();
        ImageLoader.getInstance().init(config);
    }

    public static Context getAppContext() {
        return context;
    }

    public static boolean getDebug() {
        return debug;
    }
}
