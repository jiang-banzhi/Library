package com.banzhi.library.widget.helper;

import android.support.v7.widget.helper.ItemTouchHelper;

import com.banzhi.library.base.callback.DefaultItemTouchHelperCallback;


/**
 * <pre>
 * @author : No.1
 * @time : 2018/1/26.
 * @desciption :
 * @version :
 * </pre>
 */

public class DefaultItemTouchHelper extends MyItemTouchHelper {
    DefaultItemTouchHelperCallback itemTouchHelperCallback;


    public DefaultItemTouchHelper(ItemTouchHelper.Callback callback) {
        super(callback);
        itemTouchHelperCallback = (DefaultItemTouchHelperCallback) getCallback();
    }

    /**
     * 设置是否可以被拖拽
     *
     * @param canDrag 是true，否false
     */
    public void setDragEnable(boolean canDrag) {
        itemTouchHelperCallback.setDragEnable(canDrag);
    }

    /**
     * 设置是否可以被滑动
     *
     * @param canSwipe 是true，否false
     */
    public void setSwipeEnable(boolean canSwipe) {
        itemTouchHelperCallback.setSwipeEnable(canSwipe);
    }
}
