package com.nannan.nannan.view.pager;

import android.app.Activity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.ListView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
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
 * Created by MaxwellCNZ on 2017/4/27.
 */

public class UserMinePager extends BasePager implements SwipeRefreshLayout.OnRefreshListener {

    private final Activity activity;
    private String cardType;
    private String avUserId;
    private SwipeRefreshLayout msrlRefresh;
    private ListView listView;
    private ArrayList<HomeCardTodoBean> moreData;
    private MyListViewAdapter myListViewAdapter;
    private AVObject avUser;
    private String nickName;
    private String introduce;
    private AVFile avFile;
    private MyListViewAdapter<ColumnTodoBeen> myListViewAdapter1;
    private ArrayList<ColumnTodoBeen> moreColumnData;

    public UserMinePager(Activity activity, String cardType, String avUserId) {
        this.activity = activity;
        this.cardType = cardType;
        this.avUserId = avUserId;
        avUser = AVObject.createWithoutData("_User", avUserId);
        avUser.fetchInBackground(new GetCallback<AVObject>() {
            @Override
            public void done(AVObject avObject, AVException e) {
                if (UIUtils.hasException(e)) {
                    return;
                }
                avFile = avObject.getAVFile(StringUtils.iconShow);
                nickName = avObject.getString(StringUtils.nickname);
                introduce = avObject.getString(StringUtils.introduce);
                initData();
            }
        });
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
        if (cardType.equals(StringUtils.columnTodo)){
            initColumnData();
        }else {
            initTodoData();
        }
    }

    private void initColumnData() {
        ArrayList<ColumnTodoBeen> columnTodoBeens = new ArrayList<>();
        myListViewAdapter1 = new MyListViewAdapter<ColumnTodoBeen>(columnTodoBeens, this) {
            @Override
            protected void onLoadMore(final MoreHolder moreHolder) {
                moreColumnData = null;
                AVQuery<AVObject> query = new AVQuery<>(StringUtils.columnTodo);
                query.orderByDescending(StringUtils.createdAt);
                query.whereEqualTo(StringUtils.avUserPointer, avUser);
                query.limit(5);
                query.skip(myListViewAdapter1.getDatasSize());
                query.include(StringUtils.avUserPointer);
                query.findInBackground(new FindCallback<AVObject>() {
                    @Override
                    public void done(List<AVObject> list, AVException e) {
                        if (UIUtils.hasException(e)){
                            getmProgressBarListener().setProgressBar(moreColumnData, moreHolder);
                            return;
                        }
                        moreColumnData = new ArrayList<>();
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
                                columnTodoBeen.millisecsData = object.getCreatedAt().getTime();
                                moreColumnData.add(columnTodoBeen);
                            } catch (Exception ex) {
                                continue;
                            }
                        }
                        getmProgressBarListener().setProgressBar(moreColumnData, moreHolder);
                    }
                });

            }

            @Override
            public BaseHolder getHolder() {
                return new ColumnDiscoverHolder(activity, myListViewAdapter1, listView, false);
            }
        };
        listView.setAdapter(myListViewAdapter1);
    }

    private void initTodoData() {
        ArrayList<HomeCardTodoBean> cardTodoBeans = new ArrayList<>();
        myListViewAdapter = new MyListViewAdapter<HomeCardTodoBean>(cardTodoBeans, this) {

            @Override
            protected void onLoadMore(final MoreHolder moreHolder) {
                moreData = null;
                AVQuery<AVObject> query = new AVQuery<>(StringUtils.cardMiddleTable);
                query.whereExists(cardType);
                query.whereEqualTo(StringUtils.avUser, avUser);
                query.orderByDescending(StringUtils.createdAt);
                query.limit(3);
                query.include(cardType);
                query.selectKeys(Arrays.asList(cardType));
                query.skip(myListViewAdapter.getDatasSize());
                query.findInBackground(new FindCallback<AVObject>() {
                    @Override
                    public void done(List<AVObject> list, AVException e) {
                        if (UIUtils.hasException(e)) {
                            UserMinePager.this.getmProgressBarListener().setProgressBar(moreData, moreHolder);
                            return;
                        }
                        moreData = new ArrayList<>();

                        for (AVObject object : list) {
                            try {
                                HomeCardTodoBean cardTodoBean = new HomeCardTodoBean();
                                cardTodoBean.cardMiddle = object;
                                AVObject cardTodo = object.getAVObject(cardType);
                                cardTodoBean.cardTodo = cardTodo;
                                cardTodoBean.typeTodo = cardType;
                                cardTodoBean.avUser = avUser;
                                cardTodoBean.strContent = cardTodo.getString(StringUtils.strContent);
                                cardTodoBean.millisecsData = cardTodo.getCreatedAt().getTime();
                                cardTodoBean.nickName = nickName;
                                cardTodoBean.introduce = introduce;
                                cardTodoBean.iconAVFile = avFile;
                                cardTodoBean.picAVFile = cardTodo.getAVFile(StringUtils.picPhotograph);
                                moreData.add(cardTodoBean);
                            }catch (Exception ex){
                                continue;
                            }

                        }
                        UserMinePager.this.getmProgressBarListener().setProgressBar(moreData, moreHolder);
                    }
                });
            }

            @Override
            public BaseHolder getHolder() {
                return new FunctionHolder(activity, listView, false);
            }
        };
        listView.setAdapter(myListViewAdapter);
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
        AVQuery<AVObject> query = new AVQuery<>(StringUtils.columnTodo);
        query.orderByDescending(StringUtils.createdAt);
        query.whereEqualTo(StringUtils.avUserPointer, avUser);
        query.limit(5);
        query.include(StringUtils.avUserPointer);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (UIUtils.hasException(e)){
                    msrlRefresh.setRefreshing(false);
                    return;
                }
                moreColumnData = new ArrayList<>();
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
                        columnTodoBeen.millisecsData = object.getCreatedAt().getTime();
                        moreColumnData.add(columnTodoBeen);
                    } catch (Exception ex) {
                        continue;
                    }
                }
                msrlRefresh.setRefreshing(false);
                myListViewAdapter1.setDatas(moreColumnData);
                listView.setAdapter(myListViewAdapter1);
            }
        });
    }

    private void refrashTodo() {
        AVQuery<AVObject> query = new AVQuery<>(StringUtils.cardMiddleTable);
        query.whereExists(cardType);
        query.whereEqualTo(StringUtils.avUser, avUser);
        query.orderByDescending(StringUtils.createdAt);
        query.limit(3);
        query.include(cardType);
        query.selectKeys(Arrays.asList(cardType));
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
                        HomeCardTodoBean cardTodoBean = new HomeCardTodoBean();
                        cardTodoBean.cardMiddle = object;
                        AVObject cardTodo = object.getAVObject(cardType);
                        cardTodoBean.cardTodo = cardTodo;
                        cardTodoBean.avUser = avUser;
                        cardTodoBean.typeTodo = cardType;
                        cardTodoBean.strContent = cardTodo.getString(StringUtils.strContent);
                        cardTodoBean.millisecsData = cardTodo.getCreatedAt().getTime();
                        cardTodoBean.nickName = nickName;
                        cardTodoBean.introduce = introduce;
                        cardTodoBean.iconAVFile = avFile;
                        cardTodoBean.picAVFile = cardTodo.getAVFile(StringUtils.picPhotograph);
                        moreData.add(cardTodoBean);
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
}
