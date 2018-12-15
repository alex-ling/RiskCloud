package com.miniplat.util;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.miniplat.app.AppCtx;

public class AnimUtil {
    public static void fadeIn(View v) {
        Context ctx = AppCtx.getContext();
        Animation fadeIn = AnimationUtils.loadAnimation(ctx, android.R.anim.fade_in);
        v.startAnimation(fadeIn);
        v.setVisibility(View.VISIBLE);
    }

    public static void fadeOut(View v) {
        Context ctx = AppCtx.getContext();
        Animation fadeIn = AnimationUtils.loadAnimation(ctx, android.R.anim.fade_out);
        v.startAnimation(fadeIn);
        v.setVisibility(View.GONE);
    }
}
