package com.wanggh8.fileview;

import java.io.File;

import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * Office 文件预览
 *
 * @author wanggh8
 * @version V1.0
 * @date 2020/9/29
 */
public class OfficeViewContract {

    interface View {
        // 下载文件
        void getFileSuccess(Response<ResponseBody> response, String fileName);
        void getFileFail(String fileName);

        // 存储文件
        void saveFileSuccess(File file);
        void saveFileFail();
    }

    interface Presenter {

        /**
         * 下载文件
         *
         * @param fileUrl 文件url
         * @param fileName 带类型的文件名
         */
        void getFile(String fileUrl, String fileName);

        /**
         * 下载后的文件流存储到应用专属外部存储
         *
         * @param response 网络请求返回体
         * @param fileName 文件名
         */
        void saveFile(Response<ResponseBody> response, String fileName);
    }

}
