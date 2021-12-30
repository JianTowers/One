package com.example.one.speail;

import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.LogUtils;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author :JianTao
 * @date :2021/12/18 11:34
 * @description : 线程封装api
 */
public class ThreadApi {
    Timer timer = new Timer();
    Handler handler;
    TimerTask timerTask;


    private ThreadApi() {
    }

    private volatile static ThreadApi threadApi;

    public static ThreadApi getInstance() {
        if (threadApi == null) {
            synchronized (ThreadApi.class) {
                if (threadApi == null) {
                    threadApi = new ThreadApi();
                }
            }
        }
        return threadApi;
    }

    public void doPrintLog() {
        handler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                if (msg.what == 0) {
                    LogUtils.d("日志测试中");
                }
            }
        };

        timerTask = new TimerTask() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = 0;
                handler.sendMessage(message);
            }
        };

        timer.schedule(timerTask, 0, 2000);
    }

    public void stopPrintLog() {
        if (timer != null) {
            timer.cancel();
            if (timerTask != null) {
                timerTask.cancel();
            }

            threadApi = null;
        }
    }
}
