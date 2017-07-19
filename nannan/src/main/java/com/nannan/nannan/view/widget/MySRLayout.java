package com.nannan.nannan.view.widget;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.nannan.nannan.utils.UIUtils;

/**
 * Created by MaxwellCNZ on 2017/4/12.
 */

public class MySRLayout extends SwipeRefreshLayout {

    private float y;

    public MySRLayout(Context context) {
        super(context);
    }

    public MySRLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                y = ev.getY();
                if (y < UIUtils.dip2px(230)){
                    return false;
                }
                break;
            default:
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }
}
