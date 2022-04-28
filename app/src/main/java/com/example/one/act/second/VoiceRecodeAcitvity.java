package com.example.one.act.second;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.example.one.R;
import com.example.one.tts.AudioMannger;
import com.example.one.tts.XunFeiClient;
import com.example.one.tts.audio.SpeechRecogListener;
import com.example.one.tts.audio.SpeechRecogManager;
import com.example.one.tts.audio.entity.ContentRes;
import com.example.one.tts.rtasr.DraftWithOrigin;
import com.example.one.tts.rtasr.util.EncryptUtil;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechUtility;

import java.net.URI;
import java.net.URLEncoder;
import java.util.concurrent.CountDownLatch;

public class VoiceRecodeAcitvity extends AppCompatActivity {

    Button btStart,btStop,btKStart,btKStop;

    Button btTest;

    Button btXunFeiStart,btXunFeiStop;

    TextView textView;

    AudioMannger audioMannger;

    XunFeiClient client;

    // appid
    private static final String APPID = "4f1ed419";

    // appid对应的secret_key
    private static final String SECRET_KEY = "YTMyZTcwYWVmYWY2M2MxMmNkOGI1MzM1";

    // 请求地址
    private static final String HOST = "rtasr.xfyun.cn/v1/ws";

    private static final String BASE_URL = "wss://" + HOST;

    private static final String ORIGIN = "https://" + HOST;

    /**
    *讯飞
    */
    private SpeechRecognizer mIat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_recode_acitvity);

        initXunFei();

        init();
        runFlow();
    }

    private void init(){
        btStart = (Button) findViewById(R.id.btVoiceStart);
        btStop = (Button) findViewById(R.id.btVoiceStop);
        btKStart = (Button) findViewById(R.id.btVoiceKXStart);
        btKStop = (Button) findViewById(R.id.btVoiceKXStop);

        btTest = (Button) findViewById(R.id.btVoiceTest);

        btXunFeiStart = (Button) findViewById(R.id.btVoiceXunFei);
        btXunFeiStop = (Button) findViewById(R.id.btVoiceXunFeiStop);
        textView = (TextView) findViewById(R.id.tvVoiceXunFei);

        audioMannger = new AudioMannger();
        audioMannger.init();
//
//        try {
//            URI url = new URI(BASE_URL + getHandShakeParams(APPID, SECRET_KEY));
//            DraftWithOrigin draft = new DraftWithOrigin(ORIGIN);
//            LogUtils.d(url);
//            CountDownLatch handshakeSuccess = new CountDownLatch(1);
//            CountDownLatch connectClose = new CountDownLatch(1);
//            XunFeiClient client = new XunFeiClient(url, draft, handshakeSuccess, connectClose);
//
//            client.connect();
//        }catch (Exception e){
//            e.printStackTrace();
//            LogUtils.e(e.toString());
//        }

    }

    private void runFlow(){
        btStart.setOnClickListener(v -> audioMannger.startRecode());

        btStop.setOnClickListener(v -> audioMannger.stopRecord());

        btKStart.setOnClickListener(v -> {
            SpeechRecogManager.getInstance().startSpeechRecognition();
            LogUtils.d("1");
        });

        btKStop.setOnClickListener(v -> {
            SpeechRecogManager.getInstance().release();
        });

        btTest.setOnClickListener(v -> {
            SpeechRecogManager.getInstance().testRecognition();
        });

        btXunFeiStart.setOnClickListener(v -> {
            mIat.startListening(mRecognizerListener);
        });

        btXunFeiStop.setOnClickListener(v -> {
            mIat.stopListening();
        });
    }

    // 生成握手参数
    public static String getHandShakeParams(String appId, String secretKey) {
        String ts = System.currentTimeMillis() / 1000 + "";
        String signa = "";
        try {
            signa = EncryptUtil.HmacSHA1Encrypt(EncryptUtil.MD5(appId + ts), secretKey);
            return "?appid=" + appId + "&ts=" + ts + "&signa=" + URLEncoder.encode(signa, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    /**
    * 讯飞语音识别初始化
    */
    public void initXunFei(){
        SpeechUtility.createUtility(this, SpeechConstant.APPID+"=4f1ed419");

        mIat = SpeechRecognizer.createRecognizer(this,mInitListener);
        mIat.setParameter(SpeechConstant.RESULT_TYPE,"plain");
    }

    /**
     * 初始化监听器。
     */
    private InitListener mInitListener = new InitListener() {

        @Override
        public void onInit(int code) {
            LogUtils.d("SpeechRecognizer init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                LogUtils.d("初始化失败，错误码：" + code + ",请点击网址https://www.xfyun.cn/document/error-code查询解决方案");
            }
        }
    };

    /**
     * 听写监听器。
     */
    private RecognizerListener mRecognizerListener = new RecognizerListener() {

        @Override
        public void onBeginOfSpeech() {
            // 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
            LogUtils.d("开始说话");
        }

        @Override
        public void onError(SpeechError error) {
            // Tips：
            // 错误码：10118(您没有说话)，可能是录音机权限被禁，需要提示用户打开应用的录音权限。
            LogUtils.d("onError " + error.getPlainDescription(true));
            mIat.stopListening();
            mIat.startListening(mRecognizerListener);
        }

        @Override
        public void onEndOfSpeech() {
            // 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
            LogUtils.d("结束说话");
        }

        @Override
        public void onResult(RecognizerResult results, boolean isLast) {
            LogUtils.d(results.getResultString());
            textView.setText(results.getResultString());
            mIat.stopListening();
            mIat.startListening(mRecognizerListener);
        }

        @Override
        public void onVolumeChanged(int volume, byte[] data) {
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
            // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
            // 若使用本地能力，会话id为null
            //	if (SpeechEvent.EVENT_SESSION_ID == eventType) {
            //		String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
            //		Log.d(TAG, "session id =" + sid);
            //	}
        }
    };
}