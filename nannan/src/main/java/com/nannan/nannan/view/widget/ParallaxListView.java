package com.nannan.nannan.view.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewTreeObserver;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.ListView;

/**
 * Created by MaxwellCNZ on 2017/3/9.
 */

public class ParallaxListView extends ListView {

    private ImageView imageView;
    private int imageViewHeightBackground;
    private int drawableHeightBackground;
    private int maxHeightBackground;
    private ImageView imageViewBackground;
    private int drawableHeight;
    private int imageViewHeight;
    private int maxHeight;

    public ParallaxListView(Context context) {
        super(context);
    }

    public ParallaxListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ParallaxListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setParallaxImageView(final ImageView imageView, final ImageView imageViewBackground){
        this.imageView = imageView;
        this.imageViewBackground = imageViewBackground;

        imageViewBackground.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                imageViewBackground.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                imageViewHeightBackground = imageViewBackground.getHeight();
                drawableHeightBackground = imageViewBackground.getDrawable().getIntrinsicHeight();
                maxHeightBackground = imageViewHeightBackground > drawableHeightBackground ? imageViewHeightBackground * 2 : drawableHeightBackground;
            }
        });
        imageView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                imageView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                imageViewHeight = imageView.getHeight();
                drawableHeight = Integer.MAX_VALUE;
                maxHeight = imageViewHeight > drawableHeight ? imageViewHeight * 2 : drawableHeight;
            }
        });
    }

    @Override
    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {

        if (deltaY < 0 && isTouchEvent){
            if (imageView != null && imageViewBackground != null){
                int newHeight = imageView.getHeight() - deltaY / 3;
                int newHeightBackground = imageViewBackground.getHeight() - deltaY / 3;
                if (newHeight > maxHeight && newHeightBackground > maxHeightBackground){
                    newHeight = maxHeight;
                    newHeightBackground = maxHeightBackground;
                }
                imageView.getLayoutParams().height = newHeight;
                imageViewBackground.getLayoutParams().height = newHeightBackground;
                imageView.requestLayout();
                imageViewBackground.requestLayout();
            }
        }
        return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        if (ev.getAction() == MotionEvent.ACTION_UP){
            final ValueAnimator valueAnimatorBackground = ValueAnimator.ofInt(imageViewBackground.getHeight(), imageViewHeightBackground);
            final ValueAnimator valueAnimator = ValueAnimator.ofInt(imageView.getHeight(), imageViewHeight);
            valueAnimatorBackground.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int animatedValue = (int) valueAnimatorBackground.getAnimatedValue();
                    imageViewBackground.getLayoutParams().height = animatedValue;
                    imageViewBackground.requestLayout();
                }
            });
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int animatedValue = (int) valueAnimator.getAnimatedValue();
                    imageView.getLayoutParams().height = animatedValue;
                    imageView.requestLayout();
                }
            });
            //弹性差值器
            valueAnimator.setInterpolator(new OvershootInterpolator(5));
            valueAnimatorBackground.setInterpolator(new OvershootInterpolator(5));
            valueAnimator.setDuration(350);
            valueAnimatorBackground.setDuration(350);
            valueAnimator.start();
            valueAnimatorBackground.start();
        }
        return super.onTouchEvent(ev);
    }

}
