package com.nannan.nannan.view.holder;

import android.view.View;

/**
 * Created by MaxwellCNZ on 2017/3/23.
 */

public abstract class BaseHolder<T> {

    private final View rootView;
    private T data;
    private int position;

    public BaseHolder(){

        rootView = initView();
        rootView.setTag(this);
    }

    abstract public View initView();

    public void setData(T data, int position){
        this.data = data;
        this.position = position;
        fillView(data);
    }

    public View getRootView(){
        return rootView;
    }
    protected abstract void fillView(T data);


    public T getData() {
        return data;
    }

    public int getPosition() {
        return position;
    }
}
