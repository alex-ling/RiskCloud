package com.miniplat._activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

import com.amap.api.maps.model.LatLng;
import com.jpeng.jptabbar.BadgeDismissListener;
import com.jpeng.jptabbar.JPTabBar;
import com.jpeng.jptabbar.OnTabSelectListener;
import com.jpeng.jptabbar.anno.NorIcons;
import com.jpeng.jptabbar.anno.SeleIcons;
import com.jpeng.jptabbar.anno.Titles;
import com.miniplat._fragment.AMapFragment;
import com.miniplat._fragment.WebFragment;
import com.miniplat.app.AppCtx;
import com.miniplat.core.Permission;
import com.miniplat.ui.BaseActivity;
import com.miniplat.util.ToastUtil;
import com.miniplat.widget.SlideViewPager;
import com.miniplat.ui.PageAdapter;

public class MainTabActivity extends BaseActivity implements BadgeDismissListener, OnTabSelectListener {
    private final static int REQUEST_ADD = 1;
    private static final int REQUEST_AUTH = 2;
    private Permission checker;
    private static final String[] PERMISSIONS = new String[] {
        Manifest.permission.INTERNET,
        Manifest.permission.ACCESS_NETWORK_STATE,
        Manifest.permission.ACCESS_WIFI_STATE,
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private SlideViewPager viewPager;
    private PageAdapter pageAdapter;
    private JPTabBar tabBar;
    private ImageView btnAdd;
    private AMapFragment mapView;
    private WebFragment riskView;
    private WebFragment findView;
    private WebFragment userView;

    @Titles
    private static final String[] titles = {
            "主页",
            "风险",
            "发现",
            "我"
    };
    @SeleIcons
    private static final int[] selectIcons = {
            R.mipmap.home_sel,
            R.mipmap.lbs_sel,
            R.mipmap.eye_sel,
            R.mipmap.person_sel
    };
    @NorIcons
    private static final int[] normalIcons = {
            R.mipmap.home,
            R.mipmap.lbs,
            R.mipmap.eye,
            R.mipmap.person
    };

    public static void show() {
        Context ctx = AppCtx.getContext();
        Intent i = new Intent(ctx, MainTabActivity.class);
        ctx.startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tab);

        // 检查所需权限
        checkPermissions();

        viewPager = (SlideViewPager) findViewById(R.id.view_pager);
        tabBar = (JPTabBar) findViewById(R.id.tab_bar);

        viewPager.setSlide(false);
        tabBar.setGradientEnable(true);
        tabBar.setPageAnimateEnable(true);
        tabBar.setDismissListener(this);
        tabBar.setTabListener(this);

        mapView = AMapFragment.newInstance();
        riskView = WebFragment.newInstance("风险","webapp/risk/index");
        findView = WebFragment.newInstance("发现","webapp/risk/find");
        userView = WebFragment.newInstance("我","webapp/risk/user");

        pageAdapter = new PageAdapter(getSupportFragmentManager());
        pageAdapter.addFragment(mapView);
        pageAdapter.addFragment(riskView);
        pageAdapter.addFragment(findView);
        pageAdapter.addFragment(userView);
        viewPager.setAdapter(pageAdapter);
        tabBar.setContainer(viewPager);

        btnAdd = (ImageView)tabBar.getMiddleView().findViewById(R.id.btn_image);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LatLng loc = mapView.getLocalPos();
                if (loc != null) {
                    AddActivity.showDialog(loc, REQUEST_ADD);
                }
                else {
                    ToastUtil.warn("请打开GPS获得定位信息");
                }
            }
        });
    }

    private void checkPermissions() {
        checker = new Permission(this);
        if (checker.lacksPermissions(PERMISSIONS)) {
            PermissionActivity.showResult(MainTabActivity.this, REQUEST_AUTH, PERMISSIONS);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_AUTH && resultCode == PermissionActivity.PERMISSIONS_DENIED) {
            ToastUtil.warn("请到设置里打开相应权限");
        }
        else if (requestCode == REQUEST_ADD && resultCode == AddActivity.SUCCESS) {
            // 添加数据成功，刷新地图
            mapView.loadRiskEvents();
            // 刷新数据
            riskView.loadWebUrl();
        }
    }

    public void goEvent(String id, double lng, double lat) {
        tabBar.setSelectTab(0);
        mapView.goEventMarker(id, lng, lat);
    }

    @Override
    public void onDismiss(int position) {

    }

    @Override
    public void onTabSelect(int index) {
        viewPager.setCurrentItem(index);
        if (index == 0) {
            mapView.loadRiskEvents();
        }
    }

    @Override
    public boolean onInterruptSelect(int index) {
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // 销毁子Fragement资源
        pageAdapter.onSubDestroy();
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
