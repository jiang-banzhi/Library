package com.banzhi.sample;

import android.Manifest;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.banzhi.lib.base.IBaseActivity;
import com.banzhi.lib.utils.BarUtils;
import com.banzhi.permission_kt.AndPermisstion;
import com.banzhi.permission_kt.PermissionCallback;

import java.util.List;


public class MainActivity extends IBaseActivity {


    @Override
    protected boolean hasBaseLayout() {
        return false;
    }

    @Override
    protected boolean hasToolbarLayout() {
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mToolbar.setNavigationIcon(R.mipmap.ic_launcher);

        TextView textView = findView(R.id.text);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                test();
//                Throwable e = new Throwable();
//                new HandleCrashManager().handleCrash("extra", e);
            }
        });
    }


    private void test(){
        AndPermisstion.Companion.getInstance()
                .newBuilder()
                .permissions(Manifest.permission.REQUEST_INSTALL_PACKAGES)
                .request(new PermissionCallback() {
                    @Override
                    public void onGranted() {
                        Toast.makeText(MainActivity.this, "授权成功!", Toast.LENGTH_SHORT).show();
                        Log.e("MainActivity", "onGranted");
                    }

                    @Override
                    public void onDenied(List<String> list) {
                        Log.e("MainActivity", "onDenied: ******>" + list);
                    }
                });
    }

    @Override
    public void setTitle(int resId) {
        super.setTitle(resId);
    }

    @Override
    protected boolean isTitleCenter() {
        return true;
    }


    @Override
    protected void initListener() {
    }

    @Override
    protected void initData() {
        BarUtils.setStatusBarColor(this, getResources().getColor(R.color.colorPrimary), 0);
    }

    @Override
    protected void processClick(View v) {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.test, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        TextView textView = findView(R.id.text);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) textView.getLayoutParams();
        switch (item.getItemId()) {
            case R.id.menu1:
                layoutParams.gravity = Gravity.LEFT;
                setTitle("item1");
                break;
            case R.id.menu2:
                layoutParams.gravity = Gravity.RIGHT;
                setTitle("item2");
                break;
            default:

                break;
        }

        textView.setLayoutParams(layoutParams);
        return super.onOptionsItemSelected(item);
    }
}
