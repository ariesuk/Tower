package com.example.a.tower;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.VersionInfo;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.GroundOverlayOptions;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;

import java.util.ArrayList;

/**
 * Created by a on 2016/4/1.
 */
public class FragmentBaseMap extends Fragment {
    private static final String LTAG = MainActivity.class.getSimpleName();
    private MapView mMapView;
    private BaiduMap mBaiduMap;

    boolean isFirstLoc = true; // 是否首次定位
    LocationClient mLocClient;
    public MyLocationListenner myListener = new MyLocationListenner();
    private MyLocationConfiguration.LocationMode mCurrentMode;

    // draw fake base station
    private Marker mMarkerA;
    private Marker mMarkerB;
    private Marker mMarkerC;
    private Marker mMarkerD;
    private InfoWindow mInfoWindow;

    // 初始化全局 bitmap 信息，不用时及时 recycle
    BitmapDescriptor bdA = BitmapDescriptorFactory
            .fromResource(R.drawable.icon_normal_base_station);
    BitmapDescriptor bdB = BitmapDescriptorFactory
            .fromResource(R.drawable.icon_normal_base_station);
    BitmapDescriptor bdC = BitmapDescriptorFactory
            .fromResource(R.drawable.icon_normal_base_station);
    BitmapDescriptor bdD = BitmapDescriptorFactory
            .fromResource(R.drawable.icon_orange_base_station);
    BitmapDescriptor bd = BitmapDescriptorFactory
            .fromResource(R.drawable.icon_orange_base_station);
    BitmapDescriptor bdGround = BitmapDescriptorFactory
            .fromResource(R.drawable.ground_overlay);
    /**
     * 构造广播监听类，监听 SDK key 验证以及网络异常广播
     */
    public class SDKReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            String s = intent.getAction();
            Log.d(LTAG, "action: " + s);
            TextView text = (TextView) getActivity().findViewById(R.id.map_connect_info);
            text.setTextColor(Color.RED);
            if (s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR)) {
                text.setText("key 验证出错! 请在 AndroidManifest.xml 文件中检查 key 设置");
            } else if (s
                    .equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_OK)) {
                text.setText("key 验证成功! 功能可以正常使用");
                text.setTextColor(Color.GREEN);
            }
            else if (s
                    .equals(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR)) {
                text.setText("网络出错");
            }
        }
    }

    private SDKReceiver mReceiver;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(getClass().getName(), new Exception().getStackTrace()[0].getMethodName());
        View v = inflater.inflate(R.layout.fragment_base_map,container,false);
        return v;
    }

    @Override
    public void onStart() {
        Log.d(getClass().getName(), "onStart");
        TextView text = (TextView) getActivity().findViewById(R.id.map_connect_info);
        text.setTextColor(Color.GREEN);
        text.setText("欢迎使用百度地图Android SDK v" + VersionInfo.getApiVersion());

        // 注册 SDK 广播监听者
        IntentFilter iFilter = new IntentFilter();
        iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_OK);
        iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
        iFilter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
        mReceiver = new SDKReceiver();
        getActivity().registerReceiver(mReceiver, iFilter);

        //SDKInitializer.initialize(getApplicationContext());

        mMapView = (MapView) getActivity().findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        startMyLoc();
        super.onStart();
    }


    public void  startMyLoc() {
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        // 定位初始化
        mLocClient = new LocationClient(getActivity());
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);
        mLocClient.setLocOption(option);
        mLocClient.start();
    }


    /**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) {
                return;
            }
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                            // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);
            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll).zoom(16.0f);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
                initOverlay(ll);
            }
        }

        public void onReceivePoi(BDLocation poiLocation) {
        }
    }

    public void initOverlay(LatLng myLL) {
        // add marker overlay
        LatLng llA = new LatLng(myLL.latitude + 0.003, myLL.longitude + 0.003);
        LatLng llB = new LatLng(myLL.latitude - 0.004, myLL.longitude + 0.004);
        LatLng llC = new LatLng(myLL.latitude + 0.006, myLL.longitude - 0.006);
        LatLng llD = new LatLng(myLL.latitude - 0.005, myLL.longitude - 0.005);

        MarkerOptions ooA = new MarkerOptions().position(llA).icon(bdA)
                .zIndex(9).draggable(true);
        if (true) {
            //掉下动画
            ooA.animateType(MarkerOptions.MarkerAnimateType.drop);
        }
        mMarkerA = (Marker) (mBaiduMap.addOverlay(ooA));
        MarkerOptions ooB = new MarkerOptions().position(llB).icon(bdB)
                .zIndex(5);
        if (true) {
            //掉下动画
            ooB.animateType(MarkerOptions.MarkerAnimateType.drop);
        }
        mMarkerB = (Marker) (mBaiduMap.addOverlay(ooB));
        MarkerOptions ooC = new MarkerOptions().position(llC).icon(bdC)
                .perspective(false).anchor(0.5f, 0.5f).rotate(30).zIndex(7);
        if (true) {
            //生长动画
            ooC.animateType(MarkerOptions.MarkerAnimateType.grow);
        }
        mMarkerC = (Marker) (mBaiduMap.addOverlay(ooC));
        ArrayList<BitmapDescriptor> giflist = new ArrayList<BitmapDescriptor>();
        giflist.add(bdA);
        giflist.add(bdB);
        giflist.add(bdD);
        MarkerOptions ooD = new MarkerOptions().position(llD).icons(giflist)
                .zIndex(0).period(10);
        if (true) {
            //生长动画
            ooD.animateType(MarkerOptions.MarkerAnimateType.grow);
        }
        mMarkerD = (Marker) (mBaiduMap.addOverlay(ooD));

        // add ground overlay
        LatLng southwest = new LatLng(myLL.latitude - 0.0015, myLL.longitude + 0.0015);
        LatLng northeast = new LatLng(myLL.latitude - 0.0155, myLL.longitude + 0.0155);
        LatLngBounds bounds = new LatLngBounds.Builder().include(northeast)
                .include(southwest).build();

        OverlayOptions ooGround = new GroundOverlayOptions()
                .positionFromBounds(bounds).image(bdGround).transparency(0.8f);
        mBaiduMap.addOverlay(ooGround);

        MapStatusUpdate u = MapStatusUpdateFactory
                .newLatLng(bounds.getCenter());
        mBaiduMap.setMapStatus(u);
    }

    @Override
    public void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    public void onResume() {
        mMapView.onResume();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        // 退出时销毁定位
        mLocClient.stop();
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
        // 取消监听 SDK 广播
        getActivity().unregisterReceiver(mReceiver);
        super.onDestroy();
    }
}