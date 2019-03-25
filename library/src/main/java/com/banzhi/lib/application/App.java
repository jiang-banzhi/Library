package com.banzhi.lib.application;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import com.banzhi.lib.utils.CrashUtils;
import com.banzhi.lib.utils.LogUtils;
import com.banzhi.lib.utils.Utils;
import com.banzhi.lib.utils.ValidateUtils;
import com.banzhi.rxhttp.RxHttp;
import com.banzhi.rxhttp.interceptor.RetryInterceptor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * <pre>
 * author : No.1
 * time : 2017/3/28.
 * desc :
 * </pre>
 */

public abstract class App extends Application {
    private static List<Activity> apps = new ArrayList<>();
    private static Context sContext;

    /**
     * 网络请求地址
     *
     * @return
     */
    protected abstract String getBaseUrl();

    /**
     * 崩溃日志保存目录
     *
     * @return
     */
    protected abstract String getCrashDir();

    /**
     * token过期处理
     *
     * @return
     */
    protected abstract RetryInterceptor.TokenProxy getTokenProxy();

    /**
     * 初始tinker
     */
    protected abstract void init();

    @Override
    public void onCreate() {
        super.onCreate();
        init();
        sContext = getApplicationContext();
        initUtils();
        intLog();
        initCrash();
        RxHttp.init(getApplicationContext());
        RxHttp.getInstance(getBaseUrl())
                .setTokenProxy(getTokenProxy())
                .create();
    }

    /**
     * 初始工具类
     */
    private void initUtils() {
        Utils.init(getApplicationContext());
    }

    /**
     * 初始logutils
     */
    private void intLog() {
        LogUtils.Builder builder = new LogUtils.Builder()
//                .setLogSwitch(BuildConfig.DEBUG)// 设置log总开关，包括输出到控制台和文件，默认开
//                .setConsoleSwitch(BuildConfig.DEBUG)// 设置是否输出到控制台开关，默认开
                .setLogSwitch(true)// 设置log总开关，包括输出到控制台和文件，默认开
                .setConsoleSwitch(true)// 设置是否输出到控制台开关，默认开
                .setGlobalTag(null)// 设置log全局标签，默认为空
                // 当全局标签不为空时，我们输出的log全部为该tag，
                // 为空时，如果传入的tag为空那就显示类名，否则显示tag
                .setLogHeadSwitch(true)// 设置log头信息开关，默认为开
                .setLog2FileSwitch(true)// 打印log时是否存到文件的开关，默认关
                .setDir("")// 当自定义路径为空时，写入应用的/cache/log/目录中
                .setBorderSwitch(true)// 输出日志是否带边框开关，默认开
                .setConsoleFilter(LogUtils.V)// log的控制台过滤器，和logcat过滤器同理，默认Verbose
                .setFileFilter(LogUtils.V);// log文件过滤器，和logcat过滤器同理，默认Verbose
    }

    private void initCrash() {
        CrashUtils.init(getCrashDir());
    }


    public static Context getContext() {
        return sContext;
    }

    /**
     * 将activity加入栈管理
     *
     * @param activity
     */
    public static void addActivity(Activity activity) {
        if (null != activity && !apps.contains(activity)) {
            apps.add(activity);
        }
    }

    /**
     * 将activity移除栈管理
     *
     * @param activity
     */
    public static void removeActivity(Activity activity) {
        if (null != activity && apps.contains(activity))
            apps.remove(activity);
    }

    /**
     * 结束指定的Activity
     */
    public static void finishActivity(Class<?> cls) {
        Iterator<Activity> it = apps.iterator();
        while (it.hasNext()) {
            Activity s = it.next();
            if (cls.equals(s.getClass())) {
                it.remove();
                s.finish();
            }
        }
    }

    /**
     * 移除所有activity
     */
    public static void removeAll() {
        while (ValidateUtils.isValidate(apps)) {
            Activity activity = apps.get(0);
            apps.remove(activity);
            if (activity != null && !activity.isFinishing()) {
                activity.finish();
            }
        }
    }

    /**
     * 获取当前activity
     *
     * @return 当前activity
     */
    public static Activity getCurrentActivitly() {
        if (ValidateUtils.isValidate(apps)) {
            return apps.get(apps.size() - 1);
        }
        return null;
    }

    public static int getStackActivitiesNum() {
        return apps.size();
    }
}
