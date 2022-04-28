package com.example.one.act.second;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.baidu.tts.chainofresponsibility.logger.LoggerProxy;
import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.SpeechSynthesizerListener;
import com.baidu.tts.client.TtsMode;
import com.blankj.utilcode.util.LogUtils;
import com.example.one.R;
import com.example.one.baidu.SyntheticInstance;
import com.example.one.baidu.control.InitConfig;
import com.example.one.baidu.control.MySyntherizer;
import com.example.one.baidu.listener.UiMessageListener;
import com.example.one.baidu.util.Auth;
import com.example.one.baidu.util.AutoCheck;
import com.example.one.baidu.util.IOfflineResourceConst;

import java.util.HashMap;
import java.util.Map;

public class BaiduAct extends AppCompatActivity {
    private Button btStart,btPause,btResume,btStop,btVoice;
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baidu);
        init();
        runClick();
    }

    private void init(){
        btStart = (Button) findViewById(R.id.bt_baidu_start);
        btPause = (Button) findViewById(R.id.bt_baidu_pause);
        btResume = (Button) findViewById(R.id.bt_baidu_resume);
        btStop = (Button) findViewById(R.id.bt_baidu_stop);
        btVoice = (Button) findViewById(R.id.btVoiceRecode);
        editText = (EditText) findViewById(R.id.et_baidu);
    }

    private void runClick(){
        btStart.setOnClickListener(v -> {
            if (TextUtils.isEmpty(editText.getText().toString())){
                SyntheticInstance.getInstance().speak("百度语音合成适用于泛阅读、订单播报、智能硬件等应用场景，让您的应用、设备开口说话，更具个性。");
            }else {
                SyntheticInstance.getInstance().speak(editText.getText().toString());
            }
        });

        btPause.setOnClickListener(v -> {
            SyntheticInstance.getInstance().pause();
        });

        btResume.setOnClickListener(v -> {
            SyntheticInstance.getInstance().resume();
        });

        btStop.setOnClickListener(v -> {
            SyntheticInstance.getInstance().stop();
        });

        btVoice.setOnClickListener(v -> startActivity(new Intent(this,VoiceRecodeAcitvity.class)));
    }
}