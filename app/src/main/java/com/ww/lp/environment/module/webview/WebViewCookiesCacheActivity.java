package com.ww.lp.environment.module.webview;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AbsListView;
import android.widget.Toast;

import com.microquation.linkedme.android.LinkedME;
import com.microquation.linkedme.android.callback.LMDLResultListener;
import com.microquation.linkedme.android.indexing.LMUniversalObject;
import com.microquation.linkedme.android.referral.LMError;
import com.microquation.linkedme.android.referral.PrefHelper;
import com.microquation.linkedme.android.util.LinkProperties;
import com.ww.lp.environment.BaseActivity;
import com.ww.lp.environment.R;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;

/**
 * 可以设置Cookies及缓存的WebView
 *
 * Created by lipeng on 2017/12/24.
 */

public class WebViewCookiesCacheActivity extends BaseActivity {
    private static final int PERMISSIONS_REQUEST_READ_PHONE_STATE = 1000;
    private WebView normal_wv;
    // 当前显示的url，包括锚点url
    // TODO: 3/28/18 lipeng 这个地址发布的时候需要注意一下
    private String baseLoadUrl = "http://www.windant.com/login.aspx";
    //    private String baseLoadUrl = "http://60.205.217.173:9099/browser/webview.html";
    private String loadUrl;
    //    private ProgressBar loading;
    private WebSettings webSettings = null;
    private ScrollChildSwipeRefreshLayout swiperefresh;
    private static final String APP_CACAHE_DIRNAME = "/webcache";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate: ");
        setContentView(R.layout.normal_wv_act, false, false, true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_PHONE_STATE)) {
                Toast.makeText(this, "请授予电话权限~", Toast.LENGTH_SHORT).show();
            }

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_PHONE_STATE},
                    PERMISSIONS_REQUEST_READ_PHONE_STATE);
        }
        loadUrl = baseLoadUrl;
        // TODO: 21/07/2017 demo中去除以下代码
        //----------以下针对必须在launcher页面处理数据的情况，但该处理方式存在两个缺陷：
        // 1. 因为只能在launcher页面处理获取数据，如果应用宝唤起app时，app之前已被应用宝唤起并退到后台，
        // 就无法经过launcher页面，也就无法获取到参数。
        // 2. 如果因为网络问题，获取数据比较慢，那么页面展示时间就会变长
        // ---start---

        LinkedME.getInstance().setDeepLinkListener(new LMDLResultListener() {
            @Override
            public void dlResult(Intent handledIntent, LMError lmError) {
                Log.i(TAG, "dlResult: ");
                if (lmError != null || handledIntent == null) {
                    initView();
                } else {
                    boolean jumpStatus = false;
                    //获取与深度链接相关的值
                    LinkProperties linkProperties = handledIntent.getParcelableExtra(LinkedME.LM_LINKPROPERTIES);
                    LMUniversalObject lmUniversalObject = handledIntent.getParcelableExtra(LinkedME.LM_UNIVERSALOBJECT);
                    //LinkedME SDK初始化成功，获取跳转参数，具体跳转参数在LinkProperties中，和创建深度链接时设置的参数相同；
                    if (linkProperties != null) {
                        Log.i(TAG, "Channel " + linkProperties.getChannel());
                        Log.i(TAG, "control params " + linkProperties.getControlParams());
                        Log.i(TAG, "link(深度链接) " + linkProperties.getLMLink());
                        Log.i(TAG, "是否为新安装 " + linkProperties.isLMNewUser());
                        //获取自定义参数封装成的ArrayMap对象
                        HashMap<String, String> arrayMap = linkProperties.getControlParams();

                        if (arrayMap.size() > 0) {
                            //获取传入的参数
                            Iterator iterator = arrayMap.keySet().iterator();
                            while (iterator.hasNext()) {
                                String key = (String) iterator.next();
                                String value = arrayMap.get(key);
                                if (loadUrl.contains("?")) {
                                    loadUrl = loadUrl + "&" + key + "=" + value;
                                } else {
                                    loadUrl = baseLoadUrl + "?" + key + "=" + value;
                                }
                            }
                        }

                        //清除跳转数据，该方法理论上不需要调用，因Android集成方式各种这样，若出现重复跳转的情况，可在跳转成功后调用该方法清除参数
                        //LinkedME.getInstance().clearSessionParams();
                    }
                    initView();
                }
            }
        });

        //----------以下针对必须在launcher页面处理数据的情况，但该处理方式存在两个缺陷：
        // 1. 因为只能在launcher页面处理获取数据，如果应用宝唤起app时，app之前已被应用宝唤起并退到后台，
        // 就无法经过launcher页面，也就无法获取到参数。
        // 2. 如果因为网络问题，获取数据比较慢，那么页面展示时间就会变长
        // ---end---

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_READ_PHONE_STATE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.i(TAG, "onNewIntent: " + intent);
        setIntent(intent);
        String pid = intent.getStringExtra("pid");
        String rid = intent.getStringExtra("rid");
        if (pid != null && rid != null) {
            loadUrl = baseLoadUrl + "?pid=" + pid + "&rid=" + rid;
        } else if (pid != null) {
            loadUrl = baseLoadUrl + "?pid=" + pid;
        } else if (rid != null) {
            loadUrl = baseLoadUrl + "?rid=" + rid;
        }
        initView();

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume: ");
        LinkedME.getInstance().setImmediate(true);
    }

    private void initView() {
        normal_wv = findViewById(R.id.normal_wv);
//        loading = findViewById(R.id.loading);
        // Set the scrolling view in the custom SwipeRefreshLayout.
        initWebView(normal_wv);
        swiperefresh = findViewById(R.id.swiperefresh);
        swiperefresh.setScrollUpChild(normal_wv);
        swiperefresh.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        normal_wv.reload();
//                        normal_wv.loadUrl(loadUrl);
                    }
                }
        );
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


    /**
     * 初始化
     */
    @SuppressLint("SetJavaScriptEnabled")
    private void initWebView(final WebView webView) {
        Log.i(TAG, "initWebView: loadurl === " + loadUrl);
        webSettings = webView.getSettings();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);
        } else {
            webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        }
        webSettings.setJavaScriptEnabled(true);
        // 支持多窗口，处理弹出窗口问题，比如prompt弹出式窗口
        // 如果设置为true，必须同时实现onCreateWindow方法
        // 参考：https://stackoverflow.com/questions/8578332/webview-webchromeclient-method-oncreatewindow-not-called-for-target-blank/11280814#11280814
        webSettings.setSupportMultipleWindows(true);

        webView.setVerticalScrollBarEnabled(false); //垂直不显示
        webView.setHorizontalScrollBarEnabled(false);//水平滑动条不显示
        webSettings.setBuiltInZoomControls(false); // 显示放大缩小
        webSettings.setSupportZoom(false); // 可以缩放
        webSettings.setUseWideViewPort(true);// 支持viewport元标签
        webSettings.setLoadWithOverviewMode(true); // 缩小页面以适应WebView的大小
        webSettings.setDomStorageEnabled(true);//设置支持html5本地存储，有些h5页面服务器做了缓存，webview控件也要设置，否则显示不出来页面
        String cacheDirPath = getFilesDir().getAbsolutePath() + APP_CACAHE_DIRNAME;
        Log.i(TAG, "cacheDirPath=" + cacheDirPath);

        //设置数据库缓存路径
        webSettings.setDatabasePath(cacheDirPath);
        webSettings.setDatabaseEnabled(true);

        //设置  Application Caches 缓存目录
        webSettings.setAppCachePath(cacheDirPath);
        webSettings.setAppCacheMaxSize(20 * 1024 * 1024);
        //开启 Application Caches 功能
        webSettings.setAppCacheEnabled(true);
