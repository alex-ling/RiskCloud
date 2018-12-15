package com.miniplat._fragment;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.miniplat._activity.R;
import com.miniplat.app.AppCtx;
import com.miniplat.business.RiskEvent;
import com.miniplat.business.RiskFactory;
import com.miniplat.business.RiskMarker;
import com.miniplat.core.IListHandler;
import com.miniplat.http.Http;
import com.miniplat.ui.BaseFragment;
import com.miniplat.util.ImageUtil;

import java.util.HashMap;
import java.util.List;

public class AMapFragment extends BaseFragment
        implements AMapLocationListener,
        AMap.InfoWindowAdapter,
        AMap.OnMapClickListener,
        AMap.OnCameraChangeListener,
        AMap.OnMarkerClickListener,
        AMap.OnInfoWindowClickListener,
        AMap.OnMyLocationChangeListener {
    private View layout_view;
    private MapView mapView;
    private AMap aMap;
    private AMapLocationClient locationClient;
    private AMapLocationClientOption locationOption;
    private MyLocationStyle locationStyle;
    private LatLng localPos, centerPos;
    private HashMap<String, RiskMarker> markers = new HashMap<>();
    private RiskMarker curMarker;

    public LatLng getLocalPos() {
        return localPos;
    }

    public LatLng getCenterPos() {
        return centerPos;
    }

    public static AMapFragment newInstance() {
        return new AMapFragment();
    }

    public AMapFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater i, ViewGroup c, Bundle state) {
        if (layout_view == null) {
            layout_view = i.inflate(R.layout.fragment_amap, c, false);
            initView(layout_view, state);
        }
        else {
            if (layout_view.getParent() != null) {
                ((ViewGroup)layout_view.getParent()).removeView(layout_view);
            }
        }
        return layout_view;
    }

    private void initView(View v, Bundle savedInstanceState) {
        mapView = (MapView) v.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);

        if (aMap == null) {
            aMap = mapView.getMap();
            initMap();
        }
    }

    private void initMap() {
        aMap.getUiSettings().setMyLocationButtonEnabled(true);
        aMap.getUiSettings().setScaleControlsEnabled(false);
        aMap.getUiSettings().setZoomControlsEnabled(false);

        // 定位变化时触发
        aMap.setOnMyLocationChangeListener(this);
        // 用户视角、位置发生变化时触发
        aMap.setOnCameraChangeListener(this);
        // 监听Marker
        aMap.setOnMarkerClickListener(this);
        // InfoWindow
        aMap.setInfoWindowAdapter(this);
        aMap.setOnInfoWindowClickListener(this);
        // 地图点击
        aMap.setOnMapClickListener(this);

        locationStyle = new MyLocationStyle();
        // LOCATION_TYPE_SHOW：只定位一次
        // LOCATION_TYPE_LOCATE：定位一次、移动到地图中心点
        // LOCATION_TYPE_FOLLOW：连续定位、移动到地图中心点、蓝点跟随
        // LOCATION_TYPE_MAP_ROTATE：连续定位、移动到地图中心点、地图旋转、蓝点跟随
        // LOCATION_TYPE_LOCATION_ROTATE：连续定位、移动到地图中心点，定位点旋转、蓝点跟随
        // LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER：连续定位、定位点旋转，蓝点跟随
        // LOCATION_TYPE_FOLLOW_NO_CENTER：连续定位、蓝点跟随
        // LOCATION_TYPE_MAP_ROTATE_NO_CENTER：连续定位、地图旋转
        locationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER);
        // 显示圆点与否
        locationStyle.showMyLocation(true);
        // 设置小蓝点的图标
        //locationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.mipmap.lbs_sel));
        // 设置圆形的边框颜色
        locationStyle.strokeColor(Color.argb(0,0,0,0));
        // 设置圆形的填充颜色
        locationStyle.radiusFillColor(Color.argb(0, 0, 0, 0));
        // 设置小蓝点的锚点
        //locationStyle.anchor(0, 1);
        // 设置圆形的边框粗细
        //locationStyle.strokeWidth(1.0f);
        // 频次
        locationStyle.interval(5000);
        // 设置参数
        aMap.setMyLocationStyle(locationStyle);
        // 显示定位层并触发定位，默认是false
        aMap.setMyLocationEnabled(true);

        /* 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗
           注意设置合适的定位时间的间隔（最小间隔支持为1000ms）
           并且在合适时间调用stopLocation()方法来取消定位请求
           在定位结束后，在合适的生命周期调用onDestroy()方法
           单次定位无需调用stopLocation()方法移除请求，定位sdk内部会移除
         */
        /*locationClient = new AMapLocationClient(AppCtx.getAppContext());
        locationOption = new AMapLocationClientOption();
        // 设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        locationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        // 设置定位间隔,单位毫秒,默认为2000ms
        //locationOption.setInterval(5000);
        locationOption.setOnceLocation(true);
        // 获取最近3s内精度最高的一次定位结果
        //locationOption.setOnceLocationLatest(true);
        // 设置定位参数
        locationClient.setLocationOption(locationOption);
        // 设置定位监听
        locationClient.setLocationListener(this);
        // 启动定位
        locationClient.startLocation();*/

        // 设置锚点
        //aMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f).position(localPos));
    }

    public void onMyLocationChange(android.location.Location loc) {
        if (loc != null) {
            boolean firstLocation = localPos == null;
            localPos = new LatLng(loc.getLatitude(), loc.getLongitude());
            if (firstLocation) {
                aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(localPos, 18));
            }
        }
    }

    @Override
    public void onLocationChanged(AMapLocation loc) {
        /*if (loc != null && loc.getErrorCode() == 0) {
            if (localPos == null) {
                localPos = new LatLng(loc.getLatitude(), loc.getLongitude());
                aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(localPos, 18));
            }
        }
        else {
            String errText = "定位失败," + loc.getErrorCode() + ": " + loc.getErrorInfo();
            ToastUtil.warn(errText);
        }*/
    }

    @Override
    public void onCameraChange(CameraPosition position) {
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        RiskMarker risk = markers.get(marker.getObject());
        if (curMarker == null || curMarker != risk) {
            // 更换图标
            setMarkerOption(risk, true);
            // 重置上次图标
            if (curMarker != null) {
                setMarkerOption(curMarker, false);
            }
            curMarker = risk;
            marker.showInfoWindow();
        }
        else if (curMarker.equals(risk)) {
            setMarkerOption(curMarker, false);
            curMarker.getMarker().hideInfoWindow();
            curMarker = null;
        }
        return true;
    }

    private void setMarkerOption(RiskMarker risk, boolean showLarge) {
        MarkerOptions option = risk.getMarker().getOptions();
        option.getIcon().recycle();
        View v = getMarkerView(risk.getEvent(), showLarge);
        option.icon(BitmapDescriptorFactory.fromView(v));
        risk.getMarker().setMarkerOptions(option);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        Context ctx = AppCtx.getContext();
        LayoutInflater i = LayoutInflater.from(ctx);
        View v = i.inflate(R.layout.view_marker_info, null);
        render(marker, v);
        return v;
    }

    public void render(Marker marker, View view) {
        RiskMarker risk = markers.get(marker.getObject());
        TextView title = view.findViewById(R.id.lbl_title);
        ImageView photo = view.findViewById(R.id.img_title);
        title.setText(marker.getTitle());
        String imgUrl = risk.getEvent().attach.split(",")[0];
        ImageUtil.loadUrl(photo, imgUrl);
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    @Override
    public void onMapClick(LatLng latLng) {
        if (curMarker != null) {
            setMarkerOption(curMarker, false);
            curMarker.getMarker().hideInfoWindow();
            curMarker = null;
        }
    }

    @Override
    public void onInfoWindowClick(Marker marker) {

    }

    @Override
    public void onCameraChangeFinish(CameraPosition position) {
        centerPos = position.target;
        loadRiskEvents();
    }

    public void loadRiskEvents() {
        Http.showLoading = false;
        RiskFactory.getRiskEvents(centerPos.longitude, centerPos.latitude, new IListHandler<RiskEvent>() {
            @Override
            public void callback(List<RiskEvent> events) {
                for (int i = 0; i < events.size(); i++) {
                    RiskEvent event = events.get(i);
                    if (!markers.containsKey(event.id)) {
                        drawEventMarker(events.get(i));
                    }
                }
            }
        });
        Http.showLoading = true;
    }

    public void goEventMarker(String eventId, double lng, double lat) {
        LatLng point = new LatLng(lat, lng);
        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 18));

        if (curMarker != null) {
            setMarkerOption(curMarker, false);
            curMarker.getMarker().hideInfoWindow();
            curMarker = null;
        }

        RiskMarker risk = markers.get(eventId);
        if (risk != null) {
            // 更换图标
            setMarkerOption(risk, true);
            curMarker = risk;
            risk.getMarker().showInfoWindow();
        }
    }

    private void drawEventMarker(RiskEvent event) {
        if (event.latitude > 0 && event.longitude > 0) {
            LatLng pos = new LatLng(event.latitude, event.longitude);

            MarkerOptions markerOption = new MarkerOptions();
            markerOption.position(pos);
            markerOption.title(event.date_start+"："+event.name);
            // 设置Marker可拖动
            markerOption.draggable(false);
            // 将Marker设置为贴地显示
            markerOption.setFlat(true);
            // 设置图标
            View v = getMarkerView(event, false);
            markerOption.icon(BitmapDescriptorFactory.fromView(v));

            // 设置Marker到地图上
            Marker marker = aMap.addMarker(markerOption);
            marker.setObject(event.id);
            markers.put(event.id, new RiskMarker(event, marker));
        }
    }

    private View getMarkerView(RiskEvent event, boolean large) {
        Context ctx = AppCtx.getContext();
        LayoutInflater i = LayoutInflater.from(ctx);
        View v = i.inflate(large ? R.layout.view_marker_large
                : R.layout.view_marker_normal, null);
        ImageView img = v.findViewById(R.id.img_marker);

        int resId;
        boolean repair = event.state.equals("2");
        switch (event.level) {
            case "1":
                resId = repair ? R.mipmap.lbs1_r : R.mipmap.lbs1;
                break;
            case "2":
                resId = repair ? R.mipmap.lbs2_r : R.mipmap.lbs2;
                break;
            case "3":
                resId = repair ? R.mipmap.lbs3_r : R.mipmap.lbs3;
                break;
            default:
                resId = repair ? R.mipmap.lbs4_r : R.mipmap.lbs4;
                break;
        }
        img.setImageResource(resId);
        return v;
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // 为Fragement添加缓存处理，此外不销毁内部View
        // 此处会出现null值情况，转为在onParentDestroy()调用
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 为Fragement添加缓存处理，此外不销毁内部View
        // 此处会出现null值情况，转为在onParentDestroy()调用
    }

    @Override
    public void onParentDestroy() {
        // 当父级Activity销毁时调用
        mapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
}
