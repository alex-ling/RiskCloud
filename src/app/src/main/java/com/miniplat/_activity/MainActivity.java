package com.miniplat._activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;

import com.miniplat.app.AppCtx;
import com.miniplat.ui.BaseActivity;

public class MainActivity extends BaseActivity {

    public static void show() {
        Context ctx = AppCtx.getContext();
        Intent i = new Intent(ctx, MainActivity.class);
        ctx.startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            AppCtx.exitApp();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
