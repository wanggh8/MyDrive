package com.wanggh8.mydrive.config;

import android.os.Environment;

/**
 * 常量
 *
 * @author wanggh8
 * @version V1.0
 * @date 2020/10/10
 */
public class Constant {

    /**
     * 请求超时时间
     */
    public static int REQUEST_TIME_OUT = 10;
    /**
     * 读超时时间
     */
    public static int READ_TIME_OUT = 10;
    /**
     * 写超时时间
     */
    public static int WRITE_TIME_OUT = 10;

    /**
     * 接口地址
     */
    public static String BASE_HOST_URL = "https://www.baidu.com/";


    /**
     * 应用文件目录
     */
    public static final String SDCARD_PATH = Environment.getExternalStorageDirectory().getPath() + "/MyDrive";
    /**
     * apk保存路径
     */
    public final static String APK_PATH = SDCARD_PATH + "/download/";
    public final static String APK_NAME = "MyDrive";
    /**
     * SP记录进程名称key
     */
    public final static String MAIN_PROCESS_NAME = "main_process_name";
    /**
     * SP记录进程pid的key
     */
    public final static String MY_PID = "MY_PID";



}
