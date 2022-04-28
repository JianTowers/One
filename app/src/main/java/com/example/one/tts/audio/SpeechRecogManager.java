package com.example.one.tts.audio;

import android.content.Context;
import android.util.Log;

import com.blankj.utilcode.util.LogUtils;
import com.example.one.tts.KXWebSocketClient;

import org.java_websocket.client.WebSocketClient;

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;

public class SpeechRecogManager {
    private static final String TAG = "SpeechRecogManager";


    private String BASE_URL = "ws://192.168.1.36:22184/v1/asr/ws";
    private volatile boolean isRecog = false;
    private AudioRecordManager audioRecordManager;
    private SpeechRecogListener listener;
    private WebSocketClient client;

    private SpeechRecogManager() {
    }

    public static SpeechRecogManager getInstance() {
        return RealTimeASRManagerHolder.instance;
    }

    public static void send(WebSocketClient client, byte[] bytes) {
        if (client.isClosed()) {
            Log.w(TAG, "client connect closed!");
        } else {
            client.send(bytes);
        }
    }

    public boolean isRecog() {
        return isRecog;
    }

    public SpeechRecogManager init(Context context) {
        if (context instanceof SpeechRecogListener)
            listener = (SpeechRecogListener) context;
        return this;
    }

    public void setListener(SpeechRecogListener listener) {
        this.listener = listener;
    }

    public void startSpeechRecognition() {
        isRecog = true;
        if (listener != null)
            listener.recogBefore();

        if (audioRecordManager != null) {
            audioRecordManager.release();
            audioRecordManager = null;
        }

        audioRecordManager = new AudioRecordManager();
        audioRecordManager.init();
        initSpeechRecognition(0);

        if (listener != null)
            listener.recogStart();
    }

    public void testRecognition() {
        initSpeechRecognition(1);
    }

    private void initSpeechRecognition(int flag) {
        LogUtils.d(1);
        URI url;
        try {
            url = new URI(BASE_URL);
            LogUtils.d(2);
        } catch (URISyntaxException e) {
            Log.e(TAG, "url生成异常");
            e.printStackTrace();
            LogUtils.d(1);
            if (listener != null)
                listener.recogErr(e);
            return;
        }
        CountDownLatch handshakeSuccess = new CountDownLatch(1);
        CountDownLatch connectClose = new CountDownLatch(1);
        client = new KXWebSocketClient(url, listener, handshakeSuccess, connectClose);
        client.connect();
        // 等待握手成功
        try {
            handshakeSuccess.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
            LogUtils.e(e.toString());
        }

        if (flag == 0) {
            audioRecordManager.startRecord(client);
        } else {
            // 发送音频
            byte[] bytes = new byte[1280];
            try {
                String filePath = "/sdcard/kangxu/data/test.pcm";
                InputStream inputStream = new FileInputStream(filePath);
                int len = -1;
                long lastTs = 0;
                while ((len = inputStream.read(bytes)) != -1) {
                    if (len < 1280) {
                        send(client, bytes = Arrays.copyOfRange(bytes, 0, len));
                        break;
                    }
                    long curTs = System.currentTimeMillis();
                    if (lastTs == 0) {
                        lastTs = System.currentTimeMillis();
                    } else {
                        long s = curTs - lastTs;
                    }
                    send(client, bytes);
                    // 每隔40毫秒发送一次数据
                    Thread.sleep(40);
                }
                // 发送结束标识
                send(client, "{\"action\":\"Stop\"}".getBytes());
            } catch (Exception e) {
                e.printStackTrace();
                LogUtils.d(e.toString());
            }
            // 等待连接关闭
            try {
                connectClose.await();
            } catch (Exception e) {
                e.printStackTrace();
                LogUtils.d(e.toString());
            }
        }
    }

    public void pauseSpeechRecognition() {
        isRecog = false;
        audioRecordManager.pauseRecord();
    }

    public void continueSpeechRecognition() {
        isRecog = true;
        audioRecordManager.continueRecord();
    }

    public void stopSpeechRecognition() {
        isRecog = false;
        audioRecordManager.stopRecord();
        if (listener != null)
            listener.recogStop();
    }

    public void release() {
        if (audioRecordManager != null) {
            audioRecordManager.release();
            audioRecordManager = null;
        }
        if (client != null) {
            if (client.isOpen())
                client.close();
            client = null;
        }
    }

    private static class RealTimeASRManagerHolder {
        private static SpeechRecogManager instance = new SpeechRecogManager();
    }
}
