package com.ww.lp.environment.utils;

import android.widget.Toast;

import com.ww.lp.environment.CustomApplication;


/**
 * 显示消息
 *
 * Created by LinkedME06 on 16/11/24.
 */

public class ToastUtils {

    public static void toastShort(String msg) {
        Toast.makeText(CustomApplication.self(), msg, Toast.LENGTH_SHORT).show();
    }

    public static void toastLong(String msg) {
        Toast.makeText(CustomApplication.self(), msg, Toast.LENGTH_LONG).show();
    }

    public static void toastError(Throwable error) {
        try {
            toastShort(error.getCause().getMessage());
        } catch (NullPointerException e) {
            try {
                toastShort("请求发生错误，请稍后重试");
            } catch (Exception exception) {
                toastShort("请求发生错误！");
            }
        }
    }

}
