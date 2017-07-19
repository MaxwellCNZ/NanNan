package com.nannan.nannan.view.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by MaxwellCNZ on 2017/3/23.
 */

public class NoScrollViewPager extends ViewPager {
    public NoScrollViewPager(Context context) {
        super(context);
    }

    public NoScrollViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

//    表示不对事物进行拦截，从而使嵌套在viewpager内部的viewpager可以响应滑动事件
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }


//    拦截ViewPager的触摸事件, 不做任何处理
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return false;
    }
}
