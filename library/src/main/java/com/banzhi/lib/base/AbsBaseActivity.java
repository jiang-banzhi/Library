package com.banzhi.lib.base;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.banzhi.lib.application.App;
import com.banzhi.lib.utils.BarUtils;
import com.banzhi.lib.widget.view.BaseLayout;
import com.banzhi.library.R;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.snackbar.Snackbar;

import static com.google.android.material.appbar.AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS;
import static com.google.android.material.appbar.AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL;


/**
 * <pre>
 * author : jiang
 * time : 2017/3/28.
 * desc :
 * </pre>
 */

public abstract class AbsBaseActivity extends AppCompatActivity implements BaseLayout.OnBaseLayoutClickListener, View.OnClickListener {
    public static String TAG = "TAG";
    protected BaseLayout mBaseLayout;
    private SparseArray<View> mViews;
    private Snackbar snackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.addActivity(this);
        mViews = new SparseArray<>();
        iniActionbarColor();
        setContentView(getLayoutId());
        init(savedInstanceState);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        App.removeActivity(this);
    }

    /**
     * 初始状态汇总
     */
    protected void init(Bundle savedInstanceState) {
        Log.e(TAG, "********************************************************");
        Log.e(TAG, "** init:当前activity==> " + this.getClass().getSimpleName());
        Log.e(TAG, "********************************************************");
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
        smartFragmentReplace(target, toFragment, true);
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
            mToolbar = groupView.findViewById(R.id.toolbar);
            setSupportActionBar(mToolbar);
            if (isTitleCenter()) {
                getSupportActionBar().setDisplayShowTitleEnabled(false);
                mToolbar.addView(initTitleView());
            }
            ViewGroup viewGroup = groupView.findViewById(R.id.content_container);
            viewGroup.addView(view);
            return groupView;
        }
        return view;
    }


    /**
     * @return
     */
    protected View initTitleView() {
        if (mTitleView == null && isTitleCenter()) {
            mTitleView = new TextView(this);
            mTitleView.setGravity(Gravity.CENTER);
            mTitleView.setTextColor(Color.WHITE);
            mTitleView.setSingleLine();
            mTitleView.setEllipsize(TextUtils.TruncateAt.END);
            Toolbar.LayoutParams params = new Toolbar.LayoutParams(Toolbar.LayoutParams.WRAP_CONTENT, Toolbar.LayoutParams.MATCH_PARENT);
            params.gravity = Gravity.CENTER;
            mTitleView.setLayoutParams(params);
        }
        return mTitleView;
    }

    public Toolbar mToolbar;

    private TextView mTitleView;

    @Override
    protected void onTitleChanged(CharSequence title, int color) {
        super.onTitleChanged(title, color);
        if (mTitleView != null && isTitleCenter()) {
            mTitleView.setText(title);
        }
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
     * toolbar title是否居中
     *
     * @return
     */
    protected boolean isTitleCenter() {
        return false;
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
        startActivityForResult(intent, requestCode);
        overrideAnim(0);
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


    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            onFragmentBack();
            getSupportFragmentManager().popBackStack();
        } else {
            randomExit((int) (Math.random() * 10 % 4));
            finish();
        }

    }

    protected void onFragmentBack() {

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
            default:
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
            default:
                break;
        }
    }


}
