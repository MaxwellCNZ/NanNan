package com.nannan.nannan.view.pager;

import android.content.Intent;
import android.view.View;

import com.nannan.nannan.R;
import com.nannan.nannan.utils.StringUtils;
import com.nannan.nannan.utils.UIUtils;
import com.nannan.nannan.view.activity.pager.CollectionActivity;
import com.nannan.nannan.view.activity.MainActivity;
import com.nannan.nannan.view.widget.WaterWaveImageButton;

/**
 * Created by MaxwellCNZ on 2017/4/21.
 */

public class CollectPager extends BasePager implements View.OnClickListener{

    private WaterWaveImageButton mwwivColumn;
    private WaterWaveImageButton mwwivArt;
    private WaterWaveImageButton mwwivMind;
    private WaterWaveImageButton mwwivLearn;
    private WaterWaveImageButton mwwivSport;
    private WaterWaveImageButton mwwivLeisure;
    private WaterWaveImageButton mwwivMusic;
    private final MainActivity mActivity;

    public CollectPager(MainActivity activity) {
        super();
        mActivity = activity;
        mwwivArt.setOnClickListener(this);
        mwwivMind.setOnClickListener(this);
        mwwivLearn.setOnClickListener(this);
        mwwivSport.setOnClickListener(this);
        mwwivLeisure.setOnClickListener(this);
        mwwivMusic.setOnClickListener(this);
        mwwivColumn.setOnClickListener(this);
    }

    @Override
    public View initView() {
        View inflate = UIUtils.inflate(R.layout.pager_collection);
        mwwivColumn = (WaterWaveImageButton) inflate.findViewById(R.id.wwiv_column_home);

        mwwivArt = (WaterWaveImageButton) inflate.findViewById(R.id.wwiv_art);
        mwwivMind = (WaterWaveImageButton) inflate.findViewById(R.id.wwiv_mind);
        mwwivLearn = (WaterWaveImageButton) inflate.findViewById(R.id.wwiv_learn);
        mwwivSport = (WaterWaveImageButton) inflate.findViewById(R.id.wwiv_sport);
        mwwivLeisure = (WaterWaveImageButton) inflate.findViewById(R.id.wwiv_leisure);
        mwwivMusic = (WaterWaveImageButton) inflate.findViewById(R.id.wwiv_music);

        return inflate;
    }

    @Override
    public void initData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.wwiv_column_home:
                Intent columnIntent = new Intent(mActivity, CollectionActivity.class);
                columnIntent.putExtra(StringUtils.typeTodo, StringUtils.columnTodo);
                columnIntent.putExtra(StringUtils.columnTodo, "福利社");
                mActivity.startActivity(columnIntent);
                break;
            case R.id.wwiv_art:
                Intent artIntent = new Intent(mActivity, CollectionActivity.class);
                artIntent.putExtra(StringUtils.typeTodo, StringUtils.artCardTodo);
                artIntent.putExtra(StringUtils.artCardTodo, "艺术");
                mActivity.startActivity(artIntent);
                break;
            case R.id.wwiv_learn:
                Intent learnIntent = new Intent(mActivity, CollectionActivity.class);
                learnIntent.putExtra(StringUtils.typeTodo, StringUtils.learnCardTodo);
                learnIntent.putExtra(StringUtils.learnCardTodo, "学习");
                mActivity.startActivity(learnIntent);
                break;
            case R.id.wwiv_leisure:
                Intent leisureIntent = new Intent(mActivity, CollectionActivity.class);
                leisureIntent.putExtra(StringUtils.typeTodo, StringUtils.leisureCardTodo);
                leisureIntent.putExtra(StringUtils.leisureCardTodo, "闲暇");
                mActivity.startActivity(leisureIntent);
                break;
            case R.id.wwiv_mind:
                Intent mindIntent = new Intent(mActivity, CollectionActivity.class);
                mindIntent.putExtra(StringUtils.typeTodo, StringUtils.mindsCardTodo);
                mindIntent.putExtra(StringUtils.mindsCardTodo, "脑洞");
                mActivity.startActivity(mindIntent);
                break;
            case R.id.wwiv_sport:
                Intent sportIntent = new Intent(mActivity, CollectionActivity.class);
                sportIntent.putExtra(StringUtils.typeTodo, StringUtils.sportCardTodo);
                sportIntent.putExtra(StringUtils.sportCardTodo, "运动");
                mActivity.startActivity(sportIntent);
                break;
            case R.id.wwiv_music:
                Intent musicIntent = new Intent(mActivity, CollectionActivity.class);
                musicIntent.putExtra(StringUtils.typeTodo, StringUtils.musicCardTodo);
                musicIntent.putExtra(StringUtils.musicCardTodo, "音乐");
                mActivity.startActivity(musicIntent);
                break;
            default:
                break;
        }
    }
}
