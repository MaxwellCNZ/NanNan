package com.nannan.nannan.utils;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.nannan.nannan.view.pager.BasePager;

import java.util.ArrayList;

/**
 * Created by MaxwellCNZ on 2017/3/23.
 */

public class FillPagerAdapterUtils  extends PagerAdapter {

    private final ArrayList<BasePager> list;

    public FillPagerAdapterUtils(ArrayList<BasePager> list){
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        BasePager basePager = list.get(position);
        View view = basePager.getmRootView();
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
