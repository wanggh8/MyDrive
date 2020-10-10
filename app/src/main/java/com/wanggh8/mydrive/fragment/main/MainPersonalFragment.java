package com.wanggh8.mydrive.fragment.main;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.hjq.toast.ToastUtils;
import com.wanggh8.mydrive.R;
import com.wanggh8.mydrive.base.BaseFragment;
import com.wuhenzhizao.titlebar.widget.CommonTitleBar;

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

    private RecyclerView recyclerView;


    @Override
    public int getContentLayout() {
        return R.layout.fragment_main_personal;
    }

    @Override
    public void beforeInitView() {

    }

    @Override
    public void initView() {
        titleBar = findViewById(R.id.title_bar_main_personal);
        rightView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_main_personal_titlebar_right_view, null, false);
        ivAdd = rightView.findViewById(R.id.iv_main_personal_add);
        rightView.setClickable(true);
        titleBar.setRightView(rightView);
    }

    @Override
    public void afterInitView() {

    }

    @Override
    public void bindListener() {
        ivAdd.setOnClickListener(view -> {
            ToastUtils.show("添加测试");
            // TODO: 2020/10/10 popwin
        });
    }

    @Override
    public void onClickEvent(View v) {

    }
}
