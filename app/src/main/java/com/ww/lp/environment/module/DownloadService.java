package com.ww.lp.environment.module;

import android.annotation.TargetApi;
import android.app.DownloadManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ProviderInfo;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.Process;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;

import com.microquation.linkedme.android.LinkedME;
import com.microquation.linkedme.android.referral.PrefHelper;

import java.io.File;
import java.util.Iterator;
import java.util.List;

/**
 * <p>***********************************************************************
 * <p> Author: lipeng
 * <p> CreateData: 2017-1-24
 * <p> Version: 1.0.9
 * <p> Description: 下载服务
 * <p>
 * <p>***********************************************************************
 */

public class DownloadService extends Service {

    private DownloadManager dm;
    private BroadcastReceiver receiver;
    private long enqueue;
    public static final String DOWNLOAD_FILE_URL = "download_file_url";
    public static final String DOWNLOAD_FILE_NAME = "download_file_name";
    //    public static final String DOWNLOAD_PATH = "LMDownload/apk";
    //    public static final String DOWNLOAD_FULL_PATH = File.separator + DOWNLOAD_PATH;
    private String fileName;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        fileName = intent.getStringExtra(DOWNLOAD_FILE_NAME);
        File apkDir = new File(getApplicationContext().getExternalFilesDir(null).getAbsolutePath() + "/apk");
        if (!apkDir.exists()) {
            apkDir.mkdirs();
        }
        File file = new File(apkDir, fileName);
        if (file.exists()) {
            PrefHelper.DebugInner("download file is exist.");
            openFile();
            stopSelf();
            return Service.START_NOT_STICKY;
        }
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                System.out.println(intent);
                Iterator<String> iterator = intent.getExtras().keySet().iterator();
                while (iterator.hasNext()) {
                    String key = iterator.next();
                    System.out.println(key);
                    System.out.println(intent.getExtras().get(key));
                }
                long downloadCompletedId = intent.getLongExtra(
                        DownloadManager.EXTRA_DOWNLOAD_ID, 0);
                if (enqueue != downloadCompletedId) {
                    return;
                }
                PrefHelper.DebugInner("download file downloaded.");
                openFile();
                stopSelf();
            }
        };
        registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        startDownload(intent.getStringExtra(DOWNLOAD_FILE_URL), fileName);
        return Service.START_STICKY;
    }

    /**
     * 打开apk文件引导用户安装
     */
    private void openFile() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M && getApplicationInfo().targetSdkVersion > Build.VERSION_CODES.M) {
            String authorities = "";
            List<ProviderInfo> providers = getPackageManager().queryContentProviders(getPackageName(), Process.myUid(), 0);
            if (providers != null) {
                for (ProviderInfo provider : providers) {
                    if (TextUtils.equals("android.support.v4.content.FileProvider", provider.name)) {
                        authorities = provider.authority;
                        break;
                    }
                }
            }
            PrefHelper.Debug(LinkedME.TAG, "设置FileProvider的Authorities为：" + authorities);
            if (TextUtils.isEmpty(authorities)) {
                PrefHelper.Debug(LinkedME.TAG, "未设置FileProvider的Authorities，请在Manifest.xml配置文件中配置provider，参考：https://developer.android.com/training/secure-file-sharing/setup-sharing.html。");
                stopSelf();
                return;
            }
            try {
                uri = FileProvider.getUriForFile(DownloadService.this, authorities,
                        new File(getApplicationContext().getExternalFilesDir(null).getAbsolutePath() + "/apk"
                                + File.separator + fileName));
            } catch (Exception e) {
                PrefHelper.Debug(LinkedME.TAG, "FileProvider的Authorities无正确匹配！");
                stopSelf();
                return;
            }
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            uri = Uri.fromFile(new File(getApplicationContext().getExternalFilesDir(null).getAbsolutePath() + "/apk"
                    + File.separator + fileName));
        }
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        startActivity(intent);
    }

    @Override
    public void onDestroy() {
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
        super.onDestroy();
    }


    /**
     * 开始下载文件
     *
     * @param downloadUrl 文件下载地址
     */
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    private void startDownload(String downloadUrl, String fileName) {
        dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        PrefHelper.DebugInner("Download File is = " + downloadUrl + ",name=" + fileName);
        DownloadManager.Request request = new DownloadManager.Request(
                Uri.parse(downloadUrl));
        request.setMimeType("application/vnd.android.package-archive");
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
//            if (LinkedME.getLinkActiveInstance().getSystemObserver().getWifiConnected()) {
//                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_ONLY_COMPLETION);
//            } else {
//                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
//            }
//        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        }
        request.setDestinationInExternalFilesDir(this, null, "/apk/" + fileName);
//        request.setDestinationInExternalPublicDir(DOWNLOAD_PATH, fileName);
        enqueue = dm.enqueue(request);
        PrefHelper.DebugInner("the enqueue is " + enqueue);
    }


}