package com.wanggh8.netcore.net;

import android.annotation.SuppressLint;
import android.text.TextUtils;


import com.wanggh8.netcore.logger.Logger;
import com.wanggh8.netcore.util.AESUtils;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

public class NetHttpOperater {
    /**
     * 是否加密
     */
    private static boolean ENCRYPE = false;

    public static String dynamicBaseUrl = "";

    private final static String TAG = "NetHttpOperater";
    /**
     * 请求超时时间,默认30s
     */
    private static int REQUEST_TIME_OUT = 30;
    /**
     * 读超时时间,默认30s
     */
    private static int READ_TIME_OUT = 30;
    /**
     * 写超时时间,默认30s
     */
    private static int WRITE_TIME_OUT = 30;
    /**
     * 网络请求根地址
     */
    private static String BASE_URL;
    /**
     * HTTP默认头信息
     */
    private static HashMap<String, String> DEFAULT_HEADERS;
    /**
     * HTTP自定义头信息
     */
    private static HashMap<String, String> CUSTOM_HEADERS;
    /**
     * 网络框架实例
     */
    private Retrofit mRetrofit;
    /**
     * 单例
     */
    private static NetHttpOperater operater;

    private NetHttpOperater() {
        OkHttpClient.Builder client = new OkHttpClient.Builder();
        client.connectTimeout(REQUEST_TIME_OUT, TimeUnit.SECONDS)
                .readTimeout(READ_TIME_OUT, TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIME_OUT, TimeUnit.SECONDS);

        client.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request requestOrigin = chain.request();
                Headers headersOrigin = requestOrigin.headers();
                Iterator<HashMap.Entry<String, String>> entries = checkHeader().entrySet().iterator();
                Headers.Builder builder = headersOrigin.newBuilder();
                while (entries.hasNext()) {
                    HashMap.Entry<String, String> entry = entries.next();
                    builder.set(entry.getKey(), entry.getValue());
                }
                Headers headers = builder.build();
                Request request = requestOrigin.newBuilder().headers(headers).build();
                return chain.proceed(request);
            }
        });
        if (ENCRYPE) {
            //这个拦截器用来讲发送出去的数据加密
            client.addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    //请求
                    Request request = chain.request();
                    RequestBody oldRequestBody = request.body();
                    Buffer requestBuffer = new Buffer();
                    oldRequestBody.writeTo(requestBuffer);
                    String oldBodyStr = requestBuffer.readUtf8();
                    requestBuffer.close();
                    MediaType mediaType = MediaType.parse("application/json; charset=utf-8");

                    String newBodyStr = AESUtils.get().encrypt(oldBodyStr);
                    RequestBody newBody = RequestBody.create(mediaType, newBodyStr);
                    Logger.i(TAG, "请求数据 返回前-加密:" + newBodyStr);

                    //构造新的request
                    request = request.newBuilder()
                            .header("Content-Type", "application/json")
                            .header("Content-Length", String.valueOf(newBody.contentLength()))
                            .method(request.method(), newBody)
                            .build();

                    Response response = chain.proceed(request);
                    if (response.code() == 200) {//只有约定的返回码才经过加密，才需要走解密的逻辑
                        ResponseBody oldResponseBody = response.body();
                        String oldResponseBodyStr = oldResponseBody.string();
                        String newResponseBodyStr = AESUtils.get().decrypt(oldResponseBodyStr);

                        Logger.i(TAG, "请求地址:" + request.url().url().toString() + "");
                        Logger.i(TAG, "请求数据:" + oldBodyStr);
                        Logger.i(TAG, "请求数据-加密:" + newBodyStr);
                        Logger.d(TAG, "返回数据-解密:" + newResponseBodyStr);
                        oldResponseBody.close();
                        //构造新的response
                        MediaType rspMediaType = MediaType.parse("application/json; charset=utf-8");
                        ResponseBody newResponseBody = ResponseBody.create(rspMediaType, newResponseBodyStr);
                        response = response.newBuilder().body(newResponseBody).build();
                        newResponseBody.close();
                    }

                    return response;


                }
            });
        }
        //动态切换请求地址
        client.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();

                String url = request.url().url().toString();
                if (!TextUtils.isEmpty(dynamicBaseUrl)) {
                    url = url.replace(BASE_URL, dynamicBaseUrl);
                }
                HttpUrl newBaseUrl = HttpUrl.parse(url);
//                HttpUrl newFullUrl = request.url()
//                        .newBuilder()
//                        .scheme(newBaseUrl.scheme())
//                        .host(newBaseUrl.host())
//                        .port(newBaseUrl.port())
//                        .build();
                Request.Builder builder = request.newBuilder();
                Request newReq = builder.url(newBaseUrl).build();


