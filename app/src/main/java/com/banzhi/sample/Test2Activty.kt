package com.banzhi.sample

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.banzhi.lib.base.IBaseActivity

/**
 *<pre>
 * @author : No.1
 * @time : 2019/8/14.
 * @desciption :
 * @version :
 *</pre>
 */
class Test2Activty : IBaseActivity<TestView, TestPresenter>(), TestView {
    override fun handleIntent(intent: Intent?) {
        super.handleIntent(intent)
    }

    override fun initFragment(savedInstanceState: Bundle?) {
        super.initFragment(savedInstanceState)
    }
    override fun getLayoutId(): Int {
        mPresenter.test()
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun initView(savedInstanceState: Bundle?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun initListener() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun initData() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun processClick(v: View?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}