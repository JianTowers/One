package com.example.one.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.one.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 简介：自定义验证码输入框 (组合+自绘)
 * 作者：游丰泽
 * 主要功能: （
 * 以下功能涉及到盒子样式的改变，均可单独控制功能的盒子样式,默认为画笔，可自定设置backgroundDrawable替代）
 *
 * mBox_notInput_backgroundDrawable;//盒子未输入内容背景Drawable
 * mBox_hasInput_backgroundDrawable;//盒子已输入内容背景Drawable
 * mBox_highLight_backgroundDrawable;//盒子高亮背景Drawable
 * mBox_locked_backgroundDrawable;//盒子锁定状态下背景Drawable
 *
 * mEnableHideCode 是否隐藏输入内容
 * mEnableHighLight 是否开启高亮
 * mEnableCursor 是否开启光标
 * mEnableHideNotInputBox 是否将没有输入内容的盒子隐藏
 * mEnableSoftKeyboardAutoClose 开关自动关闭软键盘
 * mEnableSoftKeyboardAutoShow 开关自动展现软键盘
 * mEnableLockCodeTextIfMaxCode 开关输入内容满足长度后是否锁定
 */

public class PasswordView extends LinearLayout {
    private final static String TAG= PasswordView.class.getName();
    private Context mContext;
    private int measureWidthMode =0;
    private int measureWidthSize =0;
    private int measureHeightMode =0;
    private int measureHeightSize =0;

    private boolean mEnableHideCode =false;//是否隐藏输入code
    private boolean mEnableHighLight=false;//是否开启高亮
    private boolean mEnableCursor =false;//是否开启光标
    private boolean mEnableHideNotInputBox=false;//是否将没有输入内容的盒子隐藏
    private boolean mEnableSoftKeyboardAutoShow=true;//是否自动打开软键盘
    private boolean mEnableSoftKeyboardAutoClose=true;//是否自动关闭软键盘（输入内容长度==最大长度）
    private boolean mEnableLockCodeTextIfMaxCode =false;//是否限制输满后锁定view
    //默认设置-盒子画笔相关
    private Paint mBox_default_paint;//笔刷
    private final String mBox_default_hideText ="*";//隐藏输入过的盒子展示的内容
    private int mBox_default_hasInputColor =Color.BLACK; //盒子输入过的颜色
    private int mBox_default_notInputColor =Color.GRAY; //盒子未输入过的颜色
    private int mBox_default_highLightColor =Color.CYAN; //盒子高亮的颜色
    private int mBox_default_cursorColor =Color.BLACK; //盒子光标的颜色
    private int mBox_default_lockColor =Color.GRAY; //盒子锁定状态下的颜色
    private int mBox_default_strokeWidth =1; //盒子宽度
    private float mBox_default_radius =5f;//圆弧半径
    //输入框样式
    private final int TEXT_INPUT_TYPE_NUMBER=200, TEXT_INPUT_TYPE_PHONE =201, TEXT_INPUT_TYPE_TEXT =202,TEXT_INPUT_TYPE_DATETIME=203;
    private boolean mIsLocked=false;
    private boolean mIsCodeFull =false;
    private int mIsFirstTime=0;
    private int mBox_nextInputIndex =0;//待输入盒子的下坐标
    private OnResultListener mOnResultListener;
    private InputMethodManager inputMethodManager;
    private EditText mEditText;
    private String mEnableHideCode_text;//隐藏输入code-显示的内容
    private int mViewBackground=Color.TRANSPARENT;//背景Drawable
    //盒子
    private RectF mBoxRectF;//矩形（绘制位置）
    private int mBox_setNumber =4;//数量
    private int mBox_setSize =50;//大小
    private int mBox_setMargin =10;//盒子之间的间距
    private Drawable mBox_notInput_backgroundDrawable;//盒子未输入内容背景Drawable
    private Drawable mBox_hasInput_backgroundDrawable;//盒子已输入内容背景Drawable
    private Drawable mBox_highLight_backgroundDrawable;//盒子高亮背景Drawable
    private Drawable mBox_locked_backgroundDrawable;//盒子锁定状态下背景Drawable
    //文字
    private Paint mPaintText;//笔刷
    private Rect mTextRect;//矩形（绘制位置）
    private String[] mCodeArray;//输入Code内容
    private int mTextColor=Color.BLACK;//颜色
    private int mTextSize=10;//大小
    private int mTextInputType=TEXT_INPUT_TYPE_NUMBER;//类型
    private boolean mTextBold=true;//粗细
    //光标
    private Timer mCursorTimer;//定时器
    private TimerTask mCursorTimerTask;//定时器任务
    private Drawable mCursorBackgroundDrawable;//背景
    private int mCursorColor;//颜色
    private int mCursorHeight =1;//上下边距
    private int mCursorWidth =1;//上下边距
    private int mCursorFrequency=500;//闪烁频率
    private boolean mCursorDisplayingByTimer =false;//显示光标-定时器-闪烁效果
    private boolean mCursorDisplayingByIndex =false;//显示光标-第一次下坐标

