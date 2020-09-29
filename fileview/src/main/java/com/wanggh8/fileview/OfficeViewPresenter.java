package com.wanggh8.fileview;

import android.content.Context;
import android.util.Log;

import com.wanggh8.fileview.base.BaseApplication;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Office 文件预览
 *
 * @author wanggh8
 * @version V1.0
 * @date 2020/9/29
 */
public class OfficeViewPresenter implements OfficeViewContract.Presenter {

    private OfficeViewContract.View view;
    private OfficeViewModel officeViewModel;
    private String TAG = "OfficeViewPresenter";
    private Context appContext = BaseApplication.getAppContext();

    public OfficeViewPresenter(OfficeViewContract.View view, Context context) {
        this.view = view;
        officeViewModel = new OfficeViewModel(context);
    }

    /**
     * 下载文件
     *
     * @param fileUrl 文件url
     * @param fileName 带类型的文件名
     */
    @Override
    public void getFile(String fileUrl, String fileName) {
        Log.d(TAG, "getFile: " + fileUrl);

        officeViewModel.getFile(fileUrl, fileName, new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d(TAG, "onResponse: 请求成功");
                view.getFileSuccess(response, fileName);

                saveFile(response, fileName);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG, "onFailure");
                view.getFileFail(fileName);
            }
        });
    }

    /**
     * 下载后的文件流存储到应用专属外部存储
     *
     * @param response 网络请求返回体
     * @param fileName 文件名
     */
    @Override
    public void saveFile(Response<ResponseBody> response, String fileName) {

        InputStream is = null;
        FileOutputStream fos = null;
        byte[] buf = new byte[2048];
        int len = 0;

        try {
            ResponseBody responseBody = response.body();
            is = responseBody.byteStream();
            long total = responseBody.contentLength();

            // 创建缓存文件
            File cacheFile = getCacheFile(fileName);
            if (!cacheFile.exists()) {
                boolean mkdir = cacheFile.createNewFile();
            }
            Log.d(TAG, "创建缓存文件： " + cacheFile.toString());

            // 进行文件写入
            fos = new FileOutputStream(cacheFile);
            long sum = 0;
            while ((len = is.read(buf)) != -1) {
                fos.write(buf, 0, len);
                sum += len;
                int progress = (int) (sum * 1.0f / total * 100);
                Log.d(TAG, "写入缓存文件" + cacheFile.getName() + "进度: " + progress);
            }
            fos.flush();
            Log.d(TAG, "文件下载成功,准备展示文件。");

            view.saveFileSuccess(cacheFile);
        } catch (Exception e) {
            Log.d(TAG, "文件下载异常 = " + e.toString());
            view.saveFileFail();
        } finally {
            try {
                if (is != null)
                    is.close();
            } catch (IOException e) {
            }
            try {
                if (fos != null)
                    fos.close();
            } catch (IOException e) {
            }
        }
    }

    /***
     * 获取缓存目录
     *
     * @return
     */
    public File getCacheDir() {
        File rootDir = appContext.getExternalCacheDir();
        Log.d(TAG, "getCacheDir: " + rootDir.toString());
        File cacheDir = new File(rootDir, "office");
        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }
        Log.d(TAG, "getCacheDir: " + cacheDir.toString());
        return cacheDir;
    }

    /***
     * 绝对路径获取缓存文件
     *
     * @param fileName 文件名
     * @return File
     */
    private File getCacheFile(String fileName) {
        File cacheFile = new File(getCacheDir(), fileName);
        Log.d(TAG, "缓存文件 = " + cacheFile.toString());
        return cacheFile;
    }


}