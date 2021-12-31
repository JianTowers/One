package com.example.one.act;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.widget.Button;

import com.example.one.R;
import com.example.one.view.AnimationTextLayout;
import com.example.one.view.VerticalScrolledListview;
import com.sunfusheng.marqueeview.MarqueeView;

import java.util.ArrayList;
import java.util.List;

public class MarqueeviewAct extends AppCompatActivity {
    private Button btStart,btPause,btResume,btStop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marqueeview);

        MarqueeView marqueeView = (MarqueeView) findViewById(R.id.marqueeView);
        VerticalScrolledListview verticalScrolledListview = (VerticalScrolledListview) findViewById(R.id.VertiaclView);
        AnimationTextLayout animationTextLayout = (AnimationTextLayout) findViewById(R.id.animationText);
        btStart = (Button) findViewById(R.id.bt_animation_start);
        btPause = (Button) findViewById(R.id.bt_animation_pause);
        btResume = (Button) findViewById(R.id.bt_animation_resume);
        btStop = (Button) findViewById(R.id.bt_animation_stop);

        List<String> info = new ArrayList<>();
        info.add("1. 大家好，我是孙福生。");
        info.add("2. 欢迎大家关注我哦！");
        info.add("3. GitHub帐号：");
        info.add("4. 新浪微博：孙福生微博");
        info.add("5. 个人博客：sunfusheng.com");
        info.add("6. 微信公众号：孙福生");
        info.add("1. 大家好，我是孙福生。");
        info.add("2. 欢迎大家关注我哦！");
        info.add("3. GitHub帐号：");
        info.add("4. 新浪微博：孙福生微博");
        info.add("5. 个人博客：sunfusheng.com");
        info.add("6. 微信公众号：孙福生");
        marqueeView.startWithList(info);

        verticalScrolledListview.setData(info);

        animationTextLayout.setData(info);
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(animationTextLayout,"deviantAngle",1f,0f);
        objectAnimator.setDuration(20000);
        objectAnimator.setRepeatCount(1);

        animationTextLayout.setOnClickSize(new AnimationTextLayout.OnClickSize() {
            @Override
            public void onClickStop() {
                objectAnimator.pause();
            }
        });


        btStart.setOnClickListener(v -> {
            objectAnimator.start();
        });

        btResume.setOnClickListener(v -> {
            objectAnimator.resume();
        });

        btPause.setOnClickListener(v -> {
            objectAnimator.pause();
        });

        btStop.setOnClickListener(v -> {
//            objectAnimator.stop();
        });
    }
}