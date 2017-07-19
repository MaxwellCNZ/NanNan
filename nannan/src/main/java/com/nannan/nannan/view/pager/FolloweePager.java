package com.nannan.nannan.view.pager;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.nannan.nannan.R;
import com.nannan.nannan.bean.AVUserInfo;
import com.nannan.nannan.utils.StringUtils;
import com.nannan.nannan.utils.UIUtils;
import com.nannan.nannan.view.activity.user.UserActivity;
import com.nannan.nannan.view.adapter.MyListViewAdapter;
import com.nannan.nannan.view.holder.AVUserInfoHolder;
import com.nannan.nannan.view.holder.BaseHolder;
import com.nannan.nannan.view.holder.MoreHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MaxwellCNZ on 2017/4/17.
 */

public class FolloweePager extends BasePager implements SwipeRefreshLayout.OnRefreshListener {


    private final String avUserId;
    private SwipeRefreshLayout msrlRefresh;
    private ListView listView;
    private MyListViewAdapter<AVUserInfo> myListViewAdapter;
    private ArrayList<AVUserInfo> moreData;

    public FolloweePager(String avUserId) {
        super();
        this.avUserId = avUserId;
    }
    public ListView getListView(){
        return listView;
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

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == myListViewAdapter.getCount()-1 ){
                    return;
                }
                AVUserInfo item = myListViewAdapter.getItem(position);
                Intent intent = new Intent(UIUtils.getContext(), UserActivity.class);
                intent.putExtra(StringUtils.avUserId, item.avUserPointerId);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                UIUtils.getContext().startActivity(intent);
            }
        });
        return inflate;
    }

    @Override
    public void initData() {
        //查询关注者
        ArrayList<AVUserInfo> avUserInfos = new ArrayList<>();
        myListViewAdapter = new MyListViewAdapter<AVUserInfo>(avUserInfos, this) {
            @Override
            protected void onLoadMore(final MoreHolder moreHolder) {
                moreData = null;
                AVQuery<AVUser> followeeQuery = AVUser.followeeQuery(avUserId, AVUser.class);
                followeeQuery.include(StringUtils.followee);
                followeeQuery.orderByDescending(StringUtils.createdAt);
                followeeQuery.limit(7);
                followeeQuery.skip(myListViewAdapter.getDatasSize());
                followeeQuery.findInBackground(new FindCallback<AVUser>() {
                    @Override
                    public void done(List<AVUser> avObjects, AVException e) {
                        if (UIUtils.hasException(e)){
                            getmProgressBarListener().setProgressBar(moreData, moreHolder);
                            return;
                        }
                        moreData = new ArrayList<>();
                        for (AVUser object : avObjects){
                            try {
                                AVUserInfo avUserInfo = new AVUserInfo();
                                avUserInfo.avUserPointerId = object.getObjectId();
                                avUserInfo.iconAVFile = object.getAVFile(StringUtils.iconShow);
                                avUserInfo.nickName = object.getString(StringUtils.nickname);
                                avUserInfo.introduce = object.getString(StringUtils.introduce);
                                moreData.add(avUserInfo);
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
                return new AVUserInfoHolder();
            }
        };
        listView.setAdapter(myListViewAdapter);
    }

    @Override
    public void onRefresh() {

        AVQuery<AVUser> followeeQuery = AVUser.followeeQuery(avUserId, AVUser.class);
        followeeQuery.include(StringUtils.followee);
        followeeQuery.orderByDescending(StringUtils.createdAt);
        followeeQuery.limit(7);
        followeeQuery.findInBackground(new FindCallback<AVUser>() {
            @Override
            public void done(List<AVUser> avObjects, AVException e) {
                if (e!=null){
                    msrlRefresh.setRefreshing(false);
                    return;
                }
//                moreData.clear();
                moreData.clear();
                for (AVUser object : avObjects){
                    try {
                        AVUserInfo avUserInfo = new AVUserInfo();
                        avUserInfo.avUserPointerId = object.getObjectId();
                        avUserInfo.iconAVFile = object.getAVFile(StringUtils.iconShow);
                        avUserInfo.nickName = object.getString(StringUtils.nickname);
                        avUserInfo.introduce = object.getString(StringUtils.introduce);
                        moreData.add(avUserInfo);
                    }catch (Exception ex){
                        continue;
                    }
                }
//                moreData = new ArrayList<>();

                msrlRefresh.setRefreshing(false);
                myListViewAdapter.setDatas(moreData);
                listView.setAdapter(myListViewAdapter);
            }
        });
    }
}
