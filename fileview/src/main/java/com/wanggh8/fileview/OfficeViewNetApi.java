package com.wanggh8.fileview;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Office 文件预览
 *
 * @author wanggh8
 * @version V1.0
 * @date 2020/9/29
 */
public interface OfficeViewNetApi {

    /**
     * 文件下载
     */
    @GET
    Call<ResponseBody> getFile(@Url String fileUrl);
}
