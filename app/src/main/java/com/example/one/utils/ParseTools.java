package com.example.one.utils;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author :JianTao
 * @date :2022/1/17 15:29
 * @description :
 */
public class ParseTools {
    private volatile static ParseTools parseTools;

    public static ParseTools getInstance() {
        if (parseTools == null) {
            synchronized (ParseTools.class) {
                if (parseTools == null) {
                    parseTools = new ParseTools();
                }
            }
        }
        return parseTools;
    }

    //读取本地properties
    public Properties getProperties(Context context, String filename){
        try {
            InputStream is = context.getAssets().open(filename);
            Properties prop = new Properties();
            prop.load(is);
            is.close();
            return prop;
        } catch (IOException e) {
            e.printStackTrace();
            throw new AuthCheckException(e);
        }
    }

    public static class AuthCheckException extends RuntimeException {
        public AuthCheckException(String message) {
            super(message);
        }

        public AuthCheckException(Throwable cause) {
            super(cause);
        }
    }
}
