package com.example.one.act.first;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;

import com.example.one.R;
import com.example.one.speail.BaseObAct;
import com.example.one.speail.EventType;
import com.example.one.speail.Teacher;

public class AnswerAct extends BaseObAct {
    private Button btAnswer1,btAnswer2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer);
        btAnswer1 = (Button) findViewById(R.id.bt_answer1);
        btAnswer2 = (Button) findViewById(R.id.bt_answer2);

        btAnswer1.setOnClickListener(v -> {
            Teacher.getInstance().postMessage(EventType.RESPONSE_T);
            finish();
        });
        btAnswer2.setOnClickListener(v -> {
            Teacher.getInstance().postMessage(EventType.RESPONSE_F);
            finish();
        });
    }
}