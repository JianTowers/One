package com.example.one.act;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.one.R;
import com.example.one.speail.ThreadApi;

import java.util.Timer;
import java.util.TimerTask;

public class ThreadAct extends AppCompatActivity {
    TextView tvThread,tvThread2,tvThread3;
    Button btThread;

    /**
     * 主线程
     */
    private Timer timer = new Timer();
    private Timer timer2 = new Timer();
    private Handler mHandler;
    private TimerTask timerTask;

    int i = 0;

    /**
    *封装线程测试
    */
    private boolean isTest = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thread);
        tvThread = (TextView) findViewById(R.id.tv_thread);
        tvThread2 = (TextView) findViewById(R.id.tv_thread2);
        tvThread3 = (TextView) findViewById(R.id.tv_thread3);
        btThread = (Button) findViewById(R.id.bt_thread);
        setAction();
        btThread.setOnClickListener(v -> {
            if(!isTest){
                ThreadApi.getInstance().doPrintLog();
                isTest = true;
            }else {
                ThreadApi.getInstance().stopPrintLog();
                isTest = false;
            }
        });
    }

    private void setAction() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                if (msg.what == 1) {
                    String text2 = Integer.toString(i);
                    tvThread2.setText(text2);
                    i++;
                }
                if (msg.what == 2) {
                    String text3 = Integer.toString(i);
                    tvThread3.setText(text3);
                    i--;
                }
            }
        };

        /**
        *定时线程的两种方法
        */
        timerTask = new TimerTask() {
            @Override
            public void run() {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        String text = Integer.toString(i);
                        tvThread.setText(text);
                        i=i+10;
                    }
                });
            }
        };


        timer.schedule(timerTask,0,1000);

        timer2.schedule(new TimerTask() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = 1;
                mHandler.sendMessage(message);
            }
        }, 0, 100);

        timer2.schedule(new TimerTask() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = 2;
                mHandler.sendMessage(message);
            }
        }, 0, 500);
    }
}