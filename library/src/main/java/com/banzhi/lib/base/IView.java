package com.banzhi.lib.base;

import android.content.Context;

import com.uber.autodispose.AutoDisposeConverter;

/**
 * Created by redli on 2017/3/13.
 */

public interface IView {
     Context getContext();
     /**
      * 绑定Android生命周期 防止RxJava内存泄漏
      *
      * @param <T>
      * @return
      */
     <T> AutoDisposeConverter<T> bindAutoDispose();
}
