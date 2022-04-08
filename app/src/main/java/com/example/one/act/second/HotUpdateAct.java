package com.example.one.act.second;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.example.one.R;
import com.taobao.sophix.SophixManager;

public class HotUpdateAct extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hot_update);
        Button btCheck = (Button) findViewById(R.id.btCheckHot);
        Button btProve = (Button) findViewById(R.id.btProveHot);
        TextView textView = (TextView) findViewById(R.id.tvHot);

        btCheck.setOnClickListener(v -> SophixManager.getInstance().queryAndLoadNewPatch());

        btProve.setOnClickListener(v -> textView.setText("已更新补丁"));
    }
}