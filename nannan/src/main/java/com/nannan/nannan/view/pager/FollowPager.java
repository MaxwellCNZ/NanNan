package com.nannan.nannan.view.pager;

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

public class FollowPager extends BasePager{

    private NoScrollViewPager mvpAttention;
    private TextView mtvFollower;
    private TextView mtvFollowee;
    private ArrayList<BasePager> pagers;
    private FolloweePager followeePager;
    private FollowerPager followerPager;

    public FollowPager() {
        super();
        //初始化viewpager
        initViewPager();
    }

    @Override
    public View initView() {
        View inflate = UIUtils.inflate(R.layout.pager_indicator);
        mtvFollowee = (TextView) inflate.findViewById(R.id.tv_left_indicator);
        mtvFollower = (TextView) inflate.findViewById(R.id.tv_right_indicator);
        mvpAttention = (NoScrollViewPager) inflate.findViewById(R.id.vp_viewpager_indicator);

        mtvFollowee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mvpAttention.setCurrentItem(0);
                mtvFollowee.setBackgroundResource(R.mipmap.indicator);
                mtvFollower.setBackgroundResource(R.color.Transparency);
            }
        });
        mtvFollower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mvpAttention.setCurrentItem(1);
                mtvFollower.setBackgroundResource(R.mipmap.indicator);
                mtvFollowee.setBackgroundResource(R.color.Transparency);
            }
        });
        return inflate;
    }

    private void initViewPager() {
        pagers = new ArrayList<>();
        followeePager = new FolloweePager(UIUtils.avUser.getObjectId());
        followerPager = new FollowerPager(UIUtils.avUser.getObjectId());
        pagers.add(followeePager);
        pagers.add(followerPager);
        mvpAttention.setAdapter(new FillPagerAdapterUtils(pagers));
        pagers.get(0).initData();
//        pagers.get(1).initData();
        mvpAttention.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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
    }

    @Override
    public void initData() {
//        pagers.get(0).initData();
        mvpAttention.setCurrentItem(0);
        mtvFollowee.setBackgroundResource(R.mipmap.indicator);
        mtvFollower.setBackgroundResource(R.color.Transparency);
    }
}
