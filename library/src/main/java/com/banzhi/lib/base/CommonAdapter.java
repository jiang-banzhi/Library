package com.banzhi.lib.base;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * <pre>
 * author : No.1
 * time : 2017/6/21.
 * desc :
 * </pre>
 */

public abstract class CommonAdapter<T> extends BaseListAdapter<T> {
    public CommonAdapter(Context context, List list) {
        super(context, list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = getViewHolder(convertView, parent, position);
        onBindView(viewHolder, mList.get(position));
        return viewHolder.getContentView();
    }

    public abstract void onBindView(ViewHolder holder, T item);

    public abstract int onCreateView();

    protected ViewHolder getViewHolder(View convertView, ViewGroup parent, int position) {
        return ViewHolder.getViewHolder(mContext, convertView, parent, onCreateView(), position);
    }

}
