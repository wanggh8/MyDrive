package com.wanggh8.mydrive.base;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import java.util.Calendar;
import java.util.logging.Logger;

/**
 * Activity基类
 *
 * @author wanggh8
 * @version V1.0
 * @date 2020/9/21
 */
public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * 设置布局文件
     **/
    public abstract int getContentLayout();

    /**
     * 在实例化布局前处理的逻辑
     **/
    public abstract void beforeInitView();

    /**
     * 实例化布局文件/组件
     **/
    public abstract void initView();

    /**
     * 在实例化布局后处理的逻辑
     **/
    public abstract void afterInitView();

    /**
     * 绑定监听事件
     **/
    public abstract void bindListener();

    /**
     * 点击事件
     **/
    public abstract void onClickEvent(View v);

    // 当前活动名，调试使用
    public String TAG = getClass().getSimpleName();
    // 上次点击的时间
    protected long lastClickTime = 0;

    public boolean isKeyBack = false;


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
        Log.d("BaseActivity", v.getId() + "  " + " BaseActivity onClick ");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // 设置布局
        setContentView(getContentLayout());

        // 方便调试，添加当前Activity名
        Log.d("onCreate mActivity ：", getClass().getSimpleName());

        beforeInitView();
        initView();
        afterInitView();
        bindListener();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.v("Lifecycle", "onStart mActivity ：" + getClass().getSimpleName());
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v("Lifecycle", "onResume mActivity ：" + getClass().getSimpleName());
    }

    @Override
    protected void onPause() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!BaseApplication.getInstance().isForeground() && !isKeyBack) {
                    // ToastUtil.show(APPUtil.getAppName(BootApplication.getAppContext()) + "已进入后台");
                }
                isKeyBack = false;
            }
        }, 200);

        super.onPause();
        Log.v("Lifecycle", "onPause mActivity ：" + getClass().getSimpleName());
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.v("Lifecycle", "onStop mActivity ：" + getClass().getSimpleName());
    }

    /**
     * 返回键事件侦听
     *
     * @param keyCode 被按下的键的键盘码
     * @param event 按键事件
     * @return 当返回true时，表示已经完整地处理了这个事件，而当返回false时，
     * 表示并没有完全处理完该事件，更希望其他回调方法继续对其进行处理
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            isKeyBack = true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
