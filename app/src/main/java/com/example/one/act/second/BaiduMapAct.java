package com.example.one.act.second;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.os.StatFs;
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

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;

public class BaiduMapAct extends AppCompatActivity {

    Button button;
    TextView textView;

    Button button2;
    TextView textView2;

    Button button3;

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


        button2 = (Button) findViewById(R.id.btStorage);
        textView2 = (TextView) findViewById(R.id.tvStorage);
        button2.setOnClickListener(v -> {
            textView2.setText(""+getUse()+"/"+getTotal());
        });

        button3 = (Button) findViewById(R.id.btClose);
        button3.setOnClickListener(v -> {
            try {
                PowerManager pManager = (PowerManager) App.getContext().getSystemService(Context.POWER_SERVICE);
                if (pManager != null) {
                    Method method = pManager.getClass().getMethod("shutdown", boolean.class, String.class, boolean.class);
                    method.invoke(pManager, false, null, false);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
//            try {
//                Runtime.getRuntime().exec("reboot -p"); //关机
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        });
    }

    private long getTotal(){
        File path = Environment.getDataDirectory();
        StatFs statFs = new StatFs(path.getPath());
        long size = statFs.getBlockSize();
        long total = statFs.getBlockCount();
        return size*total/1024/1024/1024;
    }

    private long getUse(){
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return availableBlocks * blockSize/1024/1024/1024;
    }
}