package com.example.one.bean;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * @author :JianTao
 * @date :2022/1/19 14:19
 * @description :
 */
public class WebBean extends DefaultSendBean {
    public WebBean(String cmd,String conl,String screensaverColor,String textColor,String screensaverText) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("cmd", cmd);
            jsonObject.put("conl", conl);
            jsonObject.put("screensaverColor", screensaverColor);
            jsonObject.put("textColor", textColor);
            jsonObject.put("screensaverText", screensaverText);
            content = jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
