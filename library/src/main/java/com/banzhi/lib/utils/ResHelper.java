package com.banzhi.lib.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.annotation.DimenRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

import java.lang.reflect.Field;

/**
 * <pre>
 * author : No.1
 * time : 2017/3/28.
 * desc :获取资源文件当中配置资源的帮助类
 * </pre>
 */

public class ResHelper {
    public static String getString(Context context, @StringRes int resId) {
        return context.getResources().getString(resId);
    }

    public static float getDimen(Context context, @DimenRes int dimenId) {
        return context.getResources().getDimension(dimenId);
    }

    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 获取电量条statusbar的高度
     *
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        int statusBarHeight = 0;
        try {
            Class clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            Field field = clazz.getField("status_bar_height");
            int id = Integer.parseInt(field.get(object).toString());
            //依据id值获取到状态栏的高度,单位为像素
            statusBarHeight = context.getResources().getDimensionPixelSize(id);
        } catch (Exception e) {
        }
        return statusBarHeight;
    }

    public static Drawable getDrawable(Context context, @DrawableRes int resId) {
        Drawable dw = context.getResources().getDrawable(resId);
        dw.setBounds(0, 0, dw.getMinimumWidth(), dw.getMinimumHeight());
        return dw;
    }
}
