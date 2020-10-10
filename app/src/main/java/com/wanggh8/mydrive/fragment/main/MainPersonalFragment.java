package com.wanggh8.mydrive.fragment.main;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.hjq.toast.ToastUtils;
import com.wanggh8.mydrive.R;
import com.wanggh8.mydrive.base.BaseFragment;
import com.wanggh8.mydrive.bean.DriveBean;
import com.wanggh8.mydrive.config.DriveType;
import com.wanggh8.mydrive.ui.popwin.DriveListPopupWindow;
import com.wanggh8.mydrive.utils.ScreenUtil;
import com.wuhenzhizao.titlebar.widget.CommonTitleBar;

import java.util.ArrayList;
import java.util.List;

/**
 * 主页个人设置页面
 *
 * @author wanggh8
 *
 * @version V1.0
 * @date 2020/9/30
 */
public class MainPersonalFragment extends BaseFragment {

    // titlebar
    private CommonTitleBar titleBar;
    private View rightView;
    private ImageView ivAdd;
    private LinearLayout llRightView;

    private RecyclerView recyclerView;
    private DriveListPopupWindow newDriveListPopwin;

    // 新连接
    private List<DriveBean> newDriveList = new ArrayList<>();

    @Override
    public int getContentLayout() {
        return R.layout.fragment_main_personal;
    }

    @Override
    public void beforeInitView() {
        initNewDriveList();
    }

    private void initNewDriveList() {
        if (newDriveList.isEmpty()) {
            DriveType onedrive = DriveType.oneDrive;
            newDriveList.add(new DriveBean(onedrive.getTypeName(), onedrive.getTypeName(), onedrive.getTypeIconId()));
            DriveType googleDrive = DriveType.googleDrive;
            newDriveList.add(new DriveBean(googleDrive.getTypeName(), googleDrive.getTypeName(), googleDrive.getTypeIconId()));
        }
    }

    @Override
    public void initView() {
        titleBar = findViewById(R.id.title_bar_main_personal);
        rightView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_main_personal_titlebar_right_view, null, false);
        ivAdd = rightView.findViewById(R.id.iv_main_personal_add);
        rightView.setClickable(true);
        titleBar.setRightView(rightView);
        newDriveListPopwin = new DriveListPopupWindow(getContext());
        llRightView = findViewById(R.id.ll_main_personal_titlebar_right_view);
    }

    @Override
    public void afterInitView() {

    }

    @Override
    public void bindListener() {
        ivAdd.setOnClickListener(view -> {
            // TODO: 2020/10/10 popwin

            newDriveListPopwin.showPopWindowAsDropDown(newDriveList, 0, rightView, 0, 5, new DriveListPopupWindow.OnSelectListener() {
                @Override
                public void onSelect(String selected, int position) {
                    switch (selected) {
                        case "oneDrive":

                            break;
                        case "Google Drive":
                            ToastUtils.show("暂未支持");
                            break;
                    }
                }

                @Override
                public void onCancel() {

                }
            });
        });
    }

    @Override
    public void onClickEvent(View v) {

    }
}