//        if (isNetworkConnected(WebViewCookiesCacheActivity.this)) {
        // 如果有网络则使用默认加载模式，有缓存并且没有失效则走缓存，否则走网络
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
//        } else {
//            // 如果有缓存，无论缓存是否失效都使用缓存资源，否则从网络加载
//            webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
//        }
        webSettings.setDefaultTextEncodingName("UTF-8");
        webSettings.setAllowFileAccess(true);// 设置可以访问缓存文件

        // Enable third-party cookies if on Lolipop. TODO: Make this configurable
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.setAcceptThirdPartyCookies(webView, true);
        }

        webSettings.setDefaultZoom(WebSettings.ZoomDensity.MEDIUM);
        normal_wv.addJavascriptInterface(new WebAppInterface(WebViewCookiesCacheActivity.this, normal_wv), "Android");
        normal_wv.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return Build.VERSION.SDK_INT < Build.VERSION_CODES.N && overrideUrlLoading(view, url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && overrideUrlLoading(view, request.getUrl().toString());
            }

            @Override
            public void onLoadResource(WebView view, String url) {
                Log.i(TAG, "onLoadResource: " + url);
                super.onLoadResource(view, url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                showLoading();
//                loading.setVisibility(View.VISIBLE);
                webSettings.setBlockNetworkImage(true);
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                showContent();
                if (!loadFailed) {
                    loadUrl = url;
                }
//                loading.setVisibility(View.GONE);
                if (swiperefresh.isRefreshing()) {
                    swiperefresh.setRefreshing(false);
                }
                webSettings.setBlockNetworkImage(false);
                super.onPageFinished(view, url);
            }


            @Override
            public void onReceivedError(final WebView view, WebResourceRequest request, WebResourceError error) {
                // 处理错误的情况
                showErrorDefault(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        normal_wv.loadUrl(loadUrl);
                    }
                });
                super.onReceivedError(view, request, error);
            }

        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onCreateWindow(WebView view, boolean dialog, boolean userGesture, Message resultMsg) {
                WebView newWebView = new WebView(WebViewCookiesCacheActivity.this);
                getWindowManager().addView(newWebView, new AbsListView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
                transport.setWebView(newWebView);
                resultMsg.sendToTarget();
                return true;
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                setTitle(title);
            }
        });
        // 处理缓存问题，当有网络的时候访问网络，没有网络的时候使用缓存
        if (isNetworkConnected(this)) {
            getCookie(loadUrl);
        } else {
            normal_wv.loadUrl(loadUrl);
        }
    }

    private void getCookie(final String url) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //http连接需要放到子线程中进行请求
                    URL httpUrl = new URL(url);
                    HttpURLConnection conn = (HttpURLConnection) httpUrl.openConnection();
                    conn.setRequestMethod("GET");
                    conn.connect();
                    if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        String response = conn.toString();//在conn中会有cookie信息，如下图
                        String[] connRespnse = response.split(";");
                        if (connRespnse.length > 1) {
                            synCookies(url, connRespnse[1]);
                        }
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            normal_wv.loadUrl(url);//webview控件调用需要在主线程中进行
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    /**
     * 同步一下cookie
     */
    private void synCookies(String url, String cookie) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            CookieSyncManager.createInstance(this);
        }
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setCookie(url, cookie);//如果没有特殊需求，这里只需要将session id以"key=value"形式作为cookie即可
    }

    /**
     * 是否有网络
     */
    public boolean isNetworkConnected(Context context) {

        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (mConnectivityManager != null) {
                NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
                return mNetworkInfo != null && mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    private boolean overrideUrlLoading(WebView view, String url) {
        Log.i(TAG, "shouldOverrideUrlLoading: " + url);
        //重写该方法是为了处理uri scheme,对于uri scheme则直接唤起APP
        //去掉回车、换行、tab
        String stray_spacing = "[\n\r\t\\p{Zl}\\p{Zp}\u0085]+";
        url = url.trim();
        url = url.replaceAll(stray_spacing, "");
        PrefHelper.Debug("LinkedME", "url ===== " + url);
        String rfc2396regex = "^(([a-zA-Z][a-zA-Z0-9\\+\\-\\.]*)://)(([^/?#]*)?([^?#]*)(\\?([^#]*))?)?(#(.*))?";
        String http_scheme_slashes = "^(https?://)/+(.*)";
        //(?i)后面的匹配不区分大小写
        String all_schemes_pattern = "(?i)^(http|https|ftp|mms|rtsp|wais)://.*";
        if (url.matches(all_schemes_pattern)) {
            view.loadUrl(url);
            return true;
        }
        if (url.matches(rfc2396regex)) {
            showErrorDefault(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    normal_wv.loadUrl(loadUrl);
                }
            });
            return true;
        }
        return true;
    }

}
