package com.banzhi.lib.widget.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.banzhi.library.R;


/**
 * <pre>
 * author : No.1
 * time : 2017/6/30.
 * desc :
 * </pre>
 */

public class LoadButton extends View implements View.OnClickListener {
    enum State {
        INITIAL,//初始状态
        FODDING,//伸缩状态
        LOADDING,//加载中
        ERROR,//加载失败
        SUCCESSED//加载成功
    }

    TextPaint mTextPaint;
    Paint mPaint;
    private String mText = "测试文字"; //显示的文字
    private float mTextWidth;//文字宽度
    private float mTextHeight;//文字高度
    private float mLeftpadding = 10;//左边距
    private float mRightpadding = 10;//右边距
    private float mToppadding = 10;//上边距
    private float mBottomtpadding = 10;//下边距
    private float mPadding = 10;//下边距
    private static final float DEFAULT_PADDING = 10;//默认边距
    private float mTextSize = 28;//文字大小
    float mRadius;//圆弧半径
    private int rectHeight;//中间矩形高度
    private int rectWidth;//中间矩形宽度
    private int mBackground = Color.GREEN;//背景颜色
    private int mTextColor = Color.BLACK;//文字颜色

    RectF leftRect;
    RectF rightRect;
    RectF contentRect;
    RectF progressRect;
    private int mStrokeColor = Color.BLUE;
    private float mProgressWidth = 2;
    public State currentState = State.INITIAL;
    boolean isOpen = true;//默认当前状态为展开状态

    private int mProgressSecondColor = Color.GRAY;
    private int mProgressColor = Color.WHITE;
    private float circleSweep = 270;//每次绘制的时候从起始角度扫描的角度
    private boolean progressReverse;//动画是否需要翻转
    private int mProgressStartAngel;//进度条开始角度

    private Drawable mErrorDrewable;//失败图片
    private Drawable mSuccessDrewable;//成功图片

    public LoadButton(Context context) {
        super(context);
    }

