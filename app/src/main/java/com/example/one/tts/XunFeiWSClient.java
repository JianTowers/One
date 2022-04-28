//package com.example.one.act;
//
//import android.os.Build;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONArray;
//import com.alibaba.fastjson.JSONObject;
//import com.kangxu.speechrecognition.iflytek.entity.ContentRes;
//import com.kangxu.speechrecognition.interfaces.SpeechRecogListener;
//import com.kangxu.speechrecognition.utils.LogUtils;
//
//import org.java_websocket.client.WebSocketClient;
//import org.java_websocket.drafts.Draft;
//import org.java_websocket.handshake.ServerHandshake;
//
//import java.io.UnsupportedEncodingException;
//import java.net.URI;
//import java.nio.ByteBuffer;
//import java.security.SecureRandom;
//import java.security.cert.CertificateException;
//import java.security.cert.X509Certificate;
//import java.util.Objects;
//import java.util.concurrent.CountDownLatch;
//
//import javax.net.ssl.SSLContext;
//import javax.net.ssl.SSLParameters;
//import javax.net.ssl.TrustManager;
//import javax.net.ssl.X509TrustManager;
//
///**
// * WebSocket连接
// *
// * Created by yaojian on 2021/5/26 20:43
// */
//public class XunFeiWSClient extends WebSocketClient {
//
//    private static final String TAG = "MyWebSocketClient";
//
////    private CountDownLatch handshakeSuccess;
//    private CountDownLatch connectClose;
//
//    private SpeechRecogListener listener;
//
//    public XunFeiWSClient(URI serverUri, Draft protocolDraft, SpeechRecogListener listener,
//                          CountDownLatch handshakeSuccess, CountDownLatch connectClose) {
//        super(serverUri, protocolDraft);
//        this.listener = listener;
//        this.handshakeSuccess = handshakeSuccess;
//        this.connectClose = connectClose;
//        if(serverUri.toString().contains("wss")){
//            trustAllHosts(this);
//        }
//    }
//
//    public void setListener(SpeechRecogListener listener) {
//        this.listener = listener;
//    }
//
//    @Override
//    public void onOpen(ServerHandshake handshake) {
//        LogUtils.d(TAG, "连接建立成功");
//    }
//
//    @Override
//    public void onMessage(String msg) {
//        JSONObject msgObj = JSON.parseObject(msg);
//        String action = msgObj.getString("action");
//        String code = msgObj.getString("code");
//        String desc = msgObj.getString("desc");
//        String sid = msgObj.getString("sid");
//        LogUtils.d(TAG, "Message: action " + action + ", code " + code + ", desc " + desc + ", sid " + sid);
//
//        if (Objects.equals("started", action)) {
//            // 握手成功
//            LogUtils.i(TAG, "握手成功");
//            handshakeSuccess.countDown();
//        } else if (Objects.equals("result", action)) {
//            // 转写结果
//            ContentRes contentRes = getContent(msgObj.getString("data"));
//            LogUtils.d(TAG, "result: code " + contentRes.getType() + ", content " + contentRes.getContent());
//
//            if (listener != null)
//                listener.recogMessage(contentRes);
//        } else if (Objects.equals("error", action)) {
//            // 连接发生错误
//            LogUtils.w(TAG, "连接异常");
//        }
//    }
//
//    @Override
//    public void onError(Exception e) {
//        LogUtils.e(TAG, "连接发生错误：" + e.getMessage());
//        e.printStackTrace();
//
//        //TODO 异常处理
//        if (listener != null)
//            listener.recogErr(e);
//    }
//
//    @Override
//    public void onClose(int arg0, String arg1, boolean arg2) {
//        LogUtils.i(TAG, "连接关闭");
//        connectClose.countDown();
//    }
//
//    @Override
//    public void onMessage(ByteBuffer bytes) {
//        try {
//            LogUtils.i(TAG, "服务端返回：" + new String(bytes.array(), "UTF-8"));
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    protected void onSetSSLParameters(SSLParameters sslParameters) {
//        try {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
//                super.onSetSSLParameters(sslParameters);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void trustAllHosts(XunFeiWSClient appClient) {
//        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
//            @Override
//            public X509Certificate[] getAcceptedIssuers() {
//                return new X509Certificate[]{};
//            }
//            @Override
//            public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
//            }
//            @Override
//            public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
//            }
//        }};
//
//        try {
//            SSLContext sc = SSLContext.getInstance("TLS");
//            sc.init(null, trustAllCerts, new SecureRandom());
//            appClient.setSocketFactory(sc.getSocketFactory());
//        } catch (Exception e) {
//            LogUtils.e(TAG, "SSL设置异常：" + e.getMessage());
//            e.printStackTrace();
//        }
//    }
//
//    // 把转写结果解析为句子
//    public static ContentRes getContent(String message) {
//        StringBuffer resultBuilder = new StringBuffer();
//        int type = 0;
//        try {
//            JSONObject messageObj = JSON.parseObject(message);
//            JSONObject cn = messageObj.getJSONObject("cn");
//            JSONObject st = cn.getJSONObject("st");
//            JSONArray rtArr = st.getJSONArray("rt");
//            type = st.getIntValue("type");
//            for (int i = 0; i < rtArr.size(); i++) {
//                JSONObject rtArrObj = rtArr.getJSONObject(i);
//                JSONArray wsArr = rtArrObj.getJSONArray("ws");
//                for (int j = 0; j < wsArr.size(); j++) {
//                    JSONObject wsArrObj = wsArr.getJSONObject(j);
//                    JSONArray cwArr = wsArrObj.getJSONArray("cw");
//                    for (int k = 0; k < cwArr.size(); k++) {
//                        JSONObject cwArrObj = cwArr.getJSONObject(k);
//                        String wStr = cwArrObj.getString("w");
//                        resultBuilder.append(wStr);
//                    }
//                }
//            }
//        } catch (Exception e) {
//            LogUtils.e(TAG, "文字合成异常：" + e.getMessage());
//            e.printStackTrace();
//            return new ContentRes();
//        }
//        return new ContentRes(type, resultBuilder.toString());
//    }
//
//}
