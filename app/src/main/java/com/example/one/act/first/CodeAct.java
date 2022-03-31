package com.example.one.act.first;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.ImageView;

import com.blankj.utilcode.util.LogUtils;
import com.example.one.utils.ZXingUtils;
import com.google.zxing.*;
import com.example.one.R;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class CodeAct extends AppCompatActivity {
    private Button btCode1,btPro;
    private ImageView imageView;

    private ActivityManager mActivityManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code);

        mActivityManager=(ActivityManager)getSystemService(ACTIVITY_SERVICE);

        btCode1 = (Button) findViewById(R.id.bt_Code1);
        btPro = (Button) findViewById(R.id.bt_Pro);
        imageView = (ImageView) findViewById(R.id.iv_code);

        btCode1.setOnClickListener(v -> {
            String result = null;
            String content = "https://www.zust.edu.cn/";
            try {
                // 将中文UTF-8转换成ISO-8859-1(避免中文变问号)
                result = new String(content.getBytes("UTF-8"),"iso-8859-1");
                Bitmap qrCode = ZXingUtils.createQRImage(content,200,200, Color.BLUE);
                // img为ImageView视图
                imageView.setImageBitmap(qrCode);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        });

        btPro.setOnClickListener(v -> {
//            getProcess();
            try {
                Runtime runtime = Runtime.getRuntime();
                runtime.exec("input keyevent " + KeyEvent.KEYCODE_BACK);
            }catch (Exception e){
                e.printStackTrace();
            }
        });
    }

    public void getProcess() {
        try {
            PackageManager packageManager = getApplicationContext().getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo("com.example.one", 0);
            //应用装时间
            long firstInstallTime = packageInfo.firstInstallTime;
            String data = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss").format(new java.util.Date(firstInstallTime));
            //应用最后一次更新时间
            long lastUpdateTime = packageInfo.lastUpdateTime;
            String newData = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss").format(new java.util.Date(lastUpdateTime));
            LogUtils.e("first install time : " + firstInstallTime + " \nlast update time :" + lastUpdateTime);
            LogUtils.e("first install time : " + data + " \nlast update time :" + newData);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }
}