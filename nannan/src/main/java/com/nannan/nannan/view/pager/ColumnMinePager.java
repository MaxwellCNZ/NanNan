package com.nannan.nannan.view.pager;

import android.app.Activity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.ListView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.nannan.nannan.R;
import com.nannan.nannan.bean.ColumnTodoBeen;
import com.nannan.nannan.utils.StringUtils;
import com.nannan.nannan.utils.UIUtils;
import com.nannan.nannan.view.adapter.MyListViewAdapter;
import com.nannan.nannan.view.holder.BaseHolder;
import com.nannan.nannan.view.holder.ColumnMineHolder;
import com.nannan.nannan.view.holder.MoreHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MaxwellCNZ on 2017/5/9.
 */

public class ColumnMinePager extends BasePager implements SwipeRefreshLayout.OnRefreshListener{

    private final Activity activity;
    private SwipeRefreshLayout msrlRefresh;
    private ListView listView;
    private MyListViewAdapter<ColumnTodoBeen> myListViewAdapter;
    private ArrayList<ColumnTodoBeen> moreData;

    public ColumnMinePager(Activity activity) {
        this.activity = activity;
    }

    @Override
    public View initView() {
        View inflate = UIUtils.inflate(R.layout.pager_listview);
        msrlRefresh = (SwipeRefreshLayout) inflate.findViewById(R.id.srl_refresh);
        listView = (ListView) inflate.findViewById(R.id.lv_ListView);
        msrlRefresh.setOnRefreshListener(this);
        msrlRefresh.setColorSchemeResources(R.color.MasterColor);
        msrlRefresh.setDistanceToTriggerSync(400);// 设置手指在屏幕下拉多少距离会触发下拉刷新
        msrlRefresh.setProgressBackgroundColorSchemeResource(R.color.BackgroundColor2);
        msrlRefresh.setSize(SwipeRefreshLayout.DEFAULT);
        return inflate;
    }

    @Override
    public void initData() {
        ArrayList<ColumnTodoBeen> columnTodoBeens = new ArrayList<>();
        myListViewAdapter = new MyListViewAdapter<ColumnTodoBeen>(columnTodoBeens, this) {
            @Override
            protected void onLoadMore(final MoreHolder moreHolder) {
                moreData = null;
                AVQuery<AVObject> query = new AVQuery<>(StringUtils.columnTodo);
                query.orderByDescending(StringUtils.createdAt);
                query.whereEqualTo(StringUtils.avUserPointer, UIUtils.avUser);
                query.limit(5);
                query.skip(myListViewAdapter.getDatasSize());
                query.include(StringUtils.avUserPointer);
                query.findInBackground(new FindCallback<AVObject>() {
                    @Override
                    public void done(List<AVObject> list, AVException e) {
                        if (UIUtils.hasException(e)){
                            getmProgressBarListener().setProgressBar(moreData, moreHolder);
                            return;
                        }
                        moreData = new ArrayList<>();
                        for (AVObject object : list) {
                            try {
                                ColumnTodoBeen columnTodoBeen = new ColumnTodoBeen();
                                columnTodoBeen.columnTodo = object;
                                columnTodoBeen.strContent = object.getString(StringUtils.strContent);
                                columnTodoBeen.strTitle = object.getString(StringUtils.strTitle);
                                columnTodoBeen.picAVFile = object.getAVFile(StringUtils.picPhotograph);
                                AVUser avUser = object.getAVUser(StringUtils.avUserPointer);
                                columnTodoBeen.avUserPointer = avUser;
                                columnTodoBeen.iconAVFile = avUser.getAVFile(StringUtils.iconShow);
                                columnTodoBeen.nickname = avUser.getString(StringUtils.nickname);
                                columnTodoBeen.introduce = avUser.getString(StringUtils.introduce);
                                columnTodoBeen.millisecsData = object.getCreatedAt().getTime();
                                moreData.add(columnTodoBeen);
                            } catch (Exception ex) {
                                continue;
                            }
                        }
                        getmProgressBarListener().setProgressBar(moreData, moreHolder);
                    }
                });
            }

            @Override
            public BaseHolder getHolder() {
                return new ColumnMineHolder(myListViewAdapter, activity, listView);
            }
        };
        listView.setAdapter(myListViewAdapter);
    }

    @Override
    public void onRefresh() {
        AVQuery<AVObject> query = new AVQuery<>(StringUtils.columnTodo);
        query.orderByDescending(StringUtils.createdAt);
        query.whereEqualTo(StringUtils.avUserPointer, UIUtils.avUser);
        query.limit(5);
        query.include(StringUtils.avUserPointer);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (UIUtils.hasException(e)){
                    msrlRefresh.setRefreshing(false);
                    return;
                }
                moreData = new ArrayList<>();
                for (AVObject object : list) {
                    try {
                        ColumnTodoBeen columnTodoBeen = new ColumnTodoBeen();
                        columnTodoBeen.columnTodo = object;
                        columnTodoBeen.strContent = object.getString(StringUtils.strContent);
                        columnTodoBeen.strTitle = object.getString(StringUtils.strTitle);
                        columnTodoBeen.picAVFile = object.getAVFile(StringUtils.picPhotograph);
                        AVUser avUser = object.getAVUser(StringUtils.avUserPointer);
                        columnTodoBeen.avUserPointer = avUser;
                        columnTodoBeen.iconAVFile = avUser.getAVFile(StringUtils.iconShow);
                        columnTodoBeen.nickname = avUser.getString(StringUtils.nickname);
                        columnTodoBeen.introduce = avUser.getString(StringUtils.introduce);
                        columnTodoBeen.millisecsData = object.getCreatedAt().getTime();
                        moreData.add(columnTodoBeen);
                    } catch (Exception ex) {
                        continue;
                    }
                }
                msrlRefresh.setRefreshing(false);
                myListViewAdapter.setDatas(moreData);
                listView.setAdapter(myListViewAdapter);
            }
        });
    }
}
