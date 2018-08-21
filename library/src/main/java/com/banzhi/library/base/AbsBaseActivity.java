package com.banzhi.library.base;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.banzhi.library.R;
import com.banzhi.library.Utils.BarUtils;
import com.banzhi.library.Utils.PermissionHelper;
import com.banzhi.library.application.App;
import com.banzhi.library.widget.view.BaseLayout;

import static android.support.design.widget.AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS;
import static android.support.design.widget.AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL;


/**
 * <pre>
 * author : jiang
 * time : 2017/3/28.
 * desc :
 * </pre>
 */

public abstract class AbsBaseActivity extends AppCompatActivity implements BaseLayout.OnBaseLayoutClickListener, View.OnClickListener {
    public static String TAG = "result";
    protected BaseLayout mBaseLayout;
    private SparseArray<View> mViews;
    private Snackbar snackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        updateTheme(savedInstanceState);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            //设置界面进入 退出动画  Slide滑动  Explode破碎  Fade淡出
//            //第一次进入时使用
//            getWindow().setEnterTransition(new Slide(Gravity.LEFT).setDuration(500));
//            //退出时使用
//            getWindow().setExitTransition(new Slide(Gravity.RIGHT).setDuration(500));
//            //再次进入时使用
//            getWindow().setReenterTransition(new Slide(Gravity.LEFT).setDuration(500));
//        }
        App.addActivity(this);
        mViews = new SparseArray<>();
     setOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        iniActionbarColor();
        setContentView(getLayoutId());
        init(savedInstanceState);
    }
    protected  void setOrientation(int requestedOrientation){
        setRequestedOrientation(requestedOrientation);
    }

//    protected abstract void updateTheme(Bundle savedInstanceState);


    @Override
    protected void onDestroy() {
        super.onDestroy();
        App.removeActivity(this);
    }

    /**
     * 初始状态汇总
     */
    protected void init(Bundle savedInstanceState) {
        Log.e(TAG, "init:当前activity==> " + this.getClass().getSimpleName());
        showContentView();
        if (null != getIntent()) {
            handleIntent(getIntent());
        }
        initView(savedInstanceState);
        initData();
        initFragment(savedInstanceState);
        initListener();
    }

    protected void initFragment(Bundle savedInstanceState) {
    }
/**
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
                finish();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }*/

    /**
     * 获取数据传递intent
     *
     * @param intent
     */
    protected void handleIntent(Intent intent) {


    }

    @Override
    public void onClick(View v) {
        processClick(v);
    }


    /**
     * 获取activity的layout
     *
     * @return layoutid
     */
    protected abstract int getLayoutId();

    /**
     * 初始view
     */
    protected abstract void initView(Bundle savedInstanceState);

    /**
     * 设置监听
     */
    protected abstract void initListener();

    /**
     * 初始数据
     */
    protected abstract void initData();

    /**
     * onclick点击事件
     *
     * @param v
     */
    protected abstract void processClick(View v);

    /**
     * 通过id找到view 减少findviewbyid
     *
     * @param viewId viewid
     * @param <V>    view
     * @return view
     */
    public <V extends View> V findView(int viewId) {
        V view = (V) mViews.get(viewId);
        if (view == null) {
            view = (V) findViewById(viewId);
            mViews.put(viewId, view);
        }
        return view;
    }

    /**
     * view设置onclick事件
     *
     * @param view 点击的view
     */
    public <V extends View> void setOnClick(View view) {
        view.setOnClickListener(this);
    }

    /*
     * ************Fragement相关方法************************************************
     *
     */
    private Fragment currentFragment;
    boolean isFlg;//使用replace方式添加fragment

    /**
     * Fragment替换(当前destrory,新的create)
     */
    public void fragmentReplace(int containerViewId, Fragment toFragment, boolean backStack) {
        isFlg = true;
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        String toClassName = toFragment.getClass().getSimpleName();
//transaction.setCustomAnimations(R.anim.slide_fade_right_enter,
// R.anim.slide_left_exit, R.anim.slide_fade_left_enter, R.anim.slide_right_exit);//自定义动画

        if (manager.findFragmentByTag(toClassName) == null) {//没有添加过
            transaction.replace(containerViewId, toFragment, toClassName);
            if (backStack) {
                transaction.addToBackStack(toClassName);
            }

            transaction.commit();
        }
    }


    /**
     * Fragment替换(核心为隐藏当前的,显示现在的,用过的将不会destrory与create)
     *
     * @target 容器id
     */
    public void smartFragmentReplace(int target, Fragment toFragment) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_fade_right_enter, R.anim.slide_left_exit,
                R.anim.slide_fade_left_enter, R.anim.slide_right_exit);//自定义动画
