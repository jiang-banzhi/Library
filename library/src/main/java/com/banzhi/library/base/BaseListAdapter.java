package com.banzhi.library.base;

import android.content.Context;
import android.view.LayoutInflater;

import java.util.List;

/**
 * <pre>
 * author : No.1
 * time : 2017/6/21.
 * desc :
 * </pre>
 */

public abstract class BaseListAdapter<T> extends android.widget.BaseAdapter {
    List<T> mList;
    Context mContext;
    LayoutInflater inflater;

    public BaseListAdapter(Context context, List<T> list) {
        this.mList = list;
        this.mContext = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


}
