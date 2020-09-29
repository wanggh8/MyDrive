package com.wanggh8.netcore.model;

import com.chinaunicom.ecore.core.net.NetHttpOperater;
import com.chinaunicom.ecore.core.service.CoreNetService;

public class CoreNetModel {

    /** 网络请求配置*/
    private NetHttpOperater.Builder builder = new NetHttpOperater.Builder();

    /**
     * 获取service服务
     * @param service service类名
     * @param <T> service类型
     * @return 对外服务service实例
     */
    public <T> T getService(Class<T> service){
        return CoreNetService.getInstance().getService(service, builder);
    }

    /**
     * 获取service服务
     * @param service service service类名
     * @param builder 网络配置
     * @param <T> service类型
     * @return
     */
    public <T> T getService(Class<T> service, NetHttpOperater.Builder builder){
        return CoreNetService.getInstance().getService(service, builder);
    }

    /**
     * 生成网络请求builder配置,子类可直接由此获取自动生成的实例
     * @return builder实例
     */
    public NetHttpOperater.Builder createBuilder(){
        return builder;
    }

}