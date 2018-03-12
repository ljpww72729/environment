package com.ww.lp.environment;

import android.app.Application;

import com.android.volley.toolbox.VolleySingleton;
import com.microquation.linkedme.android.LinkedME;
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

        // 初始化SDK
        LinkedME.getInstance(this);

        if (BuildConfig.DEBUG) {
            //设置debug模式下打印LinkedME日志
            LinkedME.getInstance().setDebug();
        }
        //初始时设置为false，在配置Uri Scheme的Activity的onResume()中设置为true
        LinkedME.getInstance().setImmediate(false);

    }

}
