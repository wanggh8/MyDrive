package com.wanggh8.mydrive.utils;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.hjq.toast.ToastUtils;
import com.microsoft.graph.models.extensions.Drive;
import com.microsoft.identity.client.AuthenticationCallback;
import com.microsoft.identity.client.IAccount;
import com.microsoft.identity.client.IAuthenticationResult;
import com.microsoft.identity.client.IMultipleAccountPublicClientApplication;
import com.microsoft.identity.client.IPublicClientApplication;
import com.microsoft.identity.client.PublicClientApplication;
import com.microsoft.identity.client.exception.MsalClientException;
import com.microsoft.identity.client.exception.MsalException;
import com.microsoft.identity.client.exception.MsalServiceException;
import com.microsoft.identity.client.exception.MsalUiRequiredException;
import com.wanggh8.mydrive.R;
import com.wanggh8.mydrive.bean.DriveBean;
import com.wanggh8.mydrive.config.DriveType;

import java.util.ArrayList;
import java.util.List;

/**
 * 微软 MSAL 多用户认证工具
 * 单例类
 *
 * @author wanggh8
 * @version V1.0
 * @date 2020/9/24
 */
public class AuthenticationHelper {

    // 错误代码
    public static int OTHER_ERROR = 0;
    public static int NETWORK_NOT_AVAILABLE = 1;
    public static int NO_CURRENT_ACCOUNT = 2;
    public static int Client_ERROR = 3;
    public static int SERVICE_ERROR = 4;

    private static AuthenticationHelper INSTANCE = null;
    private static IMultipleAccountPublicClientApplication mMultipleAccountApp = null;

    // 用户列表
    private List<IAccount> accountList = new ArrayList<>();
    // DriveBean列表
    private List<DriveBean> driveBeanList = new ArrayList<>();

    // 当前用户认证结果
    private IAuthenticationResult mIAuthenticationResult;
    // 当前用户token
    private String accessToken = null;

    // 获取默认authority
    private String authority;
    // 权限范围
    private String[] mScopes = { "User.Read", "Calendars.Read", "Files.ReadWrite.All" };

    /**
     * 构造方法
     *
     * @param ctx Context
     * @param listener IAuthenticationHelperCreatedListener mMultipleAccountApp创建监听
     */
    private AuthenticationHelper(Context ctx, final IAuthenticationHelperCreatedListener listener) {
        PublicClientApplication.createMultipleAccountPublicClientApplication(ctx, R.raw.msal_config,
                new IPublicClientApplication.IMultipleAccountApplicationCreatedListener() {
                    @Override
                    public void onCreated(IMultipleAccountPublicClientApplication application) {
                        mMultipleAccountApp = application;

                        authority = mMultipleAccountApp.getConfiguration().getDefaultAuthority().getAuthorityURL().toString();
                        listener.onCreated(INSTANCE);
                    }

                    @Override
                    public void onError(MsalException exception) {
                        Log.e("MSAL", "Error creating MSAL application", exception);
                        listener.onError(exception);
                    }
                });
    }

    /**
     * 单例模式设置实例，不存在则创建
     *
     * @param ctx Context
     * @param listener IAuthenticationHelperCreatedListener mMultipleAccountApp创建监听
     */
    public static synchronized void setInstance(Context ctx, IAuthenticationHelperCreatedListener listener) {
        if (INSTANCE == null) {
            INSTANCE = new AuthenticationHelper(ctx, listener);
        } else {
            listener.onCreated(INSTANCE);
        }
    }

    /**
     * 单例模式获取实例，不存在则抛出异常
     *
     * @return AuthenticationHelper实例
     */
    public static synchronized AuthenticationHelper getInstance() {
        if (INSTANCE == null) {
            throw new IllegalStateException(
                    "AuthenticationHelper has not been initialized");
        }
        return INSTANCE;
    }
    /**
     * 单例模式获取IMultipleAccountPublicClientApplication实例，不存在则抛出异常
     *
     * @return AuthenticationHelper实例
     */
    public static IMultipleAccountPublicClientApplication getMultipleAccountApp() {
        if (mMultipleAccountApp == null) {
            throw new IllegalStateException(
                    "AuthenticationHelper has not been initialized");
        }
        return mMultipleAccountApp;
    }

