package com.banzhi.library.base;

import android.support.v7.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

/**
 * <pre>
 * @author : No.1
 * @time : 2018/1/26.
 * @desciption :
 * @version :
 * </pre>
 */

public abstract class BaseItemDragAdapter<T, V extends BaseViewHolder> extends BaseRecyclerViewAdapter<T, V> {

    OnItemTouchCallbackListener onItemTouchCallbackListener;

    public BaseItemDragAdapter(List<T> datas) {
        super(datas);
    }


    public void setOnItemTouchCallbackListener(OnItemTouchCallbackListener onItemTouchCallbackListener) {
        this.onItemTouchCallbackListener = onItemTouchCallbackListener;
    }

    public int getViewHolderPosition(RecyclerView.ViewHolder viewHolder) {
        return viewHolder.getAdapterPosition() - getHeaderLayoutCount();
    }

    /**
     * 拖拽
     *
     * @param source
     * @param target
     * @return
     */
    public boolean onItemDragMoving(RecyclerView.ViewHolder source, RecyclerView.ViewHolder target) {
        if (onItemTouchCallbackListener != null) {
            int from = getViewHolderPosition(source);
            int to = getViewHolderPosition(target);
            // 更换数据源中的数据Item的位置
            Collections.swap(mDatas, from, to);
            // 更新UI中的Item的位置，主要是给用户看到交互效果
            notifyItemMoved(from, to);
            return onItemTouchCallbackListener.onMove(from, to);
        }
        return false;
    }

    /**
     * 滑动
     *
     * @param viewHolder
     */
    public void onItemSwiped(RecyclerView.ViewHolder viewHolder) {
        if (onItemTouchCallbackListener != null) {
            int po = getViewHolderPosition(viewHolder);
            onItemTouchCallbackListener.onSwiped(po);
            removeItem(po);
        }
    }



    public interface OnItemTouchCallbackListener {
        /**
         * 当某个Item被滑动删除的时候
         *
         * @param adapterPosition item的position
         */
        void onSwiped(int adapterPosition);

        /**
         * 当两个Item位置互换的时候被回调
         *
         * @param srcPosition    拖拽的item的position
         * @param targetPosition 目的地的Item的position
         * @return 开发者处理了操作应该返回true，开发者没有处理就返回false
         */
        boolean onMove(int srcPosition, int targetPosition);
    }


}
