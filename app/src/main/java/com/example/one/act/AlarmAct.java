package com.example.one.act;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

public class AlarmAct extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //显示闹钟提醒框
        new AlertDialog.Builder(AlarmAct.this)
                .setTitle("闹钟")
                .setMessage("时间到了")
                .setPositiveButton("确定", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialogInterface, int which) {
                        AlarmAct.this.finish();
                    }
                }).create().show();
    }
}