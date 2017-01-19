package com.ww.lp.environment.data;

import android.os.Parcel;

import java.util.ArrayList;

/**
 * Created by LinkedME06 on 16/11/27.
 */

public class ErrorResult extends BaseResult{

    public ArrayList<ErrorInfo> getErrors() {
        return errors;
    }

    public void setErrors(ArrayList<ErrorInfo> errors) {
        this.errors = errors;
    }

    private ArrayList<ErrorInfo> errors;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeTypedList(this.errors);
    }

    public ErrorResult() {
    }

    protected ErrorResult(Parcel in) {
        super(in);
        this.errors = in.createTypedArrayList(ErrorInfo.CREATOR);
    }

    public static final Creator<ErrorResult> CREATOR = new Creator<ErrorResult>() {
        @Override
        public ErrorResult createFromParcel(Parcel source) {
            return new ErrorResult(source);
        }

        @Override
        public ErrorResult[] newArray(int size) {
            return new ErrorResult[size];
        }
    };
}