//        transaction.setTransition(FragmentTransaction.TRANSIT_ENTER_MASK);//默认动画
        // 如有当前在使用的->隐藏当前的
        if (currentFragment != null) {
            transaction.hide(currentFragment);
        }
        String toClassName = toFragment.getClass().getSimpleName();
        // toFragment之前添加使用过->显示出来
        if (manager.findFragmentByTag(toClassName) != null) {
            transaction.show(toFragment);
        } else {// toFragment还没添加使用过->添加上去
            transaction.add(target, toFragment, toClassName);
        }
        transaction.commit();
        // toFragment更新为当前的
        currentFragment = toFragment;
    }

    /**
     * Fragment替换(核心为隐藏当前的,显示现在的,用过的将不会destrory与create)
     *
     * @target 容器id
     */
    public void smartFragmentReplace(int target, Fragment toFragment, boolean hideCurrentFragment) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_fade_right_enter, R.anim.slide_left_exit,
                R.anim.slide_fade_left_enter, R.anim.slide_right_exit);//自定义动画
//        transaction.setTransition(FragmentTransaction.TRANSIT_ENTER_MASK);//默认动画
        // 如有当前在使用的->隐藏当前的
        if (currentFragment != null) {
            if (hideCurrentFragment) {
                transaction.hide(currentFragment);
            }
        }
        String toClassName = toFragment.getClass().getSimpleName();
        // toFragment之前添加使用过->显示出来
        if (manager.findFragmentByTag(toClassName) != null) {
            transaction.show(toFragment);
        } else {// toFragment还没添加使用过->添加上去
            transaction.add(target, toFragment, toClassName);
        }
        transaction.commit();
        // toFragment更新为当前的
        currentFragment = toFragment;
    }
    //**********************状态栏************************/

    /**
     * 设置状态栏颜色
     */
    protected void iniActionbarColor() {
        BarUtils.setTransparentStatusBar(this);
    }

    protected void setScrollFlg(@AppBarLayout.LayoutParams.ScrollFlags int flags) {
        AppBarLayout appbar = (AppBarLayout) findViewById(R.id.appbar);
        AppBarLayout.LayoutParams mParams = (AppBarLayout.LayoutParams) appbar.getChildAt(0).getLayoutParams();
        mParams.setScrollFlags(flags);//的时候AppBarLayout下的toolbar就不会随着滚动条折叠

    }

    /**
     * 设置toolbar滚动影藏
     */
    protected void setScrollFlg() {
        setScrollFlg(SCROLL_FLAG_SCROLL | SCROLL_FLAG_ENTER_ALWAYS);
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        addLayoutView(LayoutInflater.from(this).inflate(layoutResID, null));
    }

    @Override
    public void setContentView(View view) {
        addLayoutView(view);
    }

    /**
     * 构建新的contentview
     *
     * @param view
     */
    private void addLayoutView(View view) {
        view = buildContentView(view);
        view = buildToolbarView(view);
        super.setContentView(view);

    }


    /**
     * @param view
     * @return
     */
    private View buildToolbarView(View view) {
        if (hasToolbarLayout()) {
            View groupView = LayoutInflater.from(this).inflate(R.layout.base_view_base_layout, null);
            mToolbar = (Toolbar) groupView.findViewById(R.id.toolbar);
            setSupportActionBar(mToolbar);
            ViewGroup viewGroup = (ViewGroup) groupView.findViewById(R.id.content_container);
            viewGroup.addView(view);
            return groupView;
        }
        return view;
    }

    public Toolbar mToolbar;

    /**
     * 设置主标题
     *
     * @param title 标题内容
     */
    public void setTitle(CharSequence title) {
        getSupportActionBar().setTitle(title);
    }

    /**
     * 设置主标题
     *
     * @param resId 标题内容
     */
    public void setTitle(@StringRes int resId) {
        getSupportActionBar().setTitle(resId);
    }

    /**
     * 设置子标题
     *
     * @param title
     */
    public void setSubTitle(CharSequence title) {
        getSupportActionBar().setSubtitle(title);
    }

    /**
     * 设置子标题
     *
     * @param resId
     */
    public void setSubTitle(@StringRes int resId) {
        getSupportActionBar().setSubtitle(resId);
    }

    /**
     * @param view
     * @return
     */
    private View buildContentView(View view) {
        if (hasBaseLayout()) {
            BaseLayout.Builder builder = new BaseLayout.Builder(this);
            builder.setClickListener(this);
            builder.setContentView(view);
            mBaseLayout = builder.build();
            return mBaseLayout;
        }
        return view;
    }


    /**
     * 是否包含共用toolbar.
     *
     * @return
     */
    protected boolean hasToolbarLayout() {
        return true;
    }

    /**
     * 是否包含基本view如progress、empty、error等.
     *
     * @return
     */
    protected boolean hasBaseLayout() {
        return true;
    }

    /**
     * Show empty view when the data of current page is null.
     */
    public void showEmptyView() {
        if (null != mBaseLayout) {
            mBaseLayout.showEmptyView();
        }
    }

    /**
     * Show error view when the request of current page is failed.
     */
    public void showErrorView() {
        if (null != mBaseLayout) {
            mBaseLayout.showErrorView();
        }
    }

    /**
     * Show progress view when request data first come in the page.
     */
    public void showProgressView() {
        if (null != mBaseLayout) {
            mBaseLayout.showLoadingView();
        }
    }

    /**
     * Show content view of current page.
     */
    public void showContentView() {
        if (null != mBaseLayout) {
            mBaseLayout.showContentView();
        }
    }

    @Override
    public void onErrorViewClick() {
        // 如果子类需要处理空页面点击事件，则需覆盖此方法
    }

    @Override
    public void onEmptyViewClick() {
        // 如果子类需要处理空页面点击事件，则需覆盖此方法
    }

    //**************************snackbar相关******************************//


    public void showShanck(String msg) {
        showShanck(msg, false, R.color.baseColorAccent);
    }

    public void showShanck(String msg, boolean hasAction) {
        showShanck(msg, hasAction, R.color.baseColorAccent);
    }

    public void showShanck(String msg, boolean hasAction, int actionColor) {
        if (snackbar == null) {
            snackbar = Snackbar.make(mBaseLayout, msg, Snackbar.LENGTH_SHORT);
            if (hasAction) {
                snackbar.setActionTextColor(getResources().getColor(actionColor));
                snackbar.setAction("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        snackbar.dismiss();
                    }
                });
            }
            snackbar.show();
        } else {
            snackbar.setText(msg);
            snackbar.show();
        }

    }

    //**************************权限******************************//
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PermissionHelper.REQUEST_CODE_PERMISSON) {
            if (!verifyPermissions(grantResults)) {
                permissionGrantedFail();
            } else {
                permissionGrantedSuccess();
            }

        }
    }

    /**
     * 请求权限成功后回调
     */
    protected void permissionGrantedSuccess() {
        //请求权限成功后调用
    }

    /**
     * 请求权限失败后回调
     */
    protected void permissionGrantedFail() {
        PermissionHelper.showTipsDialog(this);
    }


    /**
     * 检测所有的权限是否都已授权
     *
     * @param grantResults
     * @return
     */
    private boolean verifyPermissions(int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
    //**************************页面跳转******************************//

    /**
     * 页面跳转
     *
     * @param toActivity 目标页面
     */
    protected void gotoActivity(Class<?> toActivity) {
        gotoActivity(toActivity, false);
    }

    /**
     * 页面跳转 销毁当前
     *
     * @param toActivity 目标页面
     * @param isFinished 是否销毁
     */
    protected void gotoActivity(Class<?> toActivity, boolean isFinished) {
        gotoActivity(toActivity, null, null, isFinished);
    }

    protected void gotoActivity(Class<?> toActivity, String action, Bundle bundle) {
        gotoActivity(toActivity, action, bundle, false);
    }

    protected void gotoActivity(Class<?> toActivity, Bundle bundle) {
        gotoActivity(toActivity, null, bundle, false);
    }

    protected void gotoActivity(Class<?> toActivity, Bundle bundle, boolean isFinished) {
        gotoActivity(toActivity, null, bundle, isFinished);
    }

    protected void gotoActivity(Class<?> toActivity, String action) {
        gotoActivity(toActivity, action, null, false);
    }

    /**
     * @param toActivity 目标页面
     * @param action
     * @param bundle     序列化数据
     * @param isFinished 是否销毁
     */
    protected void gotoActivity(Class<?> toActivity, String action, Bundle bundle, boolean isFinished) {
        gotoActivityForResult(toActivity, action, bundle, -1, null, isFinished);
    }


    /**
     * @param toActivity   目标页面
     * @param action
     * @param bundle       序列化数据
     * @param isFinished   是否销毁
     * @param bundleOption 转场动画
     */
    protected void gotoActivity(Class<?> toActivity, String action, Bundle bundle, Bundle bundleOption, boolean isFinished) {
        gotoActivityForResult(toActivity, action, bundle, -1, bundleOption, isFinished);
    }

    /**
     * @param toActivity   目标界面
     * @param action
     * @param bundle       传递的数据
     * @param requestCode  请求码
     * @param bundleOption 转场动画
     * @param isFinished   是否销毁当前界面
     */
    protected void gotoActivityForResult(Class<?> toActivity, String action, Bundle bundle, int requestCode, Bundle bundleOption, boolean isFinished) {
        Intent intent = new Intent(this, toActivity);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        if (!TextUtils.isEmpty(action)) {
            intent.setAction(action);
        }
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            startActivityForResult(intent, requestCode);
            overrideAnim(0);
//        } else {
//            bundleOption = bundleOption == null ? ActivityOptions.makeSceneTransitionAnimation(this).toBundle() : bundleOption;
//            startActivityForResult(intent, requestCode, bundleOption);
//
//        }

        if (isFinished) {
            finish();
        }
    }


    protected void gotoActivityForResult(Class<?> toActivity, String action, Bundle bundle, int requestCode) {
        gotoActivityForResult(toActivity, action, bundle, requestCode, null, false);
    }

    protected void gotoActivityForResult(Class<?> toActivity, String action, Bundle bundle, int requestCode, Bundle bundleOption) {
        gotoActivityForResult(toActivity, action, bundle, requestCode, bundleOption, false);
    }

    protected void gotoActivityForResult(Class<?> toActivity, Bundle bundle, int requestCode) {
        gotoActivityForResult(toActivity, null, bundle, requestCode, null, false);
    }

    protected void gotoActivityForResult(Class<?> toActivity, Bundle bundle, int requestCode, Bundle bundleOption) {
        gotoActivityForResult(toActivity, null, bundle, requestCode, bundleOption, false);
    }


    protected void gotoActivityForResult(Class<?> toActivity, String action, int requestCode) {
        gotoActivityForResult(toActivity, action, null, requestCode, null, false);
    }

    protected void gotoActivityForResult(Class<?> toActivity, String action, int requestCode, Bundle bundleOption) {
        gotoActivityForResult(toActivity, action, null, requestCode, bundleOption, false);
    }

    protected void gotoActivityForResult(Class<?> toActivity, int requestCode) {
        gotoActivityForResult(toActivity, null, null, requestCode, null, false);
    }

    protected void gotoActivityForResult(Class<?> toActivity, int requestCode, Bundle bundleOption) {
        gotoActivityForResult(toActivity, null, null, requestCode, bundleOption, false);
    }

    private static final String MPARAM = "param";

    protected Bundle getBundle(String... params) {
        Bundle bundle = new Bundle();
        int count = params.length;
        if (params == null || count == 0) {
            return bundle;
        }
        for (int i = 0; i < count; i++) {
            bundle.putString(MPARAM + i, params[i]);
        }
        return bundle;
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            onFragmentBack();
            getSupportFragmentManager().popBackStack();
        } else {
//            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                randomExit((int) (Math.random() * 10 % 4));
                finish();
//            } else {
//                ActivityCompat.finishAfterTransition(this);
//            }
        }

    }

    public void onFragmentBack() {

    }

    /**
     * 随机退出动画
     */
    protected void randomExit(int rand) {
        switch (rand) {
            case 0:
                overridePendingTransition(R.anim.slide_fade_right_enter, R.anim.slide_left_exit);
                break;
            case 1:
                overridePendingTransition(R.anim.slide_fade_left_enter, R.anim.slide_right_exit);
                break;
            case 2:
                overridePendingTransition(R.anim.slide_fade_top_enter, R.anim.slide_bottom_exit);
                break;
            case 3:
                overridePendingTransition(R.anim.slide_fade_bottom_enter, R.anim.slide_top_exit);
                break;
        }
    }

    /**
     * 点击返回的界面动画
     *
     * @param mode
     */
    private void overrideAnim(int mode) {
        switch (mode) {
            case 0:
                overridePendingTransition(R.anim.slide_fade_right_enter, R.anim.slide_left_exit);
                break;
            case 1:
                overridePendingTransition(R.anim.slide_fade_left_enter, R.anim.slide_right_exit);
                break;
            case 2:
                overridePendingTransition(R.anim.slide_fade_top_enter, R.anim.slide_bottom_exit);
                break;
            case 3:
                overridePendingTransition(R.anim.slide_fade_bottom_enter, R.anim.slide_top_exit);
                break;
        }
    }


}
