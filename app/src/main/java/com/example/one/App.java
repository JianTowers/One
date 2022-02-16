package com.example.one;


import android.app.Application;
import android.content.Context;

import com.example.one.baidu.SyntheticInstance;
import com.example.one.utils.ParseTools;
import com.example.one.view.CustomUpdateConfigProvider;
import com.qweather.sdk.view.HeConfig;
import com.xuexiang.xupdate.XUpdate;
import com.xuexiang.xupdate.easy.EasyUpdate;
import com.xuexiang.xupdate.easy.config.DefaultUpdateConfigProvider;
import com.xuexiang.xupdate.entity.UpdateError;
import com.xuexiang.xupdate.listener.OnUpdateFailureListener;
import com.xuexiang.xupdate.utils.UpdateUtils;

import java.util.Properties;

/**
 * @author :JianTao
 * @date :2022/1/10 11:08
 * @description :
 */
public class App extends Application {

    private String CityId;

    public String getCityIdCustomUpdateConfigProvider() {
        return CityId;
    }

    public void setCityId(String cityId) {
        CityId = cityId;
    }

    @Override
    protected void attachBaseContext(Context base) {
//        EasyUpdate.setUpdateConfigProvider(new DefaultUpdateConfigProvider());
        super.attachBaseContext(base);
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

        EasyUpdate.setUpdateConfigProvider(new DefaultUpdateConfigProvider());
    }
}
