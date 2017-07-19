package com.nannan.nannan.view.pager;

import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.ListView;

import com.nannan.nannan.R;
import com.nannan.nannan.utils.UIUtils;

/**
 * Created by MaxwellCNZ on 2017/3/23.
 */

public class MinePager extends BasePager implements SwipeRefreshLayout.OnRefreshListener{

    private ListView listView;
    private SwipeRefreshLayout msrlRefresh;

    public MinePager() {
        super();
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
        getMineDataFromServer(listView, MinePager.this);
    }

    public void getMineDataFromServer(ListView listView, BasePager basePager){

    }

    @Override
    public void onRefresh() {
        mineRefresh(msrlRefresh);
    }

    public void mineRefresh(SwipeRefreshLayout refresh) {

    }
}
