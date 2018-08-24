package com.banzhi.sample;

import android.os.Bundle;
import android.view.View;

import com.banzhi.library.base.IBaseActivity;

public class MainActivity extends IBaseActivity {


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
//        return R.layout.base_quick_view_load_more;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {

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
