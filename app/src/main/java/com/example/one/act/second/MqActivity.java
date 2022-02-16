package com.example.one.act.second;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.LogUtils;
import com.example.one.R;
import com.example.one.view.CustomUpdateParser;
import com.xuexiang.xupdate.easy.EasyUpdate;

public class MqActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mq);
        Button button = findViewById(R.id.bt_MqStart);
        button.setOnClickListener(v -> {
            EasyUpdate.create(this,"https://gitee.com/xuexiangjys/XUpdate/raw/master/jsonapi/update_test.json")
                    .updateParser(new CustomUpdateParser())
//                    .updateUrl("https://xuexiangjys.oss-cn-shanghai.aliyuncs.com/apk/xupdate_demo_1.0.2.apk")
                    .update();
            LogUtils.d("应用更新");
        });
    }
}