package com.nannan.nannan.view.pager;

import android.app.Activity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.nannan.nannan.R;
import com.nannan.nannan.utils.FillPagerAdapterUtils;
import com.nannan.nannan.utils.UIUtils;
import com.nannan.nannan.view.widget.NoScrollViewPager;

import java.util.ArrayList;

/**
 * Created by MaxwellCNZ on 2017/3/7.
 */

public class LetterPager extends BasePager {

    private TextView mtvReceiver;
    private TextView mtvSend;
    private NoScrollViewPager mvpLetter;
    private ArrayList<BasePager> pagers;

    public LetterPager(Activity activity) {
        super();
        initViewPager(activity);
    }

    @Override
    public View initView() {
        View inflate = UIUtils.inflate(R.layout.pager_indicator);
        mtvReceiver = (TextView) inflate.findViewById(R.id.tv_left_indicator);
        mtvSend = (TextView) inflate.findViewById(R.id.tv_right_indicator);
        mvpLetter = (NoScrollViewPager) inflate.findViewById(R.id.vp_viewpager_indicator);
        mtvReceiver.setText("收件箱");
        mtvSend.setText("发件箱");
        mtvReceiver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mvpLetter.setCurrentItem(0);
                mtvReceiver.setBackgroundResource(R.mipmap.indicator);
                mtvSend.setBackgroundResource(R.color.Transparency);
            }
        });
        mtvSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mvpLetter.setCurrentItem(1);
                mtvSend.setBackgroundResource(R.mipmap.indicator);
                mtvReceiver.setBackgroundResource(R.color.Transparency);
            }
        });
        return inflate;
    }

    private void initViewPager(Activity activity) {
        pagers = new ArrayList<>();
        pagers.add(new ReceiverPager(activity));
        pagers.add(new SendPager(activity));
        mvpLetter.setAdapter(new FillPagerAdapterUtils(pagers));
        pagers.get(0).initData();
//        pagers.get(1).initData();
        mvpLetter.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                pagers.get(position).initData();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        /*//设置listview的滚动监听
        followeePager.getListView().setOnScrollListener(new AbsListView.OnScrollListener() {
            boolean hasSetMenuShow = false;
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                if (totalItemCount > 7){
                    if (!hasSetMenuShow){
                        hasSetMenuShow = true;
                        setMenuShow(followeePager.getListView());
                    }
                }else {
                    if (hasSetMenuShow){
                        hasSetMenuShow = false;
                        cancelMenuShow(followeePager.getListView());
                    }
                }
            }
        });

        //设置listview的滚动监听
        followerPager.getListView().setOnScrollListener(new AbsListView.OnScrollListener() {
            boolean hasSetMenuShow = false;
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                if (totalItemCount > 7){
                    if (!hasSetMenuShow){
                        hasSetMenuShow = true;
                        setMenuShow(followerPager.getListView());
                    }
                }else {
                    if (hasSetMenuShow){
                        hasSetMenuShow = false;
                        cancelMenuShow(followerPager.getListView());
                    }
                }
            }
        });*/
    }

    @Override
    public void initData() {
        mvpLetter.setCurrentItem(0);
        mtvReceiver.setBackgroundResource(R.mipmap.indicator);
        mtvSend.setBackgroundResource(R.color.Transparency);
    }
}
