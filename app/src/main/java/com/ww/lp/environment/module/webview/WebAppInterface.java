package com.ww.lp.environment.module.webview;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

import com.ww.lp.environment.module.login.LoginActivity;

/**
 * Created by LinkedME06 on 24/02/2017.
 */

public class WebAppInterface {
    Context mContext;
    private WebView normal_wv;

    /**
     * Instantiate the interface and set the context
     */
    WebAppInterface(Context c, WebView normal_wv) {
        mContext = c;
        this.normal_wv = normal_wv;
    }

    /**
     * Show a toast from the web page
     */
    @JavascriptInterface
    public void showToast(String toast) {
        Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
    }

    /**
     * 登出
     */
    @JavascriptInterface
    public void logout(){
        //跳转到登录页面
        Intent intent = new Intent(mContext, LoginActivity.class);
        mContext.startActivity(intent);
        ((Activity)mContext).finish();
    }

    /**
     * 后退
     */
    @JavascriptInterface
    public void back(){
        normal_wv.post(new Runnable() {
            @Override
            public void run() {
                if (normal_wv.canGoBack()) {
                    normal_wv.goBack();
                } else {
                    ((Activity)mContext).finish();
                }
            }
        });
    }
}
