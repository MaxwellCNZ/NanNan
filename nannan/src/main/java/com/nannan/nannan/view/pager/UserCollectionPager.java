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
import com.nannan.nannan.bean.HomeCardTodoBean;
import com.nannan.nannan.utils.StringUtils;
import com.nannan.nannan.utils.UIUtils;
import com.nannan.nannan.view.adapter.MyListViewAdapter;
import com.nannan.nannan.view.holder.BaseHolder;
import com.nannan.nannan.view.holder.ColumnDiscoverHolder;
import com.nannan.nannan.view.holder.FunctionHolder;
import com.nannan.nannan.view.holder.MoreHolder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by MaxwellCNZ on 2017/4/23.
 */

public class UserCollectionPager extends BasePager implements SwipeRefreshLayout.OnRefreshListener {
    private final String cardType;
    private final String avUserId;
    private final Activity activity;
    private SwipeRefreshLayout msrlRefresh;
    private ListView listView;
    private ArrayList<HomeCardTodoBean> moreDiscoverData;
    private MyListViewAdapter myDiscoverListViewAdapter;
    private final AVObject avUser;
    private MyListViewAdapter<ColumnTodoBeen> myListViewAdapter;
    private ArrayList<ColumnTodoBeen> moreData;

    public UserCollectionPager(Activity activity, String cardType, String avUserId) {
        this.activity = activity;
        this.cardType = cardType;
        this.avUserId = avUserId;
        avUser = AVObject.createWithoutData("_User", avUserId);
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

    /**
     * 从网络上加载数剧
     */
    @Override
    public void initData() {
        if (cardType.equals(StringUtils.columnTodo)){
            initColumnData();
        }else {
            initTodoData();
        }
    }

    private void initColumnData() {
        ArrayList<ColumnTodoBeen> columnTodoBeens = new ArrayList<>();
        myListViewAdapter = new MyListViewAdapter<ColumnTodoBeen>(columnTodoBeens, this) {
            @Override
            protected void onLoadMore(final MoreHolder moreHolder) {
                moreData = null;
                AVQuery<AVObject> query = new AVQuery<>(StringUtils.todoCollect);
                query.whereEqualTo(StringUtils.avUserPointer, avUser);
                query.selectKeys(Arrays.asList(cardType));
                query.whereExists(cardType);
                query.orderByDescending(StringUtils.createdAt);
                query.limit(3);
                query.include(cardType + StringUtils._avUserPointer);
                query.include(cardType);
                query.skip(myListViewAdapter.getDatasSize());
                query.findInBackground(new FindCallback<AVObject>() {
                    @Override
                    public void done(List<AVObject> list, AVException e) {
                        if (UIUtils.hasException(e)) {
                            getmProgressBarListener().setProgressBar(moreData, moreHolder);
                            return;
                        }
                        moreData = new ArrayList<>();
                        for (AVObject object : list) {
                            try {
                                ColumnTodoBeen columnTodoBeen = new ColumnTodoBeen();
                                columnTodoBeen.todoColection = object;
                                AVObject columnTodo = object.getAVObject(cardType);
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
                            }catch (Exception ex){
                                continue;
                            }

                        }
                        getmProgressBarListener().setProgressBar(moreData, moreHolder);
                    }
                });
            }

            @Override
            public BaseHolder getHolder() {
                return new ColumnDiscoverHolder(activity, myListViewAdapter, listView, false);
            }
        };
        listView.setAdapter(myListViewAdapter);
    }

    private void initTodoData() {
        ArrayList<HomeCardTodoBean> cardTodoBeans = new ArrayList<>();
        myDiscoverListViewAdapter = new MyListViewAdapter<HomeCardTodoBean>(cardTodoBeans, this) {

            @Override
            protected void onLoadMore(final MoreHolder moreHolder) {
                moreDiscoverData = null;
                AVQuery<AVObject> query = new AVQuery<>(StringUtils.todoCollect);
                query.whereEqualTo(StringUtils.avUserPointer, avUser);
                query.selectKeys(Arrays.asList(cardType));
                query.whereExists(cardType);
                query.orderByDescending(StringUtils.createdAt);
                query.limit(3);
                query.include(cardType + StringUtils._avUserPointer);
                query.include(cardType);
                query.skip(myDiscoverListViewAdapter.getDatasSize());
                query.findInBackground(new FindCallback<AVObject>() {
                    @Override
                    public void done(List<AVObject> list, AVException e) {
                        if (UIUtils.hasException(e)) {
                            UserCollectionPager.this.getmProgressBarListener().setProgressBar(moreDiscoverData, moreHolder);
                            return;
                        }
                        moreDiscoverData = new ArrayList<>();
                        for (AVObject object : list) {
                            try {
                                HomeCardTodoBean cardTodoBean = new HomeCardTodoBean();
                                AVObject avObject = object.getAVObject(cardType);
                                cardTodoBean.collectionCard = object;
                                cardTodoBean.cardTodo = avObject;
                                cardTodoBean.typeTodo = cardType;
                                cardTodoBean.avUserPointer = avObject.getAVObject(StringUtils.avUserPointer);
                                cardTodoBean.strContent = avObject.getString(StringUtils.strContent);
                                cardTodoBean.millisecsData = avObject.getCreatedAt().getTime();
                                AVObject avUser = avObject.getAVObject(StringUtils.avUserPointer);
                                cardTodoBean.avUser = avUser;
                                cardTodoBean.nickName = avUser.getString(StringUtils.nickname);
                                cardTodoBean.introduce = avUser.getString(StringUtils.introduce);
                                cardTodoBean.iconAVFile = avUser.getAVFile(StringUtils.iconShow);
                                cardTodoBean.picAVFile = avObject.getAVFile(StringUtils.picPhotograph);
                                moreDiscoverData.add(cardTodoBean);
                            }catch (Exception ex){
                                continue;
                            }
                        }
                        UserCollectionPager.this.getmProgressBarListener().setProgressBar(moreDiscoverData, moreHolder);
                    }
                });
            }

            @Override
            public BaseHolder getHolder() {
                return new FunctionHolder(activity, listView, false);
            }
        };
        listView.setAdapter(myDiscoverListViewAdapter);
    }

    @Override
    public void onRefresh() {
        if (cardType.equals(StringUtils.columnTodo)){
            refrashColumn();
        }else {
            refrashTodo();
        }
    }

    private void refrashColumn() {
        AVQuery<AVObject> query = new AVQuery<>(StringUtils.todoCollect);
        query.whereEqualTo(StringUtils.avUserPointer, avUser);
        query.selectKeys(Arrays.asList(cardType));
        query.whereExists(cardType);
        query.orderByDescending(StringUtils.createdAt);
        query.limit(3);
        query.include(cardType + StringUtils._avUserPointer);
        query.include(cardType);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (UIUtils.hasException(e)) {
                    msrlRefresh.setRefreshing(false);
                    return;
                }
                moreData = new ArrayList<>();
                for (AVObject object : list) {
                    try {
                        ColumnTodoBeen columnTodoBeen = new ColumnTodoBeen();
                        columnTodoBeen.todoColection = object;
                        AVObject columnTodo = object.getAVObject(cardType);
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
                    }catch (Exception ex){
                        continue;
                    }
                }
                msrlRefresh.setRefreshing(false);
                myListViewAdapter.setDatas(moreData);
                listView.setAdapter(myListViewAdapter);
            }
        });
    }

    private void refrashTodo() {
        AVQuery<AVObject> query = new AVQuery<>(StringUtils.todoCollect);
        query.whereEqualTo(StringUtils.avUserPointer, avUser);
        query.selectKeys(Arrays.asList(cardType));
        query.whereExists(cardType);
        query.orderByDescending(StringUtils.createdAt);
        query.limit(3);
        query.include(cardType + StringUtils._avUserPointer);
        query.include(cardType);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (UIUtils.hasException(e)) {
                    msrlRefresh.setRefreshing(false);
                    return;
                }
                moreDiscoverData = new ArrayList<>();
                for (AVObject object : list) {
                    try {
                        HomeCardTodoBean cardTodoBean = new HomeCardTodoBean();
                        AVObject avObject = object.getAVObject(cardType);
                        cardTodoBean.cardTodo = avObject;
                        cardTodoBean.typeTodo = cardType;
                        cardTodoBean.avUserPointer = avObject.getAVObject(StringUtils.avUserPointer);
                        cardTodoBean.strContent = avObject.getString(StringUtils.strContent);
                        cardTodoBean.millisecsData = avObject.getCreatedAt().getTime();
                        AVObject avUser = avObject.getAVObject(StringUtils.avUserPointer);
                        cardTodoBean.avUser = avUser;
                        cardTodoBean.nickName = avUser.getString(StringUtils.nickname);
                        cardTodoBean.introduce = avUser.getString(StringUtils.introduce);
                        cardTodoBean.iconAVFile = avUser.getAVFile(StringUtils.iconShow);
                        cardTodoBean.picAVFile = avObject.getAVFile(StringUtils.picPhotograph);
                        moreDiscoverData.add(cardTodoBean);
                    }catch (Exception ex){
                        continue;
                    }
                }
                msrlRefresh.setRefreshing(false);
                myDiscoverListViewAdapter.setDatas(moreDiscoverData);
                listView.setAdapter(myDiscoverListViewAdapter);
            }
        });
    }
}
