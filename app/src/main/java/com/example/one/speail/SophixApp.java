package com.example.one.speail;

import android.content.Context;
import android.util.Log;

import androidx.annotation.Keep;

import com.blankj.utilcode.util.LogUtils;
import com.example.one.App;
import com.taobao.sophix.SophixApplication;
import com.taobao.sophix.SophixEntry;
import com.taobao.sophix.SophixManager;
import com.taobao.sophix.listener.PatchLoadStatusListener;

/**
 * @author :JianTao
 * @date :2022/4/1 11:19
 * @description :
 */
public class SophixApp extends SophixApplication {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        initSophix();
    }

    private void initSophix() {
        String appVersion = "1";
//        try {
//            appVersion = this.getPackageManager()
//                    .getPackageInfo(this.getPackageName(), 0)
//                    .versionName;
//        } catch (Exception e) {
//        }
        final SophixManager instance = SophixManager.getInstance();
        instance.setContext(this)
                .setAppVersion(appVersion)
                .setSecretMetaData(null, null, null)
                .setEnableDebug(true)
                .setEnableFullLog()
                .setPatchLoadStatusStub(new PatchLoadStatusListener() {
                    @Override
                    public void onLoad(final int mode, final int code, final String info, final int handlePatchVersion) {
                        Log.e("AAA","mode:" + mode + "\ncode:" + code + "\ninfo:" + info);
                    }
                }).initialize();
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Keep
    @SophixEntry(App.class)
    static class RealApplicationStub {
    }
}
