package com.example.one.bean;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author :JianTao
 * @date :2022/3/14 16:00
 * @description :
 */
public class DevicesBean extends DefaultSendBean{
    public DevicesBean(String xiaomi,String action) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("xiaomi", xiaomi);
            jsonObject.put("action", action);
            content = jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
