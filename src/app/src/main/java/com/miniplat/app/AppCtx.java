package com.miniplat.app;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Process;

import com.miniplat.cache.ICache;
import com.miniplat.cache.SharedCache;
import com.miniplat.util.DialogUtil;
import com.miniplat.util.ToastUtil;

public class AppCtx {
    public static final String URL = "http://risk.miniplat.com/";
    public static final String APP_ID = "208078593874460672";
    public static final int REGIST_TYPE = 2;
    public static final String CLIENT_ID = "aceclient";
    public static final String ClIENT_Secret = "Us1e3cv5rsMet4pv";
    public static final String GRANT_Password = "password";
    public static final String GRANT_RefreshToken = "refresh_token";

    private static Dialog dialogLoading;
    private static long exitTime = 0;

    public static void killProcess() {
        Process.killProcess(Process.myPid());
    }

    public static Context getAppContext() {
        return AceApplication.getAppContext();
    }

    public static Activity getContext() {
        return Container.getTopActivity();
    }

    public static ICache getCache() {
        return new SharedCache();
    }

    public static void showLoading() {
        dialogLoading = DialogUtil.loading();
    }

    public static void closeLoading() {
        if (dialogLoading != null) {
            dialogLoading.cancel();
            dialogLoading = null;
        }
    }

    public static void exitApp() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            ToastUtil.warn("再按一次退出程序");
            exitTime = System.currentTimeMillis();
        } else {
            Container.finishAll();
            killProcess();
        }
    }
}
