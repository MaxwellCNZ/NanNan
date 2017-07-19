package com.nannan.nannan.view.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.nannan.nannan.view.holder.BaseHolder;
import com.nannan.nannan.view.holder.MoreHolder;
import com.nannan.nannan.view.pager.BasePager;

import java.util.ArrayList;

/**
 * Created by MaxwellCNZ on 2017/3/23.
 */

public abstract class MyListViewAdapter<T> extends BaseAdapter {

    private static final int TYPE_MORE = 0;
    private static final int TYPE_NORMAL = 1;
    private ArrayList<T> datas;
    private final BasePager basePager;
    private BaseHolder holder;

    public MyListViewAdapter(ArrayList<T> data, BasePager basePager) {
        this.datas = data;
        this.basePager = basePager;
        this.basePager.setonLoadMoreListener(new BasePager.onLoadMoreListener() {
            @Override
            public void setProgressBar(ArrayList moreData, MoreHolder moreHolder) {
                MyListViewAdapter.this.setProgressBar(moreHolder, moreData);
            }
        });
    }

    @Override
    public int getCount() {
        return datas.size() + 1;
    }

    @Override
    public T getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    // 返回布局类型个数
    @Override
    public int getViewTypeCount() {
        return 2;// 返回两种类型,普通布局+加载更多布局
    }

    // 返回当前位置应该展示那种布局类型
    @Override
    public int getItemViewType(int position) {
        if (position == getCount() - 1) {// 最后一个
            return TYPE_MORE;
        } else {
            return TYPE_NORMAL;/*getInnerType(position);*/
        }
    }

//    // 子类可以重写此方法来更改返回的布局类型
//    public int getInnerType(int position) {
//        return TYPE_NORMAL;// 默认就是普通类型
//    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            if (getItemViewType(position) == TYPE_MORE) {
                //设置加载更多布局
                holder = new MoreHolder(hasMore());
            } else {
                holder = getHolder(); // 具体子类布局
            }
        } else {
            holder = (BaseHolder) convertView.getTag();
        }
        if (getItemViewType(position) == TYPE_MORE) {
            MoreHolder moreHolder = (MoreHolder) holder;
            if (moreHolder.getData().intValue() == MoreHolder.STATE_MORE_MORE) {
                loadingMore(moreHolder);
            }
        } else {
            holder.setData(datas.get(position), position);
        }
        return holder.getRootView();
    }

    private boolean isLoadingMore = false;

    /**
     * 加载更多数据
     *
     * @param moreHolder
     */
    private void loadingMore(final MoreHolder moreHolder) {
        if (!isLoadingMore) {
            isLoadingMore = true;
            onLoadMore(moreHolder);
        }

    }

    /**
     * 设置ProgressBar的状态
     *
     * @param moreHolder
     * @param moreData
     */
    private void setProgressBar(MoreHolder moreHolder, ArrayList<T> moreData) {
        if (moreData != null) {
            if (moreData.size() < 3) {
                moreHolder.setData(MoreHolder.STATE_MORE_NONE, 0);
            } else {
                //还有更多数据
                moreHolder.setData(MoreHolder.STATE_MORE_MORE, 0);
            }
            datas.addAll(moreData);
//                                holder.setData(MoreHolder.STATE_MORE_HINT);
            MyListViewAdapter.this.notifyDataSetChanged();
        } else {
            //加载更多失败
            moreHolder.setData(MoreHolder.STATE_MORE_ERROR, 0);
//            SystemClock.sleep(2000);
//            holder.setData(MoreHolder.STATE_MORE_HINT);
        }
        isLoadingMore = false;
    }

    //由子类去实现加载更多数据
    protected abstract void onLoadMore(MoreHolder moreHolder);

    //默认都是有数据的
    protected boolean hasMore() {
        return true;
    }

    abstract public BaseHolder getHolder();

    /**
     * 返回当前datas的数量
     *
     * @return
     */
    public int getDatasSize() {
        return datas.size();
    }
    public ArrayList getDatas(){
        return datas;
    }
    public void setDatas(ArrayList<T> datas){
        this.datas = datas;
    }
}
