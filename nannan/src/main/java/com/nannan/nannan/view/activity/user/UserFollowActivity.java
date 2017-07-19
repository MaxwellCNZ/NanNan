package com.nannan.nannan.view.activity.user;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.nannan.nannan.R;
import com.nannan.nannan.utils.FillPagerAdapterUtils;
import com.nannan.nannan.utils.StatusBarUtils;
import com.nannan.nannan.utils.StringUtils;
import com.nannan.nannan.view.pager.BasePager;
import com.nannan.nannan.view.pager.FolloweePager;
import com.nannan.nannan.view.pager.FollowerPager;
import com.nannan.nannan.view.widget.NoScrollViewPager;

import java.util.ArrayList;

/**
 * Created by MaxwellCNZ on 2017/4/27.
 */

public class UserFollowActivity extends Activity implements OnRefreshListener{

    private ImageButton mibQuit;
    private TextView mtvFollow;
    private String followType;
    private String avUserId;
    private NoScrollViewPager mvpFollow;
    private ArrayList<BasePager> pagers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow);
        initView();
        initData();
    }

    private void initData() {
        Intent intent = getIntent();
        followType = intent.getStringExtra(StringUtils.follow);
        avUserId = intent.getStringExtra(StringUtils.avUserId);

        mtvFollow.setText(followType);
        mibQuit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initViewPager();
    }

    private void initViewPager() {
        pagers = new ArrayList<>();
        if (followType.equals(StringUtils.followeeType)){
            pagers.add(new FolloweePager(avUserId));
        }else {
            pagers.add(new FollowerPager(avUserId));
        }
        mvpFollow.setAdapter(new FillPagerAdapterUtils(pagers));
        pagers.get(0).initData();

    }

    private void initView() {
        StatusBarUtils.setWindowStatusBarColor(this, R.color.MasterColor2);
        mibQuit = (ImageButton) findViewById(R.id.ib_quit_user_follow);
        mtvFollow = (TextView) findViewById(R.id.tv_follow);
        mvpFollow = (NoScrollViewPager) findViewById(R.id.vp_viewpager_follow);
    }

    @Override
    public void onRefresh() {

    }
}
