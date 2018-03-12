package com.ww.lp.environment.module;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.microquation.linkedme.android.LinkedME;
import com.microquation.linkedme.android.util.LinkProperties;
import com.ww.lp.environment.BaseActivity;
import com.ww.lp.environment.module.webview.WebViewCookiesCacheActivity;

import java.util.HashMap;

/**
 * Created by LinkedME06 on 3/10/18.
 */

public class MiddleActivity extends BaseActivity {
    /**
     * 解析深度链获取跳转参数，开发者自己实现参数相对应的页面内容
     *
     *
     * 通过LinkProperties对象调用getControlParams方法获取自定义参数的HashMap对象,
     * 通过创建的自定义key获取相应的值,用于数据处理。
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent() != null) {
            //获取与深度链接相关的值
            LinkProperties linkProperties = getIntent().getParcelableExtra(LinkedME.LM_LINKPROPERTIES);
            if (linkProperties != null) {
                Log.i("LinkedME-Demo", "Channel " + linkProperties.getChannel());
                Log.i("LinkedME-Demo", "control params " + linkProperties.getControlParams());
                Log.i("LinkedME-Demo", "link(深度链接) " + linkProperties.getLMLink());
                Log.i("LinkedME-Demo", "是否为新安装 " + linkProperties.isLMNewUser());
                //获取自定义参数封装成的hashmap对象,参数键值对由集成方定义
                HashMap<String, String> hashMap = linkProperties.getControlParams();
                //根据key获取传入的参数的值,该key关键字View可为任意值,由集成方规定,请与web端商议,一致即可
                String pid = hashMap.get("pid");
                if (pid != null) {
                    //根据不同的参数进行页面跳转,detail代表具体跳转到哪个页面,此处语义指详情页
                    //DetailActivity类不存在,此处语义指要跳转的详情页,参数也是由上面的HashMap对象指定
                    Intent intent = new Intent(MiddleActivity.this, WebViewCookiesCacheActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("pid", pid);
                    startActivity(intent);
                }
            }
        }
        finish();
    }
}
