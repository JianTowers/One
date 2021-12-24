package com.example.one.act;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import com.example.one.utils.ZXingUtils;
import com.google.zxing.*;
import com.example.one.R;

import java.io.UnsupportedEncodingException;

public class CodeAct extends AppCompatActivity {
    private Button btCode1;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code);

        btCode1 = (Button) findViewById(R.id.bt_Code1);
        imageView = (ImageView) findViewById(R.id.iv_code);

        btCode1.setOnClickListener(v -> {
            String result = null;
            String content = "https://www.zust.edu.cn/";
            try {
                // 将中文UTF-8转换成ISO-8859-1(避免中文变问号)
                result = new String(content.getBytes("UTF-8"),"iso-8859-1");
                Bitmap qrCode = ZXingUtils.createQRImage(content,200,200, Color.BLUE);
                // img为ImageView视图
                imageView.setImageBitmap(qrCode);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        });
    }
}