//                if (!ENCRYPE) {
//                    RequestBody oldRequestBody = request.body();
//                    Buffer requestBuffer = new Buffer();
//                    oldRequestBody.writeTo(requestBuffer);
//                    String oldBodyStr = requestBuffer.readUtf8();
//
//                }

                Response response = chain.proceed(newReq);
                if (!ENCRYPE) {
                    if (response.code() == 200) {//只有约定的返回码才经过加密，才需要走解密的逻辑
                        ResponseBody oldResponseBody = response.body();
                        String oldResponseBodyStr = oldResponseBody.string();
                        RequestBody oldRequestBody = request.body();
//                        String header = response.header("new-token");

                        Buffer requestBuffer = new Buffer();
                        oldRequestBody.writeTo(requestBuffer);
                        String oldBodyStr = requestBuffer.readUtf8();


                        Logger.i(TAG, "请求地址:" + newReq.url().url().toString() + "");
                        Logger.i(TAG, "请求数据:" + oldBodyStr);
                        Logger.d(TAG, "返回数据:" + oldResponseBodyStr);
                        oldResponseBody.close();
                        //构造新的response
                        MediaType rspMediaType = MediaType.parse("application/json; charset=utf-8");
                        ResponseBody newResponseBody = ResponseBody.create(rspMediaType, oldResponseBodyStr);
                        response = response.newBuilder().body(newResponseBody).build();
                        newResponseBody.close();
                    }
                }
                return response;
            }
        }).sslSocketFactory(createSSLSocketFactory())
                .hostnameVerifier(new TrustAllHostnameVerifier());
        configRetrofit(client);
    }

    /**
     * 添加HTTPS 请求
     * <p>
     * 使用时将网站证书导入到项目assets目录下，
     */
    public void setCertificates(OkHttpClient.Builder clientBuilder, InputStream... certificates) {
        try {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null);
            int index = 0;
            for (InputStream certificate : certificates) {
                String certificateAlias = Integer.toString(index++);
                keyStore.setCertificateEntry(certificateAlias, certificateFactory.generateCertificate(certificate));
                try {
                    if (certificate != null) {
                        certificate.close();
                    }
                } catch (IOException e) {

                }
            }
            SSLContext sslContext = SSLContext.getInstance("TLS");
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);
            sslContext.init(null, trustManagerFactory.getTrustManagers(), new SecureRandom());

            clientBuilder.socketFactory(sslContext.getSocketFactory());
        } catch (Exception e) {

        }
    }


    /**
     * 检查头信息，如果存在自定义配置，优先使用自定义配置，否则加载默认配置
     *
     * @return
     */
    private HashMap<String, String> checkHeader() {
        if (CUSTOM_HEADERS != null) {
            return CUSTOM_HEADERS;
        } else {
            initDefaultHeaders();
            return DEFAULT_HEADERS;
        }
    }

    /**
     * 初始化默认头信息
     */

    private void initDefaultHeaders() {
        if (DEFAULT_HEADERS == null)
            DEFAULT_HEADERS = new HashMap<>();
        DEFAULT_HEADERS.put("Content-Type", "application/json; charset=UTF-8");
        DEFAULT_HEADERS.put("Accept-Encoding", "gzip, deflate");
        DEFAULT_HEADERS.put("Connection", "keep-alive");
        DEFAULT_HEADERS.put("Accept", "*/*");
    }

    /**
     * 默认信任所有的证书
     * TODO 最好加上证书认证，主流App都有自己的证书
     *
     * @return
     */
    @SuppressLint("TrulyRandom")
    private static javax.net.ssl.SSLSocketFactory createSSLSocketFactory() {
        javax.net.ssl.SSLSocketFactory sSLSocketFactory = null;
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{new TrustAllManager()},
                    new SecureRandom());
            sSLSocketFactory = sc.getSocketFactory();
        } catch (Exception e) {
        }
        return sSLSocketFactory;
    }

    /***
     * 配置JSON数据结构解析模板
     * @param client
     */
    private void configRetrofit(OkHttpClient.Builder client) {
        mRetrofit = new Retrofit.Builder()
                .client(client.build())
                .addConverterFactory(GsonConverterFactory.create())//json
                .addConverterFactory(SimpleXmlConverterFactory.create())//xml
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(BASE_URL)
                .build();
    }

    public static class TrustAllManager implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

    public static class TrustAllHostnameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }

    /**
     * 实例化
     *
     * @return NetHttpApi
     */
    public static NetHttpOperater getInstance() {
        if (operater == null) {
            synchronized (NetHttpOperater.class) {
                if (operater == null) {
                    operater = new NetHttpOperater();
                }
            }
        }
        return operater;
    }

    public <T> T getService(Class<T> service) {
        return mRetrofit.create(service);
    }

    public static class Builder {

        public Builder encrypt(boolean encrypt) {
            ENCRYPE = encrypt;
            return this;
        }

        /**
         * 供外部调用设置URL根地址
         *
         * @param baseUrl
         * @return
         */
        public Builder baseUrl(String baseUrl) {
            BASE_URL = baseUrl;
            return this;
        }

        /**
         * 供外部调用设置自定义头信息，一旦设置，默认头将不再生效
         *
         * @param key   http头字段名s
         * @param value http头属性值
         * @return
         */
        public Builder addHeader(String key, String value) {
            if (CUSTOM_HEADERS == null)
                CUSTOM_HEADERS = new HashMap<>();
            CUSTOM_HEADERS.put(key, value);
            return this;
        }

        /**
         * 设置连接超时时间
         *
         * @param timeout 超时时间(s)
         * @return
         */
        public Builder connectTimeout(int timeout) {
            REQUEST_TIME_OUT = timeout;
            return this;
        }

        /**
         * 设置连接读超时时间
         *
         * @param timeout 超时时间(s)
         * @return
         */
        public Builder readTimeout(int timeout) {
            READ_TIME_OUT = timeout;
            return this;
        }

        /**
         * 设置连接写超时时间
         *
         * @param timeout 超时时间(s)
         * @return
         */
        public Builder writeTimeout(int timeout) {
            WRITE_TIME_OUT = timeout;
            return this;
        }

        public NetHttpOperater build() {
            return getInstance();
        }

    }

}