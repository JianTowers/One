package com.example.one;

import android.app.Application;

import com.qweather.sdk.view.HeConfig;

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
        HeConfig.init("HE2201101641441757","b4f7d99708a146c28e3a1cf4f2b4791a");
        HeConfig.switchToDevService();
    }
}
