package com.wanggh8.mydrive.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * 手机屏幕相关工具类
 *
 * @author wanggh8
 * @version V1.0
 * @date 2020/10/10
 */
public class ScreenUtil {
    /**
     * from dp to px
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        int value = Math.round(dpValue * scale);
        if (value == 0) {
            value = 1;
        }
        return value;
    }

    /**
     * 获取屏幕的宽
     *
     * @param context
     *            当前上下文
     * @return 屏幕宽
     */
    public static int getScreenWidthPix(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }

    /**
     * 获取屏幕的高
     *
     * @param context
     *            当前上下文
     * @return 屏幕高
     */
    public static int getScreenHeightPix(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels;
    }
}
