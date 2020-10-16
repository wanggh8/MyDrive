package com.wanggh8.mydrive.callback;

import java.util.List;

/**
 * 通用获取列表监听
 *
 * @author wanggh8
 * @version V1.0
 * @date 2020/10/16
 */
public interface CommonLoadListCallback<T> {
    /**
     * 加载用户列表成功回调
     * 可进行更新UI操作
     *
     * @param list 列表
     */
    void onSuccess(final List<T> list);

    void onError(final Exception exception);
}
