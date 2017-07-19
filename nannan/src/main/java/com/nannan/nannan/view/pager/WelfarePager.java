package com.nannan.nannan.view.pager;

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
import com.nannan.nannan.view.activity.MainActivity;
import com.nannan.nannan.view.adapter.MyListViewAdapter;
import com.nannan.nannan.view.holder.BaseHolder;
import com.nannan.nannan.view.holder.ColumnDiscoverHolder;
import com.nannan.nannan.view.holder.MoreHolder;

import java.util.ArrayList;
import java.util.List;

import static com.nannan.nannan.utils.UIUtils.avUser;

/**
 * Created by MaxwellCNZ on 2017/5/11.
 */

public class WelfarePager extends BasePager implements SwipeRefreshLayout.OnRefreshListener{

    private final MainActivity mActivity;
    private SwipeRefreshLayout msrlRefresh;
    private ListView listView;
    private MyListViewAdapter<ColumnTodoBeen> myListViewAdapter;
    private ArrayList<ColumnTodoBeen> moreData;

    public WelfarePager(MainActivity mActivity) {
        this.mActivity = mActivity;
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
                AVQuery<AVObject> query = new AVQuery<>(StringUtils.columnDynamic);
                query.whereEqualTo(StringUtils.follower, avUser);
                query.orderByDescending(StringUtils.createdAt);
                query.limit(5);
                query.skip(myListViewAdapter.getDatasSize());
                query.include(StringUtils.columnTodo);
                query.include(StringUtils.columnTodo + StringUtils._avUserPointer);
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
                                AVObject columnTodo = object.getAVObject(StringUtils.columnTodo);
                                columnTodoBeen.sharetor = object.getAVObject(StringUtils.share_tor);
                                columnTodoBeen.columnTodo = columnTodo;
                                columnTodoBeen.strContent = columnTodo.getString(StringUtils.strContent);
                                columnTodoBeen.strTitle = columnTodo.getString(StringUtils.strTitle);
                                columnTodoBeen.picAVFile = columnTodo.getAVFile(StringUtils.picPhotograph);
                                AVUser avUser = columnTodo.getAVUser(StringUtils.avUserPointer);
                                columnTodoBeen.avUserPointer = avUser;
                                columnTodoBeen.iconAVFile = avUser.getAVFile(StringUtils.iconShow);
                                columnTodoBeen.nickname = avUser.getString(StringUtils.nickname);
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
                return new ColumnDiscoverHolder(mActivity, myListViewAdapter, listView, false);
            }
        };
        listView.setAdapter(myListViewAdapter);
    }

    @Override
    public void onRefresh() {
        AVQuery<AVObject> query = new AVQuery<>(StringUtils.columnDynamic);
        query.whereEqualTo(StringUtils.follower, avUser);
        query.orderByDescending(StringUtils.createdAt);
        query.limit(5);
        query.include(StringUtils.columnTodo);
        query.include(StringUtils.columnTodo + StringUtils._avUserPointer);
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
                        AVObject columnTodo = object.getAVObject(StringUtils.columnTodo);
                        columnTodoBeen.sharetor = object.getAVObject(StringUtils.share_tor);
                        columnTodoBeen.columnTodo = columnTodo;
                        columnTodoBeen.strContent = columnTodo.getString(StringUtils.strContent);
                        columnTodoBeen.strTitle = columnTodo.getString(StringUtils.strTitle);
                        columnTodoBeen.picAVFile = columnTodo.getAVFile(StringUtils.picPhotograph);
                        AVUser avUser = columnTodo.getAVUser(StringUtils.avUserPointer);
                        columnTodoBeen.avUserPointer = avUser;
                        columnTodoBeen.iconAVFile = avUser.getAVFile(StringUtils.iconShow);
                        columnTodoBeen.nickname = avUser.getString(StringUtils.nickname);
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

    public ListView getListView() {
        return listView;
    }
}
