package com.example.one.act.second;

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
    private Button btStart;
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
        editText = (EditText) findViewById(R.id.et_baidu);
    }

    private void runClick(){
        btStart.setOnClickListener(v -> {
            SyntheticInstance.getInstance().speak("你好呀");
        });
    }

//    protected void initialTts() {
//        LoggerProxy.printable(true); // 日志打印在logcat中
//        // 设置初始化参数
//        // 此处可以改为 含有您业务逻辑的SpeechSynthesizerListener的实现类
//        SpeechSynthesizerListener listener = new UiMessageListener(mainHandler);
//        InitConfig config = getInitConfig(listener);
//        mySyntherizer = new MySyntherizer(this, config, mainHandler); // 此处可以改为MySyntherizer 了解调用过程
//    }
//
//    protected InitConfig getInitConfig(SpeechSynthesizerListener listener) {
//        Map<String, String> params = getParamsA();
//        // 添加你自己的参数
//        InitConfig initConfig;
//        // appId appKey secretKey 网站上您申请的应用获取。注意使用离线合成功能的话，需要应用中填写您app的包名。包名在build.gradle中获取。
//        if (sn == null) {
//            initConfig = new InitConfig(appId, appKey, secretKey, ttsMode, params, listener);
//        } else {
//            initConfig = new InitConfig(appId, appKey, secretKey, sn, ttsMode, params, listener);
//        }
//        // 如果您集成中出错，请将下面一段代码放在和demo中相同的位置，并复制InitConfig 和 AutoCheck到您的项目中
//        // 上线时请删除AutoCheck的调用
//        AutoCheck.getInstance(getApplicationContext()).check(initConfig, new Handler() {
//            @Override
//            public void handleMessage(Message msg) {
//                if (msg.what == 100) {
//                    AutoCheck autoCheck = (AutoCheck) msg.obj;
//                    synchronized (autoCheck) {
//                        String message = autoCheck.obtainDebugMessage();
//                        LogUtils.d(message);
//                        // Log.w("AutoCheckMessage", message);
//                    }
//                }
//            }
//
//        });
//        return initConfig;
//    }
//
//
//    public Map<String,String> getParamsA() {
//        Map<String, String> params = new HashMap<>();
//        // 以下参数均为选填
//        // 设置在线发声音人： 0 普通女声（默认） 1 普通男声 3 情感男声<度逍遥> 4 情感儿童声<度丫丫>, 其它发音人见文档
//        params.put(SpeechSynthesizer.PARAM_SPEAKER, "0");
//        // 设置合成的音量，0-15 ，默认 5
//        params.put(SpeechSynthesizer.PARAM_VOLUME, "15");
//        // 设置合成的语速，0-15 ，默认 5
//        params.put(SpeechSynthesizer.PARAM_SPEED, "5");
//        // 设置合成的语调，0-15 ，默认 5
//        params.put(SpeechSynthesizer.PARAM_PITCH, "5");
//        return params;
//    }
//
//
//
//    /**
//     * speak 实际上是调用 synthesize后，获取音频流，然后播放。
//     * 获取音频流的方式见SaveFileActivity及FileSaveListener
//     * 需要合成的文本text的长度不能超过1024个GBK字节。
//     */
//    private void speak() {
//        String text = editText.getText().toString();
//        // 需要合成的文本text的长度不能超过1024个GBK字节。
//        if (TextUtils.isEmpty(text)) {
//            text = "百度语音合成适用于泛阅读、订单播报、智能硬件等应用场景，让您的应用、设备开口说话，更具个性。";
//            editText.setText(text);
//        }
//        int result = mySyntherizer.speak(text);
//        checkResult(result, "speak");
//    }
//
//    private void checkResult(int result, String method) {
//        if (result != 0) {
//            LogUtils.d(method);
//        }
//    }
}