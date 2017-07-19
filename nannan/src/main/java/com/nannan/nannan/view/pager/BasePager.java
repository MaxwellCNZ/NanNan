package com.nannan.nannan.view.pager;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.ListView;

import com.nannan.nannan.utils.UIUtils;
import com.nannan.nannan.view.holder.MoreHolder;

import java.util.ArrayList;

/**
 * Created by MaxwellCNZ on 2017/3/7.
 */

public abstract class BasePager<T> {

    private final View mRootView;
    private int viewSlop;
    public MoreHolder moreHolder;

    public BasePager() {
        viewSlop = ViewConfiguration.get(UIUtils.getContext()).getScaledTouchSlop();
        mRootView = initView();
    }

    public abstract View initView();

    public abstract void initData();

    public View getmRootView() {
        return mRootView;
    }

    /**
     * 取消监听
     * @param listView
     */
    public void cancelMenuShow(ListView listView){
        listView.setOnTouchListener(null);
    }
    /**
     * 控制pager对菜单的隐藏和显示
     */
    public void setMenuShow(ListView listView) {
        listView.setOnTouchListener(new View.OnTouchListener() {
            private float startY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startY = event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float disY = event.getY() - startY;
                        //垂直方向滑动
                        if (Math.abs(disY) > viewSlop) {
                            //是否向上滑动
                            boolean isUpSlide = disY < 0;

                            //实现底部tools的显示与隐藏
                            if (isUpSlide) {
                                mListener.onHide();
                            } else {
                                mListener.onShow();
                            }
                        }
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
    }

    /**
     * 设置监听，用来设置menu是否可以显示
     */
    public interface onMenuShowListener {

        void onShow();

        void onHide();

    }

    private onMenuShowListener mListener;

    public void setOnMenuShowListener(onMenuShowListener listener) {
        mListener = listener;
    }

    /**
     * 设置一个监听 用来加载更多数据
     */
    public interface onLoadMoreListener<T> {

        void setProgressBar(ArrayList<T> moreData, MoreHolder moreHolder);

    }

    private onLoadMoreListener mProgressBarListener;

    public void setonLoadMoreListener(onLoadMoreListener listener) {
        mProgressBarListener = listener;
    }

    public onLoadMoreListener getmProgressBarListener() {
        return mProgressBarListener;
    }
}
