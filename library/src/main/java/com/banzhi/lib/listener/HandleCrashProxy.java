package com.banzhi.lib.listener;

/**
 * <pre>
 * @author : jiang
 * @time : 2021/3/2.
 * @desciption :
 * @version :
 * </pre>
 */
public interface HandleCrashProxy {

    void handleCrash(String extra, Throwable e);
}
