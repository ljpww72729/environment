package com.ww.lp.environment.utils;

import android.support.annotation.StringRes;

import com.ww.lp.environment.CustomApplication;


/**
 * 获取资源文件文本
 *
 * Created by LinkedME06 on 16/11/24.
 */

public class StringResUtils {

    public static String getString(@StringRes int stringRes) {
        return CustomApplication.self().getString(stringRes);
    }
}
