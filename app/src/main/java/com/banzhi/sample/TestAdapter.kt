package com.banzhi.sample

import com.banzhi.lib.base.BaseRecyclerViewAdapter
import com.banzhi.lib.base.BaseViewHolder

/**
 *<pre>
 * @author : No.1
 * @time : 2019/8/14.
 * @desciption :
 * @version :
 *</pre>
 */
class TestAdapter(datas:List<TestBean>) : BaseRecyclerViewAdapter<TestBean, BaseViewHolder>(datas) {



    override fun bindView(holder: BaseViewHolder?, t: TestBean?, position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getLayoutId(viewType: Int): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}