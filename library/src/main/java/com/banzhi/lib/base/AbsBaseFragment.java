package com.banzhi.lib.base;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.banzhi.lib.widget.view.BaseLayout;
import com.banzhi.library.R;


/**
 * <pre>
 * author : No.1
 * time : 2017/3/30.
 * desc :
 * </pre>
 */

public abstract class AbsBaseFragment extends Fragment implements BaseLayout.OnBaseLayoutClickListener, View.OnClickListener {


    protected AbsBaseActivity mActivity;

    private SparseArray<View> mViews;
    //根布局
    protected View mRootView;
    //baselayout
    protected BaseLayout mBaseLayout;
    //是否是第一次加载
    private boolean mIsFirstLoad = true;
    //是否已初始化
    private boolean mIsInitialized = false;
    //是否可见
    private boolean mIsVisiable = false;
    //是否已销毁
    private boolean mIsDestroyed;

    private Toast mToast;

    Snackbar snackbar;

    /**
     * 需要加载的布局
     *
     * @return layoutid
     */
    protected abstract int getLayoutId();

    /**
     * 初始view
     */
    protected abstract void initView();

    /**
     * 初始监听
     */
    protected abstract void initListener();

    /**
     * 初始数据
     */
    protected abstract void initData();

    /**
     * 加载数据
     */
    protected abstract void loadData();

    /**
     * onclick点击事件
     *
     * @param v
     */
    protected abstract void processClick(View v);

    @Override
    public void onAttach(Activity activity) {
        mActivity = (AbsBaseActivity) activity;
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        mActivity = null;
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        mActivity = null;
        super.onDestroy();

    }

    private static final String STATE_SAVE_IS_HIDDEN = "STATE_SAVE_IS_HIDDEN";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            boolean isSupportHidden = savedInstanceState.getBoolean(STATE_SAVE_IS_HIDDEN);
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            if (isSupportHidden) {
                ft.hide(this);
            } else {
                ft.show(this);
            }
            ft.commit();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(STATE_SAVE_IS_HIDDEN, isHidden());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mIsDestroyed = false;//设置未销毁
        mViews = new SparseArray<>();
        if (mRootView == null) {//第一次加载
            View view = inflater.inflate(getLayoutId(), container, false);
            mRootView = view;
            if (hasBaseLayout()) {
                BaseLayout.Builder builder = getLayoutBuilder();
                if (builder != null) {
                    mBaseLayout = builder.setContentView(mRootView).build();
                    mRootView = mBaseLayout;
                }
            }
            initView();
            mIsInitialized = true;//设置为已加载
            lazyLoad();
        } else {
            ViewGroup localViewGroup = (ViewGroup) mRootView.getParent();
            if (localViewGroup != null) {
                localViewGroup.removeView(mRootView);
            }
        }
        return mRootView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            mIsVisiable = true;
            lazyLoad();
        } else {
            //设置以及不可见
            mIsVisiable = false;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // 判断当前fragment是否显示
        if (getUserVisibleHint()) {
            if (mIsInitialized) {
                mIsInitialized = false;
                // 加载各种数据
                loadData();
            }
        }
    }

    private void lazyLoad() {
        if (!mIsFirstLoad && !isVisible() && !mIsInitialized) {
            //如果不是第一次加载 、不是可见的、不是初始化view 则不加载数据
            return;
        }
        initData();
        initListener();
//        loadData();
        //设置已经不是第一次加载
        mIsFirstLoad = false;
    }


    /**
     * <p>
     * 获取布局Builder，主要用于自定义每个页面的progress、empty、error等View.
     * 需要自定义的页面需自行覆盖实现.
     * </p>
     *
     * @return
     */
    protected BaseLayout.Builder getLayoutBuilder() {
        BaseLayout.Builder builder = new BaseLayout.Builder(mActivity);
        builder.setClickListener(this);
        return builder;
    }

    protected boolean hasBaseLayout() {
        return false;
    }

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
            view = (V) mRootView.findViewById(viewId);
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

    @Override
    public void onClick(View v) {
        processClick(v);
    }

    /**
     * 显示toast
     *
     * @param msg      提示内容
     * @param duration 提示时长
     */
    public void showToast(String msg, int duration) {
        if (mToast == null) {
            mToast = Toast.makeText(mActivity, msg, duration);
            mToast.show();
        } else {
            mToast.setText(msg);
            mToast.setDuration(duration);
            mToast.show();
        }
    }

    /**
     * 显示短时长toast
     *
     * @param msg 提示内容
     */
    public void shortToast(String msg) {
        showToast(msg, Toast.LENGTH_SHORT);
    }

    /**
     * 显示长时长toast
     *
     * @param msg 提示内容
     */
    public void longToast(String msg) {
        showToast(msg, Toast.LENGTH_LONG);
    }

    @Override
    public void onEmptyViewClick() {
        // 如果子类需要处理空页面点击事件，则需覆盖此方法
    }

    @Override
    public void onErrorViewClick() {
        // 如果子类需要处理空页面点击事件，则需覆盖此方法
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
    //**************************snackbar相关******************************//

    public void showSnanck(String msg) {
        showSnanck(msg, false, Color.RED);
    }

    public void showSnanck(String msg, boolean hasAction) {
        showSnanck(msg, hasAction, Color.RED);
    }

    public void showSnanck(String msg, boolean hasAction, int actionColor) {
        if (snackbar == null) {
            snackbar = Snackbar.make(mRootView, msg, Snackbar.LENGTH_SHORT);
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
     * @param toActivity
     * @param action
     * @param bundle
     * @param isFinished
     */
    protected void gotoActivityForResult(Class<?> toActivity, String action, Bundle bundle, int requestCode, Bundle bundleOption, boolean isFinished) {
        Intent intent = new Intent(mActivity, toActivity);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        if (!TextUtils.isEmpty(action)) {
            intent.setAction(action);
        }
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
        startActivityForResult(intent, requestCode);
//        overrideAnim(0);
//        } else {
//            bundleOption = bundleOption == null ? ActivityOptions.makeSceneTransitionAnimation(mActivity).toBundle() : bundleOption;
//            startActivityForResult(intent, requestCode, bundleOption);
//
//        }
        if (isFinished) {
            mActivity.finish();
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

    /**
     * 点击返回的界面动画
     *
     * @param mode
     */
    private void overrideAnim(int mode) {
        switch (mode) {
            case 0:
                mActivity.overridePendingTransition(R.anim.slide_fade_right_enter, R.anim.slide_left_exit);
                break;
            case 1:
                mActivity.overridePendingTransition(R.anim.slide_fade_left_enter, R.anim.slide_right_exit);
                break;
            case 2:
                mActivity.overridePendingTransition(R.anim.slide_fade_top_enter, R.anim.slide_bottom_exit);
                break;
            case 3:
                mActivity.overridePendingTransition(R.anim.slide_fade_bottom_enter, R.anim.slide_top_exit);
                break;
            default:
        }
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


}