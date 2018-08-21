package com.banzhi.library.base;

/**
 * MVPPlugin
 *  邮箱 784787081@qq.com
 */

public class BasePresenter<V extends IView> implements IPresenter<V> {
    protected V mView;
    @Override
    public void attachView(V view) {
        mView=view;
    }

    @Override
    public void detachView() {
        mView=null;
    }
}
