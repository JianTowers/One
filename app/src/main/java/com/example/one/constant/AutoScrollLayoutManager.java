package com.example.one.constant;

import android.content.Context;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author :JianTao
 * @date :2022/4/8 14:33
 * @description :
 */
public class AutoScrollLayoutManager extends LinearLayoutManager {

    public AutoScrollLayoutManager(Context context) {
        super(context);
    }

    public AutoScrollLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public AutoScrollLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    //  修改系统默认的滚动速度需要实现该方法；
    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int
            position) {
        LinearSmoothScroller linearSmoothScroller =
                new LinearSmoothScroller(recyclerView.getContext()) {
                    @Override
                    public PointF computeScrollVectorForPosition(int targetPosition) {
                        return AutoScrollLayoutManager.this.computeScrollVectorForPosition
                                (targetPosition);
                    }

                    @Override
                    protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                        // 计算滑动每个像素需要的时间，这里应该与屏幕适配；
                        return 15f / displayMetrics.density;
                    }
                };
        linearSmoothScroller.setTargetPosition(position);
        startSmoothScroll(linearSmoothScroller);

    }

}
