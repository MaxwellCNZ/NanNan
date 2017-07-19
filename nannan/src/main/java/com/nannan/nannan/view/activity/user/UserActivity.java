package com.nannan.nannan.view.activity.user;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.avos.avoscloud.AVObject;
import com.nannan.nannan.R;
import com.nannan.nannan.utils.FillPagerAdapterUtils;
import com.nannan.nannan.view.pager.BasePager;
import com.nannan.nannan.view.pager.UserPager;
import com.nannan.nannan.view.widget.NoScrollViewPager;

import java.util.ArrayList;

import static com.nannan.nannan.utils.StringUtils.avUserId;

/**
 * Created by MaxwellCNZ on 2017/4/23.
 */

public class UserActivity extends AppCompatActivity {

    private String avUserID;
    private AVObject avUser;
    private ArrayList<BasePager> pagers;
    private NoScrollViewPager mvpViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        Intent intent = getIntent();
        avUserID = intent.getStringExtra(avUserId);
        avUser = AVObject.createWithoutData("_User", avUserID);
        initView();
    }

    private void initView() {
        mvpViewPager = (NoScrollViewPager) findViewById(R.id.vp_user_viewpager);
        pagers = new ArrayList<>();
        pagers.add(new UserPager(avUser,this));
        mvpViewPager.setAdapter(new FillPagerAdapterUtils(pagers));
        pagers.get(0).initData();
    }
}
