package com.miniplat.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.miniplat.app.AppCtx;

public class NetworkUtil {
    public static boolean isNetworkConnection() {
        ConnectivityManager manager = (ConnectivityManager) AppCtx.getAppContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo network = manager.getActiveNetworkInfo();
        if (network != null) {
            return network.isAvailable();
        }
        return false;
    }

    public static boolean isWifiConnection() {
        ConnectivityManager manager = (ConnectivityManager) AppCtx.getAppContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo network = manager.getActiveNetworkInfo();
        if (network != null) {
            return network.isAvailable() && network.getType() == ConnectivityManager.TYPE_WIFI;
        }
        return false;
    }
}
