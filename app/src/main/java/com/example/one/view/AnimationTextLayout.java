package com.example.one.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.blankj.utilcode.util.ConvertUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author :JianTao
 * @date :2021/12/31 14:34
 * @description :
 */
public class AnimationTextLayout extends FrameLayout {
    private static final String TAG = "AnimationTextLayout";
    private List<String> tipList;
    private List<Integer> displayList;
    private List<TextView> viewList;
    private List<VirtualPos> virtualPosList;
    private double deviantAngle = 0;

    private boolean isFirst = true;

    private OnClickSize monClickSize;

    public void setOnClickSize(OnClickSize onClickSize) {
        monClickSize = onClickSize;
    }

    public AnimationTextLayout(@NonNull Context context) {
        super(context);
        initView();
    }

    public AnimationTextLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public AnimationTextLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        viewList = new ArrayList<>();
        displayList = new ArrayList<>();
        virtualPosList = new ArrayList<>();
    }

    /**
     * 设置需要显示的数据
     *
     * @param data 需要显示的数据
     */
    public void setData(List<String> data) {
        this.tipList = data;
        initTips();
    }


    private void initTips() {
        while (tipList.size() > viewList.size()) {
            addTipsView();
        }
        refreshTips();
        initVirPos();

        post(new Runnable() {
            @Override
            public void run() {
                initPosition();
                requestLayout();
            }
        });

    }

    /**
     * 计算虚拟位置
     */
    private void initVirPos() {
        virtualPosList.clear();
        for (int i = 0; i < viewList.size(); i++) {
            double angle = (Math.PI * ((double) i / viewList.size()) * 2) + (deviantAngle * Math.PI * 2);
            if (angle > Math.PI * 2) {
                angle -= Math.PI * 2;
            }
            VirtualPos virtualPos = new VirtualPos();
            virtualPos.text = tipList.get(i);
            virtualPos.z = 100 * Math.sin(angle);
            virtualPos.y = 100 * Math.cos(angle);
            virtualPosList.add(virtualPos);
        }
    }

    /**
     * 将虚拟位置转化为实际高度和位置
     */
    private void initPosition() {
        for (int i = 0; i < viewList.size(); i++) {
            TextView textView = viewList.get(i);
            VirtualPos virtualPos = virtualPosList.get(i);
            int realY = (int) ((100 - virtualPos.y) / 200 * getMeasuredHeight());
            FrameLayout.LayoutParams layoutParams = (LayoutParams) textView.getLayoutParams();
            layoutParams.topMargin = realY;
            if (virtualPos.z >= 0) {
                float textSize = (float) ((virtualPos.z) / 100) * 20 + 5;
                if (monClickSize != null && textSize > 22) {
                    if (!isFirst){
                        monClickSize.onClickStop();
                    }
                    isFirst = false;
                }
                Log.d(TAG, "TextSizr==" + textSize);
                textView.setTextSize(textSize);
                textView.setVisibility(VISIBLE);
            } else {
                textView.setVisibility(GONE);
            }

//            Log.d(TAG, "initPosition: y=="+realY);
        }

    }

    private void refreshTips() {
        for (int i = 0; i < viewList.size(); i++) {
            TextView tip = viewList.get(i);
            if (i < tipList.size()) {
                tip.setVisibility(VISIBLE);
                tip.setText(tipList.get(i));
                continue;
            }
            tip.setVisibility(GONE);
        }
    }

    private TextView addTipsView() {
        TextView textView = new TextView(getContext());
        textView.setTextSize(20);
        textView.setTextColor(Color.WHITE);
        textView.setPadding(ConvertUtils.dp2px(5), ConvertUtils.dp2px(3), ConvertUtils.dp2px(5), ConvertUtils.dp2px(3));
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
        layoutParams.rightMargin = ConvertUtils.dp2px(6);
        addView(textView, layoutParams);
        viewList.add(textView);
        return textView;
    }

    /**
     * 虚拟位置，最大x,y,z 最大值为100，最小值为-100
     */
    public static class VirtualPos {
        public double x;
        public double y;
        public double z;
        public String text;
    }

    /**
     * 滚动的偏移值
     *
     * @param deviantAngle 最大为1
     */
    public void setDeviantAngle(float deviantAngle) {
        this.deviantAngle = deviantAngle;
        initVirPos();
        initPosition();
    }

    public double getDeviantAngle() {
        return deviantAngle;
    }

    public interface OnClickSize {
        void onClickStop();
    }
}


