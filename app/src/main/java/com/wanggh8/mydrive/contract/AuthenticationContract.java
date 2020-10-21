package com.wanggh8.mydrive.contract;

import android.app.Activity;

import com.microsoft.identity.client.IAccount;
import com.wanggh8.mydrive.bean.DriveBean;

import java.util.List;

/**
 * 微软统一认证
 *
 * @author wanggh8
 * @version V1.0
 * @date 2020/10/16
 */
public interface AuthenticationContract {

    interface View {

        void getDriveListSuccess(List<DriveBean> rsp);
        void getDriveListFail(String msg, int code);

        void getAccountByIdSuccess(IAccount rsp);
        void getAccountByIdFail(String msg, int code);

        void addAccountSuccess();
        void addAccountFail(String msg, int code);

        void removeAccountSuccess();
        void removeAccountFail(String msg, int code);
    }

    interface Presenter {

        /**
         * 获取网盘列表
         *
         * @param fromNet 是否不读本地缓存直接拉取网络
         */
        void getDriveList(boolean fromNet);

        /**
         * 根据id获取用户
         *
         * @param id 用户id
         * @param type 网盘类型
         */
        void getAccountById(String id, String type);

        /**
         * 添加网盘
         *
         * @param activity 调用的Activity
         * @param type 网盘类型
         */
        void addAccount(Activity activity, String type);

        /**
         * 删除在线网盘
         *
         * @param id 用户id
         * @param type 网盘类型
         */
        void removeAccountById(String id, String type);

        /**
         * 自定义网盘名
         *
         * @param id 网盘id
         * @param myName 自定义网盘名
         */
        void editDriveName(String id, String myName);

        /**
         * 获取网盘token
         *
         * @param id 网盘用户id
         */
        void getAccessToken(String id);
    }
}
