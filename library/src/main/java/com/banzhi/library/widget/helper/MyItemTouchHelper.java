package com.banzhi.library.widget.helper;

import android.support.v7.widget.helper.ItemTouchHelper;

import java.lang.reflect.Field;

/**
 * <pre>
 * @author : No.1
 * @time : 2018/1/26.
 * @desciption :
 * @version :
 * </pre>
 */

public class MyItemTouchHelper extends ItemTouchHelper {
    /**
     * Creates an ItemTouchHelper that will work with the given Callback.
     * <p>
     * You can attach ItemTouchHelper to a RecyclerView via
     * {@link #attachToRecyclerView(RecyclerView)}. Upon attaching, it will add an item decoration,
     * an onItemTouchListener and a Child attach / detach listener to the RecyclerView.
     *
     * @param callback The Callback which controls the behavior of this touch helper.
     */
    public MyItemTouchHelper(Callback callback) {
        super(callback);
    }
    public Callback getCallback() {
        Class targetClass = this.getClass().getSuperclass().getSuperclass();
        // YourSuperClass 替换为实际的父类名字
        ItemTouchHelper superInst = (ItemTouchHelper) targetClass.cast(this);
        Field field;
        try {
            field = targetClass.getDeclaredField("mCallback");
            //修改访问限制
            field.setAccessible(true);
            // superInst 为 null 可以获取静态成员
            // 非 null 访问实例成员
            return (Callback) field.get(superInst);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
