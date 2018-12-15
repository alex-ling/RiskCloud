package com.miniplat._activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.miniplat.app.AppCtx;
import com.miniplat.auth.Membership;
import com.miniplat.core.IHandler;
import com.miniplat.ui.BaseActivity;
import com.miniplat.util.ToastUtil;

public class LoginActivity extends BaseActivity implements View.OnClickListener {
    private EditText txtUid, txtPwd;
    private TextView btnLogin, btnForget, btnRegist;

    public static void show() {
        Context ctx = AppCtx.getContext();
        Intent i = new Intent(ctx, LoginActivity.class);
        ctx.startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        txtUid = (EditText)findViewById(R.id.txt_uid);
        txtPwd = (EditText)findViewById(R.id.txt_pwd);
        btnLogin = (TextView)findViewById(R.id.btn_login);
        btnForget = (TextView)findViewById(R.id.btn_forget);
        btnRegist = (TextView)findViewById(R.id.btn_regist);

        btnLogin.setOnClickListener(this);
        btnForget.setOnClickListener(this);
        btnRegist.setOnClickListener(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            AppCtx.exitApp();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                onLogin();
                break;

            case R.id.btn_regist:
                RegistActivity.show();
                break;

            case R.id.btn_forget:
                ForgetActivity.show();
                break;
        }
    }

    private void onLogin() {
        String uid = txtUid.getText().toString();
        String pwd = txtPwd.getText().toString();

        if (TextUtils.isEmpty(uid)) {
            ToastUtil.warn("手机号不能为空"); return;
        }
        if (uid.length() != 11) {
            ToastUtil.warn("请输入正确的手机号"); return;
        }
        if (TextUtils.isEmpty(pwd)) {
            ToastUtil.warn("密码不能为空"); return;
        }
        if (pwd.trim().length() < 4 || pwd.trim().length() > 20) {
            ToastUtil.warn("请输入4-20位密码"); return;
        }

        Membership.login(uid, pwd, new IHandler() {
            @Override
            public void callback() {
                MainTabActivity.show();
            }
        });
    }
}