package com.banzhi.sample;

import com.banzhi.lib.application.App;
import com.banzhi.lib.utils.CrashUtils;

/**
 * <pre>
 * @author : No.1
 * @time : 2019/3/25.
 * @desciption :
 * @version :
 * </pre>
 */

public class MyAppLication extends App {
    @Override
    protected String getBaseUrl() {
        return "http://www.baidu.com";
    }

    @Override
    protected String getCrashDir() {
        CrashUtils.initHandleCrashProxy(new HandleCrashManager());
        return null;
    }

    @Override
    protected void init() {

    }


}
