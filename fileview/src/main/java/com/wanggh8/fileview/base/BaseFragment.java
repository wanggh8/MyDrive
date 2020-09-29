package com.wanggh8.fileview.base;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IdRes;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Calendar;

/**
 * Fragment基类
 *
 * @author wanggh8
 * @version V1.0
 * @date 2020/9/21
 */
public abstract class BaseFragment extends Fragment implements View.OnClickListener {

    /**
     * 获取布局的layout
     */
    public abstract int getContentLayout();

    /**
     * 在实例化布局之前处理的逻辑
     */
    public abstract void beforeInitView();

    /**
     * 实例化布局文件/组件
     */
    public abstract void initView();

    /**
     * 在实例化之后处理的逻辑
     */
    public abstract void afterInitView();

    /**
     * 绑定监听事件
     */
    public abstract void bindListener();

    /**
     * 触发点击事件
     */
    public abstract void onClickEvent(View v);

    // 根View
    protected View mRootView;
    // 所属Activity上下文
    public Context mContext;
    // 当前Fragment文件名，调试使用
    public String TAG = getClass().getSimpleName();
    // 上次点击的时间
    protected long lastClickTime = 0;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Nullable
    @Override
    public Context getContext() {
        return mContext;
    }

    /**
     * 初始化除View外的逻辑
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        // 方便调试，添加当前Fragment名 by wanggh8
        Log.d("onCreate mFragment ：", getClass().getSimpleName());

        beforeInitView();
    }

    /**
     * 每次创建、绘制该Fragment的View组件时回调该方法，Fragment将会显示该方法返回的View组件
     * 初始化静态View
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return View
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(getContentLayout(), container, false);
            initView();
        }
        return mRootView;
    }

    /**
     * 当Fragment所在的Activity被启动完成后回调该方法
     * 添加动态View
     * 访问父Activity的View层
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        afterInitView();
        bindListener();
    }

    /**
     * 重写点击时间，设置点击间隔，防止点击速度过快多次响应
     *
     * @param v View
     */
    @Override
    public void onClick(View v) {
        long currentTime = Calendar.getInstance().getTimeInMillis();
        if (currentTime - lastClickTime > 300) {
            lastClickTime = currentTime;
            onClickEvent(v);
        }
    }

    /**
     * 封装findViewById
     *
     * @param id View id
     * @param <T> View或其子类
     * @return T
     */
    protected <T extends View> T findViewById(@IdRes int id) {
        return mRootView.findViewById(id);
    }
}
