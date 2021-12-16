package com.example.one.act;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.example.one.R;
import com.example.one.speail.BaseObAct;
import com.example.one.speail.EventType;

import java.util.Observable;

public class QuestionAct extends BaseObAct {
    Button btQuestion;
    TextView tvQuestion;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        btQuestion = (Button) findViewById(R.id.bt_question);
        tvQuestion = (TextView) findViewById(R.id.tv_question);
        btQuestion.setOnClickListener(v -> {
            startActivity(new Intent(QuestionAct.this,AnswerAct.class));
        });
    }

    @Override
    public void update(Observable observable, Object data) {
        if (data instanceof String){
            String message = (String) data;
            if (EventType.RESPONSE_T.equals(message)){
                tvQuestion.setText("回答正确");
            }
            if (EventType.RESPONSE_F.equals(message)){
                tvQuestion.setText("回答错误");
            }
        }
    }
}