    /**
     * 异步获取Account列表
     *
     * @param listener LoadAccountListListener 获取Account列表回调
     */
    public void loadAccounts(final LoadAccountListListener listener) {
        if (mMultipleAccountApp == null) {
            return;
        }
        // Async异步获取Account列表
        mMultipleAccountApp.getAccounts(new IPublicClientApplication.LoadAccountsCallback() {
            @Override
            public void onTaskCompleted(final List<IAccount> result) {
                accountList = result;
                for (IAccount account : accountList) {
                    SPManager.putObject(account.getId(), account);
                }
                listener.onSuccess(accountList);
            }

            @Override
            public void onError(MsalException exception) {
                Log.d("MSAL", "Error load the Account list", exception);
                onDealError(exception, true);
                listener.onError(exception);
            }
        });
    }

    /**
     * 从本地加载网盘列表
     *
     */
    public void loadDriveList() {
        driveBeanList.clear();
        driveBeanList = DriveDBUtil.queryAll();
    }


    public DriveBean setDriveBean(IAccount account) {
        DriveBean driveBean = new DriveBean();
        driveBean.setName(account.getUsername());
        driveBean.setType(DriveType.oneDrive.getTypeName());
        driveBean.setIconId(DriveType.oneDrive.getTypeIconId());
        driveBean.setId(account.getId());
        return driveBean;
    }



    /**
     * 交互式获取token
     *
     * @param activity Activity
     * @param callback AcquireTokenCallback
     */
    public void acquireTokenInteractively(Activity activity, AcquireTokenCallback callback) {
        mMultipleAccountApp.acquireToken(activity, mScopes, new AuthenticationCallback() {
            @Override
            public void onCancel() {

            }

            @Override
            public void onSuccess(IAuthenticationResult authenticationResult) {
                callback.onSuccess(authenticationResult);
            }

            @Override
            public void onError(MsalException exception) {
                onDealError(exception, true);
                callback.onError(exception);
            }
        });
    }

    /**
     * 静默式获取token
     *
     * @param callback AcquireTokenCallback
     */
    public void acquireTokenSilently(Activity activity, IAccount account, AcquireTokenCallback callback) {
        mMultipleAccountApp.acquireTokenSilentAsync(mScopes, account, authority, new AuthenticationCallback() {
            @Override
            public void onCancel() {

            }

            @Override
            public void onSuccess(IAuthenticationResult authenticationResult) {
                callback.onSuccess(authenticationResult);

            }

            @Override
            public void onError(MsalException exception) {
                if (exception instanceof MsalUiRequiredException) {
                    Log.d("AUTH", "Interactive login required");
                    // 静默登陆时需要重新显式登陆
                    acquireTokenInteractively(activity, callback);

                } else if (exception instanceof MsalClientException) {
                    if ("device_network_not_available".equals(exception.getErrorCode())) {
                        ToastUtils.show("网络未连接");
                    } else if (exception.getErrorCode() == "no_current_account") {
                        Log.d("AUTH", "No current account, interactive login required");
                        // 静默登陆时需要重新显式登陆doInteractiveSignIn();
                        // doInteractiveSignIn();
                    } else {
                        // Exception inside MSAL, more info inside MsalError.java
                        Log.e("AUTH", "Client error authenticating", exception);
                    }
                } else if (exception instanceof MsalServiceException) {
                    // Exception when communicating with the auth server, likely config issue
                    Log.e("AUTH", "Service error authenticating", exception);
                }
                callback.onError(exception);
            }
        });
    }

    /**
     * 删除用户
     *
     * @param id
     * @param callback
     */
    public void removeAccount(String id, IMultipleAccountPublicClientApplication.RemoveAccountCallback callback)  {
        mMultipleAccountApp.getAccount(id, new IMultipleAccountPublicClientApplication.GetAccountCallback() {
            @Override
            public void onTaskCompleted(IAccount result) {
                mMultipleAccountApp.removeAccount(result, callback);
            }

            @Override
            public void onError(MsalException exception) {

                if ("device_network_not_available".equals(exception.getErrorCode())) {
                    ToastUtils.show("网络未连接");
                }
                else {
                    ToastUtils.show("获取用户失败");
                }
            }
        });

    }

    /**
     * 根据索引获取当前账户
     *
     * @param position 用户列表索引
     * @return IAccount
     */
    public IAccount getAccount(int position) {
        return accountList.get(position);
    }