    public PasswordView(@NonNull Context context) {
        super(context);
        initial(context);
    }
    public PasswordView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray=context.obtainStyledAttributes(attrs, R.styleable.PasswordView);

        //自动弹出键盘
        mEnableSoftKeyboardAutoShow=typedArray.getBoolean(R.styleable.PasswordView_password_enableSoftKeyboardAutoShow, mEnableSoftKeyboardAutoShow);
        //自动隐藏键盘
        mEnableSoftKeyboardAutoClose =typedArray.getBoolean(R.styleable.PasswordView_password_enableSoftKeyboardAutoClose, mEnableSoftKeyboardAutoClose);
        //是否隐藏输入内容
        mEnableHideCode =typedArray.getBoolean(R.styleable.PasswordView_password_enableHideCode, mEnableHideCode);
        //隐藏输入的内容,显示设置的文案
        mEnableHideCode_text =typedArray.getString(R.styleable.PasswordView_password_enableHideCodeSetText);
        //是否将没有输入内容的盒子隐藏
        mEnableHideNotInputBox =typedArray.getBoolean(R.styleable.PasswordView_password_enableHideBoxWhenNotInput, mEnableHideNotInputBox);
        //是否绘制高亮盒子
        mEnableHighLight=typedArray.getBoolean(R.styleable.PasswordView_password_enableHighLight,mEnableHighLight);
        //是否绘制光标
        mEnableCursor =typedArray.getBoolean(R.styleable.PasswordView_password_enableCursor, mEnableCursor);
        //是否锁定组件当输入满长度后(禁止点击,可以主动setUnLock()调解除锁定)
        mEnableLockCodeTextIfMaxCode =typedArray.getBoolean(R.styleable.PasswordView_password_enableLockTextView, mEnableLockCodeTextIfMaxCode);
        //View背景Drawable
        mViewBackground =typedArray.getResourceId(R.styleable.PasswordView_password_setViewBackground,Color.TRANSPARENT);
        //文字颜色
        mTextColor=typedArray.getColor(R.styleable.PasswordView_password_text_setColor,mTextColor);
        //文字大小
        mTextSize=typedArray.getInt(R.styleable.PasswordView_password_text_setSize,mTextSize);
        //文字输入框类型
        mTextInputType=typedArray.getInt(R.styleable.PasswordView_password_text_setInputType,mTextInputType);
        //文字粗细
        mTextBold=typedArray.getBoolean(R.styleable.PasswordView_password_text_setIsBold,mTextBold);
        //设置盒子总数量
        mBox_setNumber =typedArray.getInt(R.styleable.PasswordView_password_box_setNumber, mBox_setNumber);
        //设置盒子间距
        mBox_setMargin =typedArray.getInt(R.styleable.PasswordView_password_box_setMargin, mBox_setMargin);
        //设置盒子大小
        mBox_setSize =typedArray.getInt(R.styleable.PasswordView_password_box_setSize, mBox_setSize);
        //设置盒子-未输入的背景Drawable
        mBox_notInput_backgroundDrawable =typedArray.getDrawable(R.styleable.PasswordView_password_box_notInput_backgroundDrawable);
        //设置盒子-输入后的背景Drawable
        mBox_hasInput_backgroundDrawable =typedArray.getDrawable(R.styleable.PasswordView_password_box_hasInput_backgroundDrawable);
        //设置盒子-高亮的背景Drawable
        mBox_highLight_backgroundDrawable =typedArray.getDrawable(R.styleable.PasswordView_password_box_highLight_backgroundDrawable);
        //设置盒子-锁定状态下的背景Drawable
        mBox_locked_backgroundDrawable =typedArray.getDrawable(R.styleable.PasswordView_password_box_locked_backgroundDrawable);
        //设置光标高度
        mCursorHeight =typedArray.getInt(R.styleable.PasswordView_password_cursorHeight,mCursorHeight);
        //设置光标宽度
        mCursorWidth =typedArray.getInt(R.styleable.PasswordView_password_cursorWidth,mCursorWidth);
        //设置光标闪烁频率
        mCursorFrequency=typedArray.getInt(R.styleable.PasswordView_password_cursorFrequencyMillisecond,mCursorFrequency);
        //设置光标颜色
        mCursorColor=typedArray.getInt(R.styleable.PasswordView_password_cursorColor,mBox_default_cursorColor);
        //设置光标背景
        mCursorBackgroundDrawable =typedArray.getDrawable(R.styleable.PasswordView_password_cursorBackgroundDrawable);
        //回收
        typedArray.recycle();
        //初始化
        initial(context);
    }

    //测量-CodeText大小
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measureWidthMode =MeasureSpec.getMode(widthMeasureSpec);
        measureWidthSize =MeasureSpec.getSize(widthMeasureSpec);
        measureHeightMode =MeasureSpec.getMode(heightMeasureSpec);
        measureHeightSize =MeasureSpec.getSize(heightMeasureSpec);
        if(measureWidthMode==MeasureSpec.AT_MOST && measureHeightMode==MeasureSpec.AT_MOST){
            //宽高均未声明绝对值
            //组件宽 = (盒子大小*数量)+（盒子边距*(数量-1))+画笔宽度
            //组件高 = (盒子大小)
            measureWidthSize = mBox_setSize * (mBox_setNumber) + mBox_setMargin * (mBox_setNumber - 1) ;
            measureHeightSize = mBox_setSize;
        }else if(measureWidthMode==MeasureSpec.EXACTLY && measureHeightMode==MeasureSpec.EXACTLY){
            //宽高均声明了绝对值
            //只需计算盒子大小= (测量高-（盒子边距*（数量-1）+画笔宽度）/ 盒子数量)
            mBox_setSize =(int)((measureWidthSize -  mBox_setMargin * (mBox_setNumber - 1))/(mBox_setNumber));
        }else if(measureWidthMode==MeasureSpec.EXACTLY && measureHeightMode==MeasureSpec.AT_MOST){
            //只声明了宽的绝对值，高未声明
            mBox_setSize =(int)((measureWidthSize -  mBox_setMargin * (mBox_setNumber - 1))/(mBox_setNumber));
        }else if(measureHeightMode==MeasureSpec.EXACTLY && measureWidthMode==MeasureSpec.AT_MOST){
            //只声明了高的绝对值，宽未声明
            mBox_setSize =(int)((measureWidthSize -  mBox_setMargin * (mBox_setNumber - 1))/(mBox_setNumber));
        }
        setMeasuredDimension(measureWidthSize, measureHeightSize);
    }

    //初始化-CodeText
    @SuppressLint("ResourceType")
    private void initial(Context context){
        this.mContext=context;
        try{
            Drawable drawable= getResources().getDrawable(mViewBackground);
            this.setBackground(drawable);
        }catch (Exception e){
            this.setBackgroundColor(mViewBackground);
        }
        mCodeArray =new String[mBox_setNumber];
        for(int i=0;i<mCodeArray.length;i++){
            mCodeArray[i]="";
        }
        if(null==this.mEnableHideCode_text){
            this.mEnableHideCode_text = mBox_default_hideText;
        }else if(this.mEnableHideCode_text.length()>0) {
            this.mEnableHideCode_text = mEnableHideCode_text.substring(0, 1);
        }
        mCursorTimerTask = new TimerTask() {
            @Override
            public void run() {
                mCursorDisplayingByTimer = !mCursorDisplayingByTimer;
                postInvalidate();
            }
        };
        mCursorTimer = new Timer();
        initialEditText();
        initialPaint();
        initialBoxAndRectPosition();
        setOnLayoutListener(mEditText);
        setOnTouchListener(this);
    }

    //初始化EdiText
    private void initialEditText(){
        this.mEditText=new EditText(this.getContext());
        LayoutParams layoutParams=new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        this.mEditText.setLayoutParams(layoutParams);
        this.mEditText.setBackgroundColor(Color.TRANSPARENT);
        this.mEditText.setTextColor(Color.TRANSPARENT);
        this.addView(mEditText);
        this.mEditText.setWidth(1);
        this.mEditText.setHeight(1);
        this.mEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(mBox_setNumber)});
        switch (mTextInputType){
            case TEXT_INPUT_TYPE_NUMBER:
                this.mEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
                break;
            case TEXT_INPUT_TYPE_PHONE:
                this.mEditText.setInputType(InputType.TYPE_CLASS_PHONE);
                break;
            case TEXT_INPUT_TYPE_TEXT:
                this.mEditText.setInputType(InputType.TYPE_CLASS_TEXT);
                break;
            case TEXT_INPUT_TYPE_DATETIME:
                this.mEditText.setInputType(InputType.TYPE_CLASS_DATETIME);
                break;
        }
        this.mEditText.setSingleLine();
        this.mEditText.setCursorVisible(false);
        inputMethodManager = (InputMethodManager) this.mEditText.getContext().getSystemService(this.mEditText.getContext().INPUT_METHOD_SERVICE);
        this.mEditText.addTextChangedListener(new mWatcher());
    }
    //初始化-盒子和位置
    private void initialBoxAndRectPosition(){
        this.mBox_setSize = dip2px(mContext, mBox_setSize);
        this.mBox_setMargin = dip2px(mContext, mBox_setMargin);
        this.mBox_default_radius = dip2pxFloat(mContext, mBox_default_radius);
        this.mBoxRectF=new RectF();
        this.mTextRect=new Rect();
    }
    //初始化-笔刷
    private void initialPaint(){
        //文字
        this.mPaintText=new Paint(Paint.ANTI_ALIAS_FLAG);
        this.mPaintText.setStyle(Paint.Style.FILL);
        this.mPaintText.setTextSize(dip2px(this.getContext(),mTextSize)*2);
        this.mPaintText.setColor(mTextColor);
        this.mPaintText.setFakeBoldText(mTextBold);
        //盒子
        this.mBox_default_paint =new Paint(Paint.ANTI_ALIAS_FLAG);
        this.mBox_default_paint.setStyle(Paint.Style.STROKE);
        this.mBox_default_paint.setStrokeWidth(dip2px(mContext, mBox_default_strokeWidth));

    }
    //监听点击事件-打开弹窗
    private void setOnTouchListener(View view){
        if(null != view) {
            view.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(event.getAction()==MotionEvent.ACTION_UP && (!mIsLocked || !mEnableLockCodeTextIfMaxCode )) {
                        openSoftKeyboard(mEditText);
                    }
                    return true;
                }
            });
        }
    }

    class mWatcher implements TextWatcher{
        @Override
        public void beforeTextChanged(CharSequence text, int start, int count, int after) {
        }
        @Override
        public void onTextChanged(CharSequence text, int start, int end, int count) {
        }
        @Override
        public void afterTextChanged(Editable text) {
            mBox_nextInputIndex =text.length(); //高亮盒子下坐标=当前输入内容长度
            if(null!= mCodeArray ) {
                for (int i = 0; i< mBox_setNumber; i++){
                    if(i<=text.length()-1) {
                        mCodeArray[i] = text.toString().substring(i, i + 1);
                    }else {
                        mCodeArray[i] = "";
                    }
                }
                mCursorDisplayingByIndex=true;
                if( text.length()== mBox_setNumber){ //内容长度与盒子数量一致->返回回调结果
                    mIsCodeFull = true;
                    if(null!=mOnResultListener) {
                        mOnResultListener.finish(text.toString());
                    }
                    if(mEnableSoftKeyboardAutoClose || mEnableLockCodeTextIfMaxCode){
                        closeSoftKeyboard(mEditText);
                    }
                    mIsLocked = true ;
                }else {
                    if(null!=mOnResultListener) {
                        mOnResultListener.typing(text.toString());
                    }
                }
                postInvalidate();

            }
        }
    }

    //锁定CodeText
    public void setOnLock(){
        mEnableLockCodeTextIfMaxCode=true;
        mIsLocked=true;
    }
    //解除锁定CodeText
    public void setUnLock(){
//        mEnableLockCodeTextIfMaxCode=false;
        if(mIsCodeFull) {
            openSoftKeyboard(mEditText);
            mIsLocked=false;
        }
    }

    //监听View是否渲染完成,如果开启了自动弹出软键盘,则弹出
    private void setOnLayoutListener(final View view){
        if(null != view) {
            view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (!mIsCodeFull && mIsFirstTime <= 3 && mEnableSoftKeyboardAutoShow) {
                        openSoftKeyboard(view);
                        mIsFirstTime++;
                    }
                }
            });
        }
    }

    //画布-绘制板
    @Override
    protected void onDraw(Canvas canvas) {
        for (int i = 0; i < mBox_setNumber; i++) {
            mBoxRectF.left   = (mBox_setSize + mBox_setMargin) * i;
            mBoxRectF.top    = 0;
            mBoxRectF.right  = mBoxRectF.left + mBox_setSize;
            mBoxRectF.bottom = getHeight();
            if( (mEnableHighLight||mEnableCursor) && i == mBox_nextInputIndex){ //如果开启了高亮或鼠标 且 i==待输入index
                onDrawHighLightCursor(canvas); //绘制盒子 - 高亮或光标
            } else if (mCodeArray[i].length()>=1) { //如果盒子内容的长度>=1,则视为已输入过内容的盒子
                onDrawHasInput(canvas,i); //绘制盒子 - 已输入过内容
            } else { //未输入过内容的盒子
                onDrawNotInput(canvas); //绘制盒子 - 未输入过内容
            }
        }

    }
    //绘制盒子 - 未输入过内容
    private void onDrawNotInput(Canvas canvas) {
        if(!mEnableHideNotInputBox) { //如果没有开启隐藏 未输入内容盒子
            if (null != mBox_notInput_backgroundDrawable) {  //如果有设置drawable，则绘制drawable
                mBox_notInput_backgroundDrawable.setBounds((int) mBoxRectF.left, (int) mBoxRectF.top, (int) mBoxRectF.right, (int) mBoxRectF.bottom);
                mBox_notInput_backgroundDrawable.draw(canvas);
            } else {
                mBox_default_paint.setColor(mBox_default_notInputColor);
                canvas.drawRoundRect(mBoxRectF, mBox_default_radius, mBox_default_radius, mBox_default_paint);
            }
        }
    }
    //绘制盒子 - 已输入过内容 boxAfter样式
    private void onDrawHasInput(Canvas canvas,int i){
        if(mIsLocked && mEnableLockCodeTextIfMaxCode){ //如果开启了输入完毕锁定内容,则绘制boxLock样式
            if(null!= mBox_locked_backgroundDrawable){//如果有设置高亮drawable,则绘制drawable,没有则用画笔绘制
                mBox_locked_backgroundDrawable.setBounds((int)mBoxRectF.left,(int)mBoxRectF.top,(int)mBoxRectF.right,(int)mBoxRectF.bottom);
                mBox_locked_backgroundDrawable.draw(canvas);
            }else {
                mBox_default_paint.setColor(mBox_default_lockColor);
                canvas.drawRoundRect(mBoxRectF, mBox_default_radius, mBox_default_radius, mBox_default_paint);
            }
        }else { //没有开启锁定,绘制正常的boxAfter样式
            if(null!= mBox_hasInput_backgroundDrawable) {  //如果有设置drawable，则绘制drawable
                mBox_hasInput_backgroundDrawable.setBounds((int)mBoxRectF.left,(int)mBoxRectF.top,(int)mBoxRectF.right,(int)mBoxRectF.bottom);
                mBox_hasInput_backgroundDrawable.draw(canvas);
            }else {
                mBox_default_paint.setColor(mBox_default_hasInputColor);
                canvas.drawRoundRect(mBoxRectF, mBox_default_radius, mBox_default_radius, mBox_default_paint);
            }

        }

        //绘制输入的内容文字
        mPaintText.getTextBounds(mEnableHideCode ? mEnableHideCode_text : mCodeArray[i], 0, mCodeArray[i].length(), mTextRect);
        canvas.drawText(mEnableHideCode ? mEnableHideCode_text : mCodeArray[i], (mBoxRectF.left + mBoxRectF.right) / 2 - (mTextRect.left + mTextRect.right) / 2, (mBoxRectF.top + mBoxRectF.bottom) / 2 - (mTextRect.top + mTextRect.bottom) / 2, mPaintText);

    }
    //绘制盒子 - 高亮或光标
    private void onDrawHighLightCursor(Canvas canvas){
        if(null!= mBox_highLight_backgroundDrawable) {  //如果有设置高亮drawable,则绘制drawable,没有则用画笔绘制
            mBox_highLight_backgroundDrawable.setBounds((int)mBoxRectF.left,(int)mBoxRectF.top,(int)mBoxRectF.right,(int)mBoxRectF.bottom);
            mBox_highLight_backgroundDrawable.draw(canvas);
        }else {
            mBox_default_paint.setColor(mBox_default_highLightColor);
            canvas.drawRoundRect(mBoxRectF, mBox_default_radius, mBox_default_radius, mBox_default_paint);
        }
        if(mEnableCursor) { //如果开启了光标,则绘制
            onDrawCursor(canvas, mBox_default_paint, mBoxRectF);
        }
    }
    //绘制-光标
    private void onDrawCursor(Canvas canvas,Paint paint,RectF rectF){
        if(null!= mCursorBackgroundDrawable){
            mCursorBackgroundDrawable.setBounds(
                    (int)((rectF.left + rectF.right) / 2 - mCursorWidth),
                    (int)(mCursorHeight <= 1 ? (rectF.top + rectF.bottom) / 4:mCursorHeight ),
                    (int)((rectF.left + rectF.right) / 2 + mCursorWidth),
                    (int) (rectF.bottom - (mCursorHeight <= 1 ? (rectF.top + rectF.bottom) / 4:mCursorHeight ))
            );
            if((mCursorDisplayingByTimer || mCursorDisplayingByIndex) ){
                mCursorBackgroundDrawable.draw(canvas);
            }
        }else {
            paint.setColor((mCursorDisplayingByTimer || mCursorDisplayingByIndex) ? mCursorColor : Color.TRANSPARENT);
            canvas.drawRect(
                    (float) ((rectF.left + rectF.right) / 2 - mCursorWidth),
                    (float) (mCursorHeight <= 1 ? (rectF.top + rectF.bottom) / 4:mCursorHeight ),
                    (float) ((rectF.left + rectF.right) / 2 + mCursorWidth),
                    (float) (rectF.bottom - (mCursorHeight <= 1 ? (rectF.top + rectF.bottom) / 4 :mCursorHeight))
                    , paint);
        }
        mCursorDisplayingByIndex=false;
    }
    //开始计时器，开始光标闪烁
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if(mEnableCursor) {
            if(null!=mCursorTimer) {
                mCursorTimer.scheduleAtFixedRate(mCursorTimerTask, 0, mCursorFrequency);
            }
        }
    }
    //停止计时器，停止光标闪烁
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mCursorTimer.cancel();
    }
    //打开软键盘
    public void openSoftKeyboard(View view){
        if(null != view ) {
            view.setFocusable(true);
            view.setFocusableInTouchMode(true);
            view.requestFocus();
            inputMethodManager.showSoftInput(view, 0);
        }

    }
    //关闭软键盘
    public void closeSoftKeyboard(View view){
        if(null != view) {
            if (mEnableSoftKeyboardAutoClose || mIsLocked || mEnableLockCodeTextIfMaxCode) {
                view.clearFocus();
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }
    //接口回调输入结果
    public interface OnResultListener {
        void finish(String result);
        void typing(String typing);
    }

    //监听接口回调
    public void setOnResultListener(OnResultListener onResultListener){
        mOnResultListener=onResultListener;
    }

    private int dip2px(Context context, float dipValue) {
        return (int) (dipValue * (context.getResources().getDisplayMetrics().density) + 0.5f);
    }
    private float dip2pxFloat(Context context, float dipValue) {
        return (float) (dipValue * (context.getResources().getDisplayMetrics().density) + 0.5f);
    }

}
