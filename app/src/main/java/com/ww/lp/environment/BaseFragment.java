package com.ww.lp.environment;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.ww.lp.environment.components.progress_layout.ProgressLayout;


/**
 * fragment基础类
 * Created by LinkedME06 on 16/11/13.
 */

public class BaseFragment extends Fragment {

    private ViewGroup contentView;
    private ViewGroup rootView;
    private ProgressLayout progressLayout;

    /**
     *
     * @param inflater inflater
     * @param container container
     * @param savedInstanceState savedInstanceState
     * @param layout_res 布局id
     * @param showProgressLayout 是否显示加载页面，true：显示
     * @return
     */
    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState, @LayoutRes int layout_res, boolean showProgressLayout) {
        contentView = (ViewGroup) inflater.inflate(layout_res, container, false);
        showProgressLayout(inflater, showProgressLayout);
        return rootView;
    }

    /**
     * 显示加载页面
     *
     * @param inflater           填充
     * @param showProgressLayout true:显示 false:隐藏
     */
    private View showProgressLayout(LayoutInflater inflater, boolean showProgressLayout) {
        rootView = contentView;
        if (showProgressLayout) {
            if (contentView instanceof LinearLayout) {
                rootView = new LinearLayout(getActivity());
                rootView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            } else if (contentView instanceof RelativeLayout) {
                rootView = new RelativeLayout(getActivity());
                rootView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            } else if (contentView instanceof ScrollView) {
                rootView = new ScrollView(getActivity());
                rootView.setLayoutParams(new ScrollView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            } else if (contentView instanceof FrameLayout) {
                rootView = new FrameLayout(getActivity());
                rootView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            }
            //以下方法返回的是以contentView为根节点的viewGroup
            progressLayout = (ProgressLayout) inflater.inflate(R.layout.progress_parent_view, null);
            progressLayout.addView(contentView);
            rootView.addView(progressLayout);
            rootView.setTag(contentView.getTag());
            contentView.setTag(null);
        }
        return rootView;
    }

    /**
     * 显示默认的空页面
     */
    public void showEmptyDefault() {
        progressLayout.showEmpty(ContextCompat.getDrawable(getActivity(), R.drawable.va_no_data), null, null);
    }

    /**
     * 显示默认的错误页面
     *
     * @param onClickListener 点击错误页面重试按钮后的监听事件
     */
    public void showErrorDefault(View.OnClickListener onClickListener) {
        progressLayout.showError(ContextCompat.getDrawable(getActivity(), R.drawable.va_error), null, null, null, onClickListener);
    }

    /**
     * 显示正在加载页面
     */
    public void showLoading() {
        progressLayout.showLoading();
    }

    /**
     * 显示内容页面
     */
    public void showContent() {
        progressLayout.showContent();
    }

}
