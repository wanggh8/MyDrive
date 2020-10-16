package com.wanggh8.mydrive.presenter;

import android.content.Context;

import com.hjq.toast.ToastUtils;
import com.microsoft.identity.client.IAccount;
import com.microsoft.identity.client.IMultipleAccountPublicClientApplication;
import com.microsoft.identity.client.exception.MsalException;
import com.wanggh8.mydrive.base.BasePresenter;
import com.wanggh8.mydrive.bean.DriveBean;
import com.wanggh8.mydrive.callback.CommonLoadListCallback;
import com.wanggh8.mydrive.contract.AuthenticationContract;
import com.wanggh8.mydrive.model.AuthenticationModel;
import com.wanggh8.mydrive.utils.AuthenticationHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * 微软统一认证
 *
 * @author wanggh8
 * @version V1.0
 * @date 2020/10/16
 */
public class AuthenticationPresenter extends BasePresenter<AuthenticationContract.View> implements AuthenticationContract.Presenter {

    private AuthenticationModel authenticationModel;
    // 用户列表
    private List<IAccount> accountList = new ArrayList<>();
    // DriveBean列表
    private List<DriveBean> driveBeanList = new ArrayList<>();

    public AuthenticationPresenter(Context context, AuthenticationContract.View ctrView) {
        super(context, ctrView);
        authenticationModel = new AuthenticationModel();
    }


    @Override
    public void getDriveList() {

        authenticationModel.getDriveList(new CommonLoadListCallback<DriveBean>() {
            @Override
            public void onSuccess(List<DriveBean> list) {

            }

            @Override
            public void onError(Exception exception) {

                if (exception instanceof MsalException) {
                    MsalException e = (MsalException) exception;
                    e.getErrorCode();
                }
            }
        });

    }

    @Override
    public void getAccountList() {

    }

    @Override
    public void getAccountById(String id) {

    }

    @Override
    public void addAccount() {

    }

    @Override
    public void removeAccountById(String id) {

    }

    @Override
    public void editDriveName(String id, String myName) {

    }

    @Override
    public void getAccessToken(String id) {

    }
}
