package com.miniplat._activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.loopj.android.http.RequestParams;
import com.miniplat.app.AppCtx;
import com.miniplat.http.Http;
import com.miniplat.http.JsonHandler;
import com.miniplat.ui.BaseActivity;
import com.miniplat.util.ImageUtil;
import com.miniplat.util.NetworkUtil;
import com.miniplat.util.ToastUtil;
import com.miniplat.widget.TitleBar;

public class ForgetActivity extends BaseActivity implements View.OnClickListener {
    private TitleBar titleBar;
    private EditText txtUid, txtValid, txtSms, txtPwd, txtPwd2;
    private TextView btnSms, btnOk;
    private ImageView imgValid;
    private String valid_type;

    public static void show() {
        Context ctx = AppCtx.getContext();
        Intent i = new Intent(ctx, ForgetActivity.class);
        ctx.startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget);

        titleBar = (TitleBar) findViewById(R.id.title_bar);
        titleBar.setLeftOnClick(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        txtUid = (EditText) findViewById(R.id.txt_uid);
        txtValid = (EditText) findViewById(R.id.txt_valid);
        txtSms = (EditText) findViewById(R.id.txt_sms);
        txtPwd = (EditText) findViewById(R.id.txt_pwd);
        txtPwd2 = (EditText) findViewById(R.id.txt_pwd2);
        btnSms = (TextView) findViewById(R.id.btn_sms);
        btnOk = (TextView) findViewById(R.id.btn_ok);
        imgValid = (ImageView) findViewById(R.id.img_valid);

        btnSms.setOnClickListener(this);
        btnOk.setOnClickListener(this);
        imgValid.setOnClickListener(this);

        getImageValid();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_sms:
                sendSmsRequest();
                break;
            case R.id.btn_ok:
                sendOkRequest();
                break;
            case R.id.img_valid:
                getImageValid();
                break;
        }
    }

    private void getImageValid() {
        valid_type = ImageUtil.loadValid(imgValid);
    }

    private void sendOkRequest() {
        String mobile = txtUid.getText().toString();
        String valid_code = txtSms.getText().toString();
        String pwd = txtPwd.getText().toString();
        String pwd2 = txtPwd2.getText().toString();

        if (TextUtils.isEmpty(mobile)) {
            ToastUtil.warn("手机号不能为空");
        }
        else if (mobile.length() != 11) {
            ToastUtil.warn("请输入正确的手机号");
        }
        else if (valid_code.isEmpty()) {
            ToastUtil.warn("短信验证码不能为空");
        }
        else if (TextUtils.isEmpty(pwd)) {
            ToastUtil.warn("密码不能为空");
        }
        else if (pwd.trim().length() < 4 || pwd.trim().length() > 20) {
            ToastUtil.warn("请输入4-20位密码");
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
            params.put("mobile", mobile);
            params.put("pwd", pwd);
            params.put("valid_code", valid_code);
            Http.postResJson("api/auth/postpwd", params, new JsonHandler() {
                @Override
                public void success(JSONObject res) {
                    ToastUtil.success("密码找回成功");
                    finish();
                }

                @Override
                public void failure() {
                    btnOk.setClickable(true);
                }
            }, true);
        }
    }

    private void sendSmsRequest() {
        String mobile = txtUid.getText().toString();
        String valid_code = txtValid.getText().toString();

        if (TextUtils.isEmpty(mobile)) {
            ToastUtil.warn("手机号不能为空");
        }
        else if (mobile.length() != 11) {
            ToastUtil.warn("请输入正确的手机号");
        }
        else if (valid_code.isEmpty()) {
            ToastUtil.warn("图形验证码不能为空");
        }
        else if (!NetworkUtil.isNetworkConnection()) {
            ToastUtil.warn("请检查当前网络是否可用");
        }
        else {
            // 设置不能重复点击
            btnSms.setClickable(false);

            // 发送获取验证码请求
            RequestParams params = new RequestParams();
            params.put("mobile", mobile);
            params.put("app", "home");
            params.put("ds", "user.backpwd");
            params.put("code", valid_code);
            params.put("type", valid_type);
            Http.getResJson("api/msg/getsms", params, new JsonHandler() {
                @Override
                public void success(JSONObject res) {
                    getImageValid();
                    startCounter();
                }

                @Override
                public void failure() {
                    btnSms.setClickable(true);
                    getImageValid();
                }
            });
        }
    }

    private void startCounter() {
        final CountDownTimer counter = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long mills) {
                long countTime = 60 - mills / 1000;
                btnSms.setText(String.format("重新获取(%d秒)", countTime));
            }

            @Override
            public void onFinish() {
                btnSms.setText("获取验证码");
                btnSms.setClickable(true);
            }
        };
        counter.start();
    }
}
