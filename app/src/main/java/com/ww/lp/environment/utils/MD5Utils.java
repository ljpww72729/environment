package com.ww.lp.environment.utils;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import org.json.JSONObject;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Copy from renyang@linkedme.cc on 16-3-7.
 */
public class MD5Utils {
    private static ThreadLocal<MessageDigest> MD5 = new ThreadLocal<MessageDigest>() {
        @Override
        protected MessageDigest initialValue() {
            try {
                return MessageDigest.getInstance("MD5");
            } catch (Exception ignored) {
            }
            return null;
        }
    };


    @NonNull
    private static String md5(String src) {
        return md5(src.getBytes());
    }


    @NonNull
    private static String md5(byte[] bytes) {
        if (bytes.length == 0) {
            return "";
        } else {
            MessageDigest md5 = MD5.get();
            md5.reset();
            md5.update(bytes);
            byte[] digest = md5.digest();
            return encodeHex(digest);
        }
    }

    @NonNull
    private static String encodeHex(byte[] bytes) {
        StringBuilder buf = new StringBuilder(bytes.length + bytes.length);
        for (byte b : bytes) {
            if (((int) b & 0xff) < 0x10) {
                buf.append("0");
            }
            buf.append(Long.toString((int) b & 0xff, 16));
        }
        return buf.toString();
    }

    private static byte[] long2Byte2(long[] longArray) {
        byte[] byteArray = new byte[longArray.length * 8];
        for (int i = 0; i < longArray.length; i++) {
            byteArray[0 + 8 * i] = (byte) (longArray[i] >> 56);
            byteArray[1 + 8 * i] = (byte) (longArray[i] >> 48);
            byteArray[2 + 8 * i] = (byte) (longArray[i] >> 40);
            byteArray[3 + 8 * i] = (byte) (longArray[i] >> 32);
            byteArray[4 + 8 * i] = (byte) (longArray[i] >> 24);
            byteArray[5 + 8 * i] = (byte) (longArray[i] >> 16);
            byteArray[6 + 8 * i] = (byte) (longArray[i] >> 8);
            byteArray[7 + 8 * i] = (byte) (longArray[i] >> 0);
        }
        return byteArray;
    }


    private static byte[] long2Byte(long[] longArray) {
        byte[] byteArray = new byte[longArray.length * 8];
        for (int i = 0; i < longArray.length; i++) {
            for (int j = 0; j < 8; j++) {
                byteArray[j + 8 * i] = (byte) (longArray[i] >> 8 * (7 - j));
            }
        }
        return byteArray;
    }


    @NonNull
    private static String md5(long[] array) {
        return md5(long2Byte(array));
    }

    @NonNull
    public static String encrypt(JSONObject json, String key) {
        List<String> values = new ArrayList<>();
        Iterator<String> keys = json.keys();
        while (keys.hasNext()) {
            values.add(json.opt(keys.next()).toString());
        }
        Collections.sort(values);
        return md5(TextUtils.join("&", values) + key);
    }

    @NonNull
    public static String encrypt(String input) {
        return md5(input);
    }

}
