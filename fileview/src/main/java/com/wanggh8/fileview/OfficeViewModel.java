package com.wanggh8.fileview;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Office 文件预览
 *
 * @author wanggh8
 * @version V1.0
 * @date 2020/9/29
 */
public class OfficeViewModel {

    private Context mContext;

    public OfficeViewModel(Context context) {
        this.mContext = context;
    }


    /**
     * 下载文件
     *
     * @param url 文件url
     * @param fileName 带类型文件名
     * @param callback 回调
     */
    public void getFile(String url,String fileName, Callback<ResponseBody> callback) {

        OkHttpClient build = new OkHttpClient.Builder()
                .connectTimeout(2, TimeUnit.SECONDS)
                .readTimeout(2, TimeUnit.SECONDS)
                .writeTimeout(2, TimeUnit.SECONDS)
                .build();
        if (TextUtils.isEmpty(url)) {
            return;
        }

        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.baidu.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(build)
                .build();
        Log.d("TAG", "getFile: "+ url);

        OfficeViewNetApi repo =  retrofit.create(OfficeViewNetApi.class);
        Call<ResponseBody> call = repo.getFile(url);
        call.enqueue(callback);

    }


}
