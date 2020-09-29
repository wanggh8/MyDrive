package com.wanggh8.mydrive.base;

import android.view.View;

import androidx.annotation.IdRes;
import androidx.recyclerview.widget.RecyclerView;

/**
 * ViewHolder基类
 *
 * @author wanggh8
 * @version V1.0
 * @date 2020/9/21
 */
public abstract class BaseViewHolder<T> extends RecyclerView.ViewHolder {

    /**
     * 构造方法
     * 初始化父类成员变量
     * public final View itemView;
     *
     * @param itemView View
     */
    public BaseViewHolder(View itemView) {
        super(itemView);
    }

    /**
     * 数据绑定抽象方法
     *
     * @param t 数据对象
     * @param position 位置
     */
    public abstract void onBind(T t, int position);

    /**
     * 封装findViewById
     *
     * @param id View id
     * @param <T> View或其子类
     * @return T
     */
    public <T extends View> T findViewById(@IdRes int id) {
        return (T) itemView.findViewById(id);
    }
}
