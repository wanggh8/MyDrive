package com.wanggh8.mydrive.model;

import com.microsoft.identity.client.IAccount;
import com.microsoft.identity.client.IPublicClientApplication;
import com.microsoft.identity.client.exception.MsalException;
import com.wanggh8.mydrive.base.BaseModel;
import com.wanggh8.mydrive.bean.DriveBean;
import com.wanggh8.mydrive.callback.CommonLoadListCallback;
import com.wanggh8.mydrive.utils.AuthenticationHelper;
import com.wanggh8.mydrive.utils.DriveDBUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wanggh8
 * @version V1.0
 * @date 2020/10/16
 */
public class AuthenticationModel extends BaseModel {


    public void getDriveList(CommonLoadListCallback<DriveBean> callback) {
        List<DriveBean> driveBeanList;
        // 从本地数据库查询
        driveBeanList = DriveDBUtil.queryAll();
        if (driveBeanList != null && !driveBeanList.isEmpty()) {
            callback.onSuccess(driveBeanList);
            return;
        }
        // 查询结果为空时，进行网络拉取
        List<DriveBean> driveBeanListFromNet = new ArrayList<>();
        AuthenticationHelper.getInstance().loadAccounts(new IPublicClientApplication.LoadAccountsCallback() {
            @Override
            public void onTaskCompleted(List<IAccount> result) {
                for (IAccount account : result) {
                    DriveBean bean = new DriveBean().setDriveBean(account);
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
}
