package com.banzhi.library.base.callback;

import android.graphics.Canvas;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.banzhi.library.base.BaseItemDragAdapter;
import com.banzhi.library.base.BaseRecyclerViewAdapter;


/**
 * <pre>
 * @author : No.1
 * @time : 2018/1/26.
 * @desciption :
 * @version :
 * </pre>
 */

public class DefaultItemTouchHelperCallback extends ItemTouchHelper.Callback {
    BaseItemDragAdapter mAdapter;

    public DefaultItemTouchHelperCallback(BaseItemDragAdapter adapter) {
        this.mAdapter = adapter;
    }

    private boolean canDrag = false;
    private boolean canSwipe = false;

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        if (isViewCreateByAdapter(viewHolder)) {//特殊位置布局不做拖拽
            return makeMovementFlags(0, 0);
        }
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        int dragFlags = 0;
        int swipeFlags = 0;
        if (layoutManager instanceof GridLayoutManager) {
            // 如果是Grid布局，则不能滑动，只能上下左右拖动
            dragFlags =
                    ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
            swipeFlags = 0;
        } else if (layoutManager instanceof LinearLayoutManager) {
            // 如果是纵向Linear布局，则能上下拖动，左右滑动
            if (((LinearLayoutManager) layoutManager).getOrientation() == LinearLayoutManager.VERTICAL) {
                dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                swipeFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
            } else {
                // 如果是横向Linear布局，则能左右拖动，上下滑动
                swipeFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                dragFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
            }
        }
        return makeMovementFlags(dragFlags, swipeFlags); //该方法指定可进行的操作
    }

    /**
     * 拖动时回调，在这里处理拖动事件
     *
     * @param viewHolder 被拖动的view
     * @param target     目标位置的view
     */
    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                          RecyclerView.ViewHolder target) {
        if (viewHolder.getItemViewType() != target.getItemViewType()) {
            return false;
        } else {
            return  mAdapter.onItemDragMoving(viewHolder, target);
        }

    }


    /**
     * 滑动时回调
     *
     * @param direction 回调方向
     */
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        if (!isViewCreateByAdapter(viewHolder)) {
            mAdapter.onItemSwiped(viewHolder);
        }
    }


    /**
     * 在这个回调中，如果返回true，表示可以触发长按拖动事件，false则表示不能
     */
    @Override
    public boolean isLongPressDragEnabled() {
        return canDrag;
    }

    /**
     * 在这个回调中，如果返回true，表示可以触发滑动事件，false表示不能
     */
    @Override
    public boolean isItemViewSwipeEnabled() {
        return canSwipe;
    }

    public void setDragEnable(boolean canDrag) {
        this.canDrag = canDrag;
    }

    public void setSwipeEnable(boolean canSwipe) {
        this.canSwipe = canSwipe;
    }


    private boolean isViewCreateByAdapter(RecyclerView.ViewHolder viewHolder) {
        int type = viewHolder.getItemViewType();
        return type == BaseRecyclerViewAdapter.HEADER_VIEW || type == BaseRecyclerViewAdapter.LOADING_VIEW
                || type == BaseRecyclerViewAdapter.FOOTER_VIEW || type == BaseRecyclerViewAdapter.EMPTY_VIEW;
    }
    /**
     * 这个方法可以判断当前是拖拽还是侧滑
     */
    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            //根据侧滑的位移来修改item的透明度
            float ALPHA_FULL = 1.0f;
            final float alpha = ALPHA_FULL - Math.abs(dX) / (float) viewHolder.itemView.getWidth();
            viewHolder.itemView.setAlpha(alpha);
            viewHolder.itemView.setTranslationX(dX);
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        //当前状态不是idel（空闲）状态时，说明当前正在拖拽或者侧滑
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            //看看这个viewHolder是否实现了onStateChangedListener接口
            if (viewHolder instanceof OnStateChangedListener) {
                OnStateChangedListener listener = (OnStateChangedListener) viewHolder;
                //回调ItemViewHolder中的onItemSelected方法来改变item的背景颜色
                listener.onItemSelected();
            }
        }
        super.onSelectedChanged(viewHolder, actionState);

    }


    /**
     * 当用户拖拽完或者侧滑完一个item时回调此方法，用来清除施加在item上的一些状态
     */
    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        if (viewHolder instanceof OnStateChangedListener) {
            OnStateChangedListener listener = (OnStateChangedListener) viewHolder;
            listener.onItemClear();
        }
    }
    public interface OnStateChangedListener{
        void onItemSelected();
        void onItemClear();

    }
}
