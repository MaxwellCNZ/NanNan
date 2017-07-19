package com.nannan.nannan.view.pager;

import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.ListView;

import com.nannan.nannan.R;
import com.nannan.nannan.utils.UIUtils;

/**
 * Created by MaxwellCNZ on 2017/3/23.
 */

public class DiscoverPager extends BasePager implements SwipeRefreshLayout.OnRefreshListener{
    private ListView listView;
    private SwipeRefreshLayout msrlRefresh;

//    private ListView listView;

    public DiscoverPager() {
        super();
    }

    @Override
    public View initView() {
        //强烈注意，SwipeRefreshLayout只能监听非abstract的类，如果你监听的是abstract类，该SwipeRefreshLayout没有反应
        //并且，如果你监听成功了，就不用担心SwipeRefreshLayout与listview滑动冲突的问题，他会自行判断是否滑动到顶然后选择刷新
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
        getDiscoverDataFromServer(listView, DiscoverPager.this);
    }

    /**
     * 从服务器那里获取数据
     * @param listView
     * @param basePager
     */
    public void getDiscoverDataFromServer(ListView listView, BasePager basePager){

    }

    @Override
    public void onRefresh() {
        discoverRefresh(msrlRefresh);
    }

    /**
     * 下拉刷新逻辑
     * @param refresh
     */
    public void discoverRefresh(SwipeRefreshLayout refresh) {
    }
}
