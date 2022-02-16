package com.example.one.view;

import android.content.Context;

import com.xuexiang.xupdate.easy.config.IUpdateConfigProvider;
import com.xuexiang.xupdate.easy.config.UpdateConfig;
import com.xuexiang.xupdate.utils.UpdateUtils;

/**
 * @author :JianTao
 * @date :2022/2/16 10:28
 * @description :
 */
public class CustomUpdateConfigProvider implements IUpdateConfigProvider {

    @Override
    public UpdateConfig getUpdateConfig( Context context) {
        return UpdateConfig.create()
                .setUpdateParser(new CustomUpdateParser());
    }
}