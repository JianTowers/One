package com.example.one.speail;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Observable;
import java.util.Observer;

/**
 * @author :JianTao
 * @date :2021/12/16 15:59
 * @description : 观察者基类
 */
public class BaseObAct extends AppCompatActivity implements Observer {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (this instanceof Observer){
            Teacher.getInstance().addObserver(this);
        }
    }

    @Override
    public void update(Observable observable, Object data) {
    }
}
