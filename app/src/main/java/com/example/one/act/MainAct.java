package com.example.one.act;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.LogUtils;
import com.example.one.R;
import com.example.one.act.first.CalculatorAct;
import com.example.one.act.first.CodeAct;
import com.example.one.act.first.MarqueeviewAct;
import com.example.one.act.first.MyTimerAct;
import com.example.one.act.first.QuestionAct;
import com.example.one.act.first.RollingTextAct;
import com.example.one.act.first.SocketAct;
import com.example.one.act.first.ThreadAct;
import com.example.one.act.second.BaiduAct;
import com.example.one.act.second.BaiduMapAct;
import com.example.one.act.second.FrameAnimationAct;
import com.example.one.act.second.MqActivity;
import com.example.one.act.second.SplashAct;
import com.example.one.act.second.WeatherAct;
import com.example.one.act.second.WebAct;
import com.example.one.act.second.WhiteListAct;
import com.example.one.server.AlarmReceiver;
import com.example.one.speail.ThreadApi;
import com.permissionx.guolindev.PermissionX;

import java.util.Calendar;
import java.util.Locale;

public class MainAct extends AppCompatActivity {
    private Button btAlarm, btTherad, btObserver, btThread2, btString, btCode;
    private Button btCalcuator, btMyTimer, btRollingText;
    private Button btPermission, btBattery, btMarqueeview;
    private Button btSocket;

    /**
     * 第二行
     */
    private Button btWhiteList, btWeather, btWebView, btSplash;
    private Button btLowercase, btAnimation, btBaidu,btMq;
    private Button btEquipment,btAdapter;

