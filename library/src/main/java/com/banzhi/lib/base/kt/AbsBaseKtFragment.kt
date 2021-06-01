package com.banzhi.lib.base.kt

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.banzhi.lib.base.AbsBaseActivity
import com.banzhi.lib.widget.view.BaseLayout
import com.banzhi.library.R
import com.google.android.material.snackbar.Snackbar

/**
 *<pre>
 * @author : No.1
 * @time : 2019/8/16.
 * @desciption :
 * @version :
 *</pre>
 */
abstract class AbsBaseKtFragment : Fragment(),BaseLayout.OnBaseLayoutClickListener,View.OnClickListener {

    protected var mActivity: AbsBaseActivity? = null

    private var mViews: SparseArray<View>? = null
    //根布局
    protected var mRootView: View? = null
    //baselayout
    protected var mBaseLayout: BaseLayout? = null
    //是否是第一次加载
    private var mIsFirstLoad = true
    //是否已初始化
    private var mIsInitialized = false
    //是否可见
    private var mIsVisiable = false
    //是否已销毁
    private var mIsDestroyed: Boolean = false

    internal var snackbar: Snackbar? = null

    /**
     * 需要加载的布局
     *
     * @return layoutid
     */
    protected abstract fun getLayoutId(): Int

    /**
     * 初始view
     */
    protected abstract fun initView()

    /**
     * 初始监听
     */
    protected abstract fun initListener()

    /**
     * 初始数据
     */
    protected abstract fun initData()

    /**
     * 加载数据
     */
    protected abstract fun loadData()

    /**
     * onclick点击事件
     *
     * @param v
     */
    protected abstract fun processClick(v: View)

    override fun onAttach(activity: Activity?) {
        mActivity = activity as AbsBaseActivity?
        super.onAttach(activity)
    }

    override fun onDetach() {
        mActivity = null
        super.onDetach()
    }

    override fun onDestroy() {
        mActivity = null
        super.onDestroy()

    }