    public LoadButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.LoadButton);
        mText = typedArray.getString(R.styleable.LoadButton_android_text);//显示的文字
        mTextSize = typedArray.getDimensionPixelSize(R.styleable.LoadButton_android_textSize, 24);//字体大小
        mTextColor = typedArray.getColor(R.styleable.LoadButton_android_textColor, Color.WHITE);//字体颜色
        mBackground = typedArray.getColor(R.styleable.LoadButton_backColor, Color.GREEN);//背景颜色
        mProgressSecondColor = typedArray.getColor(R.styleable.LoadButton_progressBackColor, Color.WHITE);//进度条背景色
        mProgressColor = typedArray.getColor(R.styleable.LoadButton_progressColor, Color.RED);//进度条颜色
        mProgressWidth = typedArray.getDimension(R.styleable.LoadButton_progressWidth, 2);//进度条宽度
        mErrorDrewable = typedArray.getDrawable(R.styleable.LoadButton_errorDrawable);//失败图片
        mSuccessDrewable = typedArray.getDrawable(R.styleable.LoadButton_successDrawable);//成功图片
        Log.i("result", "LoadButton: " + mProgressWidth);
        mLeftpadding = typedArray.getDimension(R.styleable.LoadButton_android_paddingLeft, DEFAULT_PADDING);//左边距
        mToppadding = typedArray.getDimension(R.styleable.LoadButton_android_paddingTop, DEFAULT_PADDING);//上边距
        mRightpadding = typedArray.getDimension(R.styleable.LoadButton_android_paddingRight, DEFAULT_PADDING);//右边距
        mBottomtpadding = typedArray.getDimension(R.styleable.LoadButton_android_paddingBottom, DEFAULT_PADDING);//下边距
        mPadding = typedArray.getDimension(R.styleable.LoadButton_android_padding, 0);//边距
        if (mPadding != 0) {
            mLeftpadding = mPadding;
            mToppadding = mPadding;
            mRightpadding = mPadding;
            mBottomtpadding = mPadding;
        }
        if (mSuccessDrewable == null) {
            mSuccessDrewable = context.getResources().getDrawable(R.drawable.load_success);
        }
        if (mErrorDrewable == null) {
            mErrorDrewable = context.getResources().getDrawable(R.drawable.load_error);
        }
        init();
    }

    ObjectAnimator shrinkAnim;//缩放动画

    /**
     * 缩放动画
     *
     * @param startWidth
     * @param endWidth
     */
    private void shrinkAnim(int startWidth, int endWidth) {

        if (shrinkAnim == null) {
            shrinkAnim = ObjectAnimator.ofInt(this, "rectWidth", startWidth, endWidth);
            shrinkAnim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    if (isOpen) {//状态 展开-->收缩
                        currentState = State.LOADDING;
                        load();
                    } else {//状态 收缩-->展开
                        currentState = State.INITIAL;
                    }
                    isOpen = !isOpen;
                }

                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                }
            });
        }
        shrinkAnim.setIntValues(startWidth, endWidth);
        shrinkAnim.setInterpolator(new LinearOutSlowInInterpolator());
        shrinkAnim.setDuration(1000);
        shrinkAnim.start();
        currentState = State.FODDING;

    }

    ObjectAnimator loadAnimator;//加载动画

    /**
     * 加载动画
     */
    public void load() {
        currentState = State.LOADDING;
        if (loadAnimator == null) {
            loadAnimator = ObjectAnimator.ofFloat(this, "circleSweep", 0, 360);
        }

        loadAnimator.setDuration(1000);
        loadAnimator.setRepeatMode(ValueAnimator.RESTART);
        loadAnimator.setRepeatCount(ValueAnimator.INFINITE);
        loadAnimator.setInterpolator(new FastOutSlowInInterpolator());
        loadAnimator.removeAllListeners();

        loadAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                progressReverse = !progressReverse;
            }
        });
        loadAnimator.start();
    }

    /**
     * 加载失败
     */
    public void loadError() {
        if (mListener != null) {
            mListener.OnError();
        }
        currentState = State.ERROR;
        cancelAnimation();
        invaidateSelft();
    }

    /**
     * 加载成功
     */
    public void loadSuccess() {
        if (mListener != null) {
            mListener.OnSuccess();
        }
        currentState = State.SUCCESSED;
        cancelAnimation();
        invaidateSelft();
    }

    public void setCircleSweep(float sweep) {
        this.circleSweep = sweep;
        invaidateSelft();
    }

    public void setRectWidth(int width) {
        this.rectWidth = width;
        invaidateSelft();
    }

    private void invaidateSelft() {
        if (Looper.myLooper() == Looper.getMainLooper()) {//在主线程刷新
            invalidate();
        } else {//在工作线程刷新
            postInvalidate();
        }
    }

    /**
     * 重置
     */
    public void reset() {
        rectWidth = (int) (getWidth() - mRadius * 2);
        shrinkAnim(0, rectWidth);
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(mStrokeColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mProgressWidth);
        mPaint.setAntiAlias(true);

        mTextPaint = new TextPaint();
        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setAntiAlias(true);
        leftRect = new RectF();
        rightRect = new RectF();
        contentRect = new RectF();
        progressRect = new RectF();
        setOnClickListener(this);
    }

    public LoadButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int cx = getWidth() / 2;
        int cy = getHeight() / 2;
        drawPath(canvas, cx, cy);
        drawText(canvas, cx, cy);
        if (currentState == State.LOADDING) {
            drawProgress(canvas, cx, cy);
        }
        if (currentState == State.ERROR) {
            drawDrawable(canvas, cx, cy, mErrorDrewable);
        }
        if (currentState == State.SUCCESSED) {
            drawDrawable(canvas, cx, cy, mSuccessDrewable);
        }
    }

    /**
     * 绘制图片
     *
     * @param canvas
     * @param cx
     * @param cy
     */
    private void drawDrawable(Canvas canvas, int cx, int cy, Drawable dw) {
        int circleR = (int) (mRadius / 2);//进度条半径
        dw.setBounds(cx - circleR, cy - circleR, cx + circleR, cy + circleR);
        dw.draw(canvas);
    }

    /**
     * 绘制进度
     */
    private void drawProgress(Canvas canvas, int cx, int cy) {
        int circleR = (int) (mRadius / 2);//进度条半径
        if (progressRect == null) {
            progressRect = new RectF();
        }
        progressRect.set(cx - circleR, cy - circleR, cx + circleR, cy + circleR);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(mProgressSecondColor);//设置进度条背景颜色
        mPaint.setStrokeWidth(mProgressWidth);
        canvas.drawCircle(cx, cy, circleR, mPaint);//绘制进度条背景圆
        mPaint.setColor(mProgressColor);//设置进度条加载时的颜色
        if (circleSweep != 360) {
            mProgressStartAngel = progressReverse ? 270 : (int) (270 + circleSweep);
            mPaint.setStrokeWidth(mProgressWidth);
            canvas.drawArc(progressRect
                    , mProgressStartAngel, progressReverse ? circleSweep : (int) (360 - circleSweep),
                    false, mPaint);
        }
        mPaint.setColor(mBackground);
    }

    Path mPath;
    int left;
    int top;
    int right;
    int bottom;

    /**
     * 画轮廓
     *
     * @param canvas
     */
    private void drawPath(Canvas canvas, int cx, int cy) {
        if (mPath == null) {
            mPath = new Path();
        }
        mPath.reset();
        left = (int) (cx - rectWidth / 2 - mRadius);  //控件左边坐标
        top = 0;  //控件顶部坐标
        right = (int) (cx + rectWidth / 2 + mRadius);  //控件右边坐标
        bottom = rectHeight;  //控件底部坐标
        leftRect.set(left, top, left + mRadius * 2, bottom);
        rightRect.set(right - mRadius * 2, top, right, bottom);
        contentRect.set(cx - (rectWidth / 2), top, cx + (rectWidth / 2), bottom);
        //移动到七点
        mPath.moveTo(cx - rectWidth / 2, bottom);
        //左边半圆
        mPath.arcTo(leftRect, 90f, 180f);
        //上边线
        mPath.lineTo(cx + rectWidth / 2, top);
        //右边圆弧
        mPath.arcTo(rightRect, 270f, 180f);
        //闭合
        mPath.close();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mBackground);
        canvas.drawPath(mPath, mPaint);
    }

    /**
     * 绘制文字
     *
     * @param canvas
     * @param cx
     * @param cy
     */
    private void drawText(Canvas canvas, int cx, int cy) {
        if (TextUtils.isEmpty(mText)) {
            return;
        }
        int textDescent = (int) mTextPaint.getFontMetrics().descent;
        int textAscent = (int) mTextPaint.getFontMetrics().ascent;
        int delta = Math.abs(textAscent) - Math.abs(textDescent);
        if (currentState == State.INITIAL) {//展开状态才绘制文字
            canvas.drawText(mText, cx, cy + delta / 2, mTextPaint);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthsize = MeasureSpec.getSize(widthMeasureSpec); //取出宽度的确切数值
        int widthmode = MeasureSpec.getMode(widthMeasureSpec); //取出宽度的测量模式

        int heightsize = MeasureSpec.getSize(heightMeasureSpec); //取出高度的确切数值
        int heightmode = MeasureSpec.getMode(heightMeasureSpec); //取出高度的测量模式
        int resultW = widthsize;
        int resultH = heightsize;

        int contentW = 0;
        int contentH = 0;
        //计算最终高度
        if (widthmode == MeasureSpec.AT_MOST) {
            contentH = (int) (mTextSize + mToppadding + mBottomtpadding);
            resultH = contentH < resultH ? contentH : resultH;
            mRadius = resultH / 2;
        } else {
            contentH = (int) (mTextSize + mToppadding + mBottomtpadding);
            resultH = contentH;
            mRadius = resultH / 2;
        }
        //计算最终宽度
        if (widthmode == MeasureSpec.AT_MOST) {
            mTextWidth = mTextPaint.measureText(mText);
            contentW = (int) (mTextWidth + mLeftpadding + mRightpadding + mRadius * 2);
            resultW = contentW < resultW ? contentW : resultW;
        } else {
            resultW = widthsize < heightsize ? widthsize : heightsize;
        }
        rectWidth = (int) (resultW - 2 * mRadius);
        rectHeight = resultH;
        Log.i("result", "onMeasure: resultW=" + resultW);
        Log.i("result", "onMeasure: resultH=" + resultH);
        Log.i("result", "onMeasure: mRadius=" + mRadius);
        setMeasuredDimension(resultW, resultH);
    }

    @Override
    public void onClick(View v) {
        if (currentState == State.FODDING) {//正在收缩状态 不相应事件
            return;
        }
//        if (isOpen) {
//            shrinkAnim(rectWidth, 0);
//        } else {
//            shrinkAnim(0, (int) (getWidth() - mRadius * 2));
//        }
        switch (currentState) {
            case INITIAL://初始状态
                shrinkAnim(rectWidth, 0);
                mListener.onClick();
                break;
            case FODDING://收缩状态
                break;
            case LOADDING://加载状态

                break;
            case SUCCESSED://加载成功
                break;
            case ERROR://加载失败
                reset();
                break;
        }
    }


    /**
     * 取消动画
     */
    private void cancelAnimation() {
        if (shrinkAnim != null && shrinkAnim.isRunning()) {
            shrinkAnim.removeAllListeners();
            shrinkAnim.cancel();
            shrinkAnim = null;
        }
        if (loadAnimator != null && loadAnimator.isRunning()) {
            loadAnimator.removeAllListeners();
            loadAnimator.cancel();
            loadAnimator = null;
        }
    }

    LoadListener mListener;

    public void setLoadListener(LoadListener listener) {
        this.mListener = listener;
    }

    public interface LoadListener {
        void onClick();

        void OnError();

        void OnSuccess();
    }
}
