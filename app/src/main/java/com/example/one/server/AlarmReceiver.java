package com.example.one.server;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.one.act.AlarmActivity;

/**
 * @author :JianTao
 * @date :2021/12/15 11:38
 * @description :
 */
public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, AlarmActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }
}
