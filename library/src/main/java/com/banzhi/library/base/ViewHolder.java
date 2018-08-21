package com.banzhi.library.base;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * <pre>
 * author : No.1
 * time : 2017/6/21.
 * desc :
 * </pre>
 */

public class ViewHolder {
    SparseArray<View> views = new SparseArray<>();
    View mContentView;

    private ViewHolder(Context context, ViewGroup parent, int layoutId, int position) {
        mContentView = LayoutInflater.from(context).inflate(layoutId, parent, false);
        mContentView.setTag(this);
    }

    public static ViewHolder getViewHolder(Context context, View convertView, ViewGroup parent, @LayoutRes int layoutId, int position) {
        if (convertView == null) {
            return new ViewHolder(context, parent, layoutId, position);
        }
        return (ViewHolder) convertView.getTag();
    }

    public View getContentView() {
        return mContentView;
    }

    public <V extends View> V getView(@IdRes int resId) {
        View view = views.get(resId);
        if (view == null) {
            view = mContentView.findViewById(resId);
            views.put(resId, view);
        }
        return (V) view;
    }

    public ViewHolder setText(@IdRes int resId, String values) {
        TextView textView = getView(resId);
        if (!TextUtils.isEmpty(values))
            textView.setText(values);
        return this;
    }

    public ViewHolder setText(@IdRes int idRes, @StringRes int resId) {
        TextView textView = getView(idRes);
        textView.setText(resId);
        return this;
    }

    /**
     * 为ImageView设置图片
     *
     * @param viewId
     * @param drawableId
     * @return
     */
    public ViewHolder setImageResource(@IdRes int viewId, @DrawableRes int drawableId) {
        ImageView view = getView(viewId);
        view.setImageResource(drawableId);
        return this;
    }

    public ViewHolder setImageBitmap(@IdRes int viewId, Bitmap bitmap) {
        ImageView view = getView(viewId);
        if (bitmap != null)
            view.setImageBitmap(bitmap);
        return this;
    }

    /**
     * 设置开/关两种状态的按钮
     *
     * @param viewId
     * @param checked
     * @return
     */
    public ViewHolder setCheck(@IdRes int viewId, boolean checked) {
        View view = getView(viewId);
        if (view instanceof CompoundButton) {
            ((CompoundButton) view).setChecked(checked);
        } else if (view instanceof CheckedTextView) {
            ((CheckedTextView) view).setChecked(checked);
        }
        return this;
    }

}