    /**
     * 闹钟管理器
     */
    private AlarmManager alarmManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViewFirst();
        initViewSecond();
        onRunFirst();
        onRunSecond();
    }

    private void initViewFirst() {
        btAlarm = (Button) findViewById(R.id.bt_alarm);
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        btTherad = (Button) findViewById(R.id.bt_thread);
        btObserver = (Button) findViewById(R.id.bt_observer);
        btThread2 = (Button) findViewById(R.id.bt_thread2);
        btString = (Button) findViewById(R.id.bt_String);
        btCode = (Button) findViewById(R.id.bt_Code);
        btCalcuator = (Button) findViewById(R.id.bt_Calculator);
        btMyTimer = (Button) findViewById(R.id.bt_MyTimer);
        btRollingText = (Button) findViewById(R.id.bt_RollingText);
        btPermission = (Button) findViewById(R.id.bt_Permission);
        btBattery = (Button) findViewById(R.id.bt_Battery);
        btMarqueeview = (Button) findViewById(R.id.bt_Marqueeview);
        btSocket = (Button) findViewById(R.id.bt_Socket);

    }

    private void initViewSecond() {
        btWhiteList = (Button) findViewById(R.id.bt_White_List);
        btWeather = (Button) findViewById(R.id.bt_Weather);
        btWebView = (Button) findViewById(R.id.bt_WebView);
        btSplash = (Button) findViewById(R.id.bt_Splash);
        btLowercase = (Button) findViewById(R.id.bt_Lowercase);
        btAnimation = (Button) findViewById(R.id.bt_Animation);
        btBaidu = (Button) findViewById(R.id.bt_Baidu);
        btMq = (Button) findViewById(R.id.bt_mq);
        btEquipment = (Button) findViewById(R.id.bt_Equipment);
        btAdapter = (Button) findViewById(R.id.btAdapter);
    }

    private void onRunFirst() {
        btAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setClock(v);
            }
        });

        btTherad.setOnClickListener(v -> {
            ThreadApi.getInstance().stopPrintLog();
            Intent intent = new Intent(MainAct.this, ThreadAct.class);
            startActivity(intent);
        });

        btObserver.setOnClickListener(v -> {
            Intent intent = new Intent(MainAct.this, QuestionAct.class);
            startActivity(intent);
        });

        btThread2.setOnClickListener(v -> {
            startActivity(new Intent(MainAct.this,MyTimerAct.class));
        });

        btString.setOnClickListener(v -> {
            Intent intent = new Intent(MainAct.this, MainAct.class);
            startActivity(intent);
            finish();
            overridePendingTransition(0, 0);
        });

        btCode.setOnClickListener(v -> {
            startActivity(new Intent(MainAct.this, CodeAct.class));
        });

        btCalcuator.setOnClickListener(v -> {
            startActivity(new Intent(MainAct.this, CalculatorAct.class));
        });

        btMyTimer.setOnClickListener(v -> {
            startActivity(new Intent(MainAct.this, MyTimerAct.class));
        });

        btRollingText.setOnClickListener(v -> {
            startActivity(new Intent(MainAct.this, RollingTextAct.class));
        });

        btMarqueeview.setOnClickListener(v -> {
            startActivity(new Intent(MainAct.this, MarqueeviewAct.class));
        });

        //动态权限获取
        btPermission.setOnClickListener(v -> {
            PermissionX.init(this).
                    permissions(Manifest.permission.READ_CONTACTS, Manifest.permission.CAMERA, Manifest.permission.CALL_PHONE,
                            Manifest.permission.ACCESS_NETWORK_STATE,
                            Manifest.permission.MODIFY_AUDIO_SETTINGS,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
                            Manifest.permission.CHANGE_WIFI_STATE)
                    .onExplainRequestReason((scope, deniedList) -> {
                        scope.showRequestReasonDialog(deniedList, "UmsCamEpp需要您同意以下授权才能正常使用", "同意", "拒绝");
                    })
                    .request(((allGranted, grantedList, deniedList) -> {
                        if (allGranted) {
                            Toast.makeText(this, "您同意了所有权限!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "您拒绝了以下权限:" + deniedList, Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }));
        });

        //手机电量
        btBattery.setOnClickListener(v -> {
            getSystemBattery(this);
        });

        btSocket.setOnClickListener(v -> {
            startActivity(new Intent(MainAct.this, SocketAct.class));
        });
    }

    private void onRunSecond() {
        btWhiteList.setOnClickListener(v -> {
            startActivity(new Intent(MainAct.this, WhiteListAct.class));
        });

        btWeather.setOnClickListener(v -> {
            startActivity(new Intent(MainAct.this, WeatherAct.class));
        });

        btWebView.setOnClickListener(v -> {
            startActivity(new Intent(MainAct.this, WebAct.class));
        });

        btSplash.setOnClickListener(v -> {
            startActivity(new Intent(MainAct.this, SplashAct.class));
        });

        btLowercase.setOnClickListener(v -> {
            String string = "我是中国adkkk";
            LogUtils.d(string.toUpperCase(Locale.ROOT));
        });

        btAnimation.setOnClickListener(v -> {
            startActivity(new Intent(MainAct.this, FrameAnimationAct.class));
        });

        btBaidu.setOnClickListener(v -> {
            startActivity(new Intent(MainAct.this, BaiduAct.class));
        });

        btMq.setOnClickListener(v -> startActivity(new Intent(MainAct.this, MqActivity.class)));

        btEquipment.setOnClickListener(v -> startActivity(new Intent(MainAct.this, BaiduMapAct.class)));
    }

    /**
     * 闹钟服务
     */
    public void setClock(View view) {
        //获取当前系统时间
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        //弹出闹钟框
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Calendar c = Calendar.getInstance();    //获取日期对象
                c.set(Calendar.HOUR_OF_DAY, hourOfDay); //设置闹钟小时数
                c.set(Calendar.MINUTE, minute); //设置闹钟分钟数
                Intent intent = new Intent(MainAct.this, AlarmReceiver.class);
                //创建pendingIntent
                PendingIntent pendingIntent = PendingIntent.getBroadcast(MainAct.this, 0X102, intent, 0);
                //设置闹钟
                alarmManager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
                LogUtils.d("闹钟设置成功");
            }
        }, hour, minute, true);
        timePickerDialog.show();
    }

    /**
     * 手机电量
     */
    private void getSystemBattery(Context context) {
        int level = 0;
        Intent batteryInfoIntent = context.getApplicationContext().registerReceiver(null,
                new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        level = batteryInfoIntent.getIntExtra("level", 0);
        int batterySum = batteryInfoIntent.getIntExtra("scale", 100);
        int percentBattery = 100 * level / batterySum;
        LogUtils.d("手机当前电量" + percentBattery);
    }

}