package com.example.one.act.second;

import android.app.ActivityManager;
import android.app.Instrumentation;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.LogUtils;
import com.example.one.App;
import com.example.one.R;
import com.example.one.server.AccessService;
import com.example.one.utils.AccessUtils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

public class InstrumentationAct extends AppCompatActivity {

    Button btSimulation, btSimulationOk;
    int[] location = new int[2];

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

    /**
     * 打开app
     */
    public static void openApp(Context context, String packageName) {
        final Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        context.startActivity(intent);
    }

    /**
     * 检查无障碍服务
     */
    public static boolean isServiceON(Context context, String className) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo>
                runningServices = activityManager.getRunningServices(100);
        if (runningServices.size() < 0) {
            return false;
        }
        for (int i = 0; i < runningServices.size(); i++) {
            ComponentName service = runningServices.get(i).service;
            if (service.getClassName().contains(className)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instrumentation);

        btSimulation = (Button) findViewById(R.id.btSimulation);
        btSimulationOk = (Button) findViewById(R.id.btSimulationOK);

        initService();


        btSimulation.setOnClickListener(v -> {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    SystemClock.sleep(1000);
                    openApp(App.getContext(), "com.example.framework");

//                    AccessUtils.findNodeByContent();

                    SystemClock.sleep(3000);
                    execShellCmd("screencap -p /sdcard/Assistant/tim.jpg");

//                    AccessibilityNodeInfo nodeInfo = AccessUtils.findNodeInfosByText("首页");
//                    Rect rect = new Rect();
//                    nodeInfo.getBoundsInScreen(rect);
//                    String cmd = "input tap "+rect.centerX()+" "+rect.centerY();
//                    SystemClock.sleep(1000);
//                    execShellCmd(cmd);
//
//                    AccessibilityNodeInfo info = nodeInfo.getParent().getParent().getParent();
//                    for (int i=0;i<info.getChildCount();i++){
//                        for (int j = 0;j <info.getChild(i).getChildCount();j++){
//                            LogUtils.d("说明\\n"+info.getChild(i).getChild(j).toString());
//                        }
//                    }


//                    SystemClock.sleep(1000);
//                    AccessibilityNodeInfo nodeInfo2 = AccessUtils.findNodeInfosByText(info,"Android技术周刊");
//                    Rect rect2 = new Rect();
//                    nodeInfo2.getBoundsInScreen(rect2);
//                    String cmd2 = "input tap " + (rect2.centerX()) + " " + rect2.centerY();
//                    SystemClock.sleep(1000);
//                    execShellCmd(cmd2);
                }
            }).start();
        });

        btSimulationOk.setOnClickListener(v -> {
            LogUtils.d("被点击了");
        });
    }

    /**
     * 无障碍服务初始化
     */
    private void initService() {
        if (!isServiceON(this, AccessService.class.getName())) {
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
//        AccessService accessService = new AccessService();
//        if (!AccessUtils.isServiceRunning(accessService)){
//            AccessUtils.openAccessibilityServiceSettings(this);
//        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        btSimulationOk.getLocationOnScreen(location);
        LogUtils.d("x坐标:" + location[0] + "\ny坐标：" + location[1]);
    }

    /**
     * 不可以跨进程app
     */
    public void tap(int x, int y) {
        try {
            Instrumentation inst = new Instrumentation();
            inst.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(),
                    SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, x, y, 0));    //x,y 即是事件的坐标
            inst.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(),
                    SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, x, y, 0));
        } catch (Exception e) {
            LogUtils.d("点击出错");
            LogUtils.d(e);
        }
    }

    /**
     * 获取包名
     * 浏览器 com.android.browser
     */
    private void getAppInfo(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            //获取所有安装的app
            List<PackageInfo> installedPackages = packageManager.getInstalledPackages(0);
            for (PackageInfo info : installedPackages) {
                String packageName = info.packageName;//app包名
                ApplicationInfo ai = packageManager.getApplicationInfo(packageName, 0);
                String appName = (String) packageManager.getApplicationLabel(ai);//获取应用名称
                if (appName.equals("米家")) {
                    LogUtils.d(appName + packageName);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.d(e.toString());
        }
    }

    /**
     * 截屏
     */
    private void saveScreen() {

        //构建Bitmap
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        int w = display.getWidth();
        int h = display.getHeight();
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);

        //获取屏幕
        View view = this.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        bitmap = view.getDrawingCache();
        String path = "/sdcard/Assistant/temp.jpg";

        //保存bitmap
        try {
            File file = new File(path);
//            if (!file.exists()) {
//                file.createNewFile();
//            }
            FileOutputStream fos = new FileOutputStream(file);
            if (null != fos) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, fos);
                fos.flush();
                fos.close();
                LogUtils.d("截屏已保存");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}