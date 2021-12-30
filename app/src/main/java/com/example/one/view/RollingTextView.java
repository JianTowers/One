package com.example.one.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;


import com.example.one.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Author：SkySmile
 * Date：2020/1/14
 * Description：跑马灯效果的View，目前可以从下往上、从右往左滚动
 * 跑马灯所有配置文件最好在xml中
 */
public class RollingTextView extends AppCompatTextView {
    //滚动方向
    //不滚动
    public static final int SCROLL_NO = 1;
    //从下往上
    public static final int SCROLL_BT = 2;
    //从右往左
    public static final int SCROLL_RL = 3;

    //滚动速度
    //慢速
    public static final int SPEED_SLOW = 4;
    //中速
    public static final int SPEED_NORMAL = 5;
    //快速
    public static final int SPEED_FAST = 6;
    //特快
    public static final int SPEED_EXPRESS = 7;

    //垂直滚动需要的数据
    private float lineSpace;
    private float verticalSpeed = 0.1f;
    private List<String> textList = new ArrayList<>();
    private StringBuilder textBuilder = new StringBuilder();

    //水平滚动需要的数据
    private float horizontalSpeed = 2f;
    private Rect rect;

    private Paint paint;
    //默认不滚动
    private int scrollType;
    //每次更滚动的距离
    private float scrollStep = 0f;

    public RollingTextView(Context context) {
        this(context, null);
    }

    public RollingTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RollingTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.MarqueeView);
        scrollType = array.getInt(R.styleable.MarqueeView_scrollType, SCROLL_NO);
        int scrollSpeed = array.getInt(R.styleable.MarqueeView_speedType, SPEED_NORMAL);
        int color = array.getColor(R.styleable.MarqueeView_textColor, 0x000000);
        lineSpace = array.getInt(R.styleable.MarqueeView_lineSpace, 0);
        array.recycle();

        setSpeedType(scrollSpeed);

        paint = getPaint();
        paint.setColor(color);
        rect = new Rect();
    }


    @Override
    public void setTextColor(int color) {
        super.setTextColor(color);
        paint.setColor(color);
    }

    /**
     * 设置滚动方向
     *
     * @param scrollType 滚动方向
     */
    public void setScrollType(int scrollType) {
        this.scrollType = scrollType;
        invalidate();
    }


    /**
     * 设置滚动速度
     *
     * @param speedType 滚动速度
     */
    public void setSpeedType(int speedType) {
        switch (speedType) {
            case SPEED_SLOW:
                verticalSpeed = 0;
                horizontalSpeed = 0;
                break;
            case SPEED_NORMAL:
                verticalSpeed = 0.2f;
                horizontalSpeed = 1f;
                break;
            case SPEED_FAST:
                verticalSpeed = 1.2f;
                horizontalSpeed = 6f;
                break;
            case SPEED_EXPRESS:
                verticalSpeed = 1.6f;
                horizontalSpeed = 8f;
                break;
            default:
                break;
        }
        invalidate();
    }

    /**
     * 设置行高
     *
     * @param lineSpace 行高
     */
    public void setLineSpace(float lineSpace) {
        this.lineSpace = lineSpace;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        String text = getText().toString();
        if (!TextUtils.isEmpty(text) && scrollType == SCROLL_BT) {
            //由下往上滚动需要测量高度
            setTextList(widthMeasureSpec, text);
        }
        invalidate();
    }

    /**
     * 根据TextView宽度和字体大小，计算显示的行数。
     *
     * @param widthMeasureSpec 测量模式
     * @param text             文本
     */
    private void setTextList(int widthMeasureSpec, String text) {
        textList.clear();
        float width = MeasureSpec.getSize(widthMeasureSpec);
        float length = 0;
        for (int i = 0; i < text.length(); i++) {
            if (length <= width) {
                textBuilder.append(text.charAt(i));
                length += paint.measureText(text.substring(i, i + 1));
                if (i == text.length() - 1) {
                    if (length <= width) {
                        textList.add(textBuilder.toString());
                    } else {
                        if (textBuilder.toString().length() == 1) {
                            //每行最多显示一个字
                            textList.add(text.substring(text.length() - 1));
                        } else {
                            //去掉最后一个字，否则最后一个字显示不完整
                            textList.add(textBuilder.toString().substring(0, textBuilder.toString().length() - 1));
                            //最后一个字单独一行
                            textList.add(text.substring(text.length() - 1));
                        }
                    }
                }
            } else {
                if (textBuilder.toString().length() == 1) {
                    //每行最多显示一个字
                    textList.add(textBuilder.toString());
                    textBuilder.delete(0, textBuilder.length());
                    i--;
                    length = 0;
                } else {
                    //去掉最后一个字，否则最后一个字显示不完整
                    textList.add(textBuilder.toString().substring(0, textBuilder.toString().length() - 1));
                    textBuilder.delete(0, textBuilder.length() - 1);
                    i--;
                    length = paint.measureText(text.substring(i, i + 1));
                }
            }
        }
        //清空textBuilder
        textBuilder.delete(0, textBuilder.length());
    }


    @Override
    public void onDraw(Canvas canvas) {
        String text = getText().toString();
        if (TextUtils.isEmpty(text)) {
            super.onDraw(canvas);
            return;
        }
        switch (scrollType) {
            case SCROLL_NO:
                super.onDraw(canvas);
                break;
            case SCROLL_BT:
                //从下往上滚动，首次不显示文字，后续从下往上显示
                float textSize = paint.getTextSize();
                for (int i = 0; i < textList.size(); i++) {
                    float currentY = getHeight() + (i + 1) * textSize - scrollStep;
                    if (i > 0) {
                        currentY = currentY + i * lineSpace;
                    }
                    canvas.drawText(textList.get(i), 0, currentY, paint);
                }
                scrollStep = scrollStep + verticalSpeed;
                if (scrollStep >= getHeight() + textList.size() * textSize + (textList.size() - 1) * lineSpace) {
                    scrollStep = 0;
                }

                invalidate();
                break;
            case SCROLL_RL:
                //从右向左滚动，首次不显示文字，后续每次往左偏移speed像素
                paint.getTextBounds(text, 0, text.length(), rect);
                int textWidth = rect.width();
                int textHeight = rect.height();
                int viewWidth = getWidth();
                float currentX = viewWidth - scrollStep;
                canvas.drawText(text, currentX, textHeight, paint);
                scrollStep = scrollStep + horizontalSpeed;
                if (scrollStep >= viewWidth + textWidth) {
                    scrollStep = 0;
                }
                invalidate();
                break;
            default:
                break;
        }
    }
}
