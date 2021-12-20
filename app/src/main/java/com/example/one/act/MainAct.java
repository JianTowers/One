package com.example.one.act;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.LogUtils;
import com.example.one.R;
import com.example.one.server.AlarmReceiver;
import com.example.one.speail.ThreadApi;

import java.util.Calendar;

public class MainAct extends AppCompatActivity{
    private Button btAlarm,btTherad,btObserver,btThread2;

    /**
    *闹钟管理器
    */
    private AlarmManager alarmManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        onRun();
    }

    private void initView(){
        btAlarm = (Button) findViewById(R.id.bt_alarm);
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        btTherad = (Button) findViewById(R.id.bt_thread);
        btObserver = (Button) findViewById(R.id.bt_observer);
        btThread2 = (Button) findViewById(R.id.bt_thread2);
    }

    private void onRun(){
        btAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setClock(v);
            }
        });

        btTherad.setOnClickListener(v -> {
            ThreadApi.getInstance().stopPrintLog();
            Intent intent = new Intent(MainAct.this,ThreadAct.class);
            startActivity(intent);
        });

        btObserver.setOnClickListener(v -> {
            Intent intent = new Intent(MainAct.this,QuestionAct.class);
            startActivity(intent);
        });

        btThread2.setOnClickListener(v -> {
            ThreadApi.getInstance().doPrintLog();
        });
    }

    public void setClock(View view){
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
        },hour,minute,true);
        timePickerDialog.show();
    }
}