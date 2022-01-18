package com.example.one.baidu;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.SpeechSynthesizerListener;
import com.example.one.baidu.control.InitConfig;
import com.example.one.baidu.control.MySyntherizer;
import com.example.one.baidu.listener.UiMessageListener;
import com.example.one.baidu.util.IOfflineResourceConst;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author :JianTao
 * @date :2022/1/18 14:28
 * @description :
 */
public class SyntheticInstance {
    private volatile static SyntheticInstance syntheticInstance;
    protected MySyntherizer mySyntherizer;

    public static SyntheticInstance getInstance() {
        if (syntheticInstance == null) {
            synchronized (SyntheticInstance.class) {
                if (syntheticInstance == null) {
                    syntheticInstance = new SyntheticInstance();
                }
            }
        }
        return syntheticInstance;
    }

    public void initialTts(Context context) {
        // 设置初始化参数
        Handler mainHandler = new Handler() {
            /*
             * @param msg
             */
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
            }
        };
        SpeechSynthesizerListener listener = new UiMessageListener(mainHandler);
        InitConfig config = getInitConfig(context, listener);
        mySyntherizer = new MySyntherizer(context, config, mainHandler); // 此处可以改为MySyntherizer 了解调用过程
    }

    protected InitConfig getInitConfig(Context context, SpeechSynthesizerListener listener) {
        Map<String, String> params = getParamsA();
        Properties properties = getProperties(context, "auth.properties");
        String appId = properties.getProperty("appId");
        String appKey = properties.getProperty("appKey");
        String secretKey = properties.getProperty("secretKey");
        InitConfig initConfig = new InitConfig(appId, appKey, secretKey, IOfflineResourceConst.DEFAULT_SDK_TTS_MODE, params,listener);
        return initConfig;
    }

    protected Map<String, String> getParamsA() {
        Map<String, String> params = new HashMap<>();
        // 以下参数均为选填
        // 设置在线发声音人： 0 普通女声（默认） 1 普通男声 3 情感男声<度逍遥> 4 情感儿童声<度丫丫>, 其它发音人见文档
        params.put(SpeechSynthesizer.PARAM_SPEAKER, "0");
        // 设置合成的音量，0-15 ，默认 5
        params.put(SpeechSynthesizer.PARAM_VOLUME, "15");
        // 设置合成的语速，0-15 ，默认 5
        params.put(SpeechSynthesizer.PARAM_SPEED, "5");
        // 设置合成的语调，0-15 ，默认 5
        params.put(SpeechSynthesizer.PARAM_PITCH, "5");
        return params;
    }

    public void speak(String text) {
        if (!TextUtils.isEmpty(text)) {
            int result = mySyntherizer.speak(text);
            checkResult(result, "speak");
        }
    }

    private void checkResult(int result, String method) {
        if (result != 0) {
        }
    }

    //读取本地properties
    private Properties getProperties(Context context, String filename) {
        try {
            InputStream is = context.getAssets().open(filename);
            Properties prop = new Properties();
            prop.load(is);
            is.close();
            return prop;
        } catch (IOException e) {
            e.printStackTrace();
            throw new AuthCheckException(e);
        }
    }

    public static class AuthCheckException extends RuntimeException {
        public AuthCheckException(String message) {
            super(message);
        }

        public AuthCheckException(Throwable cause) {
            super(cause);
        }
    }
}
