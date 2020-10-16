package com.wanggh8.mydrive.base;

import android.content.Context;

/**
 * @author wanggh8
 * @version V1.0
 * @date 2020/10/16
 */
public class BasePresenter<T> {

    protected Context context;
    protected T ctrView;

    public BasePresenter(Context context, T ctrView) {
        this.context = context;
        this.ctrView = ctrView;
    }


}
