package com.miniplat.util;

import android.util.Log;

public class LogUtil {
    private static boolean showTime = true;
    private static long lastTime = System.currentTimeMillis();
    private static long startTime;

    public static void setShowTime(boolean showTime) {
        LogUtil.showTime = showTime;
    }

    public static boolean getShowTime() {
        return showTime;
    }

    private synchronized static String setLog(StackTraceElement[] stacks) {
        String fulClassName = stacks[1].getClassName();
        if (fulClassName.indexOf("$") != -1) {
            fulClassName = fulClassName.substring(0, fulClassName.indexOf("$"));
        }
        String className = fulClassName.substring(
                fulClassName.lastIndexOf(".") + 1, fulClassName.length());
        String log = "at " + fulClassName + "." + stacks[1].getMethodName()
                + "(" + className + ".java:" + stacks[1].getLineNumber() + ")";

        long currentTime = System.currentTimeMillis();
        if (getShowTime()) {
            if (startTime == 0) {
                startTime = currentTime;
            }
            if (lastTime == 0) {
                lastTime = currentTime;
            }
            log = log + "_" + (currentTime - startTime) + "ms_"
                    + (currentTime - lastTime) + "ms";
        } else {
        }
        lastTime = currentTime;
        return log;
    }

    public static void v() {
        StackTraceElement[] stacks = new Throwable().getStackTrace();
        String log = setLog(stacks);
        Log.v("D", log);
    }

    public static void d() {
        StackTraceElement[] stacks = new Throwable().getStackTrace();
        String log = setLog(stacks);
        Log.d("D", log);
    }

    public static void i() {
        StackTraceElement[] stacks = new Throwable().getStackTrace();
        String log = setLog(stacks);
        Log.i("D", log);
    }

    public static void w() {
        StackTraceElement[] stacks = new Throwable().getStackTrace();
        String log = setLog(stacks);
        Log.w("D", log);
    }

    public static void e() {
        StackTraceElement[] stacks = new Throwable().getStackTrace();
        String log = setLog(stacks);
        Log.e("D", log);
    }

    public static void v(String s) {
        StackTraceElement[] stacks = new Throwable().getStackTrace();
        String log = setLog(stacks);
        Log.v("D", log + " " + s);
    }

    public static void d(String s) {
        StackTraceElement[] stacks = new Throwable().getStackTrace();
        String log = setLog(stacks);
        Log.d("D", log + " " + s);
    }

    public static void i(String s) {
        StackTraceElement[] stacks = new Throwable().getStackTrace();
        String log = setLog(stacks);
        Log.i("D", log + " " + s);
    }

    public static void w(String s) {
        StackTraceElement[] stacks = new Throwable().getStackTrace();
        String log = setLog(stacks);
        Log.w("D", log + " " + s);
    }

    public static void e(String s) {
        StackTraceElement[] stacks = new Throwable().getStackTrace();
        String log = setLog(stacks);
        Log.e("D", log + " " + s);
    }
}
