package com.ww.lp.environment;

import android.app.Application;

import com.android.volley.toolbox.VolleySingleton;
import com.orhanobut.logger.LogLevel;
import com.orhanobut.logger.Logger;

/**
 * Created by LinkedME06 on 16/11/12.
 */

public class CustomApplication extends Application {

    private static CustomApplication app;

    public static CustomApplication self() {
        return app;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (app == null) {
            app = this;
        }
        //初始化volley
        VolleySingleton.init(this);

        //logger配置

        if (BuildConfig.DEBUG) {
            Logger.init("lp_log");
        } else {
            //release版本需要隐藏日志
            Logger.init("lp_log").logLevel(LogLevel.NONE);
        }

    }
}
