package com.example.one.act.first;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.LogUtils;
import com.example.one.R;

import java.util.Timer;
import java.util.TimerTask;

public class MyTimerAct extends AppCompatActivity {
    private TextView textView;
    private Button button;

    private Timer myTimer = new Timer();
    private Handler mHandler;

    private int i = -1;

    private Button btLog;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_timer);
        textView = (TextView) findViewById(R.id.tv_MyTimer);
        button = (Button) findViewById(R.id.bt_MyTimer_Test);
        btLog = (Button) findViewById(R.id.bt_MyTimer_Log);
        setAction();
        button.setOnClickListener(v -> {
            if (myTimer == null) {
                myTimer = new Timer();
                myTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Message message = new Message();
                        message.what = 0;
                        mHandler.sendMessage(message);
                    }
                }, 0, 1000);
            } else {
                myTimer.cancel();
                myTimer =null;
            }
        });

        handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                if (msg.what == 0){
                    SystemClock.sleep( 1000);
                    LogUtils.d(msg.arg1);
                }
            }
        };

        btLog.setOnClickListener(v -> {
            for (int i=0;i<5;i++){
                Message message = new Message();
                message.what = 0;
                message.arg1 = i;
                handler.sendMessage(message);
            }

        });
    }

    private void setAction() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 0) {
                    String text = Integer.toString(i);
                    textView.setText(text);
                    i++;
                }
            }
        };

        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = 0;
                mHandler.sendMessage(message);
            }
        }, 0, 1000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        myTimer.cancel();
        myTimer =null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (myTimer == null) {
            myTimer = new Timer();
            myTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Message message = new Message();
                    message.what = 0;
                    mHandler.sendMessage(message);
                }
            }, 0, 1000);
        }
    }
}