    /**
     * 根据索引获取当前账户
     * 可获取当前内存中和sp中的IAccount
     *
     * @param id 网盘用户唯一索引id
     * @return IAccount
     */
    public IAccount getAccountById(String id) {
        for (IAccount account : accountList) {
            if (id.equals(account.getId())) {
                return account;
            }
        }
        IAccount spAccount = SPManager.getObject(id, IAccount.class);
        if (spAccount != null) {
            return spAccount;
        }
        return null;
    }

    /**
     * 网络请求根据索引获取当前账户
     *
     * @param id 网盘用户唯一索引id
     * @param callback IMultipleAccountPublicClientApplication.GetAccountCallback
     */
    public void getAccountByIdFromNet(String id, IMultipleAccountPublicClientApplication.GetAccountCallback callback) {
        mMultipleAccountApp.getAccount(id, callback);
    }

    /**
     * 获取用户列表
     *
     * @return List<IAccount>
     */
    public List<IAccount> getAccountList() {
        return accountList;
    }

    public List<DriveBean> getDriveBeanList() {
        return driveBeanList;
    }

    public void setDriveBeanList(List<DriveBean> driveBeanList) {
        this.driveBeanList = driveBeanList;
    }

    /**
     * 获取当前认证结果
     *
     * @return IAuthenticationResult
     */
    public IAuthenticationResult getIAuthenticationResult() {
        return mIAuthenticationResult;
    }

    /**
     * 获取当前用户token
     *
     * @return String
     */
    public String getAccessToken() {
        return accessToken;
    }

    /**
     * 设置当前认证结果
     *
     * @param mIAuthenticationResult IAuthenticationResult
     */
    public void setIAuthenticationResult(IAuthenticationResult mIAuthenticationResult) {
        this.mIAuthenticationResult = mIAuthenticationResult;
        this.accessToken = mIAuthenticationResult.getAccessToken();
    }


    /**
     * 网络请求失败处理
     *
     * @param exception MsalException
     * @param isToast 是否显示Toast
     * @return 错误代码
     * @see AuthenticationHelper
     */
    public static int onDealError(MsalException exception, boolean isToast) {
        if (!isToast) {
            if (exception instanceof MsalClientException) {
                if ("device_network_not_available".equals(exception.getErrorCode())) {
                    ToastUtils.show("网络未连接");
                    return NETWORK_NOT_AVAILABLE;
                } else if (exception.getErrorCode() == "no_current_account") {
                    Log.d("AUTH", "No current account, interactive login required");
                    // 静默登陆时需要重新显式登陆doInteractiveSignIn();
                    // doInteractiveSignIn();
                    return NO_CURRENT_ACCOUNT;
                } else {
                    // Exception inside MSAL, more info inside MsalError.java
                    Log.e("AUTH", "Client error authenticating", exception);
                    return Client_ERROR;
                }
            } else if (exception instanceof MsalServiceException) {
                // Exception when communicating with the auth server, likely config issue
                Log.e("AUTH", "Service error authenticating", exception);
                return SERVICE_ERROR;
            }
        }
        else {
            if (exception instanceof MsalClientException) {
                if ("device_network_not_available".equals(exception.getErrorCode())) {
                    return NETWORK_NOT_AVAILABLE;
                } else if (exception.getErrorCode() == "no_current_account") {
                    Log.d("AUTH", "No current account, interactive login required");
                    return NO_CURRENT_ACCOUNT;
                } else {
                    Log.e("AUTH", "Client error authenticating", exception);
                    return Client_ERROR;
                }
            } else if (exception instanceof MsalServiceException) {
                Log.e("AUTH", "Service error authenticating", exception);
                return SERVICE_ERROR;
            }
        }
        return OTHER_ERROR;
    }


    /**
     * 创建多帐户公共客户端应用监听
     */
    public interface IAuthenticationHelperCreatedListener {
        /**
         * 创建成功回调
         *
         * @param authHelper AuthenticationHelper实例
         */
        void onCreated(final AuthenticationHelper authHelper);

        void onError(final MsalException exception);
    }

    /**
     * 加载用户列表监听
     */
    public interface LoadAccountListListener {
        /**
         * 加载用户列表成功回调
         * 可进行更新UI操作
         *
         * @param accountList 用户列表
         */
        void onSuccess(final List<IAccount> accountList);

        void onError(final MsalException exception);
    }

    /**
     * 获取token监听回调
     */
    public interface AcquireTokenCallback {

        void onSuccess(final IAuthenticationResult authenticationResult);

        void onError(final MsalException exception);
    }


}

