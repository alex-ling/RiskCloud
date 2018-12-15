package com.miniplat.util;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.miniplat.app.AppCtx;

public class ToastUtil {
    public static void success(String msg) {
        Context ctx = AppCtx.getContext();
        Toast t = Short(ctx, msg);
        t.show();
    }

    public static void warn(String msg) {
        Context ctx = AppCtx.getContext();
        Toast t = Short(ctx, msg);
        t.show();
    }

    public static void error(String msg) {
        Context ctx = AppCtx.getContext();
        Toast t = Short(ctx, msg);
        t.show();
    }

    private static Toast Short(Context context, CharSequence message) {
        return Toast.makeText(context, message, Toast.LENGTH_SHORT);
    }

    private static Toast Long(Context context, CharSequence message) {
        return Toast.makeText(context, message, Toast.LENGTH_LONG);
    }

    private static Toast setColor(Toast t, int messageColor, int backgroundColor) {
        View view = t.getView();
        view.setBackgroundColor(Color.TRANSPARENT);
        TextView message = ((TextView) view.findViewById(android.R.id.message));
        message.setBackgroundColor(backgroundColor);
        message.setTextColor(messageColor);
        return t;
    }

    private static Toast setBack(Toast t, int messageColor, int background) {
        View view = t.getView();
        view.setBackgroundColor(Color.TRANSPARENT);
        TextView message = ((TextView) view.findViewById(android.R.id.message));
        message.setBackgroundResource(background);
        message.setTextColor(messageColor);
        return t;
    }
}
