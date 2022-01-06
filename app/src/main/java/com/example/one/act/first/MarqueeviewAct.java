package com.example.one.act.first;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.one.R;
import com.example.one.view.AnimationTextLayout;
import com.sunfusheng.marqueeview.MarqueeView;
import com.superluo.textbannerlibrary.TextBannerView;

import java.util.ArrayList;
import java.util.List;

public class MarqueeviewAct extends AppCompatActivity {
    private Button btStart, btPause, btResume, btStop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marqueeview);

        MarqueeView marqueeView = (MarqueeView) findViewById(R.id.marqueeView);
        TextBannerView textBannerView = (TextBannerView) findViewById(R.id.textBanner);
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

        List<String> imm = new ArrayList<>();
        imm.add("我的需求不只于此");
        imm.add("里面的布局复杂");
        imm.add("网上的方案已经不满足我的需求");
        imm.add("所以我参考别人的垂直跑马灯");
        imm.add("自己写了一个支持任意布局的跑马灯效果");
        imm.add("代码已上传至");

        marqueeView.startWithList(info);

        textBannerView.setDatas(info);
        textBannerView.stopViewAnimator();

        btStart.setOnClickListener(v -> {
            textBannerView.startViewAnimator();
        });

        btResume.setOnClickListener(v -> {
        });

        btPause.setOnClickListener(v -> {
            textBannerView.stopViewAnimator();
        });

        btStop.setOnClickListener(v -> {
            textBannerView.setDatas(info);
        });
    }

    //        animationTextLayout.setData(info);
//        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(animationTextLayout,"deviantAngle",1f,0f);
//        objectAnimator.setDuration(50000);
//        objectAnimator.setRepeatCount(1);
}