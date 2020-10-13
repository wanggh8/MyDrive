package com.wanggh8.mydrive.utils;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.hjq.toast.ToastUtils;
import com.microsoft.identity.client.AuthenticationCallback;
import com.microsoft.identity.client.IAccount;
import com.microsoft.identity.client.IMultipleAccountPublicClientApplication;
import com.microsoft.identity.client.IPublicClientApplication;
import com.microsoft.identity.client.PublicClientApplication;
import com.microsoft.identity.client.exception.MsalException;
import com.wanggh8.mydrive.R;

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

    private static AuthenticationHelper mInstance = null;
    private static IMultipleAccountPublicClientApplication mMultipleAccountApp = null;

    // 用户列表
    private List<IAccount> accountList = new ArrayList<>();
    // 当前选中用户
    private IAccount selectedAccount;
    // 获取默认authority
    private String authority;
    // 权限范围
    private String[] mScopes = { "User.Read", "Calendars.Read" };

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
                        listener.onCreated(mInstance);
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
        if (mInstance == null) {
            mInstance = new AuthenticationHelper(ctx, listener);
        } else {
            listener.onCreated(mInstance);
        }
    }

    /**
     * 单例模式获取实例，不存在则抛出异常
     *
     * @return AuthenticationHelper实例
     */
    public static synchronized AuthenticationHelper getInstance() {
        if (mInstance == null) {
            throw new IllegalStateException(
                    "AuthenticationHelper has not been initialized");
        }
        return mInstance;
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
                listener.onSuccess(accountList);
            }

            @Override
            public void onError(MsalException exception) {
                Log.d("MSAL", "Error load the Account list", exception);
                if ("".equals(exception.getErrorCode())) {
                    ToastUtils.show("网络未连接");
                }
                listener.onError(exception);
            }
        });
    }


    public void acquireTokenInteractively(Activity activity, AuthenticationCallback callback) {
        mMultipleAccountApp.acquireToken(activity, mScopes, callback);
    }

    public void acquireTokenSilently(AuthenticationCallback callback) {
        mMultipleAccountApp.acquireTokenSilentAsync(mScopes, selectedAccount, authority, callback);
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
     * 获取用户列表
     *
     * @return List<IAccount>
     */
    public List<IAccount> getAccountList() {
        return accountList;
    }

    /**
     * 设置选中用户
     *
     * @param account IAccount
     */
    public void setSelectedAccount(IAccount account) {
        if (account != null) {
            if (accountList.contains(account)) {
                selectedAccount = account;
            }
        }
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

}

