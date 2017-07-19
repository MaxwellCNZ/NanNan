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
import com.nannan.nannan.bean.HomeCardTodoBean;
import com.nannan.nannan.utils.StringUtils;
import com.nannan.nannan.utils.UIUtils;
import com.nannan.nannan.view.adapter.MyListViewAdapter;
import com.nannan.nannan.view.holder.BaseHolder;
import com.nannan.nannan.view.holder.FunctionHolder;
import com.nannan.nannan.view.holder.MoreHolder;

import java.util.ArrayList;
import java.util.List;

import static com.nannan.nannan.utils.UIUtils.avUser;
import static com.nannan.nannan.view.pager.HomePager.typeNum2cardTodoType;

/**
 * Created by MaxwellCNZ on 2017/4/21.
 */

public class FriendsDynamicPager extends BasePager implements SwipeRefreshLayout.OnRefreshListener {

    private final Activity activity;
    private SwipeRefreshLayout msrlRefresh;
    private ListView listView;
    private MyListViewAdapter myListViewAdapter;
    private ArrayList<HomeCardTodoBean> moreData;

    public FriendsDynamicPager(Activity activity) {
        super();
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

    /**
     * 把listview传出去监听菜单的显示与消失
     *
     * @return
     */
    public ListView getListView() {
        return listView;
    }

    @Override
    public void initData() {
        ArrayList<HomeCardTodoBean> homeCardTodoBeen = new ArrayList<>();
        myListViewAdapter = new MyListViewAdapter<HomeCardTodoBean>(homeCardTodoBeen, this) {
            @Override
            protected void onLoadMore(final MoreHolder moreHolder) {
                moreData = null;
                AVQuery<AVObject> query = new AVQuery<>(StringUtils.friendsDynamic);
                query.whereEqualTo(StringUtils.follower, avUser);
                query.orderByDescending(StringUtils.createdAt);
                query.limit(5);
                query.skip(myListViewAdapter.getDatasSize());
                query.include(StringUtils.artCardTodo);
                query.include(StringUtils.learnCardTodo);
                query.include(StringUtils.musicCardTodo);
                query.include(StringUtils.sportCardTodo);
                query.include(StringUtils.mindsCardTodo);
                query.include(StringUtils.leisureCardTodo);
                query.include(StringUtils.artCardTodo_avUserPointer);
                query.include(StringUtils.learnCardTodo_avUserPointer);
                query.include(StringUtils.musicCardTodo_avUserPointer);
                query.include(StringUtils.sportCardTodo_avUserPointer);
                query.include(StringUtils.mindsCardTodo_avUserPointer);
                query.include(StringUtils.leisureCardTodo_avUserPointer);
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
                                HomeCardTodoBean cardTodoBean = new HomeCardTodoBean();
                                String typeNum = object.getString(StringUtils.typeNum);
                                cardTodoBean.sharetor = object.getAVObject(StringUtils.share_tor);
                                String typeTodo = typeNum2cardTodoType(typeNum);
                                AVObject cardTodo = object.getAVObject(typeTodo);
                                cardTodoBean.cardTodo = cardTodo;
                                cardTodoBean.typeTodo = typeTodo;
                                cardTodoBean.strContent = cardTodo.getString(StringUtils.strContent);
                                //将时间转为毫秒
                                cardTodoBean.millisecsData = cardTodo.getCreatedAt().getTime();
                                cardTodoBean.picAVFile = cardTodo.getAVFile(StringUtils.picPhotograph);

                                AVObject avUser = cardTodo.getAVObject(StringUtils.avUserPointer);
                                cardTodoBean.avUser = avUser;
                                cardTodoBean.nickName = avUser.getString(StringUtils.nickname);
                                cardTodoBean.introduce = avUser.getString(StringUtils.introduce);
                                cardTodoBean.iconAVFile = avUser.getAVFile(StringUtils.iconShow);

                                moreData.add(cardTodoBean);
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
                return new FunctionHolder(activity, listView, false);
            }
        };
        listView.setAdapter(myListViewAdapter);

    }

    @Override
    public void onRefresh() {
        AVQuery<AVObject> query = new AVQuery<>(StringUtils.friendsDynamic);
        query.whereEqualTo(StringUtils.follower, avUser);
        query.orderByDescending(StringUtils.createdAt);
        query.limit(5);
        query.include(StringUtils.artCardTodo);
        query.include(StringUtils.learnCardTodo);
        query.include(StringUtils.musicCardTodo);
        query.include(StringUtils.sportCardTodo);
        query.include(StringUtils.mindsCardTodo);
        query.include(StringUtils.leisureCardTodo);
        query.include(StringUtils.artCardTodo_avUserPointer);
        query.include(StringUtils.learnCardTodo_avUserPointer);
        query.include(StringUtils.musicCardTodo_avUserPointer);
        query.include(StringUtils.sportCardTodo_avUserPointer);
        query.include(StringUtils.mindsCardTodo_avUserPointer);
        query.include(StringUtils.leisureCardTodo_avUserPointer);
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
                        String typeNum = object.getString(StringUtils.typeNum);
                        cardTodoBean.sharetor = object.getAVObject(StringUtils.share_tor);
                        String typeTodo = typeNum2cardTodoType(typeNum);
                        AVObject cardTodo = object.getAVObject(typeTodo);
                        cardTodoBean.cardTodo = cardTodo;
                        cardTodoBean.typeTodo = typeTodo;
                        cardTodoBean.strContent = cardTodo.getString(StringUtils.strContent);
                        //将时间转为毫秒
                        cardTodoBean.millisecsData = cardTodo.getCreatedAt().getTime();
                        cardTodoBean.picAVFile = cardTodo.getAVFile(StringUtils.picPhotograph);

                        AVObject avUser = cardTodo.getAVObject(StringUtils.avUserPointer);
                        cardTodoBean.avUser = avUser;
                        cardTodoBean.nickName = avUser.getString(StringUtils.nickname);
                        cardTodoBean.introduce = avUser.getString(StringUtils.introduce);
                        cardTodoBean.iconAVFile = avUser.getAVFile(StringUtils.iconShow);

                        moreData.add(cardTodoBean);
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
