package com.banzhi.lib.base;

import java.lang.ref.WeakReference;

/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class BasePresenter<V extends IView> implements IPresenter<V> {

    protected WeakReference<V> mViewReference;

    @Override
    public void attachView(V view) {
        mViewReference = new WeakReference<>(view);
    }

    @Override
    public void detachView() {
        if (mViewReference != null) {
            mViewReference.clear();
            mViewReference = null;
        }
    }

    protected V getView() {
        return mViewReference.get();
    }


    protected boolean isAttached() {
        return mViewReference != null && mViewReference.get() != null;
    }

    protected boolean isDetach() {
        return mViewReference == null || mViewReference.get() == null;
    }

}
