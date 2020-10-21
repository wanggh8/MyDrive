package com.wanggh8.mydrive.presenter;

import android.app.Activity;
import android.content.Context;

import com.microsoft.identity.client.AuthenticationCallback;
import com.microsoft.identity.client.IAccount;
import com.microsoft.identity.client.IAuthenticationResult;
import com.microsoft.identity.client.exception.MsalException;
import com.wanggh8.mydrive.base.BasePresenter;
import com.wanggh8.mydrive.bean.DriveBean;
import com.wanggh8.mydrive.callback.CommonLoadCallback;
import com.wanggh8.mydrive.callback.CommonLoadListCallback;
import com.wanggh8.mydrive.contract.AuthenticationContract;
import com.wanggh8.mydrive.model.AuthenticationModel;
import com.wanggh8.mydrive.utils.AuthenticationHelper;

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

    // 错误消息
    String errorMsg;
    // 错误代码
    int errorCode;

    public AuthenticationPresenter(Context context, AuthenticationContract.View ctrView) {
        super(context, ctrView);
        authenticationModel = new AuthenticationModel();
    }


    /**
     * 获取网盘列表
     *
     * @param fromNet 是否不读本地缓存直接拉取网络
     */
    @Override
    public void getDriveList(boolean fromNet) {
        authenticationModel.getDriveList(fromNet, new CommonLoadListCallback<DriveBean>() {
            @Override
            public void onSuccess(List<DriveBean> list) {
                ctrView.getDriveListSuccess(list);
            }

            @Override
            public void onError(Exception exception) {
                onDealError(exception);
                ctrView.getDriveListFail(errorMsg, errorCode);
            }
        });
    }

    /**
     * 根据id获取用户
     *
     * @param id 用户id
     * @param type 网盘类型
     */
    @Override
    public void getAccountById(String id, String type) {
        switch (type) {
            case "OneDrive":
                authenticationModel.getMSALAccountById(id, new CommonLoadCallback<IAccount>() {
                    @Override
                    public void onSuccess(IAccount rsp) {
                        ctrView.getAccountByIdSuccess(rsp);
                    }

                    @Override
                    public void onError(Exception exception) {
                        onDealError(exception);
                        ctrView.getAccountByIdFail(errorMsg, errorCode);

                    }
                });
                break;
        }
    }

    /**
     * 添加网盘
     *
     * @param activity 调用的Activity
     * @param type 网盘类型
     */
    @Override
    public void addAccount(Activity activity, String type) {
        switch (type) {
            case "OneDrive":
                signInOneDrive(activity);
                break;
        }
    }

    /**
     * OneDrive登陆
     *
     * @param activity 调用的Activity
     */
    private void signInOneDrive(Activity activity) {
        AuthenticationHelper.getInstance().acquireTokenInteractively(activity, new AuthenticationCallback() {
            @Override
            public void onCancel() {

            }

            @Override
            public void onSuccess(IAuthenticationResult authenticationResult) {
                AuthenticationHelper.getInstance().setIAuthenticationResult(authenticationResult);
                ctrView.addAccountSuccess();
            }

            @Override
            public void onError(MsalException exception) {
                onDealError(exception);
                ctrView.addAccountFail(errorMsg, errorCode);
            }
        });
    }

    /**
     * 删除在线网盘
     *
     * @param id 用户id
     * @param type 网盘类型
     */
    @Override
    public void removeAccountById(String id, String type) {
        switch (type) {
            case "OneDrive":
                signOutOneDrive(id);
                break;
        }
    }

    /**
     * 退出登陆OneDrive
     *
     * @param id 用户id
     */
    private void signOutOneDrive(String id) {
        authenticationModel.getMSALAccountById(id, new CommonLoadCallback<IAccount>() {
            @Override
            public void onSuccess(IAccount rsp) {
                authenticationModel.removeMSALAccount(rsp, new CommonLoadCallback<String>() {
                    @Override
                    public void onSuccess(String rsp) {
                       ctrView.removeAccountSuccess();
                    }

                    @Override
                    public void onError(Exception exception) {
                        onDealError(exception);
                        ctrView.removeAccountFail(errorMsg, errorCode);
                    }
                });
            }

            @Override
            public void onError(Exception exception) {
                onDealError(exception);
                ctrView.removeAccountFail(errorMsg, errorCode);
            }
        });
    }

    /**
     * 自定义网盘名
     *
     * @param id 网盘id
     * @param myName 自定义网盘名
     */
    @Override
    public void editDriveName(String id, String myName) {

    }

    /**
     * 获取网盘token
     *
     * @param id 网盘用户id
     */
    @Override
    public void getAccessToken(String id) {

    }

    /**
     * 异常处理方法
     *
     * @param exception Exception
     */
    private void onDealError(Exception exception) {
        if (exception instanceof MsalException) {
            MsalException e = (MsalException) exception;
            errorCode = AuthenticationHelper.onDealError(e);
            errorMsg = e.getErrorCode();
        }
    }

}
