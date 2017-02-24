package com.ww.lp.environment.module.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ww.lp.environment.BaseActivity;
import com.ww.lp.environment.BaseFragment;
import com.ww.lp.environment.BuildConfig;
import com.ww.lp.environment.R;
import com.ww.lp.environment.data.user.UserInfo;
import com.ww.lp.environment.databinding.LoginFragBinding;
import com.ww.lp.environment.module.webview.NormalWVActvity;
import com.ww.lp.environment.utils.SPUtils;

import static com.google.gson.internal.$Gson$Preconditions.checkNotNull;

/**
 * 登录 Created by LinkedME06 on 16/10/27.
 */

public class LoginFragment extends BaseFragment implements LoginContract.View {

    private LoginFragBinding loginFragBinding;
    private LoginContract.Presenter mPresenter;

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.subscribe();
    }

    @Override
    public void onPause() {
        super.onPause();
        mPresenter.unsubscribe();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = onCreateView(inflater, container, savedInstanceState, R.layout.login_frag, false);
        loginFragBinding = LoginFragBinding.bind(root);
        UserInfo userInfo = new UserInfo();
        userInfo.setUsername((String) SPUtils.get(getActivity(), SPUtils.USER_NAME, ""));
        userInfo.setPassword((String) SPUtils.get(getActivity(), SPUtils.PASSWORD, ""));
        if (BuildConfig.DEBUG) {
            userInfo.setUsername("tester1");
            userInfo.setPassword("testerone");
        }
        loginFragBinding.setUserInfo(userInfo);
        loginFragBinding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.login(loginFragBinding.getUserInfo());
            }
        });
        if (!TextUtils.isEmpty((String) SPUtils.get(getActivity(), SPUtils.USER_NAME, ""))) {
            mPresenter.login(loginFragBinding.getUserInfo());
        }
        return root;
    }

    @Override
    public void setPresenter(@NonNull LoginContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Override
    public void success(boolean result) {
        removeProgressDialog();
//        Intent intent = new Intent(getActivity(), NormalWVActvity.class);
//        intent.putExtra(NormalWVActvity.LOADURL, "http://www.wowofun.com/test/epapp/index.html");
//        startActivity(intent);
//        getActivity().finish();
        if (result) {
            //请求成功
            if (loginFragBinding.checkBox.isChecked()) {
                //存储密码
                SPUtils.put(getActivity(), SPUtils.USER_NAME, loginFragBinding.getUserInfo().getUsername());
                SPUtils.put(getActivity(), SPUtils.PASSWORD, loginFragBinding.getUserInfo().getPassword());
            }
            Intent intent = new Intent(getActivity(), NormalWVActvity.class);
            intent.putExtra(NormalWVActvity.LOADURL, "http://www.wowofun.com/test/epapp/index.html");
            startActivity(intent);
            getActivity().finish();
        }
    }

    @Override
    public void showProgressDialog(String msg) {
        if (TextUtils.isEmpty(msg)) {
            ((BaseActivity) getActivity()).showProgressDialogLP();
        } else {
            ((BaseActivity) getActivity()).showProgressDialogLP(msg);
        }
    }

    @Override
    public void removeProgressDialog() {
        ((BaseActivity) getActivity()).removeProgressDialogLP();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
