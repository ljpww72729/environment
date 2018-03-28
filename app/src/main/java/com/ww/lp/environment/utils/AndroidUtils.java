package com.ww.lp.environment.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

/**
 * Created by LinkedME06 on 3/27/18.
 */

public class AndroidUtils {

    //mac地址
    private static String macSerial = "";

    @SuppressLint("MissingPermission")
    public static String getIMEI(Context context) {
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            return tm.getDeviceId();
        } catch (Exception ignore) {
        }
        return "";
    }

    public static String getAndroidId(Context context) {
        try {
            return Settings.System.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        } catch (Exception ignore) {
        }
        return "";
    }

    public static String getMAC(Context context) {
        try {
            if (!TextUtils.isEmpty(macSerial)) {
                return macSerial;
            }
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                WifiManager wifi = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                if (wifi != null) {
                    WifiInfo wifiInfo = wifi.getConnectionInfo();
                    if (wifiInfo != null && !TextUtils.isEmpty(wifiInfo.getMacAddress()) &&
                            !TextUtils.equals("02:00:00:00:00:00", wifiInfo.getMacAddress().trim())) {
                        macSerial = wifiInfo.getMacAddress().trim();
                        return macSerial;
                    }
                }
            }
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                macSerial = getMacCompat23();
            } else {
                macSerial = getMacCompat24();
            }

            if (!TextUtils.isEmpty(macSerial)) {
                return macSerial;
            }
        } catch (Exception ignore) {
        }
        return "";
    }

    /**
     * Android 23及以下版本使用该方法获取mac地址
     */
    private static String getMacCompat23() throws Exception {
        String mac23 = null;
        String str = "";
        String streth = "";
        //无线mac地址
        Process pp = Runtime.getRuntime().exec(
                "cat /sys/class/net/wlan0/address");
        InputStreamReader ir = new InputStreamReader(pp.getInputStream());
        LineNumberReader input = new LineNumberReader(ir);

        for (; null != str; ) {
            str = input.readLine();
            if (str != null) {
                mac23 = str.trim();// 去空格
                break;
            }
        }
        if (!TextUtils.isEmpty(mac23)) {
            return mac23;
        }
        //有线mac地址
        Process ppeth = Runtime.getRuntime().exec(
                "cat /sys/class/net/eth0/address");
        InputStreamReader ireth = new InputStreamReader(ppeth.getInputStream());
        LineNumberReader inputeth = new LineNumberReader(ireth);

        for (; null != streth; ) {
            streth = inputeth.readLine();
            if (streth != null) {
                mac23 = streth.trim();// 去空格
                break;
            }
        }
        return mac23;
    }

    /**
     * Android7.0及以上通过网络接口取mac地址
     */
    private static String getMacCompat24() throws Exception {
        List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
        for (NetworkInterface nif : all) {
            if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

            byte[] macBytes = nif.getHardwareAddress();
            if (macBytes == null) {
                return null;
            }

            StringBuilder res1 = new StringBuilder();
            for (byte b : macBytes) {
                res1.append(String.format("%02X:", b));
            }

            if (res1.length() > 0) {
                res1.deleteCharAt(res1.length() - 1);
            }
            return res1.toString();
        }
        return null;
    }
}
