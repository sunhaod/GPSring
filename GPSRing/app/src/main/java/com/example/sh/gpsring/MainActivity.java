package com.example.sh.gpsring;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private WebView webView;
    private double nowLatitude;//当前的纬度
    private double nowLongitude;//当前的经度
    private String provider;//位置提供器。
    private LocationManager locationManager;//位置服务
    private Location location;
    private EditText editTarget;
    private Button btnShow;
    private Button btnConfirm;
    private boolean locConfirmed = false;
    private String targetLoc;//目标地点名称
    private double targetLatitude;//目标纬度
    private double targetLongitude;//目标经度
    private MediaPlayer mediaPlayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = findViewById(R.id.id_web);
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("https://www.earthol.com/");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                webView.loadUrl("javascript:document.getElementsByTagName('table')[0].parentNode.removeChild(document.getElementsByTagName('table')[0])");
                webView.loadUrl("javascript:document.getElementsByTagName('table')[0].parentNode.removeChild(document.getElementsByTagName('table')[0])");
                webView.loadUrl("javascript:document.getElementsByTagName('center')[0].parentNode.removeChild(document.getElementsByTagName('center')[0])");
                webView.loadUrl("javascript:document.getElementsByTagName('p')[1].parentNode.removeChild(document.getElementsByTagName('p')[1])");
                webView.loadUrl("javascript:document.getElementsByTagName('strong')[0].parentNode.removeChild(document.getElementsByTagName('strong')[0])");
                webView.loadUrl("javascript:document.getElementsByTagName('ins')[0].parentNode.removeChild(document.getElementsByTagName('ins')[0])");
                webView.loadUrl("javascript:document.getElementsByTagName('ins')[0].parentNode.removeChild(document.getElementsByTagName('ins')[0])");

            }
        },1500);

        btnConfirm = findViewById(R.id.btn_confirm);
        btnShow = findViewById(R.id.btn_show);
        editTarget = findViewById(R.id.edit_target);
        btnConfirm.bringToFront();
        btnShow.bringToFront();
        editTarget.bringToFront();
        Log.e("hh","jia zai wan cheng");
        mediaPlayer = MediaPlayer.create(this,R.raw.ring);


        btnShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                targetLoc =  editTarget.getText().toString();
                webView.loadUrl("javascript:gothere(\""+targetLoc+"\")");
                Log.e("hh","javascript:gothere(\""+targetLoc+"\")");

                //我也不知道为什么下面要延时，不延时得到的经纬度就不对，延时一秒就可以得到目标正确经纬度
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        webView.evaluateJavascript("makeurl(\"SH\")", new ValueCallback<String>() {
                            @Override
                            public void onReceiveValue(String s) {
                                targetLongitude = Double.valueOf(s.split("x=")[1].split("&y=")[0]);
                                targetLatitude = Double.valueOf(s.split("y=")[1].split("&zoom")[0]);
                            }
                        });
                    }
                },1000);
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                locConfirmed = true;
                Toast.makeText(MainActivity.this,"已添加"+targetLoc+"为目标点",Toast.LENGTH_SHORT).show();
                Log.e("hh",targetLatitude+","+targetLongitude);
            }
        });

        //判断权限
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //请求权限
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            //有权限
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            //获取所有可用的位置提供器
            List<String> providerList = locationManager.getProviders(true);
            if (providerList.contains(LocationManager.GPS_PROVIDER)) {
                provider = LocationManager.GPS_PROVIDER;
            } else if (providerList.contains(LocationManager.NETWORK_PROVIDER)) {
                provider = LocationManager.NETWORK_PROVIDER;
            } else {
                //当没有可用的位置提供器时，弹出Toast提示用户
                Toast.makeText(this, "No Location provider to use", Toast.LENGTH_SHORT).show();
                return;
            }

            //获取坐标
            Location location = locationManager.getLastKnownLocation(provider);
            Toast.makeText(MainActivity.this,location.getLongitude()+","+location.getLatitude(),Toast.LENGTH_LONG).show();
            locationManager.requestLocationUpdates(provider,800,0,locationListener);
        }

        /*locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);//获得位置服务
        List<String> providerlist = locationManager.getProviders(true);
        if (providerlist.contains(LocationManager.GPS_PROVIDER)) {//优先选用GPS定位
            provider = LocationManager.GPS_PROVIDER;
        } else if (providerlist.contains(LocationManager.NETWORK_PROVIDER)) {
            provider = LocationManager.NETWORK_PROVIDER;
        }else {
            provider=LocationManager.PASSIVE_PROVIDER;
        }
        if(provider != null) {//为了压制getLastKnownLocation方法的警告
            if(ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }
        location = locationManager.getLastKnownLocation(provider);
        Log.e("hh",provider);

        if(location == null) {
            Log.e("hh","fuck");
        }
        //Log.e("hh",location.getLatitude()+"");
        //Log.e("hh",location.getLongitude()+"");
        locationManager.requestLocationUpdates(provider,800,0,locationListener);*/

    }




    LocationListener locationListener = new LocationListener() { //位置监听
        @Override
        public void onLocationChanged(Location location) {
            if (locConfirmed) {

                nowLatitude = location.getLatitude();
                nowLongitude = location.getLongitude();
                Log.e("hh","当前经度"+nowLongitude);
                Log.e("hh","目标经度"+targetLongitude);
                if(Math.abs(nowLatitude - targetLatitude) < 0.015 && Math.abs(nowLongitude - targetLongitude) < 0.015) {
                    locConfirmed = false;
                    //播放声音
                    Toast.makeText(MainActivity.this,"到了",Toast.LENGTH_LONG).show();
                    mediaPlayer.start();
                    mediaPlayer.setLooping(true);
                    //显示提示框

                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("到站啦")
                            .setMessage("您已到达目的地,该下车了")
                            .setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    mediaPlayer.stop();
                                    mediaPlayer = MediaPlayer.create(MainActivity.this,R.raw.ring);
                                }
                            })
                            .show();
                }
            }
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
            Log.e("hh","dao da");
        }

        @Override
        public void onProviderEnabled(String s) {
            Log.e("hh","dao da");
        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };


}
