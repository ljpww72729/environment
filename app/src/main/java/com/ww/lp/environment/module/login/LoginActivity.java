package com.ww.lp.environment.module.login;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.ww.lp.environment.BaseActivity;
import com.ww.lp.environment.R;
import com.ww.lp.environment.utils.ActivityUtils;

/**
 * 登录页面
 * Created by LinkedME06 on 16/10/27.
 */

public class LoginActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_act, false, false, false);
        setTitle(getString(R.string.login));

        LoginFragment loginFragment = (LoginFragment) getSupportFragmentManager()
                .findFragmentById(R.id.contentFrame);

        if (loginFragment == null) {
            loginFragment = LoginFragment.newInstance();

            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),
                    loginFragment, R.id.contentFrame);
        }

        // Create the presenter
        new LoginPresenter(TAG, loginFragment);
    }


}
