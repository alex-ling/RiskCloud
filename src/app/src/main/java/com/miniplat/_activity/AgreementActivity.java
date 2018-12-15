package com.miniplat._activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.miniplat.app.AppCtx;
import com.miniplat.ui.BaseActivity;
import com.miniplat.widget.TitleBar;

public class AgreementActivity extends BaseActivity {
    private TitleBar titleBar;

    public static void show() {
        Context ctx = AppCtx.getContext();
        Intent i = new Intent(ctx, AgreementActivity.class);
        ctx.startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agreement);

        titleBar = (TitleBar) findViewById(R.id.title_bar);
        titleBar.setLeftOnClick(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
