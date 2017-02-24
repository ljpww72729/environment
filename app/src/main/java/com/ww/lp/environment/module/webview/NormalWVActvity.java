package com.ww.lp.environment.module.webview;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.ww.lp.environment.BaseActivity;
import com.ww.lp.environment.R;


/**
 * Created by LinkedME06 on 16/11/25.
 */

public class NormalWVActvity extends BaseActivity {

    private WebView normal_wv;
    public static final String LOADURL = "loadUrl";
    private String loadUrl = "";
    private ProgressBar loading;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.normal_wv_act, false, false, true);
        setTitle("详情");
        if (getIntent() != null) {
            loadUrl = getIntent().getStringExtra(LOADURL);
        }
        initView();
    }

    private void initView() {
        normal_wv = (WebView) findViewById(R.id.normal_wv);
        loading = (ProgressBar) findViewById(R.id.loading);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            normal_wv.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);
        } else {
            normal_wv.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        }
        WebSettings webSettings = normal_wv.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setAppCacheMaxSize(1024 * 1024 * 8);//设置缓冲大小，我设的是8M
        String appCacheDir = this.getApplicationContext().getDir("cache", Context.MODE_PRIVATE).getPath();
        webSettings.setAppCachePath(appCacheDir);
        webSettings.setAllowFileAccess(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
//        webSettings.setBlockNetworkImage(true);
        normal_wv.addJavascriptInterface(new WebAppInterface(NormalWVActvity.this, normal_wv), "Android");
//        normal_wv.loadUrl(loadUrl);
        normal_wv.loadUrl("file:///android_asset/EPAPP/index.html");
        normal_wv.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
//                showLoading();
                loading.setVisibility(View.VISIBLE);
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
//                showContent();
                loading.setVisibility(View.GONE);
//                ToastUtils.toastShort(view.getTitle());
//                normal_wv.getSettings().setBlockNetworkImage(false);
                super.onPageFinished(view, url);
            }


            @Override
            public void onReceivedError(final WebView view, WebResourceRequest request, WebResourceError error) {
//                showErrorDefault(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        normal_wv.loadUrl(loadUrl);
//                    }
//                });
                super.onReceivedError(view, request, error);
            }
        });

        WebChromeClient m_chromeClient = new WebChromeClient() {
            //扩充缓存的容量
            @Override
            public void onReachedMaxAppCacheSize(long spaceNeeded,
                                                 long totalUsedQuota, WebStorage.QuotaUpdater quotaUpdater) {
                quotaUpdater.updateQuota(spaceNeeded * 2);
            }
        };
        normal_wv.setWebChromeClient(m_chromeClient);


    }

    @Override
    public void onBackPressed() {
        back();
    }

    public void back() {
        if (normal_wv.canGoBack()) {
            normal_wv.goBack();
        } else {
            finish();
        }
    }
}
