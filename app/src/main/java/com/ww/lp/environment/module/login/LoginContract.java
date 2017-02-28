package com.ww.lp.environment.module.login;

import android.support.annotation.NonNull;

import com.ww.lp.environment.BasePresenter;
import com.ww.lp.environment.BaseView;
import com.ww.lp.environment.data.user.UserInfo;


/**
 * Created by LinkedME06 on 16/10/27.
 */

public class LoginContract {

    interface View extends BaseView<Presenter> {
        void success(String userId, boolean result);
        void showProgressDialog(String msg);
        void removeProgressDialog();
    }

    interface Presenter extends BasePresenter {
        void login(@NonNull UserInfo userInfo);

    }

}
