package com.miniplat._activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import com.loopj.android.http.RequestParams;
import com.miniplat.app.AppCtx;
import com.miniplat.auth.Membership;
import com.miniplat.core.Permission;
import com.miniplat.core.Version;
import com.miniplat.http.Http;
import com.miniplat.ui.BaseActivity;
import com.miniplat.util.ToastUtil;
import com.miniplat.widget.Updater;

public class WelcomeActivity extends BaseActivity {
    private static final int REQUEST_CODE = 1;
    private boolean checkVersion = false;
    private Version version;
    private Permission checker;

    // 所需的全部权限
    private String[] getPermissions() {
        // SDK 23+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return new String[] {
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.REQUEST_INSTALL_PACKAGES
            };
        }
        else {
            return new String[] {
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            };
        }
    }

    public static void show() {
        Context ctx = AppCtx.getContext();
        Intent i = new Intent(ctx, WelcomeActivity.class);
        ctx.startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        checker = new Permission(this);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // 版本升级
        if (!checkVersion) {
            sendVersionRequest();
            checkVersion = true;
        }
        else {
            enterApplication();
        }
    }

    private void enterApplication() {
        delayedEnter(1000);
    }

    private void sendVersionRequest() {
        Http.showLoading = false;
        RequestParams params = new RequestParams();
        params.put("clientId", AppCtx.APP_ID);
        Http.getRes("api/client/getversion", params, new com.miniplat.http.Handler<Version>() {
            @Override
            public Class<Version> getClazz() { return Version.class; }
            @Override
            public void success(Version res) {
                if (res != null) {
                    version = res;
                    checkVersion();
                }
                else {
                    enterApplication();
                }
            }
            @Override
            public void failure() {
                enterApplication();
            }
        });
    }

    private void checkVersion() {
        String ver = BuildConfig.VERSION_NAME;
        if (version.adrVersion.compareTo(ver) > 0) {
            showUpdateDialog();
        }
        else {
            ToastUtil.success("已是最新版本");
            enterApplication();
        }
    }

    private void showUpdateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.Theme_AppCompat_Light_Dialog_Alert);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setTitle("发现新版本");
        builder.setMessage(version.adrChangeLog);

        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();

                String[] permissions = getPermissions();
                if (checker.lacksPermissions(permissions)) {
                    PermissionActivity.showResult(WelcomeActivity.this, REQUEST_CODE, permissions);
                }
                else {
                    Updater updateManager = new Updater(WelcomeActivity.this);
                    updateManager.DownloadFile(version.adrPackage, version.adrForce);
                }
            }
        });
        if (!version.adrForce) {
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                    enterApplication();
                }
            });
        }
        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == PermissionActivity.PERMISSIONS_DENIED) {
            ToastUtil.warn("请到设置里打开相应权限");
            enterApplication();
        }
        else if (requestCode == REQUEST_CODE && resultCode == PermissionActivity.PERMISSIONS_GRANTED) {
            Updater updateManager = new Updater(WelcomeActivity.this);
            updateManager.DownloadFile(version.adrPackage, version.adrForce);
        }
    }

    private final Handler handlers = new Handler();
    private final Runnable enterRunnable = new Runnable() {
        @Override
        public void run() {
            Http.showLoading = true;
            if (Membership.getLogined()) {
                MainTabActivity.show();
            }
            else {
                LoginActivity.show();
            }
        }
    };

    private void delayedEnter(int delayMillis) {
        handlers.postDelayed(enterRunnable, delayMillis);
    }
}
