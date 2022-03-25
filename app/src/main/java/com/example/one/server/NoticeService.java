package com.example.one.server;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import com.example.one.R;
import com.example.one.act.MainAct;

public class NoticeService extends Service {
    public NoticeService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String ID = "com.example.one";	//这里的id里面输入自己的项目的包的路径
        String NAME = "Channel One";
        Intent intent0 = new Intent(NoticeService.this, MainAct.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent0, 0);

        NotificationCompat.Builder notification; //创建服务对象
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(ID, NAME, manager.IMPORTANCE_HIGH);
            channel.enableLights(true);
            channel.setShowBadge(true);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            manager.createNotificationChannel(channel);
            notification = new NotificationCompat.Builder(NoticeService.this).setChannelId(ID);
        } else {
            notification = new NotificationCompat.Builder(NoticeService.this);
        }

        notification.setContentTitle("标题")
                .setContentText("内容")
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.icon_logo)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.icon_logo))
                .setContentIntent(pendingIntent)
                .build();

        Notification notification1 = notification.build();
        startForeground(1,notification1);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}