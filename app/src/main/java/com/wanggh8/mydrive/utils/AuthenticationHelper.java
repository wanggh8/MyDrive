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
import com.microsoft.identity.client.SilentAuthenticationCallback;
import com.microsoft.identity.client.exception.MsalClientException;
import com.microsoft.identity.client.exception.MsalException;
import com.microsoft.identity.client.exception.MsalServiceException;
import com.microsoft.identity.client.exception.MsalUiRequiredException;
import com.wanggh8.mydrive.R;
import com.wanggh8.mydrive.bean.DriveBean;
import com.wanggh8.mydrive.config.DriveType;

import java.util.ArrayList;
import java.util.List;

import static com.wanggh8.mydrive.config.Code.Client_ERROR;
import static com.wanggh8.mydrive.config.Code.NEED_LOGIN;
import static com.wanggh8.mydrive.config.Code.NETWORK_NOT_AVAILABLE;
import static com.wanggh8.mydrive.config.Code.NO_CURRENT_ACCOUNT;
import static com.wanggh8.mydrive.config.Code.OTHER_ERROR;
import static com.wanggh8.mydrive.config.Code.SERVICE_ERROR;

/**
 * 微软 MSAL 多用户认证工具
 * 单例类
 *
 * @author wanggh8
 * @version V1.0
 * @date 2020/9/24
 */
public class AuthenticationHelper {



    private static AuthenticationHelper INSTANCE = null;
    private static IMultipleAccountPublicClientApplication mMultipleAccountApp = null;

    // 当前用户认证结果
    private IAuthenticationResult mIAuthenticationResult;
    // 当前用户token
    private String accessToken = null;

    // 获取默认authority
    private String authority;
    // 权限范围
    private String[] mScopes = { "User.Read", "Files.ReadWrite.All" };

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
                        listener.onCreated(mMultipleAccountApp);
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
            if (mMultipleAccountApp != null) {
                listener.onCreated(mMultipleAccountApp);
            }
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
     */
    public void loadAccounts(IPublicClientApplication.LoadAccountsCallback callback) {
        if (mMultipleAccountApp == null) {
            throw new IllegalStateException(
                    "AuthenticationHelper has not been initialized");
        }
        // Async异步获取Account列表
        mMultipleAccountApp.getAccounts(callback);
    }

    /**
     * 交互式获取token
     *
     * @param activity Activity
     * @param callback AcquireTokenCallback
     */
    public void acquireTokenInteractively(Activity activity, AuthenticationCallback callback) {
        mMultipleAccountApp.acquireToken(activity, mScopes, callback);
    }

    /**
     * 静默式获取token
     *
     * @param callback SilentAuthenticationCallback
     */
    public void acquireTokenSilently(Activity activity, IAccount account, SilentAuthenticationCallback callback) {
        mMultipleAccountApp.acquireTokenSilentAsync(mScopes, account, authority, callback);
    }

    /**
     * 删除用户
     *
     * @param account IAccount
     * @param callback
     */
    public void removeAccount(IAccount account, IMultipleAccountPublicClientApplication.RemoveAccountCallback callback)  {
        mMultipleAccountApp.removeAccount(account, callback);
    }

    /**
     * 网络请求根据索引获取当前账户
     *
     * @param id 网盘用户唯一索引id
     * @param callback IMultipleAccountPublicClientApplication.GetAccountCallback
     */
    public void getAccountById(String id, IMultipleAccountPublicClientApplication.GetAccountCallback callback) {
        mMultipleAccountApp.getAccount(id, callback);
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
     * @return 错误代码
     * @see AuthenticationHelper
     */
    public static int onDealError(MsalException exception) {
        if (exception instanceof MsalUiRequiredException) {
            Log.d("AUTH", "Interactive login required");
            // 静默登陆时需要重新显式登陆
            return NEED_LOGIN;
        } else if (exception instanceof MsalClientException) {
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
        return OTHER_ERROR;
    }


    /**
     * 创建多帐户公共客户端应用监听
     */
    public interface IAuthenticationHelperCreatedListener {
        /**
         * 创建成功回调
         *
         * @param multipleAccountApp IMultipleAccountPublicClientApplication实例
         */
        void onCreated(final IMultipleAccountPublicClientApplication multipleAccountApp);

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

