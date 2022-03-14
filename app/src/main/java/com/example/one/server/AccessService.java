package com.example.one.server;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.blankj.utilcode.util.LogUtils;
import com.example.one.utils.AccessUtils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.List;

/**
 * @author :JianTao
 * @date :2022/3/8 9:55
 * @description :
 */
public class AccessService extends AccessibilityService {
    AccessService mService;

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        mService = this;
        AccessUtils.mService = this;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
//        LogUtils.d("屏幕信息"+event.getSource().toString());
    }


    /**
     * 执行ADB指令，返回结果
     * 可以跨进程app，需要root
     *
     * @param cmd
     */
    public static String execShellCmd(String cmd) {
        String result = "";
        DataOutputStream dos = null;
        DataInputStream dis = null;
        try {
            Process p = Runtime.getRuntime().exec("su");// 经过Root处理的android系统即有su命令
            dos = new DataOutputStream(p.getOutputStream());
            dis = new DataInputStream(p.getInputStream());

            dos.writeBytes(cmd + "\n");
            dos.flush();
            dos.writeBytes("exit\n");
            dos.flush();
            String line;
            while ((line = dis.readLine()) != null) {
                result += line;
            }
            p.waitFor();
        } catch (Exception e) {

        } finally {
            try {
                if (dos != null) {
                    dos.close();
                }
                if (dis != null) {
                    dis.close();
                }
            } catch (Exception e) {

            }
        }
        return result;
    }

    @Override
    public void onInterrupt() {

    }
}
