package com.miniplat._activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.cameraview.AspectRatio;
import com.google.android.cameraview.CameraView;
import com.miniplat._fragment.ConfirmFragment;
import com.miniplat._fragment.RatioFragment;
import com.miniplat.app.AppCtx;
import com.miniplat.ui.BaseActivity;
import com.miniplat.util.ToastUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;

public class CaptureActivity extends BaseActivity implements View.OnClickListener,
        ActivityCompat.OnRequestPermissionsResultCallback,
        RatioFragment.Listener {
    public final static int PHOTO_REQUEST = 100;
    public final static int ALBUM_REQUEST = 110;
    public final static int VIDEO_REQUEST = 120;

    public static void showPhoto(Fragment ctx) {
        Intent i = new Intent(ctx.getActivity(), CaptureActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        ctx.startActivityForResult(i, PHOTO_REQUEST);
    }

    public static void showVideo() {
        Activity ctx = AppCtx.getContext();
        Intent i = new Intent(ctx, CaptureActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        ctx.startActivityForResult(i, VIDEO_REQUEST);
    }

    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private static final String FRAGMENT_PERMISSION = "permission";
    private static final String FRAGMENT_PICTURE = "picture";
    private static final int[] FLASH_OPTIONS = {
            CameraView.FLASH_AUTO,
            CameraView.FLASH_OFF,
            CameraView.FLASH_ON,
    };
    private static final int[] FLASH_ICONS = {
            R.drawable.ic_flash_auto,
            R.drawable.ic_flash_off,
            R.drawable.ic_flash_on,
    };
    private static final int[] FLASH_TITLES = {
            R.string.flash_auto,
            R.string.flash_off,
            R.string.flash_on,
    };

    private int currentFlash;
    private CameraView cameraView;
    private CameraView.Callback callback = new CameraView.Callback() {
        @Override
        public void onCameraError(CameraView cameraView) {
        }
        @Override
        public void onCameraOpened(CameraView cameraView) {
        }
        @Override
        public void onCameraClosed(CameraView cameraView) {
        }
        @Override
        public void onPictureTaken(CameraView cameraView, final byte[] data) {
            ToastUtil.success("拍照完成");

            Bitmap photo = BitmapFactory.decodeByteArray(data, 0, data.length);
            Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), photo, null,null));

            // 此处返回
            Intent result = new Intent();
            result.setData(uri);
            setResult(RESULT_OK, result);
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);

        cameraView = (CameraView) findViewById(R.id.camera_view);
        if (cameraView != null) cameraView.addCallback(callback);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.take_picture);
        if (fab != null) fab.setOnClickListener(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.setDisplayShowTitleEnabled(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.take_picture:
                if (cameraView != null) {
                    cameraView.takePicture();
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            try {
                cameraView.start();
            }
            catch (Exception e) {
                ToastUtil.warn("启动拍照失败");
            }
        }
        else if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            ConfirmFragment.newInstance(R.string.camera_confirm,
                    new String[] { Manifest.permission.CAMERA },
                    REQUEST_CAMERA_PERMISSION, R.string.camera_granted)
                    .show(getSupportFragmentManager(), FRAGMENT_PERMISSION);
        }
        else {
            ActivityCompat.requestPermissions(this,
                    new String[] { Manifest.permission.CAMERA },
                    REQUEST_CAMERA_PERMISSION);
        }
    }

    @Override
    protected void onPause() {
        try {
            cameraView.stop();
        }
        catch (Exception e) {
            //error
        }

        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
        @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA_PERMISSION:
                if (permissions.length != 1 || grantResults.length != 1) {
                    throw new RuntimeException("Error on requesting camera permission.");
                }
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, R.string.camera_granted, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_capture, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.aspect_ratio:
                FragmentManager fragmentManager = getSupportFragmentManager();
                if (cameraView != null && fragmentManager.findFragmentByTag(FRAGMENT_PERMISSION) == null) {
                    final Set<AspectRatio> ratios = cameraView.getSupportedAspectRatios();
                    final AspectRatio currentRatio = cameraView.getAspectRatio();
                    RatioFragment.newInstance(ratios, currentRatio).show(fragmentManager, FRAGMENT_PERMISSION);
                }
                return true;

            case R.id.switch_flash:
                if (cameraView != null) {
                    currentFlash = (currentFlash + 1) % FLASH_OPTIONS.length;
                    item.setTitle(FLASH_TITLES[currentFlash]);
                    item.setIcon(FLASH_ICONS[currentFlash]);
                    cameraView.setFlash(FLASH_OPTIONS[currentFlash]);
                }
                return true;

            case R.id.switch_camera:
                if (cameraView != null) {
                    int facing = cameraView.getFacing();
                    cameraView.setFacing(facing == CameraView.FACING_FRONT ? CameraView.FACING_BACK : CameraView.FACING_FRONT);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAspectRatioSelected(@NonNull AspectRatio ratio) {
        if (cameraView != null) {
            ToastUtil.success(ratio.toString());
            cameraView.setAspectRatio(ratio);
        }
    }
}
