package com.wanggh8.mydrive.utils;

import android.content.Context;
import android.os.Environment;

import com.wanggh8.mydrive.base.BaseApplication;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author wanggh8
 * @version V1.0
 * @date 2020/10/16
 */
public class FileUtil {

    /**
     * 删除方法 这里只会删除某个文件夹下的文件，如果传入的directory是个文件，将不做处理
     *
     * @param directory
     */
    public static void deleteFilesByDirectory(File directory) {
        if (directory != null && directory.exists() && directory.isDirectory()) {
            for (File item : directory.listFiles()) {
                item.delete();
            }
        }
    }

    /**
     * 删除file文件下的所有文件
     *
     * @param file
     * @param fileSelf 是否删除file本身
     */
    public static void deleteFile(File file, boolean fileSelf) {
        if (file == null && !file.exists()) {
            return;
        }

        if (file.isFile() && fileSelf) {
            file.delete();
            return;
        }

        if (file.isDirectory()) {
            File[] childFiles = file.listFiles();
            if ((childFiles == null || childFiles.length == 0) && fileSelf) {
                file.delete();
                return;
            }

            for (int i = 0; i < childFiles.length; i++) {
                deleteFile(childFiles[i], true);
            }
            if (fileSelf) {
                file.delete();
            }
        }
    }

    /**
     * 获取文件
     * SDCard/Android/giftList/你的应用的包名/files/
     *
     * @param file 目录，一般放一些长时间保存的数据
     * @return
     * @throws Exception
     */
    public static long getFolderSize(File file) throws Exception {
        long size = 0;
        try {
            File[] fileList = file.listFiles();
            for (int i = 0; i < fileList.length; i++) {
                // 如果下面还有文件
                if (fileList[i].isDirectory()) {
                    size = size + getFolderSize(fileList[i]);
                } else {
                    size = size + fileList[i].length();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

    /**
     * 判断文件是否存在
     *
     * @param path
     * @return
     */
    public static boolean isHasFile(String path) {
        try {
            File file = new File(path);
            if (file.exists()) {
                return true;
            }
        } catch (Exception ex) {
            return false;
        }
        return false;
    }

    public static boolean makeDirs(File parentFile) {
        return parentFile.mkdirs();
    }

    public static boolean makeDirs(String absPath) {
        File file = new File(absPath);
        return file.mkdirs();
    }

    public static void saveTextToFile(String text) {
        File file = new File(Environment.getExternalStorageDirectory(), "CNException.txt");
        try {
            file.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(text.getBytes());
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveText2File(String text, String fileName) {
        File file = new File(Environment.getExternalStorageDirectory(), fileName);
        try {
            file.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(text.getBytes());
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveText3File(String text, File dir, String fileName) {
        File file = new File(dir, fileName);
        try {
            file.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(text.getBytes());
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //复制目录下的文件
    public static int CopySdcardFile(String fromFile, String toFile) {

        try {
            InputStream fosfrom = new FileInputStream(fromFile);
            OutputStream fosto = new FileOutputStream(toFile);
            byte bt[] = new byte[1024];
            int c;
            while ((c = fosfrom.read(bt)) > 0) {
                fosto.write(bt, 0, c);
            }
            fosfrom.close();
            fosto.close();
            return 0;

        } catch (Exception ex) {
            return -1;
        }
    }

    public static File getCacheFile(String name) {
        String cachePath;
        Context context = BaseApplication.getAppContext();
        if (Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {

            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }

        File file = new File(cachePath + File.separator + name);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }
}
