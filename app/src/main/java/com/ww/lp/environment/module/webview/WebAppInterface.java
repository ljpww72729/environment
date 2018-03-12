package com.ww.lp.environment.module.webview;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

import com.ww.lp.environment.module.login.LoginActivity;
import com.ww.lp.environment.utils.SPUtils;

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
    public void logout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage("确定退出应用？").setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).setPositiveButton("退出", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                //跳转到登录页面
                SPUtils.remove(mContext, SPUtils.PASSWORD);
                SPUtils.remove(mContext, SPUtils.USER_ID);
                Intent intent = new Intent(mContext, LoginActivity.class);
                mContext.startActivity(intent);
                ((Activity) mContext).finish();
            }
        }).create().show();

    }

    /**
     * 后退
     */
    @JavascriptInterface
    public void back() {
        normal_wv.post(new Runnable() {
            @Override
            public void run() {
                if (normal_wv.canGoBack()) {
                    normal_wv.goBack();
                } else {
                    ((Activity) mContext).finish();
                }
            }
        });
    }

    /**
     * 设置值
     */
    @JavascriptInterface
    public void setData(String key, String value) {
        SPUtils.put(mContext, key, value);
    }

    /**
     * 获取值
     */
    @JavascriptInterface
    public String getData(String key, String defaultValue) {
        return (String) SPUtils.get(mContext, key, defaultValue);
    }

    /**
     * 包含值
     */
    @JavascriptInterface
    public boolean containsData(String key) {
        return SPUtils.contains(mContext, key);
    }

    /**
     * 清除所有值
     */
    @JavascriptInterface
    public void clearData() {
        SPUtils.clear(mContext);
    }

    /**
     * 获取UserId
     *
     * @return userId
     */
    @JavascriptInterface
    public String getUserId() {
        return (String) SPUtils.get(mContext, SPUtils.USER_ID, "");
    }
}
