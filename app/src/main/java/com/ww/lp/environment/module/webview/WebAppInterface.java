package com.ww.lp.environment.module.webview;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

import com.ww.lp.environment.module.DownloadService;
import com.ww.lp.environment.utils.AndroidUtils;
import com.ww.lp.environment.utils.SPUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;

import static com.ww.lp.environment.module.DownloadService.DOWNLOAD_FILE_NAME;
import static com.ww.lp.environment.module.DownloadService.DOWNLOAD_FILE_URL;

/**
 * Created by LinkedME06 on 24/02/2017.
 */

/**
 * <p>***********************************************************************
 * <p> Author: lipeng
 * <p> CreateData: 2017-02-24
 * <p> Version: 1.0.9
 * <p> Description: js交互接口
 * <p>
 * <p>***********************************************************************
 */

public class WebAppInterface {
    Context mContext;
    private WebView normal_wv;
    private boolean checkVersion = false;

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
//                跳转到登录页面
//                SPUtils.remove(mContext, SPUtils.PASSWORD);
//                SPUtils.remove(mContext, SPUtils.USER_ID);
//                Intent intent = new Intent(mContext, LoginActivity.class);
//                mContext.startActivity(intent);
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

    /**
     * 分享图片
     *
     * @param imageUrl 图片地址
     */
    @JavascriptInterface
    public void shareImage(final String imageUrl) {
        Toast.makeText(mContext, "正在创建分享，请稍候...", Toast.LENGTH_LONG).show();
        new Thread(new Runnable() {
            @Override
            public void run() {

                Bitmap bitmap;
                try {
                    // Download Image from URL
                    InputStream input = new URL(imageUrl).openStream();
                    // Decode Bitmap
                    bitmap = BitmapFactory.decodeStream(input);
                    File imgFileDir = new File(mContext.getFilesDir().getAbsolutePath() + "/shared/images");
                    if (!imgFileDir.exists()) {
                        imgFileDir.mkdirs();
                    }
                    String suffix = ".jpg";
                    if (imageUrl.contains(".png")) {
                        suffix = ".png";
                    }
                    final File imgFile = new File(imgFileDir, "share" + suffix);
                    if (!imgFile.exists()) {
                        imgFile.createNewFile();
                    } else {
                        imgFile.delete();
                        imgFile.createNewFile();
                    }
                    FileOutputStream fileOutputStream = new FileOutputStream(imgFile);
                    Bitmap.CompressFormat compressFormat = Bitmap.CompressFormat.JPEG;
                    if (suffix.equals(".png")) {
                        compressFormat = Bitmap.CompressFormat.PNG;
                    }
                    bitmap.compress(compressFormat, 100, fileOutputStream);
                    input.close();
                    fileOutputStream.close();
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Intent share = new Intent(Intent.ACTION_SEND);
                            share.setType("image/*");
                            Uri imageUri = FileProvider.getUriForFile(mContext,
                                    mContext.getApplicationContext().getPackageName() + ".fileprovider",
                                    imgFile);
                            share.putExtra(Intent.EXTRA_STREAM, imageUri);
                            share.putExtra(Intent.EXTRA_SUBJECT, "好运购分享");
                            share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            mContext.startActivity(Intent.createChooser(share, "分享到"));
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(mContext, "分享失败~", Toast.LENGTH_LONG).show();
                }
            }
        }).start();

    }

    /**
     * 分享内容
     *
     * @param content 分享内容
     */
    @JavascriptInterface
    public void shareText(final String content) {
        Toast.makeText(mContext, "正在创建分享，请稍候...", Toast.LENGTH_LONG).show();
        try {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.setType("text/plain");
                    share.putExtra(Intent.EXTRA_TEXT, content);
                    share.putExtra(Intent.EXTRA_SUBJECT, "好运购分享");
                    mContext.startActivity(Intent.createChooser(share, "分享到"));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(mContext, "分享失败~", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 获取mac地址
     */
    @JavascriptInterface
    public String getMacAddress() {
        return AndroidUtils.getMAC(mContext);
    }

    /**
     * 版本更新
     *
     * @param versionCode 数字版本号，例如：5
     * @param downloadUrl apk下载地址，例如：http://xxxx/xx/x.apk
     * @param content     更新内容，例如：修复bug\n美化UI
     */
    @JavascriptInterface
    public void checkAppVersion(final String versionCode, final String downloadUrl, final String content) {
        if (checkVersion) {
            return;
        }
        checkVersion = true;
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                try {
                    if (TextUtils.isEmpty(downloadUrl) || !downloadUrl.startsWith("http")) {
                        return;
                    }
                    int currentVersionCode = 0;
                    // ---get the package info---
                    PackageManager pm = mContext.getPackageManager();
                    PackageInfo pi = pm.getPackageInfo(mContext.getPackageName(), 0);
                    currentVersionCode = pi.versionCode;

                    if (currentVersionCode < Integer.valueOf(versionCode)) {
                        // 更新app
                        AlertDialog.Builder b = new AlertDialog.Builder(mContext);
                        b.setTitle("版本更新");
                        String updateContent = "检测到新版，请更新~";
                        if (!TextUtils.isEmpty(content)) {
                            updateContent = content;
                        }
                        b.setMessage(updateContent);
                        b.setPositiveButton("升级", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                downloadApkService(mContext, versionCode, downloadUrl);
                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        b.setCancelable(false);
                        b.create().show();
                    }
                } catch (Exception e) {
                    Log.e("VersionInfo", "Exception", e);
                }
            }
        });

    }

    private void downloadApkService(Context mContext, String versionCode, String url) {
        // 开始下载上报
        Intent intent = new Intent(mContext, DownloadService.class);
        intent.putExtra(DOWNLOAD_FILE_URL, url);
        String fileName = "haoyungou-v" + versionCode + ".apk";
//        Uri uri = Uri.parse(url);
//        if (url.endsWith(".apk")) {
//            String[] apkUrl = uri.getEncodedPath().split("/");
//            if (apkUrl.length > 1) {
//                fileName = apkUrl[apkUrl.length - 1];
//            }
//        }
        intent.putExtra(DOWNLOAD_FILE_NAME, fileName);

        mContext.startService(intent);
    }

    /**
     * 获取mac地址
     */
    @JavascriptInterface
    public String getAppVersion() {
        String versionName = "";
        try {
            PackageManager pm = mContext.getPackageManager();
            PackageInfo pi = null;
            pi = pm.getPackageInfo(mContext.getPackageName(), 0);
            versionName = pi.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }
}
