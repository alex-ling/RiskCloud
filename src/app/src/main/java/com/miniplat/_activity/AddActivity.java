package com.miniplat._activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.amap.api.maps.model.LatLng;
import com.miniplat.app.AppCtx;
import com.miniplat.ui.BaseActivity;

public class AddActivity extends BaseActivity {
    public static final int SUCCESS = 1;

    public static void showDialog(LatLng loc, int REQUEST_CODE) {
        Activity ctx = AppCtx.getContext();
        Intent i = new Intent(ctx, AddActivity.class);
        i.putExtra("lat", loc.latitude);
        i.putExtra("lng", loc.longitude);
        ctx.startActivityForResult(i, REQUEST_CODE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
    }
}
