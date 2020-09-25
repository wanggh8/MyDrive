package com.wanggh8.mydrive;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import java.util.List;

/**
 * @author wanggh8
 * @version V1.0
 * @date 2020/9/23
 */
public class BootApplication extends Application {

    // 单例实例
    private static BootApplication mInstance;
    // 活动列表
    private List<Activity> mActivityList;
    // 全局APP上下文
    private static Context mAppContext;

    public BootApplication() {
        super();
        mInstance = this;
    }

    public static BootApplication getInstance() {
        return mInstance;
    }

    public static Context getAppContext() {
        if (mAppContext == null) {
            mAppContext = mInstance.getApplicationContext();
        }
        return mAppContext;
    }


}
