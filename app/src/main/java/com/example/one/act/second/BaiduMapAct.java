package com.example.one.act.second;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.Utils;
import com.example.one.App;
import com.example.one.R;
import com.example.one.speail.MyLocationUtils;

public class BaiduMapAct extends AppCompatActivity {

    Button button;
    TextView textView;

    public LocationClient mLocationClient = null;
    private MyLocationUtils myListener = new MyLocationUtils();

    LocationClientOption locationClientOption = new LocationClientOption();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baidu_map);

        mLocationClient = new LocationClient(this);
        //声明LocationClient类
        mLocationClient.registerLocationListener(myListener);
        //注册监听函数
        locationClientOption.setIsNeedAddress(true);
        locationClientOption.setNeedNewVersionRgc(true);

        mLocationClient.setLocOption(locationClientOption);

        mLocationClient.start();

        button = (Button) findViewById(R.id.bt_BaiduMapStart);
        textView = (TextView) findViewById(R.id.tv_BaiduMap);

        button.setOnClickListener(v -> {
            App app = (App) Utils.getApp();
            BDLocation bdLocation = app.getBdLocation();
            if (bdLocation!=null){
                textView.setText(bdLocation.getCity());
            }
        });

    }
}