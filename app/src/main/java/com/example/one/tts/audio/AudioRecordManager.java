package com.example.one.tts.audio;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import com.blankj.utilcode.util.LogUtils;

import org.java_websocket.client.WebSocketClient;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class AudioRecordManager {
    private static final String TAG = "AudioRecordManager";

    // 音频输入-麦克风
    public final static int AUDIO_INPUT = MediaRecorder.AudioSource.MIC;

    //8K采集率
    public final static int AUDIO_SAMPLE_RATE = 16000;

    //格式
    public final static int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;

    //16Bit
    public final static int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;

    // 缓冲区字节大小
    private int bufferSizeInBytes = 0;

    private AudioRecord audioRecord;

    // 录音状态
    private Status status = Status.STATUS_NO_READY;
    // 暂停
    private volatile boolean isPause = false;

    Thread audioHandle;

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

    public Status getStatus() {
        return status;
    }

    public boolean init() {

        // 获得缓冲区字节大小
        bufferSizeInBytes = AudioRecord.getMinBufferSize(AUDIO_SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT);

        // 创建AudioRecord对象
        // MONO单声道，STEREO为双声道立体声
        audioRecord = new AudioRecord(AUDIO_INPUT, AUDIO_SAMPLE_RATE,
                CHANNEL_CONFIG, AUDIO_FORMAT, bufferSizeInBytes);

        status = Status.STATUS_READY;
        return true;
    }

    public void startRecord(WebSocketClient client) {
        LogUtils.d(1);
        if (status == Status.STATUS_NO_READY) {
            throw new IllegalStateException("录音尚未初始化,请检查是否禁止了录音权限");
        }
        if (status == Status.STATUS_START) {
            throw new IllegalStateException("正在录音");
        }
        audioRecord.startRecording();

        // 将录音状态设置成正在录音状态
        status = Status.STATUS_START;

        audioHandle = new Thread(new Runnable() {
            @Override
            public void run() {
                audioStreamHandle(client);
            }
        });
        audioHandle.start();
        LogUtils.d("开始录音");
    }

    public void pauseRecord() {
        isPause = true;
    }

    public void continueRecord() {
        isPause = false;
    }

    public void stopRecord() {
        if (status == Status.STATUS_NO_READY || status == Status.STATUS_READY) {
        } else {
            status =Status.STATUS_STOP;
            if (audioHandle != null) {
                audioHandle.interrupt();
                try {
                    audioHandle.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                audioHandle = null;
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
        status = Status.STATUS_NO_READY;
    }

    private void audioStreamHandle(WebSocketClient client) {
        try {
            // new一个byte数组用来存一些字节数据，大小为缓冲区大小
            byte[] audiodata = new byte[bufferSizeInBytes];

            FileOutputStream fos = null;
            String filePath = "/sdcard/kangxu/data/test.pcm";
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

            int readsize;

            //将录音状态设置成正在录音状态
            status = Status.STATUS_START;
            long lastTs = 0;
            while (status == Status.STATUS_START) {

                if (fos != null) {
                    try {
                        fos.write(audiodata);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if (isPause) {
                    readsize = bufferSizeInBytes;
                    audiodata = new byte[bufferSizeInBytes];
                } else {
                    readsize = audioRecord.read(audiodata, 0, bufferSizeInBytes);
                }
                if (AudioRecord.ERROR_INVALID_OPERATION != readsize) {

                    if (readsize < bufferSizeInBytes) {
                        send(client, Arrays.copyOfRange(audiodata, 0, readsize));
                        break;
                    }

                    long curTs = System.currentTimeMillis();
                    if (lastTs == 0) {
                        lastTs = System.currentTimeMillis();
                    } else {
                        long s = curTs - lastTs;
                        if (s < 40) {
                            Log.d(TAG, "error time interval: " + s + " ms");
                        }
                    }
                    send(client, audiodata);
                    // 每隔100毫秒发送一次数据
                    try {
                        Thread.sleep(100);
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
            // 发送结束标识
            send(client, "{\"action\":\"Stop\"}".getBytes());
        }catch (Exception e){
            LogUtils.d(e.toString());
        }
    }

    public static void send(WebSocketClient client, byte[] bytes) {
        if (client.isClosed()) {
            Log.w(TAG, "client connect closed!");
        } else {
            client.send(bytes);
        }
    }
}
