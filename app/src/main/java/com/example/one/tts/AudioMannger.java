package com.example.one.tts;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import com.blankj.utilcode.util.LogUtils;
import com.example.one.tts.rtasr.util.EncryptUtil;

import org.java_websocket.client.WebSocketClient;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

/**
 * @author :JianTao
 * @date :2022/4/24 10:36
 * @description :
 */
public class AudioMannger {

    private static final String TAG = "AndioMannger";

    // 音频输入-麦克风
    private final static int AUDIO_INPUT = MediaRecorder.AudioSource.MIC;
    // 采用频率
    // 44100是目前的标准，但是某些设备仍然支持22050，16000，11025
    // 采样频率一般共分为22.05KHz、44.1KHz、48KHz三个等级
    private final static int AUDIO_SAMPLE_RATE = 16000;
    // 声道 单声道
    private final static int AUDIO_CHANNEL = AudioFormat.CHANNEL_IN_MONO;
    // 编码
    private final static int AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyy-MM-dd HH:mm:ss.SSS");
    // 缓冲区字节大小
    private int bufferSizeInBytes = 0;
    // 录音对象
    private AudioRecord audioRecord;
    // 录音状态
    private AudioMannger.Status status = AudioMannger.Status.STATUS_NO_READY;
    // 音频处理线程
    private Thread audioThread;
    // 暂停
    private volatile boolean isPause = false;
    private volatile boolean isRecog = false;

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

    public static void send(WebSocketClient client, byte[] bytes) {
        if (client.isClosed()) {
            throw new RuntimeException("client connect closed!");
        }

        client.send(bytes);
    }

    public static String getCurrentTimeStr() {
        return sdf.format(new Date());
    }

    public Status getStatus() {
        return status;
    }

    public boolean init() {
        bufferSizeInBytes = AudioRecord.getMinBufferSize(AUDIO_SAMPLE_RATE, AUDIO_CHANNEL, AUDIO_ENCODING);
//        bufferSizeInBytes = 1280;
        LogUtils.d(TAG, "bufferSizeInBytes " + bufferSizeInBytes);
        audioRecord = new AudioRecord(AUDIO_INPUT, AUDIO_SAMPLE_RATE, AUDIO_CHANNEL, AUDIO_ENCODING, bufferSizeInBytes);
        LogUtils.e(TAG, "====== " + audioRecord.getState());
        LogUtils.e(TAG, "====== " + audioRecord.getRecordingState());

        status = AudioMannger.Status.STATUS_READY;
        return true;
    }

    public void startRecode() {
        if (status == Status.STATUS_NO_READY) {
            LogUtils.d("尚未初始化");
        }
        if (status == Status.STATUS_START) {
            LogUtils.d("麦克风已开启");
        } else {
            audioRecord.startRecording();
            status = Status.STATUS_START;
        }
        isRecog = true;
        audioThread = new Thread(new Runnable() {
            @Override
            public void run() {
                audioStreamHandleW();
            }
        });
        audioThread.start();
        LogUtils.d("开始录音");
    }

    public void stopRecord() {
        if (status == AudioMannger.Status.STATUS_NO_READY || status == AudioMannger.Status.STATUS_READY) {
            LogUtils.d("录音尚未开始");
        } else {
            isRecog = false;
            if (audioThread != null) {
                audioThread.interrupt();
                try {
                    audioThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                audioThread = null;
                LogUtils.d(TAG, "停止识别");
            }
        }
    }

    public void release() {
        stopRecord();
        if (audioRecord != null) {
            audioRecord.release();
            audioRecord = null;
        }
        status = AudioMannger.Status.STATUS_NO_READY;
    }

    private void audioStreamHandleW() {
        try {
            // new一个byte数组用来存一些字节数据，大小为缓冲区大小
            byte[] audiodata = new byte[bufferSizeInBytes];
            int readsize;

            String filePath = "/sdcard/kangxu/data/test.pcm";
            FileOutputStream fos = null;
            try {
                String fn = filePath;
                LogUtils.d(fn);
                File file = new File(fn);
                if (file.exists()) {
                    file.delete();
                }
                // 建立一个可存取字节的文件
                fos = new FileOutputStream(file);
            } catch (IllegalStateException | FileNotFoundException e) {
                LogUtils.e(TAG, e.getMessage());
            }


            // 将录音状态设置成正在录音状态
            status = AudioMannger.Status.STATUS_START;

            long lastTs = 0;
            while (status == AudioMannger.Status.STATUS_START && isRecog) {
                // 读取音频流
                readsize = audioRecord.read(audiodata, 0, bufferSizeInBytes);

                if (fos != null) {
                    try {
                        fos.write(audiodata);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                LogUtils.e(TAG, readsize + "");

                if (AudioRecord.ERROR_INVALID_OPERATION != readsize) {
                    if (readsize < bufferSizeInBytes) {
//                        send(client, Arrays.copyOfRange(audiodata, 0, readsize));
                        break;
                    }

                    long curTs = System.currentTimeMillis();
                    if (lastTs == 0) {
                        lastTs = System.currentTimeMillis();
                    } else {
                        long s = curTs - lastTs;
                        if (s < 40) {
                            LogUtils.d(TAG, "error time interval: " + s + " ms");
                        }
                    }
//                    send(client, audiodata);
                    // 每隔40毫秒发送一次数据
                    try {
                        Thread.sleep(40);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }

            try {
                if (fos != null) {
                    fos.close();// 关闭写入流
                }
            } catch (IOException e) {
                LogUtils.e(TAG, e.getMessage());
            }
        } catch (Exception e) {
            LogUtils.i("音频处理线程关闭");
            LogUtils.e(e.toString());
        }
    }


    public enum Status {
        //未开始
        STATUS_NO_READY,
        //预备
        STATUS_READY,
        //录音
        STATUS_START,
        //暂停
        STATUS_PAUSE,
        //停止
        STATUS_STOP
    }
}
