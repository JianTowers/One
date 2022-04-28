//package com.example.one.act;
//
//import android.media.AudioFormat;
//import android.media.AudioRecord;
//import android.media.MediaRecorder;
//
//import com.kangxu.speechrecognition.utils.LogUtils;
//import com.kangxu.speechrecognition.utils.PathUtils;
//import com.kangxu.speechrecognition.utils.TimeUtils;
////import com.kangxu.webrtc.WebRtcUtils;
//
//import org.java_websocket.client.WebSocketClient;
//
////import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
////import java.nio.ByteBuffer;
////import java.nio.ByteOrder;
//import java.util.Arrays;
//
///**
// * PCM录音管理
// *
// * Created by yaojian on 2021/5/26 20:36
// */
//public class AudioRecord2Manager {
//
//    private static final String TAG = "AudioRecord2Manager";
//
//    // 音频输入-麦克风
//    private final static int AUDIO_INPUT = MediaRecorder.AudioSource.MIC;
//    // 采用频率
//    // 44100是目前的标准，但是某些设备仍然支持22050，16000，11025
//    // 采样频率一般共分为22.05KHz、44.1KHz、48KHz三个等级
//    private final static int AUDIO_SAMPLE_RATE = 16000;
//    // 声道 单声道
//    private final static int AUDIO_CHANNEL = AudioFormat.CHANNEL_IN_MONO;
//    // 编码
//    private final static int AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
//    // 缓冲区字节大小
//    private int bufferSizeInBytes = 0;
//    // 录音对象
//    private AudioRecord audioRecord;
//    // 录音状态
//    private Status status = Status.STATUS_NO_READY;
//    // 音频处理线程
//    private Thread audioThread;
//    // 暂停
//    private volatile boolean isPause = false;
//    private volatile boolean isRecog = false;
//
//    public enum Status {
//        //未开始
//        STATUS_NO_READY,
//        //预备
//        STATUS_READY,
//        //录音
//        STATUS_START,
//        //暂停
//        STATUS_PAUSE,
//        //停止
//        STATUS_STOP
//    }
//
//    public Status getStatus() {
//        return status;
//    }
//
//    public boolean init() {
//        bufferSizeInBytes = AudioRecord.getMinBufferSize(AUDIO_SAMPLE_RATE, AUDIO_CHANNEL, AUDIO_ENCODING);
//        bufferSizeInBytes = 1280;
//        LogUtils.d(TAG, "bufferSizeInBytes " + bufferSizeInBytes);
//        audioRecord = new AudioRecord(AUDIO_INPUT, AUDIO_SAMPLE_RATE, AUDIO_CHANNEL, AUDIO_ENCODING, bufferSizeInBytes);
//
//        LogUtils.e(TAG, "====== " + audioRecord.getState());
//        LogUtils.e(TAG, "====== " + audioRecord.getRecordingState());
//
////        WebRtcUtils.webRtcAgcInit(0, 255, AUDIO_SAMPLE_RATE);
////        WebRtcUtils.webRtcNsInit(AUDIO_SAMPLE_RATE, 2);
//
//        status = Status.STATUS_READY;
//        return true;
//    }
//
//    public void startRecord(WebSocketClient client, int channel) {
//        if (status == Status.STATUS_NO_READY) {
//            throw new IllegalStateException("录音尚未初始化,请检查是否禁止了录音权限");
//        }
//        if (status == Status.STATUS_START) {
//            // 已经打开麦克风
//        } else {
//            LogUtils.d(TAG, "打开麦克风");
//            audioRecord.startRecording();
//            status = Status.STATUS_START;
//        }
//        LogUtils.d(TAG, "开始识别");
//        isRecog = true;
//        audioThread = new Thread(() -> {
//            if (channel == 1)
//                audioStreamHandleXF(client);
//            else
//                audioStreamHandleKX(client);
//        });
//        audioThread.start();
//    }
//
//    public void pauseRecord() {
//        isPause = true;
//    }
//
//    public void continueRecord() {
//        isPause = false;
//    }
//
//    public void stopRecord() {
//        if (status == Status.STATUS_NO_READY || status == Status.STATUS_READY) {
//            LogUtils.d(TAG, "录音尚未开始");
//        } else {
//            isRecog = false;
//            if (audioThread != null) {
//                audioThread.interrupt();
//                try {
//                    audioThread.join();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                audioThread = null;
//                LogUtils.d(TAG, "停止识别");
//            }
//        }
//    }
//
//    public void release() {
//        stopRecord();
//        if (audioRecord != null) {
//            audioRecord.release();
//            audioRecord = null;
//        }
//
////        WebRtcUtils.webRtcNsFree();
////        WebRtcUtils.webRtcAgcFree();
//
//        status = Status.STATUS_NO_READY;
//    }
//
//    private void audioStreamHandleXF(WebSocketClient client) {
//        try {
//            // new一个byte数组用来存一些字节数据，大小为缓冲区大小
//            byte[] audiodata = new byte[bufferSizeInBytes];
//            int readsize;
//
//            String filePath = "/mnt/internal_sd/Android/data/com.kangxu.smartphone/files/audio";
//            String fileName = "audio_" + TimeUtils.millis2String(System.currentTimeMillis(), TimeUtils.format_date6);
//            FileOutputStream fos = null;
//            try {
//                String fn = filePath + "/" + fileName;
//                File file = new File(fn);
//                if (file.exists()) {
//                    file.delete();
//                }
//                // 建立一个可存取字节的文件
//                fos = new FileOutputStream(file);
//            } catch (IllegalStateException | FileNotFoundException e) {
//                LogUtils.e(TAG, e.getMessage());
//            }
//
//            // 将录音状态设置成正在录音状态
//            status = Status.STATUS_START;
//
//            long lastTs = 0;
//            while (status == Status.STATUS_START && isRecog) {
////                int len = isPause ? bufferSizeInBytes : 1280;
////                // new一个byte数组用来存一些字节数据，大小为缓冲区大小
////                byte[] audiodata = new byte[len];
//
//                // 读取音频流
//                readsize = audioRecord.read(audiodata, 0, bufferSizeInBytes);
//
////                short[] shortData = new short[audiodata.length >> 1];
////                short[] processData = new short[audiodata.length >> 1];
////                ByteBuffer.wrap(audiodata).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(shortData);
////                short[] nsProcessData = WebRtcUtils.webRtcNsProcess(AUDIO_SAMPLE_RATE, shortData.length, shortData);
////                WebRtcUtils.webRtcAgcProcess(nsProcessData, processData, shortData.length);
////                audiodata = shortsToBytes(processData);
//
//                if (fos != null) {
//                    try {
//                        fos.write(audiodata);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                LogUtils.e(TAG, readsize + "");
//                if (AudioRecord.ERROR_INVALID_OPERATION != readsize) {
//                    if (readsize < bufferSizeInBytes) {
//                        send(client, Arrays.copyOfRange(audiodata, 0, readsize));
//                        break;
//                    }
//
//                    long curTs = System.currentTimeMillis();
//                    if (lastTs == 0) {
//                        lastTs = System.currentTimeMillis();
//                    } else {
//                        long s = curTs - lastTs;
//                        if (s < 40) {
//                            LogUtils.d(TAG, "error time interval: " + s + " ms");
//                        }
//                    }
//                    send(client, audiodata);
//                    // 每隔40毫秒发送一次数据
//                    try {
//                        Thread.sleep(40);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//
//            try {
//                if (fos != null) {
//                    fos.close();// 关闭写入流
//                }
//            } catch (IOException e) {
//                LogUtils.e(TAG, e.getMessage());
//            }
//
//            // 发送结束标识
//            client.send("{\"end\": true}".getBytes());
//        } catch (Exception e){
//            LogUtils.i("音频处理线程关闭");
//        }
//    }
//
//    private void audioStreamHandleKX(WebSocketClient client) {
//        try {
//            // new一个byte数组用来存一些字节数据，大小为缓冲区大小
//            byte[] audiodata = new byte[bufferSizeInBytes];
//            int readsize;
//
//            // 将录音状态设置成正在录音状态
//            status = Status.STATUS_START;
//
//            long lastTs = 0;
//            while (status == Status.STATUS_START && isRecog) {
//                // 读取音频流
//                readsize = audioRecord.read(audiodata, 0, bufferSizeInBytes);
//                if (AudioRecord.ERROR_INVALID_OPERATION != readsize) {
//                    if (readsize < bufferSizeInBytes) {
//                        send(client, Arrays.copyOfRange(audiodata, 0, readsize));
//                        break;
//                    }
//
//                    long curTs = System.currentTimeMillis();
//                    if (lastTs == 0) {
//                        lastTs = System.currentTimeMillis();
//                    } else {
//                        long s = curTs - lastTs;
//                        if (s < 40) {
//                            LogUtils.d(TAG, "error time interval: " + s + " ms");
//                        }
//                    }
//                    send(client, audiodata);
//                    // 每隔40毫秒发送一次数据
//                    try {
//                        Thread.sleep(40);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//
//            // 发送结束标识
//            client.send("{\"signal\": \"end\"}".getBytes());
//        } catch (Exception e){
//            LogUtils.i("音频处理线程关闭");
//        }
//    }
//
//    public void send(WebSocketClient client, byte[] bytes) {
//        if (client.isClosed()) {
//            LogUtils.w(TAG, "client connect closed!");
//        } else {
//            if (isPause) {
//                LogUtils.w(TAG, "client connect isPause!");
//                bytes = new byte[bytes.length];
//            }
//            client.send(bytes);
//        }
//    }
//
//    private byte[] shortsToBytes(short[] data) {
//        byte[] buffer = new byte[data.length * 2];
//        int shortIndex, byteIndex;
//        shortIndex = byteIndex = 0;
//        for (; shortIndex != data.length; ) {
//            buffer[byteIndex] = (byte) (data[shortIndex] & 0x00FF);
//            buffer[byteIndex + 1] = (byte) ((data[shortIndex] & 0xFF00) >> 8);
//            ++shortIndex;
//            byteIndex += 2;
//        }
//        return buffer;
//    }
//
//}
