package com.wanggh8.mydrive.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.wanggh8.mydrive.base.BaseApplication;

import java.lang.reflect.Type;

/**
 * sharedPreferences封装管理工具
 *
 * @author wanggh8
 * @version V1.0
 * @date 2020/9/23
 */
public class SPManager {

    private static SharedPreferences sharedPreferences;
    private static boolean isUseEncryptMode = false;

    public static SharedPreferences getInstance() {
        if (sharedPreferences == null) {
            sharedPreferences = BaseApplication.getAppContext()
                    .getSharedPreferences("MyDrive", Context.MODE_PRIVATE);
        }
        return sharedPreferences;
    }

    public static String getString(String key, String defValue) {
        if (isUseEncryptMode) {
            String spValue = getInstance().getString(key, defValue);
            String result = AESUtil.decryptString(spValue);
            return result == null ? spValue : result;
        } else {
            return getInstance().getString(key, defValue);
        }
    }

    public static void putString(String key, String value) {
        SharedPreferences.Editor editor = getInstance().edit();
        if (isUseEncryptMode) {
            editor.putString(key, AESUtil.encryptString(value));
        } else {
            editor.putString(key, value);
        }
        editor.apply();
    }

    public static long getLong(String key, long defValue) {
        return getInstance().getLong(key, defValue);
    }

    public static void putLong(String key, long value) {
        SharedPreferences.Editor editor = getInstance().edit();
        editor.putLong(key, value);
        editor.apply();
    }

    public static int getInt(String key, int defValue) {
        return getInstance().getInt(key, defValue);
    }

    public static void putInt(String key, int value) {
        SharedPreferences.Editor editor = getInstance().edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static boolean getBoolean(String key, boolean defValue) {
        return getInstance().getBoolean(key, defValue);
    }

    public static void putBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = getInstance().edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static void putObject(String key, Object obj) {
        putString(key, new Gson().toJson(obj));
    }

    public static <T> T getObject(String key, Class<T> model) {
        String s = getString(key, "");
        T t = null;
        try {
            t = new Gson().fromJson(s, model);
        } catch (Exception e) {
            // not handle exception
        }
        return t;
    }
    public static <T> T getObject(String key, Type type) {
        String s = getString(key, "");
        T t = null;
        try {
            t = new Gson().fromJson(s, type);
        } catch (Exception e) {
            // not handle exception
        }
        return t;
    }


    public static void clearSP() {
        SharedPreferences sharedPreferences = getInstance();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    public static void removeValue(String key) {
        SharedPreferences sharedPreferences = getInstance();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(key);
        editor.apply();
    }
}
