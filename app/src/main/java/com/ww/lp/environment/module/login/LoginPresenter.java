package com.ww.lp.environment.module.login;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.VolleySingleton;
import com.orhanobut.logger.Logger;
import com.ww.lp.environment.R;
import com.ww.lp.environment.data.user.UserInfo;
import com.ww.lp.environment.utils.StringResUtils;
import com.ww.lp.environment.utils.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * 登录Presenter
 *
 * Created by LinkedME06 on 16/10/27.
 */

public class LoginPresenter implements LoginContract.Presenter {

    @NonNull
    private String requestTag;
    @NonNull
    private final LoginContract.View mView;

    public LoginPresenter(@NonNull String requestTag,
                          @NonNull LoginContract.View loginView) {
        this.requestTag = requestTag;
        mView = checkNotNull(loginView, "loginView cannot be null!");
        mView.setPresenter(this);
    }

    @Override
    public void login(@NonNull UserInfo userInfo) {
        if (validate(userInfo)) {
            mView.showProgressDialog("正在登录，请稍后...");
            // Request a string response from the provided URL.
            StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://123.56.197.245:9096/webapi/v2/useraccounts/login?username=" + userInfo.getUsername() + "&password=" + userInfo.getPassword(),
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // Display the first 500 characters of the response string.
                            try {
                                if (!TextUtils.isEmpty(response)) {
                                    JSONObject jsonObject = new JSONObject(response);
                                    String userId = jsonObject.optString("userid");
                                    mView.success(userId, true);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mView.success("", false);
                    ToastUtils.toastShort("账号或密码不正确，请重新登录！");
                }
            });
// Add the request to the RequestQueue.
            VolleySingleton.getInstance().addToRequestQueue(stringRequest);
        }
    }

    public boolean validate(UserInfo userInfo) {
        boolean valid = true;

        String username = userInfo.getUsername();
        String password = userInfo.getPassword();
        Logger.d(userInfo);

        if (TextUtils.isEmpty(username)) {
            valid = false;
            ToastUtils.toastShort(StringResUtils.getString(R.string.error_username_empty));
        }

        if (TextUtils.isEmpty(password)) {
            valid = false;
            ToastUtils.toastShort(StringResUtils.getString(R.string.error_password_empty));
        }
        return valid;
    }

    @Override
    public void subscribe() {
        //此处为页面打开后开始加载数据时调用的方法
    }

    @Override
    public void unsubscribe() {
    }
}
