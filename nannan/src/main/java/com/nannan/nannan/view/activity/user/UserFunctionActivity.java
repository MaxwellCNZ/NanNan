package com.nannan.nannan.view.activity.user;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.nannan.nannan.R;
import com.nannan.nannan.utils.FillPagerAdapterUtils;
import com.nannan.nannan.utils.StringUtils;
import com.nannan.nannan.view.pager.BasePager;
import com.nannan.nannan.view.pager.UserCollectionPager;
import com.nannan.nannan.view.pager.UserMinePager;
import com.nannan.nannan.view.widget.NoScrollViewPager;

import java.util.ArrayList;

/**
 * Created by MaxwellCNZ on 2017/4/27.
 */

public class UserFunctionActivity extends Activity {

    private TextView mtvTodo;
    private TextView mtvCollection;
    private NoScrollViewPager mvpFunction;
    private ArrayList<BasePager> pagers;
    private String typeTodo;
    private String avUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pager_indicator);
        Intent intent = getIntent();
        typeTodo = intent.getStringExtra(StringUtils.typeTodo);
        avUserId = intent.getStringExtra(StringUtils.avUserId);
        initView();
        initData();
    }

    private void initData() {
        mtvTodo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mvpFunction.setCurrentItem(0);
                mtvTodo.setBackgroundResource(R.mipmap.indicator);
                mtvCollection.setBackgroundResource(R.color.Transparency);
            }
        });
        mtvCollection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mvpFunction.setCurrentItem(1);
                mtvCollection.setBackgroundResource(R.mipmap.indicator);
                mtvTodo.setBackgroundResource(R.color.Transparency);
            }
        });
    }

    private void initView() {
        mtvTodo =  (TextView) findViewById(R.id.tv_left_indicator);
        mtvCollection = (TextView) findViewById(R.id.tv_right_indicator);
        mvpFunction = (NoScrollViewPager) findViewById(R.id.vp_viewpager_indicator);
        mtvCollection.setText("ta 的收藏");
        mtvTodo.setText("ta 的 喃");
        initViewPager();
    }

    private void initViewPager() {
        pagers = new ArrayList<>();
        pagers.add(new UserMinePager(this, typeTodo, avUserId));
        pagers.add(new UserCollectionPager(this, typeTodo, avUserId));
        mvpFunction.setAdapter(new FillPagerAdapterUtils(pagers));
        pagers.get(1).initData();
//        pagers.get(1).initData();
    }
}
