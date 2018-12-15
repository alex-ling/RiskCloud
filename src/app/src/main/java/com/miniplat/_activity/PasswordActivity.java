package com.miniplat._activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.loopj.android.http.RequestParams;
import com.miniplat.app.AppCtx;
import com.miniplat.http.Http;
import com.miniplat.http.JsonHandler;
import com.miniplat.ui.BaseActivity;
import com.miniplat.util.NetworkUtil;
import com.miniplat.util.ToastUtil;
import com.miniplat.widget.TitleBar;

public class PasswordActivity extends BaseActivity implements View.OnClickListener {
    private TitleBar titleBar;
    private EditText txtOldPwd, txtPwd, txtPwd2;
    private TextView btnOk;

    public static void show() {
        Context ctx = AppCtx.getContext();
        Intent i = new Intent(ctx, PasswordActivity.class);
        ctx.startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        titleBar = (TitleBar) findViewById(R.id.title_bar);
        titleBar.setLeftOnClick(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        txtOldPwd = (EditText) findViewById(R.id.txt_oldpwd);
        txtPwd = (EditText) findViewById(R.id.txt_pwd);
        txtPwd2 = (EditText) findViewById(R.id.txt_pwd2);
        btnOk = (TextView) findViewById(R.id.btn_ok);

        btnOk.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_ok:
                sendOkRequest();
                break;
        }
    }

    private void sendOkRequest() {
        String oldPwd = txtOldPwd.getText().toString();
        String pwd = txtPwd.getText().toString();
        String pwd2 = txtPwd2.getText().toString();

        if (TextUtils.isEmpty(oldPwd)) {
            ToastUtil.warn("原密码不能为空");
        }
        else if (oldPwd.trim().length() < 4 || oldPwd.trim().length() > 20) {
            ToastUtil.warn("请输入4-20位原密码");
        }
        else if (TextUtils.isEmpty(pwd)) {
            ToastUtil.warn("新密码不能为空");
        }
        else if (pwd.trim().length() < 4 || pwd.trim().length() > 20) {
            ToastUtil.warn("请输入4-20位新密码");
        }
        else if (!pwd.equals(pwd2)) {
            ToastUtil.warn("两次输入密码不一致");
        }
        else if (!NetworkUtil.isNetworkConnection()) {
            ToastUtil.warn("请检查当前网络是否可用");
        }
        else {
            // 设置不能重复点击
            btnOk.setClickable(false);

            // 发送获取验证码请求
            RequestParams params = new RequestParams();
            params.put("pwd", oldPwd);
            params.put("newpwd", pwd);
            Http.postResJson("api/auth/putpwd", params, new JsonHandler() {
                @Override
                public void success(JSONObject res) {
                    ToastUtil.success("密码修改成功");
                    finish();
                }
                @Override
                public void failure() {
                    btnOk.setClickable(true);
                }
            }, true);
        }
    }
}