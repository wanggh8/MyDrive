package com.wanggh8.mydrive.fragment.main;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.hjq.toast.ToastUtils;
import com.microsoft.identity.client.IAccount;
import com.microsoft.identity.client.IAuthenticationResult;
import com.microsoft.identity.client.IMultipleAccountPublicClientApplication;
import com.microsoft.identity.client.exception.MsalException;
import com.wanggh8.mydrive.R;
import com.wanggh8.mydrive.adapter.DriveAdapter;
import com.wanggh8.mydrive.base.BaseFragment;
import com.wanggh8.mydrive.bean.DriveBean;
import com.wanggh8.mydrive.bean.DriveNewBean;
import com.wanggh8.mydrive.config.DriveType;
import com.wanggh8.mydrive.contract.AuthenticationContract;
import com.wanggh8.mydrive.presenter.AuthenticationPresenter;
import com.wanggh8.mydrive.ui.popwin.DriveListPopupWindow;
import com.wanggh8.mydrive.utils.AuthenticationHelper;
import com.wanggh8.mydrive.utils.DriveDBUtil;
import com.wuhenzhizao.titlebar.widget.CommonTitleBar;
import com.yanzhenjie.recyclerview.OnItemMenuClickListener;
import com.yanzhenjie.recyclerview.SwipeMenu;
import com.yanzhenjie.recyclerview.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.SwipeMenuItem;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

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
public class MainPersonalFragment extends BaseFragment implements AuthenticationContract.View{

    // titlebar
    private CommonTitleBar titleBar;
    private View rightView;
    private ImageView ivAdd;
    private LinearLayout llRightView;

    // 网盘列表
    private SwipeRecyclerView rvDriveList;
    private DriveAdapter driveAdapter;
    // 新连接弹窗
    private DriveListPopupWindow newDriveListPopwin;

    // 新连接
    private List<DriveNewBean> newDriveList = new ArrayList<>();

    // OneDrive
    private IAccount selectedAccount;
    private List<IAccount> accountList;
    // 已连接网盘列表
    private List<DriveBean> driveBeanList = new ArrayList<>();

    private AuthenticationContract.Presenter authPresenter;

    @Override
    public int getContentLayout() {
        return R.layout.fragment_main_personal;
    }

    @Override
    public void beforeInitView() {
        authPresenter = new AuthenticationPresenter(mContext, this);
        initNewDriveList();
    }


    private void initNewDriveList() {
        if (newDriveList.isEmpty()) {
            DriveType onedrive = DriveType.oneDrive;
            newDriveList.add(new DriveNewBean(onedrive.getTypeName(), onedrive.getTypeName(), onedrive.getTypeIconId()));
            DriveType googleDrive = DriveType.googleDrive;
            newDriveList.add(new DriveNewBean(googleDrive.getTypeName(), googleDrive.getTypeName(), googleDrive.getTypeIconId()));
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
        rvDriveList = findViewById(R.id.rv_drive_list);
    }

    @Override
    public void afterInitView() {
        // 创建侧滑菜单
        SwipeMenuCreator mSwipeMenuCreator = new SwipeMenuCreator() {
            @Override
            public void onCreateMenu(SwipeMenu leftMenu, SwipeMenu rightMenu, int position) {

                int width = getResources().getDimensionPixelSize(R.dimen.suit_70);
                // 1. MATCH_PARENT 自适应高度，保持和Item一样高;
                // 2. 指定具体的高，比如80;
                // 3. WRAP_CONTENT，自身高度，不推荐;
                int height = ViewGroup.LayoutParams.MATCH_PARENT;

//                SwipeMenuItem setDefaultItem = new SwipeMenuItem(mContext)
//                        .setWidth(width)
//                        .setHeight(height)
//                        .setBackground(R.color.colorSwipeSetDefault)
//                        .setText(R.string.setDefault);
//                rightMenu.addMenuItem(setDefaultItem);

                SwipeMenuItem editItem = new SwipeMenuItem(mContext)
                        .setWidth(width)
                        .setHeight(height)
                        .setBackground(R.color.colorSwipeEdit)
                        .setText(R.string.edit);
                rightMenu.addMenuItem(editItem);

                SwipeMenuItem deleteItem = new SwipeMenuItem(mContext)
                        .setWidth(width)
                        .setHeight(height)
                        .setBackground(R.color.colorSwipeSetDelete)
                        .setText(R.string.delete);
                rightMenu.addMenuItem(deleteItem);
            }
        };
        rvDriveList.setAdapter(null);
        rvDriveList.setSwipeMenuCreator(mSwipeMenuCreator);
        // 菜单点击监听。
        rvDriveList.setOnItemMenuClickListener(new OnItemMenuClickListener() {
            @Override
            public void onItemClick(SwipeMenuBridge menuBridge, int adapterPosition) {
                // 任何操作必须先关闭菜单，否则可能出现Item菜单打开状态错乱。
                menuBridge.closeMenu();

                // 左侧还是右侧菜单：
                int direction = menuBridge.getDirection();
                // 菜单在Item中的Position：
                int menuPosition = menuBridge.getPosition();

                if (direction == SwipeRecyclerView.RIGHT_DIRECTION) {
                    Log.d(TAG, "onItemClick: " + menuPosition);
                    if (menuPosition == 0) {

                    } else if (menuPosition == 1) {
                        authPresenter.removeAccountById(driveAdapter.getItem(adapterPosition).getId(), driveAdapter.getItem(adapterPosition).getType());
                    }
                }
                if (direction == SwipeRecyclerView.LEFT_DIRECTION) {

                }
            }
        });

        driveAdapter = new DriveAdapter(mContext);
        rvDriveList.setLayoutManager(new LinearLayoutManager(mContext));
        rvDriveList.setAdapter(driveAdapter);
        authPresenter.getDriveList(false);
    }

    @Override
    public void bindListener() {
        rightView.setOnClickListener(view -> {
            newDriveListPopwin.showPopWindowAsDropDown(newDriveList, 0, rightView, 0, 5, new DriveListPopupWindow.OnSelectListener() {
                @Override
                public void onSelect(String selected, int position) {
                    switch (selected) {
                        case "OneDrive":
                            authPresenter.addAccount(getActivity(), "OneDrive");
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

        driveAdapter.setSimpleOnItemClickListener((position, bean) -> {
            ToastUtils.show(bean.getName());
        });
    }

    @Override
    public void onClickEvent(View v) {

    }


    @Override
    public void getDriveListSuccess(List<DriveBean> rsp) {
        driveBeanList.clear();
        driveBeanList = rsp;
        if (driveAdapter != null) {
            driveAdapter.setCollection(driveBeanList);
        }
    }

    @Override
    public void getDriveListFail(String msg, int code) {

    }

    @Override
    public void getAccountByIdSuccess(IAccount rsp) {

    }

    @Override
    public void getAccountByIdFail(String msg, int code) {

    }

    @Override
    public void addAccountSuccess() {
        authPresenter.getDriveList(true);
    }

    @Override
    public void addAccountFail(String msg, int code) {

    }

    @Override
    public void removeAccountSuccess() {
        authPresenter.getDriveList(false);
    }

    @Override
    public void removeAccountFail(String msg, int code) {

    }
}
