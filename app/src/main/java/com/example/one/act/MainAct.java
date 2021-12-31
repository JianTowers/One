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
import com.example.one.server.AlarmReceiver;
import com.example.one.speail.ThreadApi;
import com.permissionx.guolindev.PermissionX;

import java.util.Calendar;

public class MainAct extends AppCompatActivity {
    private Button btAlarm, btTherad, btObserver, btThread2, btString, btCode;
    private Button btCalcuator, btMyTimer, btRollingText;
    private Button btPermission,btBattery,btMarqueeview;

    /**
     * 闹钟管理器
     */
    private AlarmManager alarmManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        onRun();
    }

    private void initView() {
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
    }

    private void onRun() {
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
            ThreadApi.getInstance().doPrintLog();
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
            startActivity(new Intent(MainAct.this,MarqueeviewAct.class));
        });

        //动态权限获取
        btPermission.setOnClickListener(v -> {
            PermissionX.init(this).
                    permissions(Manifest.permission.READ_CONTACTS, Manifest.permission.CAMERA, Manifest.permission.CALL_PHONE)
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
    }

    /**
    *闹钟服务
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
    *手机电量
    */
    private void getSystemBattery(Context context) {
        int level = 0;
        Intent batteryInfoIntent = context.getApplicationContext().registerReceiver(null,
                new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        level = batteryInfoIntent.getIntExtra("level", 0);
        int batterySum = batteryInfoIntent.getIntExtra("scale", 100);
        int percentBattery= 100 *  level / batterySum;
        LogUtils.d("手机当前电量"+percentBattery);
    }

}