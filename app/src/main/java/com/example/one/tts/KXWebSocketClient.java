package com.example.one.tts;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.LogUtils;
import com.example.one.tts.audio.SpeechRecogListener;
import com.example.one.tts.audio.entity.ContentRes;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

public class KXWebSocketClient extends WebSocketClient {
    private static final String TAG = "KXWebSocketClient";

    private CountDownLatch handshakeSuccess;
    private CountDownLatch connectClose;

    private SpeechRecogListener listener;

    public KXWebSocketClient(URI serverUri, SpeechRecogListener listener,
                             CountDownLatch handshakeSuccess, CountDownLatch connectClose) {
        super(serverUri, new Draft_6455());
        this.listener = listener;
        this.handshakeSuccess = handshakeSuccess;
        this.connectClose = connectClose;
    }

    public void setListener(SpeechRecogListener listener) {
        this.listener = listener;
    }

    private int total = 0;
    private long time;

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        Log.d(TAG, "连接建立成功");

        JSONObject startJsonObject = new JSONObject();
        startJsonObject.put("action", "Start");
        startJsonObject.put("id", String.valueOf(System.currentTimeMillis()/1000));
        startJsonObject.put("sample_rate", 16000);
        send(JSONObject.toJSONString(startJsonObject));
    }

    @Override
    public void onMessage(String message) {
        Log.d(TAG, "onMessage");
        JSONObject msgObj = JSON.parseObject(message);
        String action = msgObj.getString("action");
        String task_id = msgObj.getString("task_id");
        total++;

        String desc = msgObj.getString("payload");
        if (Objects.equals("TranscribeStarted", action)) {
            // 握手成功
            Log.i(TAG, "握手成功");
            time = System.currentTimeMillis();
            handshakeSuccess.countDown();
        } else if (Objects.equals("EndOfSentence", action)) {
            // 转写结果
            ContentRes contentRes =JSON.parseObject(desc,ContentRes.class);
            if (listener != null)
                listener.recogMessage(contentRes);
            LogUtils.json(contentRes);
            LogUtils.e(total);
            LogUtils.e(System.currentTimeMillis()-time);
        } else if (Objects.equals("TranscribeCompleted", action)) {
            // 上传结束
            Log.w(TAG, "结束");
            if (listener != null)
                listener.recogEnd();
            LogUtils.e(total);
            LogUtils.e(System.currentTimeMillis()-time);
        } else if (Objects.equals("TaskFailed", action)) {
            // 连接发生错误
            Log.w(TAG, "连接异常");
            LogUtils.e(total);
            LogUtils.e(System.currentTimeMillis()-time);
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        LogUtils.e(reason);
        Log.e("JWebSocketClient", "onClose()");
        connectClose.countDown();
        LogUtils.e(total);
        LogUtils.e(System.currentTimeMillis()-time);
    }

    @Override
    public void onError(Exception e) {
        Log.e("JWebSocketClient", "onError()");
        LogUtils.e(e.toString());
        if (listener != null)
            listener.recogErr(e);
    }
}
