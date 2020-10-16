package com.wanggh8.mydrive.contract;

/**
 * 微软统一认证
 *
 * @author wanggh8
 * @version V1.0
 * @date 2020/10/16
 */
public interface AuthenticationContract {

    interface View {

        void getDriveListSuccess();
        void getDriveListFail();

        void getAccountListSuccess();
        void getAccountListFail();

        void getAccountByIdSuccess();
        void getAccountByIdFail();

        void addAccountSuccess();
        void addAccountFail();

        void removeAccountSuccess();
        void removeAccountFail();
    }

    interface Presenter {

        void getDriveList();

        void getAccountList();

        void getAccountById(String id);

        void addAccount();

        void removeAccountById(String id);

        void editDriveName(String id, String myName);

        void getAccessToken(String id);
    }
}
