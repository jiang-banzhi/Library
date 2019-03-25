package com.banzhi.sample;

import com.banzhi.lib.application.App;

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
    protected String getFileUrl() {
        return null;
    }

    @Override
    protected String getCrashDir() {
        return null;
    }

    @Override
    protected void init() {

    }
}
