package com.example.one.act.second;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.LogUtils;
import com.example.one.R;
import com.qweather.sdk.bean.geo.GeoBean;
import com.qweather.sdk.bean.weather.WeatherDailyBean;
import com.qweather.sdk.bean.weather.WeatherNowBean;
import com.qweather.sdk.view.QWeather;

public class WeatherAct extends AppCompatActivity {
    private Button btTest;
    private String id;
    private TextView tvWind,tvTemp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        init();
        runFlow();
        getCityId();
}

    private void init() {
        btTest = findViewById(R.id.bt_Test);
        tvTemp = (TextView) findViewById(R.id.tvTemp);
        tvWind = (TextView) findViewById(R.id.tvWind);
    }

    private void runFlow() {
        btTest.setOnClickListener(v -> {
            getWeather();
        });
    }


    /**
     * 获得城市Id
     */
    private void getCityId() {
        QWeather.getGeoCityLookup(this, "拱墅区", new QWeather.OnResultGeoListener() {
            @Override
            public void onError(Throwable throwable) {
            }

            @Override
            public void onSuccess(GeoBean geoBean) {
                id = geoBean.getLocationBean().get(0).getId();
            }
        });
    }

    /**
    * 当前天气
    */
    private void getWeather(){
        QWeather.getWeatherNow(this, id, new QWeather.OnResultWeatherNowListener() {
            @Override
            public void onError(Throwable throwable) {
                LogUtils.d(throwable);
            }

            @Override
            public void onSuccess(WeatherNowBean weatherNowBean) {
                tvTemp.setText(weatherNowBean.getNow().getTemp()+"\u2103");
                tvWind.setText(weatherNowBean.getNow().getWindDir());
            }
        });
    }


}