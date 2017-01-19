package com.ww.lp.environment.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by LinkedME06 on 25/12/2016.
 */

public class BaseResult implements Parcelable {
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    private int status;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.status);
    }

    public BaseResult() {
    }

    protected BaseResult(Parcel in) {
        this.status = in.readInt();
    }

    public static final Creator<BaseResult> CREATOR = new Creator<BaseResult>() {
        @Override
        public BaseResult createFromParcel(Parcel source) {
            return new BaseResult(source);
        }

        @Override
        public BaseResult[] newArray(int size) {
            return new BaseResult[size];
        }
    };
}
