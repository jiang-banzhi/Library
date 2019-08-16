package com.banzhi.lib.base.kt

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.annotation.StringRes
import android.support.design.widget.AppBarLayout
import android.support.design.widget.AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS
import android.support.design.widget.AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.util.Log
import android.util.SparseArray
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.banzhi.lib.application.App
import com.banzhi.lib.utils.BarUtils
import com.banzhi.lib.widget.view.BaseLayout
import com.banzhi.library.R

/**
 *<pre>
 * @author : No.1
 * @time : 2019/8/14.
 * @desciption :
 * @version :
 *</pre>
 */
abstract class AbsBaseKtActivity : AppCompatActivity(), View.OnClickListener, BaseLayout.OnBaseLayoutClickListener {
    var TAG = "TAG"
    protected var mBaseLayout: BaseLayout? = null
    private var mViews: SparseArray<View>? = null
    private var snackbar: Snackbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.addActivity(this)
        mViews = SparseArray()
        iniActionbarColor()
        setContentView(getLayoutId())
        init(savedInstanceState)
    }


    override fun onDestroy() {
        super.onDestroy()
        App.removeActivity(this)
    }

    /**
     * 初始状态汇总
     */
    protected fun init(savedInstanceState: Bundle?) {
        Log.e(TAG, "********************************************************")
        Log.e(TAG, "** init:当前activity==> " + this.javaClass.simpleName)
        Log.e(TAG, "********************************************************")
        showContentView()
        if (null != intent) {
            handleIntent(intent)
        }
        initView(savedInstanceState)
        initData()
        initFragment(savedInstanceState)
        initListener()
    }

    protected open fun initFragment(savedInstanceState: Bundle?) {}


    /**
     * 获取数据传递intent
     *
     * @param intent
     */
    protected open fun handleIntent(intent: Intent) {


    }

    override fun onClick(v: View) {
        processClick(v)
    }


    /**
     * 获取activity的layout
     *
     * @return layoutid
     */
    protected abstract fun getLayoutId(): Int

    /**
     * 初始view
     */
    protected abstract fun initView(savedInstanceState: Bundle?)

    /**
     * 设置监听
     */
    protected abstract fun initListener()

    /**
     * 初始数据
     */
    protected abstract fun initData()

    /**
     * onclick点击事件
     *
     * @param v
     */
    protected abstract fun processClick(v: View)

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
            view = findViewById<View>(viewId) as V
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

    /*
     * ************Fragement相关方法************************************************
     *
     */
    private var currentFragment: Fragment? = null
    internal var isFlg: Boolean = false//使用replace方式添加fragment

    /**
     * Fragment替换(当前destrory,新的create)
     */
    fun fragmentReplace(containerViewId: Int, toFragment: Fragment, backStack: Boolean) {
        isFlg = true
        val manager = supportFragmentManager
        val transaction = manager.beginTransaction()
        val toClassName = toFragment.javaClass.simpleName
        if (manager.findFragmentByTag(toClassName) == null) {//没有添加过
            transaction.replace(containerViewId, toFragment, toClassName)
            if (backStack) {
                transaction.addToBackStack(toClassName)
            }
            transaction.commit()
        }
    }


    /**
     * Fragment替换(核心为隐藏当前的,显示现在的,用过的将不会destrory与create)
     *
     * @target 容器id
     */
    fun smartFragmentReplace(target: Int, toFragment: Fragment) {
        smartFragmentReplace(target, toFragment, true)
    }

    /**
     * Fragment替换(核心为隐藏当前的,显示现在的,用过的将不会destrory与create)
     *
     * @target 容器id
     */
    fun smartFragmentReplace(target: Int, toFragment: Fragment, hideCurrentFragment: Boolean) {
        val manager = supportFragmentManager
        val transaction = manager.beginTransaction()
        transaction.setCustomAnimations(R.anim.slide_fade_right_enter, R.anim.slide_left_exit,
                R.anim.slide_fade_left_enter, R.anim.slide_right_exit)//自定义动画
        // 如有当前在使用的->隐藏当前的
        if (currentFragment != null) {
            if (hideCurrentFragment) {
                transaction.hide(currentFragment)
            }
        }
        val toClassName = toFragment.javaClass.simpleName
        // toFragment之前添加使用过->显示出来
        if (manager.findFragmentByTag(toClassName) != null) {
            transaction.show(toFragment)
        } else {// toFragment还没添加使用过->添加上去
            transaction.add(target, toFragment, toClassName)
        }
        transaction.commit()
        // toFragment更新为当前的
        currentFragment = toFragment
    }
    //**********************状态栏************************/

    /**
     * 设置状态栏颜色
     */
    protected fun iniActionbarColor() {
        BarUtils.setTransparentStatusBar(this)
    }

    protected fun setScrollFlg(@AppBarLayout.LayoutParams.ScrollFlags flags: Int) {
        val appbar = findViewById(R.id.appbar) as AppBarLayout
        val mParams = appbar.getChildAt(0).layoutParams as AppBarLayout.LayoutParams
        mParams.scrollFlags = flags//的时候AppBarLayout下的toolbar就不会随着滚动条折叠

    }

    /**
     * 设置toolbar滚动影藏
     */
    protected fun setScrollFlg() {
        setScrollFlg(SCROLL_FLAG_SCROLL or SCROLL_FLAG_ENTER_ALWAYS)
    }

    override fun setContentView(@LayoutRes layoutResID: Int) {
        addLayoutView(LayoutInflater.from(this).inflate(layoutResID, null))
    }

    override fun setContentView(view: View) {
        addLayoutView(view)
    }

    /**
     * 构建新的contentview
     *
     * @param view
     */
    private fun addLayoutView(view: View?) {
        var view = view
        view = buildContentView(view)
        view = buildToolbarView(view)
        super.setContentView(view)

    }


    /**
     * @param view
     * @return
     */
    private fun buildToolbarView(view: View?): View? {
        if (hasToolbarLayout()) {
            val groupView = LayoutInflater.from(this).inflate(R.layout.base_view_base_layout, null)
            mToolbar = groupView.findViewById(R.id.toolbar)
            setSupportActionBar(mToolbar)
            if (isTitleCenter()) {
                supportActionBar!!.setDisplayShowTitleEnabled(false)
                mToolbar?.addView(initTitleView())
            }
            val viewGroup = groupView.findViewById(R.id.content_container) as ViewGroup
            viewGroup.addView(view)
            return groupView
        }
        return view
    }


    /**
     * @return
     */
    protected open fun initTitleView(): View? {
        if (mTitleView == null && isTitleCenter()) {
            mTitleView = TextView(this)
            mTitleView!!.gravity = Gravity.CENTER
            mTitleView!!.setTextColor(Color.WHITE)
            mTitleView!!.setSingleLine()
            mTitleView!!.ellipsize = TextUtils.TruncateAt.END
            val params = Toolbar.LayoutParams(Toolbar.LayoutParams.WRAP_CONTENT, Toolbar.LayoutParams.MATCH_PARENT)
            params.gravity = Gravity.CENTER
            mTitleView!!.layoutParams = params
        }
        return mTitleView
    }

    var mToolbar: Toolbar? = null

    private var mTitleView: TextView? = null

    override fun onTitleChanged(title: CharSequence, color: Int) {
        super.onTitleChanged(title, color)
        if (mTitleView != null && isTitleCenter()) {
            mTitleView!!.text = title
        }
    }

    /**
     * 设置子标题
     *
     * @param title
     */
    fun setSubTitle(title: CharSequence) {
        supportActionBar!!.subtitle = title
    }

    /**
     * 设置子标题
     *
     * @param resId
     */
    fun setSubTitle(@StringRes resId: Int) {
        supportActionBar!!.setSubtitle(resId)
    }

    /**
     * @param view
     * @return
     */
    private fun buildContentView(view: View?): View? {
        if (hasBaseLayout()) {
            val builder = BaseLayout.Builder(this)
            builder.setClickListener(this)
            if (view != null) {
                builder.setContentView(view)
            }
            mBaseLayout = builder.build()
            return mBaseLayout
        }
        return view
    }


    /**
     * 是否包含共用toolbar.
     *
     * @return
     */
    protected open fun hasToolbarLayout(): Boolean {
        return true
    }

    /**
     * 是否包含基本view如progress、empty、error等.
     *
     * @return
     */
    protected open fun hasBaseLayout(): Boolean {
        return true
    }

    /**
     * toolbar title是否居中
     *
     * @return
     */
    protected open fun isTitleCenter(): Boolean {
        return false
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

    override fun onErrorViewClick() {
        // 如果子类需要处理空页面点击事件，则需覆盖此方法
    }

    override fun onEmptyViewClick() {
        // 如果子类需要处理空页面点击事件，则需覆盖此方法
    }

    //**************************snackbar相关******************************//


    fun showShanck(msg: String) {
        showShanck(msg, false, R.color.baseColorAccent)
    }

    fun showShanck(msg: String, hasAction: Boolean) {
        showShanck(msg, hasAction, R.color.baseColorAccent)
    }

    fun showShanck(msg: String, hasAction: Boolean, actionColor: Int) {
        if (snackbar == null) {
            snackbar = Snackbar.make(mBaseLayout!!, msg, Snackbar.LENGTH_SHORT)
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

    protected fun gotoActivity(toActivity: Class<*>, bundle: Bundle, isFinished: Boolean) {
        gotoActivity(toActivity, null, bundle, isFinished)
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
     * @param toActivity   目标界面
     * @param action
     * @param bundle       传递的数据
     * @param requestCode  请求码
     * @param bundleOption 转场动画
     * @param isFinished   是否销毁当前界面
     */
    protected fun gotoActivityForResult(toActivity: Class<*>, action: String?, bundle: Bundle?, requestCode: Int, bundleOption: Bundle?, isFinished: Boolean) {
        val intent = Intent(this, toActivity)
        if (bundle != null) {
            intent.putExtras(bundle)
        }
        if (!TextUtils.isEmpty(action)) {
            intent.action = action
        }
        startActivityForResult(intent, requestCode)
        overrideAnim(0)
        if (isFinished) {
            finish()
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


    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 1) {
            onFragmentBack()
            supportFragmentManager.popBackStack()
        } else {
            randomExit((Math.random() * 10 % 4).toInt())
            finish()
        }

    }

    protected open fun onFragmentBack() {

    }

    /**
     * 随机退出动画
     */
    protected fun randomExit(rand: Int) {
        when (rand) {
            0 -> overridePendingTransition(R.anim.slide_fade_right_enter, R.anim.slide_left_exit)
            1 -> overridePendingTransition(R.anim.slide_fade_left_enter, R.anim.slide_right_exit)
            2 -> overridePendingTransition(R.anim.slide_fade_top_enter, R.anim.slide_bottom_exit)
            3 -> overridePendingTransition(R.anim.slide_fade_bottom_enter, R.anim.slide_top_exit)
            else -> {
            }
        }
    }

    /**
     * 点击返回的界面动画
     *
     * @param mode
     */
    private fun overrideAnim(mode: Int) {
        when (mode) {
            0 -> overridePendingTransition(R.anim.slide_fade_right_enter, R.anim.slide_left_exit)
            1 -> overridePendingTransition(R.anim.slide_fade_left_enter, R.anim.slide_right_exit)
            2 -> overridePendingTransition(R.anim.slide_fade_top_enter, R.anim.slide_bottom_exit)
            3 -> overridePendingTransition(R.anim.slide_fade_bottom_enter, R.anim.slide_top_exit)
            else -> {
            }
        }
    }


}