package com.banzhi.lib.base.kt

import android.content.Context
import android.os.Bundle
import androidx.lifecycle.Lifecycle
import com.banzhi.lib.base.BasePresenter
import com.banzhi.lib.base.IView
import com.uber.autodispose.AutoDispose
import com.uber.autodispose.AutoDisposeConverter
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider
import java.lang.reflect.ParameterizedType

/**
 *<pre>
 * @author : No.1
 * @time : 2019/8/16.
 * @desciption :
 * @version :
 *</pre>
 */
abstract class IBaseKtFragment<V : IView, T : BasePresenter<V>> : AbsBaseKtFragment(), IView {
    var mPresenter: T? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mPresenter = getInstance<T>(this, 1)
        if (mPresenter != null) {
            mPresenter!!.attachView(this as V)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        if (mPresenter != null)
            mPresenter!!.detachView()
    }

    override fun getContext(): Context? {
        return super.getContext()
    }

    fun <T> getInstance(o: Any, i: Int): T? {
        try {
            return ((o.javaClass
                    .genericSuperclass as ParameterizedType).actualTypeArguments[i] as Class<T>)
                    .newInstance()
        } catch (e: InstantiationException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: ClassCastException) {
            e.printStackTrace()
        } catch (e: java.lang.InstantiationException) {
            e.printStackTrace()
        }

        return null
    }

    override fun <T> bindAutoDispose(): AutoDisposeConverter<T> {
        return AutoDispose.autoDisposable(AndroidLifecycleScopeProvider
                .from(this, Lifecycle.Event.ON_DESTROY))
    }
}