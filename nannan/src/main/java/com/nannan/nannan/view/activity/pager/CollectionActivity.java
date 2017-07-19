package com.nannan.nannan.view.activity.pager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.nannan.nannan.R;
import com.nannan.nannan.utils.FillPagerAdapterUtils;
import com.nannan.nannan.utils.StatusBarUtils;
import com.nannan.nannan.utils.StringUtils;
import com.nannan.nannan.view.pager.BasePager;
import com.nannan.nannan.view.pager.DetailCollectionColumnPager;
import com.nannan.nannan.view.pager.DetailCollectionPager;
import com.nannan.nannan.view.widget.NoScrollViewPager;

import java.util.ArrayList;

/**
 * Created by MaxwellCNZ on 2017/4/23.
 */

public class CollectionActivity extends Activity{

    private ImageButton mibQuit;
    private TextView mtvType;
    private NoScrollViewPager mvpCollection;
    private ArrayList<BasePager> pagers;
    private String cardType;
    private String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);
        StatusBarUtils.setWindowStatusBarColor(this, R.color.MasterColor2);
        Intent intent = getIntent();
        cardType = intent.getStringExtra(StringUtils.typeTodo);
        title = intent.getStringExtra(cardType);
        initView();
    }

    private void initView() {
        mibQuit = (ImageButton) findViewById(R.id.ib_quit_collection);
        mtvType = (TextView) findViewById(R.id.tv_type_collection);
        mvpCollection = (NoScrollViewPager) findViewById(R.id.vp_collection_viewpager);
        mtvType.setText(title);

        mibQuit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        pagers = new ArrayList<>();
        if (title.equals("福利社")){
            pagers.add(new DetailCollectionColumnPager(this, cardType));
        }else {
            pagers.add(new DetailCollectionPager(this, cardType));
        }
        mvpCollection.setAdapter(new FillPagerAdapterUtils(pagers));
        pagers.get(0).initData();
    }

}
