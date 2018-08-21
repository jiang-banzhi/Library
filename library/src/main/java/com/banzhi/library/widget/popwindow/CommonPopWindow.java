package com.banzhi.library.widget.popwindow;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;
import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;

import com.banzhi.library.utils.ScreenUtils;


/**
 * <pre>
 * author : No.1
 * time : 2017/7/24.
 * desc :
 * </pre>
 */

public class CommonPopWindow extends PopupWindow {
    PopupController controller;
    View mContentView;
    Context mContext;
    float transparencyShow;//显示时的透明度
    float defaultTran = 1f;//

    private CommonPopWindow(PopupController controller) {
        this.controller = controller;
        this.mContentView = controller.mContentView;
        mContext = mContentView.getContext();
        setContentView(mContentView);
        setOutsideTouchable(controller.touchable);
        setFocusable(controller.focusable);
        setBackgroundDrawable(controller.mDrawable);
        setWidth(controller.mWidth);
        setHeight(controller.mHeight);
        if (controller.isShowAnim) {
            setAnimationStyle(controller.animation);
        }
        transparencyShow = controller.transparencyShow;
    }

    /**
     * 更改透明度
     * pop显示影藏时修改window背景
     *
     * @param transparency
     */
    public void updateAtributes(float transparency) {
        WindowManager.LayoutParams lp = ((Activity) mContext).getWindow().getAttributes();
        lp.alpha = transparency;
        ((Activity) mContext).getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        ((Activity) mContext).getWindow().setAttributes(lp);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        updateAtributes(defaultTran);
    }

    @Override
    public void showAsDropDown(View anchor) {
        updateAtributes(transparencyShow);
        super.showAsDropDown(anchor);
    }

    @Override
    public void showAsDropDown(View anchor, int xoff, int yoff) {
        updateAtributes(transparencyShow);
        super.showAsDropDown(anchor, xoff, yoff);
    }

    @Override
    public void showAsDropDown(View anchor, int xoff, int yoff, int gravity) {
        updateAtributes(transparencyShow);
        super.showAsDropDown(anchor, xoff, yoff, gravity);
    }

    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        updateAtributes(transparencyShow);
        super.showAtLocation(parent, gravity, x, y);
    }


    public static class Builder {
        Context mContext;
        View mPopView;
        int mWidth;
        int mHeight;
        boolean mTouchable = true;
        boolean mFocusable = true;
        Drawable mDrawable;
        int mAnimation;
        boolean isShowAnimation;
        float transparencyShow = 0.7f;

        public Builder(Context mContext) {
            this.mContext = mContext;
        }

        /**
         * 设置布局
         *
         * @param popView
         * @return
         */
        public Builder setView(View popView) {
            this.mPopView = popView;
            mPopView.setFocusable(true);
            mPopView.setFocusableInTouchMode(true);
            return this;
        }

        /**
         * 设置布局
         *
         * @param layoutId
         * @return
         */
        public Builder setView(@LayoutRes int layoutId) {
            View view = LayoutInflater.from(mContext).inflate(layoutId, null);
            return setView(view);
        }

        /**
         * 设置宽高
         *
         * @param width
         * @param height
         * @return
         */
        public Builder setWidthAndHeight(int width, int height) {
            this.mWidth = width;
            this.mHeight = height;
            return this;
        }

        /**
         * 设置宽度
         *
         * @param width
         * @return
         */
        public Builder setWidth(int width) {
            this.mWidth = width;
            return this;
        }

        /**
         * 按比例设置宽度
         *
         * @param percent
         * @return
         */
        public Builder setWidthPercent(float percent) {
            this.mWidth = (int) (ScreenUtils.getScreenWidth() * percent);
            return this;
        }

        /**
         * 设置高度
         *
         * @param height
         * @return
         */
        public Builder setHeight(int height) {
            this.mHeight = height;
            return this;
        }

        /**
         * 按比例设置高度
         *
         * @param percent
         * @return
         */
        public Builder setHeightPercent(float percent) {
            this.mHeight = (int) (ScreenUtils.getScreenHeight() * percent);
            return this;
        }

        /**
         * 设置view背景颜色
         *
         * @param colorId
         * @return
         */
        public Builder setViewBackGround(@ColorRes int colorId) {
            mPopView.setBackgroundColor(mContext.getResources().getColor(colorId));
//            mPopView.setBackgroundResource(colorId);
            return this;
        }

        /**
         * 设置pop背景
         *
         * @param colorId
         * @return
         */
        public Builder setPopBackgroundResouse(@ColorRes int colorId) {
            mDrawable = new ColorDrawable(mContext.getResources().getColor(colorId));
            return this;
        }

        public Builder setPopBackground(int colorId) {
            mDrawable = new ColorDrawable(colorId);
            return this;
        }

        public Builder setPopBackground(Drawable drawable) {
            this.mDrawable = drawable;
            return this;
        }

        /**
         * 设置点击外部消失
         *
         * @param touchable
         * @return
         */
        public Builder setOutsideTouchable(boolean touchable) {
            this.mTouchable = touchable;
            return this;
        }

        /**
         * 设置获取焦点
         *
         * @param focusable
         * @return
         */
        public Builder setFocusable(boolean focusable) {
            this.mFocusable = focusable;
            return this;
        }

        public Builder setAnimationStyle(int animationStyle) {
            isShowAnimation = true;//判断是否设置动画 避免未设置动画是异常
            this.mAnimation = animationStyle;
            return this;
        }

        /**
         * pop显示时设置背景变色程度
         *
         * @param transparency
         * @return
         */
        public Builder setTransparency(float transparency) {
            this.transparencyShow = transparency;
            return this;
        }

        public CommonPopWindow create() {
            if (mTouchable && mDrawable == null) {//设置pop默认背景
                mDrawable = new ColorDrawable(0x00000000);
            }
            PopupController controller = new PopupController(mPopView);
            controller.setFocusable(mFocusable);
            controller.setDrawable(mDrawable);
            controller.setTouchable(mTouchable);
            if (mWidth == 0) {
                controller.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
            } else {
                controller.setWidth(mWidth);
            }
            if (mHeight == 0) {
                controller.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
            } else {
                controller.setHeight(mHeight);
            }
            if (isShowAnimation) {
                controller.setAnimation(mAnimation);
            }
            controller.setTransparency(transparencyShow);
            return new CommonPopWindow(controller);
        }
    }

    public static class PopupController {
        View mContentView;
        boolean touchable;
        boolean focusable;
        Drawable mDrawable;
        int mWidth;
        int mHeight;
        boolean isShowAnim;
        int animation;
        float transparencyShow;//显示时的透明度


        public PopupController(View mContentView) {
            this.mContentView = mContentView;
        }

        public void setTouchable(boolean touchable) {
            this.touchable = touchable;
        }

        public void setFocusable(boolean focusable) {
            this.focusable = focusable;
        }

        public void setDrawable(Drawable drawable) {
            this.mDrawable = drawable;
        }

        public void setWidth(int width) {
            this.mWidth = width;
        }

        public void setHeight(int height) {
            this.mHeight = height;
        }

        public void setAnimation(int animation) {
            isShowAnim = true;
            this.animation = animation;
        }

        public void setTransparency(float transparency) {
            this.transparencyShow = transparency;
        }
    }
}
