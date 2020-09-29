package com.wanggh8.fileview;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.content.FileProvider;

import java.io.File;
import java.util.List;

/**
 * 使用wps打开文件工具类
 *
 * @author wanggh8
 * @version V1.0
 * @date 2020/9/23
 */
public class OpenWithWPSUtil {

    /**
     * 判断是否安装对应应用
     *
     * @param context 上下文
     * @param packageName 应用的包名
     * @return
     */
    public static boolean isPackageExist(Context context, String packageName ) {
        final PackageManager packageManager = context.getPackageManager();
        // 获取所有已安装程序的包信息
        List<PackageInfo> packageInfo = packageManager.getInstalledPackages(0);
        for ( int i = 0; i < packageInfo.size(); i++ ) {
            if(packageInfo.get(i).packageName.equalsIgnoreCase(packageName))
                return true;
        }
        return false;
    }

    /**
     * 使用wps打开文件
     *
     * @param context 上下文
     * @param file 要打开的文件
     * @param canWrite 是否可写
     */
    public static void openWithWPS(Context context, File file, boolean canWrite) {
        // WPS个人版的包名
        String packageName = "cn.wps.moffice_eng";
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);

        if (isPackageExist(context, packageName)) {
            Bundle bundle = new Bundle();
            if (canWrite) {// 判断是否可以编辑文档
                bundle.putString("OpenMode", "Normal");// 一般模式
            } else {
                bundle.putString("OpenMode", "ReadOnly");// 只读模式
            }
            bundle.putBoolean("SendSaveBroad", true);// 关闭保存时是否发送广播
            bundle.putBoolean("SendCloseBroad", true);// 关闭文件时是否发送广播
            bundle.putBoolean("HomeKeyDown", true);// 按下Home键
            bundle.putBoolean("BackKeyDown", true);// 按下Back键
            bundle.putBoolean("IsShowView", false);// 是否显示wps界面
            bundle.putBoolean("AutoJump", true);// //第三方打开文件时是否自动跳转
            //设置广播
            bundle.putString("ThirdPackage", context.getPackageName());
            //第三方应用的包名，用于对改应用合法性的验证
            //bundle.putBoolean(Define.CLEAR_FILE, true);
            //关闭后删除打开文件
            intent.setAction(Intent.ACTION_MAIN);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//授予临时权限别忘了
            intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
            Uri fileUrl = FileProvider.getUriForFile(context, "com.chinaunicom.cloudnurse.provider", file);
            intent.setData(fileUrl);
            intent.putExtras(bundle);
            context.startActivity(intent);
        } else {
            // 从市场上下载
            Uri uri = Uri.parse("market://details?id=" + packageName);
            // 直接从指定网址下载
            Intent it = new Intent(Intent.ACTION_VIEW, uri);
            context.startActivity(it);
        }
    }
}
