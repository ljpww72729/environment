package com.ww.lp.environment.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by LinkedME06 on 16/11/24.
 */

public class ErrorInfo implements Parcelable {
    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    private int code;
    private String message;
    private String param;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.code);
        dest.writeString(this.message);
        dest.writeString(this.param);
    }

    public ErrorInfo() {
    }

    protected ErrorInfo(Parcel in) {
        this.code = in.readInt();
        this.message = in.readString();
        this.param = in.readString();
    }

    public static final Creator<ErrorInfo> CREATOR = new Creator<ErrorInfo>() {
        @Override
        public ErrorInfo createFromParcel(Parcel source) {
            return new ErrorInfo(source);
        }

        @Override
        public ErrorInfo[] newArray(int size) {
            return new ErrorInfo[size];
        }
    };
}
