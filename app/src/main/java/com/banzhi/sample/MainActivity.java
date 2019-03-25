package com.banzhi.sample;

import android.os.Bundle;
import android.view.View;

import com.banzhi.lib.base.IBaseActivity;
import com.banzhi.lib.utils.BarUtils;


public class MainActivity extends IBaseActivity {


    @Override
    protected boolean hasBaseLayout() {
        return false;
    }
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
    }

    @Override
    protected void initListener() {
    }

    @Override
    protected void initData() {
        BarUtils.setStatusBarColor(this,getResources().getColor(R.color.colorPrimary),0);
    }

    @Override
    protected void processClick(View v) {
    }
}
