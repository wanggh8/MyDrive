package com.wanggh8.netcore.service;


import com.wanggh8.netcore.net.NetHttpOperater;

public class CoreNetService {

    /** 单例*/
    private static CoreNetService instance;
    /** 相册业务请求服务*/
    private static Object mService;
    /** 网络请求配置*/
    private static NetHttpOperater.Builder builder = new NetHttpOperater.Builder();

    /**
     * CoreNetService
     * @return CoreNetService
     */
    public static CoreNetService getInstance(){
        if (instance == null){
            instance = new CoreNetService();
        }
        return instance;
    }

    /**
     * 使用默认builder构建对外Service服务
     * @param service service名称
     * @param <T> 响应数据结构封装类
     * @return 业务服务
     */
    public <T> T getService(Class<T> service) {
        if (mService == null){
            mService = builder.build().getService(service);
        }
        return (T) mService;
    }

    /**
     * 构建对外Service服务
     * @param service service名称
     * @param builder 构建参数（支持配置HTTP HEADER、TIMEOUT，可扩展配置）
     * @param <T> 响应数据结构封装类
     * @return 业务服务
     */
    public <T> T getService(Class<T> service, NetHttpOperater.Builder builder) {
        if (mService == null){
            mService = builder.build().getService(service);
        }
        return (T) mService;
    }

    /**
     * 对外提供设置builder接口
     * @param builder
     */
    public void build(NetHttpOperater.Builder builder){
        this.builder = builder;
    }

    /**
     * 对外提供获取现有builder接口
     * @return
     */
    public NetHttpOperater.Builder build(){
        return builder;
    }

}