package com.miniplat.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.miniplat.app.AppCtx;
import com.miniplat._activity.R;
import com.miniplat.core.IHandler;

public class DialogUtil {
    public static void alert(String msg) {
        alert(msg, null);
    }

    public static void alert(String msg, final IHandler okHandler) {
        alert("提示", msg, okHandler, null);
    }

    public static void alert(String title, String msg, final IHandler okHandler, final IHandler cancelHandler) {
        Context ctx = AppCtx.getContext();
        AlertDialog.Builder dialog = new AlertDialog.Builder(ctx);
        dialog.setTitle(title);
        dialog.setMessage(msg);
        dialog.setCancelable(false);
        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (okHandler != null) {
                    okHandler.callback();
                }
            }
        });
        if (cancelHandler != null) {
            dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    cancelHandler.callback();
                }
            });
        }
        dialog.show();
    }

    public static Dialog progress() {
        return progress("正在执行...");
    }

    public static Dialog progress(String msg) {
        return progress("请稍候", msg);
    }

    public static Dialog progress(String title, String msg) {
        Context ctx = AppCtx.getContext();
        ProgressDialog dialog = new ProgressDialog(ctx);
        dialog.setTitle(title);
        dialog.setMessage(msg);
        dialog.setCancelable(false);
        dialog.show();
        return dialog;
    }

    public static Dialog loading() {
        return loading("正在加载...");
    }

    public static Dialog loading(String msg) {
        Context ctx = AppCtx.getContext();
        LayoutInflater i = LayoutInflater.from(ctx);
        View v = i.inflate(R.layout.dialog_loading, null);

        LinearLayout layout = (LinearLayout)v.findViewById(R.id.view);
        ImageView image = (ImageView)v.findViewById(R.id.image);
        TextView text = (TextView)v.findViewById(R.id.text);
        Animation ani = AnimationUtils.loadAnimation(ctx, R.anim.ani_circle);
        image.startAnimation(ani);
        text.setText(msg);

        Dialog dialog = new Dialog(ctx, R.style.dialog_loading);
        dialog.setCancelable(true);
        dialog.setContentView(layout, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        dialog.show();
        return dialog;
    }
}
