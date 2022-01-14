package com.example.one.act.second;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.widget.ImageView;

import com.example.one.R;

public class FrameAnimationAct extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frame_animation);
        ImageView fan = (ImageView) findViewById(R.id.fan);
        AnimationDrawable fanRoll = (AnimationDrawable)fan.getDrawable();
        fanRoll.start();//开启旋转
    }
}