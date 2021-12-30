package com.example.one.act;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;

import com.example.one.R;
import com.example.one.view.RollingTextView;

public class RollingTextAct extends AppCompatActivity {
    private Button button;
    private RollingTextView rollingTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rolling_text);
        button = (Button) findViewById(R.id.bt_RollingChange);
        rollingTextView  = (RollingTextView) findViewById(R.id.tv_Rolling);
        rollingTextView.setText("除了布局文件的优化外，代码中不需要立即显示的视图和动画都做成延迟加载，" +
                "比如AnimationDrawable、TypedArray数组、Typeface、addView等，值得一提的是，" +
                "初始化AnimationDrawable、TypedArray数组和Typeface会很耗时，并且AnimationDrawable特别耗内存，" +
                "如果不是进入界面就需要使用，强烈建议在需要使用的地方再初始化，分开初始化可以大大减小页面初始化的耗时。");
        button.setOnClickListener(v -> {
            rollingTextView.setText("activity 或者 fragment 每次跳转传值的时候，你是不是都很厌烦那种，参数传递。\n" +
                    "那么如果数据极其多的情况下，你的代码将苦不堪言，即使在很好的设计下，也会很蛋疼。那么今天我给大家推荐一个工具\n" +
                    "和咱原生跳转进行比较");
            rollingTextView.requestLayout();
        });
    }
}