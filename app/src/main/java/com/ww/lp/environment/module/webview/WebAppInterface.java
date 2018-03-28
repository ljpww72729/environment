package com.ww.lp.environment.module.webview;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

import com.ww.lp.environment.utils.AndroidUtils;
import com.ww.lp.environment.utils.SPUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;

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
        }
    }

    /**
     * 获取mac地址
     */
    @JavascriptInterface
    public String getMacAddress() {
        return AndroidUtils.getMAC(mContext);
    }

}
