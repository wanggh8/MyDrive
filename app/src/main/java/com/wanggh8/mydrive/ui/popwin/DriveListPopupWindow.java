package com.wanggh8.mydrive.ui.popwin;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wanggh8.mydrive.R;
import com.wanggh8.mydrive.adapter.DriveItemAdapter;
import com.wanggh8.mydrive.base.BaseAdapter;
import com.wanggh8.mydrive.bean.DriveBean;
import com.wanggh8.mydrive.utils.ScreenUtil;

import java.util.List;

/**
 * 单RecyclerView的PopupWindow
 *
 * @author wanggh8
 * @version V1.0
 * @date 2020/10/10
 */
public class DriveListPopupWindow extends PopupWindow {
    private RecyclerView mRecyclerView;
    private Context context;
    private String selectedStr = "";
    private int selectedPos;
    private DriveItemAdapter mAdapter;

    public DriveListPopupWindow(Context context) {
        this.context = context;
        initPopWindow();
    }

    /**
     * 初始化popupWindow
     */
    private void initPopWindow() {
        ViewGroup vgPopupWindow = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.popwin_base, null);
        mRecyclerView = vgPopupWindow.findViewById(R.id.rv_content);
        mAdapter = new DriveItemAdapter(context);
        mAdapter.setSimpleOnItemClickListener((position, bean) -> {
            mAdapter.setSelected(position);
            selectedPos = position;
            selectedStr = bean.getName();
            dismiss();
        });
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        mRecyclerView.setAdapter(mAdapter);

        // 获取屏幕宽度和高度
        ScreenUtil.getScreenHeightPix(context);
        this.setContentView(vgPopupWindow);
        this.setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
        this.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        this.setAnimationStyle(R.style.WindowStyle);
    }

    /**
     * 显示popupWindow
     *
     * @param itemArray        展示的数据
     * @param position        设置选中的位置
     * @param OnSelectListener 回调
     */
    public void showPopWindowAsDropDown(List<DriveBean> itemArray, int position, View parent,int xoff, int yoff, final OnSelectListener OnSelectListener) {
        selectedPos = position;
        mAdapter.setSelected(selectedPos);
        mAdapter.setCollection(itemArray);
        this.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                if (!TextUtils.isEmpty(selectedStr)) {
                    if (OnSelectListener != null && position != -1) {
                        OnSelectListener.onSelect(selectedStr, selectedPos);
                    }
                    selectedStr = "";
                } else {
                    if (OnSelectListener != null) {
                        OnSelectListener.onCancel();
                    }
                }
            }
        });
        showAsDropDown(parent, xoff, yoff);
    }

    public interface OnSelectListener {
        void onSelect(String selected, int position);

        void onCancel();
    }

}