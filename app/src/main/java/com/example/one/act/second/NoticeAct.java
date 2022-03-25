package com.example.one.act.second;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;

import com.example.one.R;
import com.example.one.server.NoticeService;

public class NoticeAct extends AppCompatActivity {

    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);

        startService(new Intent(NoticeAct.this, NoticeService.class));

        button  = (Button) findViewById(R.id.btNotice);
        button.setOnClickListener(v -> finish());
    }
}