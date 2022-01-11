package com.example.one.act.second;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.one.R;

import me.wangyuwei.particleview.ParticleView;

public class SplashAct extends AppCompatActivity {

    ParticleView particleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        particleView = (ParticleView) findViewById(R.id.pv);
        particleView.startAnim();
    }
}