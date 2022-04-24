package com.example.one.utils;

import com.blankj.utilcode.util.LogUtils;

import java.io.DataInputStream;
import java.io.DataOutputStream;

/**
 * @author :JianTao
 * @date :2022/3/10 14:23
 * @description : 特殊权限
 */
public class RootUtils {

    /**
     * 截屏
     */
    public static void adbScreen() {
        execShellCmd("screencap -p /sdcard/Assistant/xiaomi.jpg");
    }

    /**
     * 点击
     */
    public static void adbClick(int x, int y) {
        execShellCmd("input tap " + x + " " + y);
    }

    /**
     * 打开app
     * 该命令需要被打开的app具有DeFAult权限，一般app不设置此权限或者较难获取启动页名称
     */
    public static void openApp() {
        execShellCmd("am start -n com.xiaomi.smarthome/.SmartHomeMainActivity");
    }

    /**
     * 关闭app
     */
    public static void closeApp() {
        execShellCmd("am force-stop com.xiaomi.smarthome");
    }

    /**
     * 回到主页
     */
    public static void goHome() {
        execShellCmd("input keyevent 3");
    }

    /**
     * 唤醒屏幕执行Power命令
     */
    public static void startPhone() {
        execShellCmd("input keyevent 26");
    }

    /**
     * 滑动解锁屏幕
     */
    public static void swipeScreen() {
        execShellCmd("input swipe 100 800 100 100");
    }

    /**
     * 返回
     */
    public static void goBack() {
        execShellCmd("input keyevent 4");
    }

    /**
     * 执行ADB指令，返回结果
     * 可以跨进程app，需要root
     *
     * @param cmd
     */
    public static String execShellCmd(String cmd) {
        long time = System.currentTimeMillis();
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
        LogUtils.d(System.currentTimeMillis() - time);
        LogUtils.e(result);
        return result;
    }
}
