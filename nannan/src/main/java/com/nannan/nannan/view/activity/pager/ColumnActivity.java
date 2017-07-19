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
import com.nannan.nannan.utils.UIUtils;
import com.nannan.nannan.view.pager.BasePager;
import com.nannan.nannan.view.pager.ColumnDiscoverPager;
import com.nannan.nannan.view.pager.ColumnMinePager;
import com.nannan.nannan.view.widget.NoScrollViewPager;

import java.util.ArrayList;

/**
 * Created by MaxwellCNZ on 2017/5/9.
 */

public class ColumnActivity extends Activity implements View.OnClickListener{

    private NoScrollViewPager mFunctionViewPager;
    private ImageButton mibQuit;
    private ImageButton mibAdd;
    private TextView mtvDiscover;
    private TextView mtvMine;
    private ArrayList<BasePager> pagers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_function);
        StatusBarUtils.setWindowStatusBarColor(this, R.color.MasterColor);
        initView();
        initViewPager();
    }

    private void initView() {
        mFunctionViewPager = (NoScrollViewPager) findViewById(R.id.vp_function_viewpager);
        mibQuit = (ImageButton) findViewById(R.id.ib_quit_user_edit);
        mibAdd = (ImageButton) findViewById(R.id.ib_add_function);
        TextView mtvType = (TextView) findViewById(R.id.tv_type_function);
        mtvType.setText("福利社");
        mtvDiscover = (TextView) findViewById(R.id.tv_discover_function);
        mtvMine = (TextView) findViewById(R.id.tv_mine_function);
        mtvDiscover.setOnClickListener(this);
        mtvMine.setOnClickListener(this);
        mibQuit.setOnClickListener(this);
        mibAdd.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ib_quit_user_edit:
                finish();
                break;
            case R.id.ib_add_function:
                Intent intent = new Intent(this, SetUpColumnActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_discover_function:
                mtvDiscover.setBackgroundColor(UIUtils.getColorId(R.color.BackgroundColor3));
                mtvMine.setBackgroundColor(UIUtils.getColorId(R.color.BackgroundColor2));
                mFunctionViewPager.setCurrentItem(0);
                break;
            case R.id.tv_mine_function:
                mtvDiscover.setBackgroundColor(UIUtils.getColorId(R.color.BackgroundColor2));
                mtvMine.setBackgroundColor(UIUtils.getColorId(R.color.BackgroundColor3));
                mFunctionViewPager.setCurrentItem(1);
                break;
            default:
                break;
        }
    }

    private void initViewPager() {
        pagers = new ArrayList<>();
        pagers.add(new ColumnDiscoverPager(this));
        pagers.add(new ColumnMinePager(this));
        mFunctionViewPager.setAdapter(new FillPagerAdapterUtils(pagers));
        pagers.get(0).initData();
        pagers.get(1).initData();
    }
}
