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



    @Override
    public void onInterrupt() {

    }
}
