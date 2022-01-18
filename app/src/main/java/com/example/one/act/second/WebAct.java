package com.example.one.act.second;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.blankj.utilcode.util.LogUtils;
import com.example.one.R;

public class WebAct extends AppCompatActivity {

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        progressBar = (ProgressBar) findViewById(R.id.pb);

        WebView webView = findViewById(R.id.web);
        webView.setWebViewClient(new WebViewClient());
        String url = "https://widget-page.qweather.net/h5/index.html?md=0123456&bg=1&lc=accu&key=8b09e77631cd471e9135792ef74b59aa&v=_1642139291620";
        webView.loadUrl(url);

        //解决加载空白屏
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress ==100){
                    progressBar.setVisibility(View.GONE);
                }else {
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setProgress(newProgress);
                    LogUtils.d(newProgress);
                }
            }
        });
    }
}