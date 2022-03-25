package com.example.one.speail;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.Utils;
import com.example.one.App;

/**
 * @author :JianTao
 * @date :2022/2/23 10:58
 * @description :
 */
public class MyLocationUtils extends BDAbstractLocationListener {

    @Override
    public void onReceiveLocation(BDLocation location) {
        String city = location.getCity();    //获取城市
        String district = location.getDistrict();    //获取区县
        if (location.getLocType() == 61 || location.getLocType() == 161) {
            App app = (App) Utils.getApp();
            app.setBdLocation(location);
        }
        LogUtils.d("返回码" + location.getLocType());
        LogUtils.d(location.getLocTypeDescription());
        LogUtils.d("定位结果" + city + district);
    }

}
