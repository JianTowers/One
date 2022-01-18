package com.example.one;

import android.app.Application;

import com.example.one.baidu.SyntheticInstance;
import com.example.one.utils.ParseTools;
import com.qweather.sdk.view.HeConfig;

import java.util.Properties;

/**
 * @author :JianTao
 * @date :2022/1/10 11:08
 * @description :
 */
public class App extends Application {

    private String CityId;

    public String getCityId() {
        return CityId;
    }

    public void setCityId(String cityId) {
        CityId = cityId;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Properties weather = ParseTools.getInstance().getProperties(getApplicationContext(),"weather.properties");
        String publicId = weather.getProperty("publicId");
        String key = weather.getProperty("key");
        HeConfig.init(publicId,key);
        HeConfig.switchToDevService();
        SyntheticInstance.getInstance().initialTts(this);
    }
}