    private val STATE_SAVE_IS_HIDDEN = "STATE_SAVE_IS_HIDDEN"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            val isSupportHidden = savedInstanceState.getBoolean(STATE_SAVE_IS_HIDDEN)
            val ft = fragmentManager?.beginTransaction()
            if (isSupportHidden) {
                ft?.hide(this)
            } else {
                ft?.show(this)
            }
            ft?.commit()
        }
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState!!.putBoolean(STATE_SAVE_IS_HIDDEN, isHidden)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mIsDestroyed = false//设置未销毁
        mViews = SparseArray()
        if (mRootView == null) {//第一次加载
            val view = inflater!!.inflate(getLayoutId(), container, false)
            mRootView = view
            if (hasBaseLayout()) {
                val builder = getLayoutBuilder()
                if (builder != null) {
                    mBaseLayout = builder.setContentView(mRootView!!).build()
                    mRootView = mBaseLayout
                }
            }
            initView()
            mIsInitialized = true//设置为已加载
            lazyLoad()
        } else {
            val localViewGroup = mRootView!!.parent as ViewGroup
            localViewGroup?.removeView(mRootView)
        }
        return mRootView
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            mIsVisiable = true
            lazyLoad()
        } else {
            //设置以及不可见
            mIsVisiable = false
        }
    }

    override fun onResume() {
        super.onResume()
        // 判断当前fragment是否显示
        if (userVisibleHint) {
            if (mIsInitialized) {
                mIsInitialized = false
                // 加载各种数据
                loadData()
            }
        }
    }

    private fun lazyLoad() {
        if (!mIsFirstLoad && !isVisible && !mIsInitialized) {
            //如果不是第一次加载 、不是可见的、不是初始化view 则不加载数据
            return
        }
        initData()
        initListener()
        //设置已经不是第一次加载
        mIsFirstLoad = false
    }


    /**
     *
     *
     * 获取布局Builder，主要用于自定义每个页面的progress、empty、error等View.
     * 需要自定义的页面需自行覆盖实现.
     *
     *
     * @return
     */
    protected fun getLayoutBuilder(): BaseLayout.Builder {
        val builder = BaseLayout.Builder(mActivity)
        builder.setClickListener(this)
        return builder
    }

    protected fun hasBaseLayout(): Boolean {
        return false
    }

    /**
     * 通过id找到view 减少findviewbyid
     *
     * @param viewId viewid
     * @param <V>    view
     * @return view
    </V> */
    fun <V : View> findView(viewId: Int): V? {
        var view: V? = mViews!!.get(viewId) as V
        if (view == null) {
            view = mRootView!!.findViewById<View>(viewId) as V
            mViews!!.put(viewId, view)
        }
        return view
    }

    /**
     * view设置onclick事件
     *
     * @param view 点击的view
     */
    fun <V : View> setOnClick(view: View) {
        view.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        processClick(v)
    }


    override fun onEmptyViewClick() {
        // 如果子类需要处理空页面点击事件，则需覆盖此方法
    }

    override fun onErrorViewClick() {
        // 如果子类需要处理空页面点击事件，则需覆盖此方法
    }

    /**
     * Show empty view when the data of current page is null.
     */
    fun showEmptyView() {
        if (null != mBaseLayout) {
            mBaseLayout!!.showEmptyView()
        }
    }

    /**
     * Show error view when the request of current page is failed.
     */
    fun showErrorView() {
        if (null != mBaseLayout) {
            mBaseLayout!!.showErrorView()
        }
    }

    /**
     * Show progress view when request data first come in the page.
     */
    fun showProgressView() {
        if (null != mBaseLayout) {
            mBaseLayout!!.showLoadingView()
        }
    }

    /**
     * Show content view of current page.
     */
    fun showContentView() {
        if (null != mBaseLayout) {
            mBaseLayout!!.showContentView()
        }
    }
    //**************************snackbar相关******************************//

    fun showSnanck(msg: String) {
        showSnanck(msg, false, Color.RED)
    }

    fun showSnanck(msg: String, hasAction: Boolean) {
        showSnanck(msg, hasAction, Color.RED)
    }

    fun showSnanck(msg: String, hasAction: Boolean, actionColor: Int) {
        if (snackbar == null) {
            snackbar = Snackbar.make(mRootView!!, msg, Snackbar.LENGTH_SHORT)
            if (hasAction) {
                snackbar!!.setActionTextColor(resources.getColor(actionColor))
                snackbar!!.setAction("确定") { snackbar!!.dismiss() }
            }
            snackbar!!.show()
        } else {
            snackbar!!.setText(msg)
            snackbar!!.show()
        }

    }

    //**************************页面跳转******************************//

    /**
     * 页面跳转
     *
     * @param toActivity 目标页面
     */
    protected fun gotoActivity(toActivity: Class<*>) {
        gotoActivity(toActivity, false)
    }

    /**
     * 页面跳转 销毁当前
     *
     * @param toActivity 目标页面
     * @param isFinished 是否销毁
     */
    protected fun gotoActivity(toActivity: Class<*>, isFinished: Boolean) {
        gotoActivity(toActivity, null, null, isFinished)
    }

    protected fun gotoActivity(toActivity: Class<*>, action: String, bundle: Bundle) {
        gotoActivity(toActivity, action, bundle, false)
    }

    protected fun gotoActivity(toActivity: Class<*>, bundle: Bundle) {
        gotoActivity(toActivity, null, bundle, false)
    }

    protected fun gotoActivity(toActivity: Class<*>, action: String) {
        gotoActivity(toActivity, action, null, false)
    }

    /**
     * @param toActivity 目标页面
     * @param action
     * @param bundle     序列化数据
     * @param isFinished 是否销毁
     */
    protected fun gotoActivity(toActivity: Class<*>, action: String?, bundle: Bundle?, isFinished: Boolean) {
        gotoActivityForResult(toActivity, action, bundle, -1, null, isFinished)
    }


    /**
     * @param toActivity   目标页面
     * @param action
     * @param bundle       序列化数据
     * @param isFinished   是否销毁
     * @param bundleOption 转场动画
     */
    protected fun gotoActivity(toActivity: Class<*>, action: String, bundle: Bundle, bundleOption: Bundle, isFinished: Boolean) {
        gotoActivityForResult(toActivity, action, bundle, -1, bundleOption, isFinished)
    }

    /**
     * @param toActivity
     * @param action
     * @param bundle
     * @param isFinished
     */
    protected fun gotoActivityForResult(toActivity: Class<*>, action: String?, bundle: Bundle?, requestCode: Int, bundleOption: Bundle?, isFinished: Boolean) {
        val intent = Intent(mActivity, toActivity)
        if (bundle != null) {
            intent.putExtras(bundle)
        }
        if (!TextUtils.isEmpty(action)) {
            intent.action = action
        }
        startActivityForResult(intent, requestCode)
        overrideAnim(0)
        if (isFinished) {
            mActivity!!.finish()
        }
    }

    protected fun gotoActivityForResult(toActivity: Class<*>, action: String, bundle: Bundle, requestCode: Int) {
        gotoActivityForResult(toActivity, action, bundle, requestCode, null, false)
    }

    protected fun gotoActivityForResult(toActivity: Class<*>, action: String, bundle: Bundle, requestCode: Int, bundleOption: Bundle) {
        gotoActivityForResult(toActivity, action, bundle, requestCode, bundleOption, false)
    }

    protected fun gotoActivityForResult(toActivity: Class<*>, bundle: Bundle, requestCode: Int) {
        gotoActivityForResult(toActivity, null, bundle, requestCode, null, false)
    }

    protected fun gotoActivityForResult(toActivity: Class<*>, bundle: Bundle, requestCode: Int, bundleOption: Bundle) {
        gotoActivityForResult(toActivity, null, bundle, requestCode, bundleOption, false)
    }


    protected fun gotoActivityForResult(toActivity: Class<*>, action: String, requestCode: Int) {
        gotoActivityForResult(toActivity, action, null, requestCode, null, false)
    }

    protected fun gotoActivityForResult(toActivity: Class<*>, action: String, requestCode: Int, bundleOption: Bundle) {
        gotoActivityForResult(toActivity, action, null, requestCode, bundleOption, false)
    }

    protected fun gotoActivityForResult(toActivity: Class<*>, requestCode: Int) {
        gotoActivityForResult(toActivity, null, null, requestCode, null, false)
    }

    protected fun gotoActivityForResult(toActivity: Class<*>, requestCode: Int, bundleOption: Bundle) {
        gotoActivityForResult(toActivity, null, null, requestCode, bundleOption, false)
    }

    /**
     * 点击返回的界面动画
     *
     * @param mode
     */
    private fun overrideAnim(mode: Int) {
        when (mode) {
            0 -> mActivity!!.overridePendingTransition(R.anim.slide_fade_right_enter, R.anim.slide_left_exit)
            1 -> mActivity!!.overridePendingTransition(R.anim.slide_fade_left_enter, R.anim.slide_right_exit)
            2 -> mActivity!!.overridePendingTransition(R.anim.slide_fade_top_enter, R.anim.slide_bottom_exit)
            3 -> mActivity!!.overridePendingTransition(R.anim.slide_fade_bottom_enter, R.anim.slide_top_exit)
        }
    }

}