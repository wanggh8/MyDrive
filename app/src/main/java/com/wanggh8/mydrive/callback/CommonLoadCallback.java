package com.wanggh8.mydrive.callback;

import java.util.List;

/**
 * 通用获取对象监听
 *
 * @author wanggh8
 * @version V1.0
 * @date 2020/10/21
 */
public interface CommonLoadCallback<T> {

    /**
     * 加载用户列表成功回调
     * 可进行更新UI操作
     *
     * @param rsp 返回结果
     */
    void onSuccess(final T rsp);

    void onError(final Exception exception);
}
