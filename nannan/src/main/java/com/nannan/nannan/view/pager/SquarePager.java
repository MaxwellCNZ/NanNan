package com.nannan.nannan.view.pager;

import android.view.View;
import android.widget.AbsListView;
import android.widget.TextView;

import com.nannan.nannan.R;
import com.nannan.nannan.utils.FillPagerAdapterUtils;
import com.nannan.nannan.utils.UIUtils;
import com.nannan.nannan.view.activity.MainActivity;
import com.nannan.nannan.view.widget.NoScrollViewPager;

import java.util.ArrayList;

/**
 * Created by MaxwellCNZ on 2017/3/7.
 */

public class SquarePager extends BasePager {

    private NoScrollViewPager mvpSquare;
    private ArrayList<BasePager> pagers;
    private FriendsDynamicPager friendsDynamicPager;
    private CollectPager collectPager;
    private final MainActivity mActivity;
    private TextView tvDynamic;
    private TextView tvColection;
    private TextView tvWelfare;
    private WelfarePager welfarePager;

    public SquarePager(MainActivity activity) {
        super();
        mActivity = activity;
        setListener();
    }

    @Override
    public View initView() {
        View inflate = UIUtils.inflate(R.layout.pager_square);
        tvDynamic = (TextView) inflate.findViewById(R.id.tv_dynamic);
        tvWelfare = (TextView) inflate.findViewById(R.id.tv_welfare);
        tvColection = (TextView) inflate.findViewById(R.id.tv_colection);
        mvpSquare = (NoScrollViewPager) inflate.findViewById(R.id.vp_square_indicator);
        return inflate;
    }

    private void setListener() {
        tvDynamic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mvpSquare.setCurrentItem(0);
                tvDynamic.setBackgroundResource(R.mipmap.indicator);
                tvWelfare.setBackgroundResource(R.color.Transparency);
                tvColection.setBackgroundResource(R.color.Transparency);
            }
        });
        tvWelfare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mvpSquare.setCurrentItem(1);
                tvDynamic.setBackgroundResource(R.color.Transparency);
                tvWelfare.setBackgroundResource(R.mipmap.indicator);
                tvColection.setBackgroundResource(R.color.Transparency);
            }
        });
        tvColection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mvpSquare.setCurrentItem(2);
                tvDynamic.setBackgroundResource(R.color.Transparency);
                tvWelfare.setBackgroundResource(R.color.Transparency);
                tvColection.setBackgroundResource(R.mipmap.indicator);
            }
        });

//        //初始化viewpager
        pagers = new ArrayList<>();
        friendsDynamicPager = new FriendsDynamicPager(mActivity);
        welfarePager = new WelfarePager(mActivity);
        collectPager = new CollectPager(mActivity);
        pagers.add(friendsDynamicPager);
        pagers.add(welfarePager);
        pagers.add(collectPager);
        mvpSquare.setAdapter(new FillPagerAdapterUtils(pagers));
        pagers.get(0).initData();
        pagers.get(1).initData();

        //设置listview的滚动监听
        friendsDynamicPager.getListView().setOnScrollListener(new AbsListView.OnScrollListener() {
            boolean hasSetMenuShow = false;
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                    if (totalItemCount > 5){
                        if (!hasSetMenuShow){
                            hasSetMenuShow = true;
                            setMenuShow(friendsDynamicPager.getListView());
                        }
                    }else {
                        if (hasSetMenuShow){
                            hasSetMenuShow = false;
                            cancelMenuShow(friendsDynamicPager.getListView());
                        }
                    }
            }
        });

        welfarePager.getListView().setOnScrollListener(new AbsListView.OnScrollListener() {
            boolean hasSetMenuShow = false;
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                if (totalItemCount > 3){
                    if (!hasSetMenuShow){
                        hasSetMenuShow = true;
                        setMenuShow(welfarePager.getListView());
                    }
                }else {
                    if (hasSetMenuShow){
                        hasSetMenuShow = false;
                        cancelMenuShow(welfarePager.getListView());
                    }
                }
            }
        });
    }

    @Override
    public void initData() {
        mvpSquare.setCurrentItem(0);
        tvDynamic.setBackgroundResource(R.mipmap.indicator);
        tvWelfare.setBackgroundResource(R.color.Transparency);
        tvColection.setBackgroundResource(R.color.Transparency);
    }
}
