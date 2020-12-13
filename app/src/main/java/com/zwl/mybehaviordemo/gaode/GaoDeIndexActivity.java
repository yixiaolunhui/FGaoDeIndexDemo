package com.zwl.mybehaviordemo.gaode;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.amap.api.maps.AMap;
import com.amap.api.maps.TextureMapView;
import com.gyf.immersionbar.ImmersionBar;
import com.zwl.mybehaviordemo.R;

/**
 * 高德地图首页
 */
public class GaoDeIndexActivity extends AppCompatActivity {

    private static final String TAG = "GaoDeIndexActivity";
    private TextureMapView mapView;
    private AMap aMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("高德首页");
        setContentView(R.layout.activity_gaode_index);
        ImmersionBar.with(this)
                .statusBarDarkFont(true)
                .fitsSystemWindows(false)
                .init();
        mapView = findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        //初始化地图控制器对象
        if (aMap == null) {
            aMap = mapView.getMap();
        }


        ViewGroup bottom_sheet = findViewById(R.id.bottom_sheet);
        GaoDeBottomSheetBehavior behavior = GaoDeBottomSheetBehavior.from(bottom_sheet);
        behavior.setBottomSheetCallback(new GaoDeBottomSheetBehavior.BottomSheetCallback() {

            @SuppressLint("WrongConstant")
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case 1:
                        //过渡状态此时用户正在向上或者向下拖动bottom sheet
                        Log.e(TAG, "====用户正在向上或者向下拖动");
                        break;
                    case 2:
                        // 视图从脱离手指自由滑动到最终停下的这一小段时间
                        Log.e(TAG, "====视图从脱离手指自由滑动到最终停下的这一小段时间");
                        break;
                    case 3:
                        //处于完全展开的状态
                        Log.e(TAG, "====处于完全展开的状态");
                        break;
                    case 4:
                        //默认的折叠状态
                        Log.e(TAG, "====默认的折叠状态");
                        break;
                    case 5:
                        //下滑动完全隐藏 bottom sheet
                        Log.e(TAG, "====下滑动完全隐藏");
                        break;
                    case 6:
                        //下滑动完全隐藏 bottom sheet
                        Log.e(TAG, "====中间位置");
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                Log.e(TAG, "===" + slideOffset);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mapView != null) {
            mapView.onResume();
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mapView != null) {
            mapView.onSaveInstanceState(outState);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mapView != null) {
            mapView.onDestroy();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mapView != null) {
            mapView.onPause();
        }
    }
}