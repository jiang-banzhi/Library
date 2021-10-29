package com.banzhi.lib.application

import android.app.Activity
import android.app.Application
import android.content.Context
import com.banzhi.lib.utils.CrashUtils
import com.banzhi.lib.utils.LogUtils
import com.banzhi.lib.utils.Utils
import com.banzhi.lib.utils.ValidateUtils
import com.banzhi.rxhttp.RxHttp
import com.banzhi.rxhttp.interceptor.CacheInterceptor
import com.banzhi.rxhttp.interceptor.RequestInterceptor
import okhttp3.Cache
import okhttp3.logging.HttpLoggingInterceptor
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * <pre>
 * author : No.1
 * time : 2017/3/28.
 * desc :
</pre> *
 */
open abstract class App : Application() {
    /**
     * 网络请求地址
     *
     * @return
     */
    protected abstract val baseUrl: String?

    /**
     * 崩溃日志保存目录
     *
     * @return
     */
    protected abstract val crashDir: String?

    protected open fun openCrash(): Boolean {
        return true
    }

    protected open fun openRxHttp(): Boolean {
        return true
    }

    /**
     * 初始tinker
     */
    protected abstract fun init()
    override fun onCreate() {
        super.onCreate()
        init()
        context = applicationContext
        initUtils()
        intLog()
        if (openCrash()) {
            initCrash()
        }
        if (openRxHttp()) {
            RxHttp.getInstance().baseUrl(baseUrl ?: "")
                .create {
                    retryOnConnectionFailure(true)
                    addInterceptor(RequestInterceptor(null))
                    addInterceptor(CacheInterceptor(applicationContext))
                    val logInterceptor = HttpLoggingInterceptor()
                    logInterceptor.level = HttpLoggingInterceptor.Level.BODY
                    addInterceptor(logInterceptor)
                    externalCacheDir?.let { cache(Cache(it, 1024 * 1024 * 100)) }
                    readTimeout(60, TimeUnit.SECONDS)
                    writeTimeout(60, TimeUnit.SECONDS)
                    connectTimeout(60, TimeUnit.SECONDS)
                }
        }
    }

    /**
     * 初始工具类
     */
    private fun initUtils() {
        Utils.init(applicationContext)
    }

    /**
     * 初始logutils
     */
    private fun intLog() {
        val builder =
            LogUtils.Builder() //                .setLogSwitch(BuildConfig.DEBUG)// 设置log总开关，包括输出到控制台和文件，默认开
                //                .setConsoleSwitch(BuildConfig.DEBUG)// 设置是否输出到控制台开关，默认开
                .setLogSwitch(true) // 设置log总开关，包括输出到控制台和文件，默认开
                .setConsoleSwitch(true) // 设置是否输出到控制台开关，默认开
                .setGlobalTag(null) // 设置log全局标签，默认为空
                // 当全局标签不为空时，我们输出的log全部为该tag，
                // 为空时，如果传入的tag为空那就显示类名，否则显示tag
                .setLogHeadSwitch(true) // 设置log头信息开关，默认为开
                .setLog2FileSwitch(true) // 打印log时是否存到文件的开关，默认关
                .setDir("") // 当自定义路径为空时，写入应用的/cache/log/目录中
                .setBorderSwitch(true) // 输出日志是否带边框开关，默认开
                .setConsoleFilter(LogUtils.V) // log的控制台过滤器，和logcat过滤器同理，默认Verbose
                .setFileFilter(LogUtils.V) // log文件过滤器，和logcat过滤器同理，默认Verbose
    }

    private fun initCrash() {
        CrashUtils.init(crashDir)
    }

    companion object {
        var apps: MutableList<Activity?> = ArrayList()
        var context: Context? = null
            private set

        /**
         * 将activity加入栈管理
         *
         * @param activity
         */
        @JvmStatic
        fun addActivity(activity: Activity?) {
            if (null != activity && !apps.contains(activity)) {
                apps.add(activity)
            }
        }

        /**
         * 将activity移除栈管理
         *
         * @param activity
         */
        @JvmStatic
        fun removeActivity(activity: Activity?) {
            if (null != activity && apps.contains(activity)) apps.remove(activity)
        }

        /**
         * 结束指定的Activity
         */
        fun finishActivity(cls: Class<*>) {
            val it = apps.iterator()
            while (it.hasNext()) {
                val s = it.next()
                if (cls == s!!.javaClass) {
                    it.remove()
                    s.finish()
                }
            }
        }

        /**
         * 移除所有activity
         */
        fun removeAll() {
            while (ValidateUtils.isValidate(apps)) {
                val activity = apps[0]
                apps.remove(activity)
                if (activity != null && !activity.isFinishing) {
                    activity.finish()
                }
            }
        }

        /**
         * 获取当前activity
         *
         * @return 当前activity
         */
        val currentActivitly: Activity?
            get() = if (ValidateUtils.isValidate(apps)) {
                apps[apps.size - 1]
            } else null
        val stackActivitiesNum: Int
            get() = apps.size

        /**
         * 回退到指定activity
         * @param cls 指定activity
         * @param finishThis 是否销毁指定activity
         */
        fun backToAcitity(cls: Class<*>, finishThis: Boolean) {
            val it = apps.iterator()
            while (it.hasNext()) {
                val s = it.next()
                if (cls == s!!.javaClass) {
                    if (finishThis) {
                        it.remove()
                        s.finish()
                    }
                    break
                } else {
                    it.remove()
                    s.finish()
                }
            }
        }
    }
}