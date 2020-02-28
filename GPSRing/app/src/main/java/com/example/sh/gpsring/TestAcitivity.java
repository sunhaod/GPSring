package com.example.sh.gpsring;

import android.support.v7.app.AppCompatActivity;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class TestAcitivity extends AppCompatActivity {

    private LocationManager lm;
    private TextView tv_show;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_acitivity);
        Log.e("hh","1");
        tv_show = findViewById(R.id.tv_show);
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Log.e("hh","2");
        if (!isGpsAble(lm)) {
            Toast.makeText(TestAcitivity.this, "请打开GPS~", Toast.LENGTH_SHORT).show();
            openGPS2();
        }
        Log.e("hh","3");
        //从GPS获取最近的定位信息
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        if(lm.getLastKnownLocation(LocationManager.GPS_PROVIDER) == null) {
            Log.e("hh","fuck");
        }
        Location lc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        Log.e("hh","fuck");
        if(lc == null) {
            Log.e("hh","fuck");
        }
        updateShow(lc);
        //设置间隔两秒获得一次GPS定位信息
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 8, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                // 当GPS定位信息发生改变时，更新定位
                updateShow(location);
            }
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }
            @SuppressLint("MissingPermission")
            @Override
            public void onProviderEnabled(String provider) {
                updateShow(lm.getLastKnownLocation(provider));
            }
            @Override
            public void onProviderDisabled(String provider) {
                updateShow(null);
            }
        });
    }

    private boolean isGpsAble(LocationManager lm){
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER)? true:false;
    }


    //打开设置页面让用户自己设置
    private void openGPS2() {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivityForResult(intent, 0);
    }

    //定义一个更新显示的方法
    private void updateShow(Location location) {
        if (location != null) {
            StringBuilder sb = new StringBuilder();
            sb.append("当前的位置信息：\n");
            sb.append("经度：" + location.getLongitude() + "\n");
            sb.append("纬度：" + location.getLatitude() + "\n");
            sb.append("高度：" + location.getAltitude() + "\n");
            sb.append("速度：" + location.getSpeed() + "\n");
            sb.append("方向：" + location.getBearing() + "\n");
            sb.append("定位精度：" + location.getAccuracy() + "\n");
            tv_show.setText(sb.toString());
        } else tv_show.setText("");
    }
}
