package com.ww.lp.environment;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.VolleySingleton;
import com.ww.lp.environment.components.progress_layout.ProgressLayout;


/**
 * Created by LinkedME06 on 16/10/26.
 */

public class BaseActivity extends AppCompatActivity {

    protected final String TAG = this.getClass().getName();
    protected ProgressLayout progressLayout;
    public static ProgressDialog dialog_lp = null;
    protected boolean loadFailed = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置app支持vector资源的使用，可参考https://plus.google.com/+AndroidDevelopers/posts/B7QhFkWZ6YX
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    public void setContentView(@LayoutRes int layoutResID) {
        setContentView(layoutResID, true, true, true);
    }

    /**
     * 是否显示加载页面
     *
     * @param layoutResID        布局id
     * @param showAppBar         true:显示 false:隐藏
     * @param showProgressLayout true:显示 false:隐藏
     */
    public void setContentView(@LayoutRes int layoutResID, boolean showAppBar, boolean showHome, boolean showProgressLayout) {
        super.setContentView(layoutResID);
        showAppBar(showAppBar);
        showProgressLayout(showProgressLayout);
        if (showAppBar) {
            //设置toolbar
            setToolBar(showHome);
        }
    }

    /**
     * 显示加载页面
     *
     * @param showProgressLayout true:显示 false:隐藏
     */
    private void showProgressLayout(boolean showProgressLayout) {
        if (showProgressLayout) {
            View userLayout;
            ViewGroup contentView = (ViewGroup) findViewById(R.id.base_container);
            if (contentView == null) {
                contentView = (ViewGroup) findViewById(android.R.id.content);
                userLayout = contentView.getChildAt(0);
            } else {
                userLayout = contentView.getChildAt(contentView.getChildCount() - 1);
            }
            contentView.removeView(userLayout);
            LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //以下方法返回的是以contentView为根节点的viewGroup
            ViewGroup progressParentView = (ViewGroup) layoutInflater.inflate(R.layout.progress_parent_view, contentView);
            progressLayout = (ProgressLayout) progressParentView.findViewById(R.id.progress_layout);
            progressLayout.addView(userLayout);
        }
    }

    /**
     * 显示页面头
     *
     * @param showAppBar true:显示 false:隐藏
     */
    private void showAppBar(boolean showAppBar) {
        if (showAppBar) {
            ViewGroup contentView = (ViewGroup) findViewById(android.R.id.content);
            View userLayout = contentView.getChildAt(0);
            contentView.removeView(userLayout);
            LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //以下方法返回的是以contentView为根节点的viewGroup
            ViewGroup progressParentView = (ViewGroup) layoutInflater.inflate(R.layout.base_appbar, contentView);
            LinearLayout base_container = (LinearLayout) progressParentView.findViewById(R.id.base_container);
            base_container.addView(userLayout);
        }
    }

    /**
     * 设置ToolBar
     *
     * @param showHome 是否显示回退图标
     */
    public void setToolBar(boolean showHome) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            ActionBar ab = getSupportActionBar();
            ab.setDisplayHomeAsUpEnabled(showHome);
            ab.setDisplayShowHomeEnabled(showHome);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 自类重写该方法返回事件
     */
    @Override
    public void onBackPressed() {
        finish();
    }

    //重写，关闭当前activity
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    /**
     * 显示默认的空页面
     */
    public void showEmptyDefault() {
        progressLayout.showEmpty(ContextCompat.getDrawable(this, R.drawable.va_no_data), null, null);
    }

    /**
     * 显示默认的错误页面
     *
     * @param onClickListener 点击错误页面重试按钮后的监听事件
     */
    public void showErrorDefault(View.OnClickListener onClickListener) {
        loadFailed = true;
        progressLayout.showError(ContextCompat.getDrawable(this, R.drawable.va_error), null, null, null, onClickListener);
    }

    /**
     * 显示正在加载页面
     */
    public void showLoading() {
        loadFailed = false;
        progressLayout.showLoading();
    }

    /**
     * 显示内容页面
     */
    public void showContent() {
        if (!loadFailed) {
            progressLayout.showContent();
        }
    }


    public void showProgressDialogLP() {
        showProgressDialogLP("加载中...");
    }

    public void showProgressDialogLP(String message) {
        dialog_lp = ProgressDialog.show(this, null, message, true, true);
    }

    public void removeProgressDialogLP() {
        if (dialog_lp != null && dialog_lp.isShowing()) {
            dialog_lp.dismiss();
            dialog_lp.onDetachedFromWindow();
            dialog_lp = null;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        //停止当前页面的所有请求
        VolleySingleton.getInstance().getRequestQueue().cancelAll(new RequestQueue.RequestFilter() {
            @Override
            public boolean apply(Request<?> request) {
                return ((String) request.getTag()).contains(TAG);
            }
        });
    }

    @Override
    protected void onDestroy() {
        removeProgressDialogLP();
        super.onDestroy();
    }
}
