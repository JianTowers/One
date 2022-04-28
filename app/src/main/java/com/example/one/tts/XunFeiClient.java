package com.example.one.tts;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.LogUtils;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ServerHandshake;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * @author :JianTao
 * @date :2022/4/24 15:05
 * @description :
 */
public class XunFeiClient extends WebSocketClient {

    private CountDownLatch handshakeSuccess;
    private CountDownLatch connectClose;

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyy-MM-dd HH:mm:ss.SSS");

    public XunFeiClient(URI serverUri, Draft protocolDraft, CountDownLatch handshakeSuccess, CountDownLatch connectClose) {
        super(serverUri, protocolDraft);
        this.handshakeSuccess = handshakeSuccess;
        this.connectClose = connectClose;
        if (serverUri.toString().contains("wss")) {
            trustAllHosts(this);
        }
    }

    @Override
    public void onOpen(ServerHandshake handshake) {
        LogUtils.i("连接建立成功");
    }

    @Override
    public void onMessage(String msg) {
        JSONObject msgObj = JSON.parseObject(msg);
        String action = msgObj.getString("action");
        if (Objects.equals("started", action)) {
            // 握手成功
            LogUtils.d(getCurrentTimeStr()+"\t握手成功！sid:"+msgObj.getString("sid"));
            handshakeSuccess.countDown();
        } else if (Objects.equals("result", action)) {
            // 转写结果t
            LogUtils.d(getCurrentTimeStr() + "\tresult: " + getContent(msgObj.getString("data")));
        } else if (Objects.equals("error", action)) {
            // 连接发生错误
            LogUtils.d("Error:"+msg);
        }
    }

    @Override
    public void onError(Exception e) {
        LogUtils.d(getCurrentTimeStr() + "\t连接发生错误：" + e.getMessage() + ", " + new Date());
        e.printStackTrace();
    }

    @Override
    public void onClose(int arg0, String arg1, boolean arg2) {
        LogUtils.d(getCurrentTimeStr() + "\t链接关闭");
        connectClose.countDown();
    }

    @Override
    public void onMessage(ByteBuffer bytes) {
        try {
            LogUtils.d(getCurrentTimeStr() + "\t服务端返回：" + new String(bytes.array(), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void trustAllHosts(XunFeiClient appClient) {
        LogUtils.d("wss");
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[]{};
            }

            @Override
            public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                // TODO Auto-generated method stub

            }

            @Override
            public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                // TODO Auto-generated method stub

            }
        }};

        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            appClient.setSocket(sc.getSocketFactory().createSocket());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getCurrentTimeStr() {
        return sdf.format(new Date());
    }

    // 把转写结果解析为句子
    public static String getContent(String message) {
        StringBuffer resultBuilder = new StringBuffer();
        try {
            JSONObject messageObj = JSON.parseObject(message);
            JSONObject cn = messageObj.getJSONObject("cn");
            JSONObject st = cn.getJSONObject("st");
            JSONArray rtArr = st.getJSONArray("rt");
            for (int i = 0; i < rtArr.size(); i++) {
                JSONObject rtArrObj = rtArr.getJSONObject(i);
                JSONArray wsArr = rtArrObj.getJSONArray("ws");
                for (int j = 0; j < wsArr.size(); j++) {
                    JSONObject wsArrObj = wsArr.getJSONObject(j);
                    JSONArray cwArr = wsArrObj.getJSONArray("cw");
                    for (int k = 0; k < cwArr.size(); k++) {
                        JSONObject cwArrObj = cwArr.getJSONObject(k);
                        String wStr = cwArrObj.getString("w");
                        resultBuilder.append(wStr);
                    }
                }
            }
        } catch (Exception e) {
            return message;
        }

        return resultBuilder.toString();
    }
}
