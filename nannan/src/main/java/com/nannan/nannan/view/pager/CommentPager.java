package com.nannan.nannan.view.pager;

import android.app.Activity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.ListView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.nannan.nannan.R;
import com.nannan.nannan.bean.CommentTodoBean;
import com.nannan.nannan.utils.StringUtils;
import com.nannan.nannan.utils.UIUtils;
import com.nannan.nannan.view.adapter.MyListViewAdapter;
import com.nannan.nannan.view.holder.BaseHolder;
import com.nannan.nannan.view.holder.CommentHolder;
import com.nannan.nannan.view.holder.MoreHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MaxwellCNZ on 2017/5/5.
 */

public class CommentPager extends BasePager implements SwipeRefreshLayout.OnRefreshListener {

    private final AVObject cardTodo;
    private final String typeTodo;
    private final Activity activity;
    private SwipeRefreshLayout msrlRefresh;
    private ListView listView;
    private MyListViewAdapter<CommentTodoBean> myListViewAdapter;
    private ArrayList<CommentTodoBean> moreData;

    public CommentPager(Activity activity, AVObject cardTodo, String typeTodo) {
        this.cardTodo = cardTodo;
        this.activity = activity;
        this.typeTodo = typeTodo;
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
        ArrayList<CommentTodoBean> commentTodoBeens = new ArrayList<>();
        myListViewAdapter = new MyListViewAdapter<CommentTodoBean>(commentTodoBeens, this) {
            @Override
            protected void onLoadMore(final MoreHolder moreHolder) {
                moreData = null;
                AVQuery<AVObject> query = new AVQuery<>(StringUtils.commentTodo);
                query.whereEqualTo(typeTodo, cardTodo);
                query.limit(7);
                query.include(StringUtils.avUser);
                query.skip(myListViewAdapter.getDatasSize());
                query.findInBackground(new FindCallback<AVObject>() {
                    @Override
                    public void done(List<AVObject> list, AVException e) {
                        if (UIUtils.hasException(e)) {
                            CommentPager.this.getmProgressBarListener().setProgressBar(moreData, moreHolder);
                            return;
                        }
                        moreData = new ArrayList<>();
                        for (AVObject object : list) {
                            try {
                                CommentTodoBean commentTodoBean = new CommentTodoBean();
                                commentTodoBean.cardTodoId = object.getString(StringUtils.cardTodoId);
                                commentTodoBean.commentTodo = object;
                                commentTodoBean.upvoteNum = object.getInt(StringUtils.upVote);
                                commentTodoBean.strComment = object.getString(StringUtils.strComment);
                                commentTodoBean.millisecsData = object.getCreatedAt().getTime();
                                AVObject avUser = object.getAVObject(StringUtils.avUser);
                                commentTodoBean.avUser = avUser;
                                commentTodoBean.nickName = avUser.getString(StringUtils.nickname);
                                commentTodoBean.iconAVFile = avUser.getAVFile(StringUtils.iconShow);
                                moreData.add(commentTodoBean);
                            } catch (Exception ex) {
                                continue;
                            }
                        }
                        CommentPager.this.getmProgressBarListener().setProgressBar(moreData, moreHolder);
                    }
                });
            }

            @Override
            public BaseHolder getHolder() {
                return new CommentHolder(myListViewAdapter, activity, listView, false);
            }
        };
        listView.setAdapter(myListViewAdapter);
    }

    @Override
    public void onRefresh() {
        AVQuery<AVObject> query = new AVQuery<>(StringUtils.commentTodo);
        query.whereEqualTo(typeTodo, cardTodo);
        query.whereExists(typeTodo);
        query.limit(7);
        query.include(StringUtils.avUser);
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
                        CommentTodoBean commentTodoBean = new CommentTodoBean();
                        commentTodoBean.cardTodoId = object.getString(StringUtils.cardTodoId);
                        commentTodoBean.commentTodo = object;
                        commentTodoBean.upvoteNum = object.getInt(StringUtils.upVote);
                        commentTodoBean.strComment = object.getString(StringUtils.strComment);
                        commentTodoBean.millisecsData = object.getCreatedAt().getTime();
                        AVObject avUser = object.getAVObject(StringUtils.avUser);
                        commentTodoBean.avUser = avUser;
                        commentTodoBean.nickName = avUser.getString(StringUtils.nickname);
                        commentTodoBean.iconAVFile = avUser.getAVFile(StringUtils.iconShow);
                        moreData.add(commentTodoBean);
                    } catch (Exception ex) {
                        continue;
                    }
                }
                myListViewAdapter.setDatas(moreData);
                listView.setAdapter(myListViewAdapter);
                msrlRefresh.setRefreshing(false);
            }
        });
    }

    public MyListViewAdapter<CommentTodoBean> getMyListViewAdapter() {
        return myListViewAdapter;
    }
}
