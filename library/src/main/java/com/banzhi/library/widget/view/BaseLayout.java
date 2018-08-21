package com.banzhi.library.widget.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.jiang.baselibrary.R;
import com.jiang.baselibrary.utils.ViewUtils;


/**
 * <pre>
 * author : No.1
 * time : 2017/3/28.
 * desc :
 * </pre>
 */
public class BaseLayout extends RelativeLayout implements View.OnClickListener {
    public static final String TAG_EMPTY = "empty";
    public static final String TAG_ERROR = "error";

    View mContentView;
    View mEmptyView;
    View mErrorView;
    View mLoadingView;
    OnBaseLayoutClickListener mBaseClickListener;

    public BaseLayout(Context context) {
        super(context);
    }

    public BaseLayout(Context context, @NonNull View contentView, View emptyView, View errorView, View loadingView, OnBaseLayoutClickListener clickListener) {
        super(context);
        if (contentView == null) {
            throw new NullPointerException("The content view must not null ");
        }
        this.mContentView = contentView;//内容页
        this.mEmptyView = emptyView;//空数据页
        this.mErrorView = errorView;//错误页
        this.mLoadingView = loadingView;//加载页
        this.mBaseClickListener = clickListener;//空数据 错误页点击事件
        //1.将contentview添加到布局
        LayoutParams contentParmas = new LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        addView(contentView, contentParmas);
        //2.将空数据emptyview添加到布局
        if (emptyView == null) {
            emptyView = inflate(context, R.layout.base_view_default_empty, null);
        }
        mEmptyView = emptyView;
        LayoutParams emptyParams = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
        addView(mEmptyView, emptyParams);
        //3.将错误页errorview添加到布局
        if (errorView == null) {
            errorView = inflate(context, R.layout.base_view_default_error, null);
        }
        mErrorView = errorView;
        LayoutParams errorParams = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
        addView(mErrorView, errorParams);
        //4.添加进度条
        if (loadingView == null) {
            loadingView = inflate(context, R.layout.base_view_default_progressbar, null);
        }
        mLoadingView = loadingView;
        LayoutParams pbParams = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
        addView(loadingView, pbParams);
        //5.添加事件监听
        mEmptyView.setTag(TAG_EMPTY);
        mErrorView.setTag(TAG_ERROR);
        mLoadingView.setOnClickListener(this);
        mEmptyView.setOnClickListener(this);
        mErrorView.setOnClickListener(this);

    }

    public void setEmptyView(Context context, int resId) {
        removeView(mEmptyView);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mEmptyView = inflater.inflate(resId, null);
        FrameLayout frameLayout = new FrameLayout(getContext());
        FrameLayout.LayoutParams emptyViewParams = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        emptyViewParams.gravity = Gravity.CENTER;
        LayoutParams emptyLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
        frameLayout.addView(mEmptyView, emptyViewParams);
        addView(frameLayout, emptyLayoutParams);
        mEmptyView.setTag(TAG_EMPTY);
        mEmptyView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        ViewUtils.setDelayedClickable(v, 500);
        if (mBaseClickListener != null) {
            Object object = v.getTag();
            if (null != object) {
                String tag = v.getTag().toString();
                if (TAG_EMPTY.equals(tag)) {
                    mBaseClickListener.onEmptyViewClick();
                }
                if (TAG_ERROR.equals(tag)) {
                    mBaseClickListener.onErrorViewClick();
                }
            }

        }
    }

    /**
     * 显示内容
     */
    public void showContentView() {
        mContentView.setVisibility(VISIBLE);
        mEmptyView.setVisibility(GONE);
        mErrorView.setVisibility(GONE);
        mLoadingView.setVisibility(GONE);
    }

    /**
     * 显示空数据
     */
    public void showEmptyView() {
        mContentView.setVisibility(GONE);
        mEmptyView.setVisibility(VISIBLE);
        mErrorView.setVisibility(GONE);
        mLoadingView.setVisibility(GONE);
    }

    /**
     * 显示错误
     */
    public void showErrorView() {
        mContentView.setVisibility(GONE);
        mEmptyView.setVisibility(GONE);
        mErrorView.setVisibility(VISIBLE);
        mLoadingView.setVisibility(GONE);
    }

    /**
     * 显示进度
     */
    public void showLoadingView() {
        mContentView.setVisibility(GONE);
        mEmptyView.setVisibility(GONE);
        mErrorView.setVisibility(GONE);
        mLoadingView.setVisibility(VISIBLE);
    }

    public static class Builder {
        Context context;
        LayoutInflater inflater;
        View contentView;
        View emptyView;
        View errorView;
        View loadingView;
        OnBaseLayoutClickListener clickListener;

        public Builder(Context context) {
            this.context = context;
            inflater = LayoutInflater.from(context);
        }

        /**
         * 设置内容
         *
         * @param contentView
         */
        public Builder setContentView(@NonNull View contentView) {
            this.contentView = contentView;
            return this;
        }

        /**
         * 设置内容
         *
         * @param resId
         */
        public Builder setContentView(@NonNull int resId) {
            this.contentView = inflater.inflate(resId, null);
            return this;
        }

        /**
         * 设置空数据页
         *
         * @param emptyView
         */
        public Builder setEmptyView(View emptyView) {
            this.emptyView = emptyView;
            return this;
        }

        /**
         * 设置空数据页
         *
         * @param resId
         */
        public Builder setEmptyView(@NonNull int resId) {
            this.emptyView = inflater.inflate(resId, null);
            return this;
        }

        /**
         * 设置错误页
         *
         * @param errorView
         */
        public Builder setErrorView(View errorView) {
            this.errorView = errorView;
            return this;
        }

        /**
         * 设置错误页
         *
         * @param resId
         */
        public Builder setErrorView(@NonNull int resId) {
            this.errorView = inflater.inflate(resId, null);
            return this;
        }

        /**
         * 设置加载页
         *
         * @param loadingView
         */
        public Builder setLoadingView(View loadingView) {
            this.loadingView = loadingView;
            return this;
        }

        /**
         * 设置加载页
         *
         * @param resId
         */
        public Builder setLoadingView(@NonNull int resId) {
            this.loadingView = inflater.inflate(resId, null);
            return this;
        }

        public void setClickListener(OnBaseLayoutClickListener clickListener) {
            this.clickListener = clickListener;
        }

        public BaseLayout build() {
            return new BaseLayout(context, contentView, emptyView, errorView, loadingView, clickListener);
        }
    }

    public interface OnBaseLayoutClickListener {
        void onErrorViewClick();

        void onEmptyViewClick();
    }
}
