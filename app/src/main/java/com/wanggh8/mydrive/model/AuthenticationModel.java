package com.wanggh8.mydrive.model;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.microsoft.identity.client.IAccount;
import com.microsoft.identity.client.IAuthenticationResult;
import com.microsoft.identity.client.IMultipleAccountPublicClientApplication;
import com.microsoft.identity.client.IPublicClientApplication;
import com.microsoft.identity.client.exception.MsalException;
import com.wanggh8.mydrive.base.BaseModel;
import com.wanggh8.mydrive.bean.DriveBean;
import com.wanggh8.mydrive.callback.CommonLoadCallback;
import com.wanggh8.mydrive.callback.CommonLoadListCallback;
import com.wanggh8.mydrive.utils.APPUtil;
import com.wanggh8.mydrive.utils.AuthenticationHelper;
import com.wanggh8.mydrive.utils.DriveDBUtil;
import com.wanggh8.mydrive.utils.SPManager;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wanggh8
 * @version V1.0
 * @date 2020/10/16
 */
public class AuthenticationModel extends BaseModel {

    // 微软用户列表
    private List<IAccount> msalAccountList = new ArrayList<>();

    /**
     * 获取网盘列表
     *
     * @param fromNet 是否不读本地缓存直接拉取网络
     * @param callback CommonLoadListCallback
     */
    public void getDriveList(boolean fromNet, CommonLoadListCallback<DriveBean> callback) {
        if (!fromNet) {
            List<DriveBean> driveBeanList;
            // 从本地数据库查询
            driveBeanList = DriveDBUtil.queryAll();
            if (driveBeanList != null && !driveBeanList.isEmpty()) {
                callback.onSuccess(driveBeanList);
                return;
            }
        }

        /* - - - - - - - 查询结果为空时，进行网络拉取 - - - - - - - */

        List<DriveBean> driveBeanListFromNet = new ArrayList<>();
        // 微软账户拉取
        AuthenticationHelper.getInstance().loadAccounts(new IPublicClientApplication.LoadAccountsCallback() {
            @Override
            public void onTaskCompleted(List<IAccount> result) {
                msalAccountList = result;

                for (IAccount account : result) {
                    DriveBean bean = new DriveBean();
                    bean.setDriveBean(account);
                    driveBeanListFromNet.add(bean);
                    DriveDBUtil.update(bean);
                }
                callback.onSuccess(driveBeanListFromNet);
            }

            @Override
            public void onError(MsalException exception) {
                callback.onError(exception);
            }
        });

    }

    /**
     * 微软根据id获取用户
     *
     * @param id id
     * @param callback CommonLoadCallback
     */
    public void getMSALAccountById(String id, CommonLoadCallback<IAccount> callback) {
        for (IAccount account : msalAccountList) {
            if (id.equals(account.getId())) {
                callback.onSuccess(account);
                return;
            }
        }

        // 查询结果为空时，读取sp缓存
        IAccount spAccount = SPManager.getObject(id, IAccount.class);
        if (spAccount != null) {
            callback.onSuccess(spAccount);
            return;
        }

        // 查询结果为空时，进行网络拉取
        AuthenticationHelper.getInstance().getAccountById(id, new IMultipleAccountPublicClientApplication.GetAccountCallback() {
            @Override
            public void onTaskCompleted(IAccount result) {
                if (msalAccountList == null) {
                    msalAccountList = new ArrayList<>();
                }
                msalAccountList.add(result);
                SPManager.putObject(id, result);
                callback.onSuccess(result);
            }

            @Override
            public void onError(MsalException exception) {
                callback.onError(exception);
            }
        });
    }

    /**
     * 退出微软账户
     *
     * @param account IAccount
     * @param callback CommonLoadCallback
     */
    public void removeMSALAccount(IAccount account, CommonLoadCallback<String> callback) {
        AuthenticationHelper.getInstance().removeAccount(account, new IMultipleAccountPublicClientApplication.RemoveAccountCallback() {
            @Override
            public void onRemoved() {
                DriveDBUtil.deleteById(account.getId());
                SPManager.removeValue(account.getId());
                callback.onSuccess("删除成功");
            }

            @Override
            public void onError(@NonNull MsalException exception) {
                callback.onError(exception);
            }
        });
    }

}
