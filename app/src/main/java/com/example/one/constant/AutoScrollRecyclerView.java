package com.example.one.constant;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.recyclerview.widget.RecyclerView;

/**
 * @author :JianTao
 * @date :2022/4/8 14:35
 * @description :
 */
public class AutoScrollRecyclerView extends RecyclerView {

    public AutoScrollRecyclerView(Context context) {
        super(context);
    }

    public AutoScrollRecyclerView(Context context,  AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public AutoScrollRecyclerView(Context context,  AttributeSet attrs) {
        super(context, attrs);
    }

    // 拦截事件；
    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        // 拦截触摸事件；
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        // 消费事件；
        return true;
    }
}

