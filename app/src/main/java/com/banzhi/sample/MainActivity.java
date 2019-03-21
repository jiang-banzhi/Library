package com.banzhi.sample;

import android.os.Bundle;
import android.view.View;

import com.banzhi.library.base.IBaseActivity;

public class MainActivity extends IBaseActivity {

//
//    @Override
//    protected boolean hasBaseLayout() {
//        return false;
//    }
//
//    @Override
//    protected boolean hasToolbarLayout() {
//        return false;
//    }

    @Override
    protected int getLayoutId() {

        return R.layout.activity_main;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
mToolbar.setNavigationIcon(R.mipmap.ic_launcher);
    }

    @Override
    protected void initListener() {

    }

    @Override
    protected void initData() {
//showEmptyView();
    }

    @Override
    protected void processClick(View v) {

    }
}
