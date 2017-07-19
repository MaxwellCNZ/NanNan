package com.nannan.nannan.view.activity.pager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.nannan.nannan.R;
import com.nannan.nannan.bean.HomeCardTodoBean;
import com.nannan.nannan.utils.FillPagerAdapterUtils;
import com.nannan.nannan.utils.SharedPreferenceUtils;
import com.nannan.nannan.utils.StatusBarUtils;
import com.nannan.nannan.utils.StringUtils;
import com.nannan.nannan.utils.UIUtils;
import com.nannan.nannan.view.adapter.MyListViewAdapter;
import com.nannan.nannan.view.holder.BaseHolder;
import com.nannan.nannan.view.holder.FunctionHolder;
import com.nannan.nannan.view.holder.HomeHolder;
import com.nannan.nannan.view.holder.MoreHolder;
import com.nannan.nannan.view.pager.BasePager;
import com.nannan.nannan.view.pager.DiscoverPager;
import com.nannan.nannan.view.pager.MinePager;
import com.nannan.nannan.view.widget.NoScrollViewPager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.nannan.nannan.utils.UIUtils.avUser;

/**
 * Created by MaxwellCNZ on 2017/3/22.
 */

public class FunctionActivity extends Activity implements View.OnClickListener {

    private ImageButton mibQuit;
    private ImageButton mibAdd;
    private NoScrollViewPager mFunctionViewPager;
    private ArrayList<BasePager> mPagers;
    private TextView mtvType;
    private TextView mtvDiscover;
    private TextView mtvMine;
    public SharedPreferenceUtils sp;
    public int recordDeleteNum = 0;
    private ArrayList<HomeCardTodoBean> moreDiscoverData;
    private MyListViewAdapter<HomeCardTodoBean> myDiscoverListViewAdapter;
    private ArrayList<HomeCardTodoBean> moreMineData;
    private MyListViewAdapter<HomeCardTodoBean> myMineListViewAdapter;
    private ListView discoverListview;
    private ListView mineListview;
    private String cardType;
    private String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_function);
        sp = new SharedPreferenceUtils();
        StatusBarUtils.setWindowStatusBarColor(this, R.color.MasterColor);
        initView();
        initViewPager();
    }

    /**
     * 绑定viewpager
     */
    private void initViewPager() {
        mPagers = new ArrayList<>();
        mPagers.add(new DiscoverPager() {
            @Override
            public void getDiscoverDataFromServer(ListView listView, BasePager basePager) {
                FunctionActivity.this.getDiscoverDataFromServer(listView, basePager);
            }

            @Override
            public void discoverRefresh(SwipeRefreshLayout refresh) {
                FunctionActivity.this.discoverRefresh(refresh);
            }
        });
        mPagers.add(new MinePager() {
            @Override
            public void getMineDataFromServer(ListView listView, BasePager basePager) {
                FunctionActivity.this.getMineDataFromServer(listView, basePager);
            }

            @Override
            public void mineRefresh(SwipeRefreshLayout refresh) {
                FunctionActivity.this.mineRefresh(refresh);
            }
        });
        mFunctionViewPager.setAdapter(new FillPagerAdapterUtils(mPagers));
        mPagers.get(0).initData();
        mPagers.get(1).initData();
    }

    /**
     * 通过网络来加载数据
     */
    public void getDiscoverDataFromServer(final ListView listView, final BasePager basePager){
        discoverListview = listView;
        ArrayList<HomeCardTodoBean> cardTodoBeans = new ArrayList<>();
        myDiscoverListViewAdapter = new MyListViewAdapter<HomeCardTodoBean>(cardTodoBeans, basePager) {

            @Override
            protected void onLoadMore(final MoreHolder moreHolder) {
                moreDiscoverData = null;
                AVQuery<AVObject> query = new AVQuery<>(cardType);
                query.orderByDescending(StringUtils.createdAt);
                query.limit(3);
                query.include(StringUtils.avUserPointer);
                query.skip(myDiscoverListViewAdapter.getDatasSize());
                query.findInBackground(new FindCallback<AVObject>() {
                    @Override
                    public void done(List<AVObject> list, AVException e) {
                        if (UIUtils.hasException(e)) {
                            basePager.getmProgressBarListener().setProgressBar(moreDiscoverData, moreHolder);
                            return;
                        }
                        moreDiscoverData = new ArrayList<>();
                        for (AVObject object : list) {
                            try {
                                HomeCardTodoBean cardTodoBean = new HomeCardTodoBean();
                                cardTodoBean.cardTodo = object;
                                cardTodoBean.typeTodo = cardType;
                                cardTodoBean.avUserPointer = object.getAVObject(StringUtils.avUserPointer);
                                cardTodoBean.strContent = object.getString(StringUtils.strContent);
                                cardTodoBean.millisecsData = object.getCreatedAt().getTime();
                                AVObject avUser = object.getAVObject(StringUtils.avUserPointer);
                                cardTodoBean.avUser = avUser;
                                cardTodoBean.nickName = avUser.getString(StringUtils.nickname);
                                cardTodoBean.introduce = avUser.getString(StringUtils.introduce);
                                cardTodoBean.iconAVFile = avUser.getAVFile(StringUtils.iconShow);
                                cardTodoBean.picAVFile = object.getAVFile(StringUtils.picPhotograph);
                                moreDiscoverData.add(cardTodoBean);
                            }catch (Exception ex){
                                continue;
                            }
                        }
                        basePager.getmProgressBarListener().setProgressBar(moreDiscoverData, moreHolder);
                    }
                });
            }

            @Override
            public BaseHolder getHolder() {
                return new FunctionHolder(FunctionActivity.this, listView, false);
            }
        };
        listView.setAdapter(myDiscoverListViewAdapter);
    }

    public void getMineDataFromServer(final ListView listView, final BasePager basePager){
        mineListview = listView;
        ArrayList<HomeCardTodoBean> cardTodoBeans = new ArrayList<>();
        myMineListViewAdapter = new MyListViewAdapter<HomeCardTodoBean>(cardTodoBeans, basePager) {
            @Override
            protected void onLoadMore(final MoreHolder moreHolder) {
                moreMineData = null;
                AVQuery<AVObject> query = new AVQuery<>(StringUtils.cardMiddleTable);
                query.whereExists(cardType);
                query.whereEqualTo(StringUtils.avUser, avUser);
                query.orderByDescending(StringUtils.createdAt);
                query.limit(3);
                query.include(cardType);
                query.selectKeys(Arrays.asList(cardType));
                query.skip(myMineListViewAdapter.getDatasSize());
                query.findInBackground(new FindCallback<AVObject>() {
                    @Override
                    public void done(List<AVObject> list, AVException e) {
                        if (UIUtils.hasException(e)) {
                            basePager.getmProgressBarListener().setProgressBar(moreMineData, moreHolder);
                            return;
                        }
                        moreMineData = new ArrayList<>();

                        for (AVObject object : list) {
                            try {
                                HomeCardTodoBean cardTodoBean = new HomeCardTodoBean();
                                cardTodoBean.cardMiddle = object;
                                AVObject cardTodo = object.getAVObject(cardType);
                                cardTodoBean.upVoteNum = cardTodo.getInt(StringUtils.upVote);
                                cardTodoBean.cardTodo = cardTodo;
                                cardTodoBean.typeTodo = cardType;
                                cardTodoBean.strContent = cardTodo.getString(StringUtils.strContent);
                                cardTodoBean.millisecsData = cardTodo.getCreatedAt().getTime();
                                cardTodoBean.nickName = sp.getString(StringUtils.nickname);
                                cardTodoBean.introduce = sp.getString(StringUtils.introduce);
                                cardTodoBean.iconUri = StringUtils.getCachePicturePath() + "/" +
                                        StringUtils.avObjectIdTransitionPicForm(sp.getString(StringUtils.iconAVObjectId));
                                cardTodoBean.picAVFile = cardTodo.getAVFile(StringUtils.picPhotograph);
                                moreMineData.add(cardTodoBean);
                            }catch (Exception ex){
                                continue;
                            }

                        }
                        basePager.getmProgressBarListener().setProgressBar(moreMineData, moreHolder);
                    }
                });
            }

            @Override
            public BaseHolder getHolder() {
                return new HomeHolder(myMineListViewAdapter, listView, FunctionActivity.this).setOnRecordDeleteNumListener(new HomeHolder.onRecordDeleteNumListener() {
                    @Override
                    public void onRecord() {
                        recordDeleteNum++;
                    }
                });
            }
        };
        listView.setAdapter(myMineListViewAdapter);
    }

    public void discoverRefresh(final SwipeRefreshLayout mRefresh){
        AVQuery<AVObject> query = new AVQuery<>(cardType);
        query.orderByDescending(StringUtils.createdAt);
        query.limit(3);
        query.include(StringUtils.avUserPointer);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (UIUtils.hasException(e)) {
                    mRefresh.setRefreshing(false);
                    return;
                }
                moreDiscoverData = new ArrayList<>();
                for (AVObject object : list) {
                    try {
                        HomeCardTodoBean cardTodoBean = new HomeCardTodoBean();
                        cardTodoBean.cardTodo = object;
                        cardTodoBean.typeTodo = cardType;
                        cardTodoBean.avUserPointer = object.getAVObject(StringUtils.avUserPointer);
                        cardTodoBean.strContent = object.getString(StringUtils.strContent);
                        cardTodoBean.millisecsData = object.getCreatedAt().getTime();
                        AVObject avUser = object.getAVObject(StringUtils.avUserPointer);
                        cardTodoBean.avUser = avUser;
                        cardTodoBean.nickName = avUser.getString(StringUtils.nickname);
                        cardTodoBean.introduce = avUser.getString(StringUtils.introduce);
                        cardTodoBean.iconAVFile = avUser.getAVFile(StringUtils.iconShow);
                        cardTodoBean.picAVFile = object.getAVFile(StringUtils.picPhotograph);
                        moreDiscoverData.add(cardTodoBean);
                    }catch (Exception ex){
                        continue;
                    }

                }
                mRefresh.setRefreshing(false);
                myDiscoverListViewAdapter.setDatas(moreDiscoverData);
                discoverListview.setAdapter(myDiscoverListViewAdapter);
            }
        });
    }

    public void mineRefresh(final SwipeRefreshLayout mRefresh){
        AVQuery<AVObject> query = new AVQuery<>(StringUtils.cardMiddleTable);
        query.whereExists(cardType);
        query.whereEqualTo(StringUtils.avUser, avUser);
        query.orderByDescending(StringUtils.createdAt);
        query.limit(3);
        query.include(cardType);
        query.selectKeys(Arrays.asList(cardType));
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (UIUtils.hasException(e)) {
                    mRefresh.setRefreshing(false);
                    return;
                }
                moreMineData = new ArrayList<>();

                for (AVObject object : list) {
                    try {
                        HomeCardTodoBean cardTodoBean = new HomeCardTodoBean();
                        cardTodoBean.cardMiddle = object;
                        AVObject cardTodo = object.getAVObject(cardType);
                        cardTodoBean.upVoteNum = cardTodo.getInt(StringUtils.upVote);
                        cardTodoBean.cardTodo = cardTodo;
                        cardTodoBean.typeTodo = cardType;
                        cardTodoBean.strContent = cardTodo.getString(StringUtils.strContent);
                        cardTodoBean.millisecsData = cardTodo.getCreatedAt().getTime();
                        cardTodoBean.nickName = sp.getString(StringUtils.nickname);
                        cardTodoBean.introduce = sp.getString(StringUtils.introduce);
                        cardTodoBean.iconUri = StringUtils.getCachePicturePath() + "/" +
                                StringUtils.avObjectIdTransitionPicForm(sp.getString(StringUtils.iconAVObjectId));
                        cardTodoBean.picAVFile = cardTodo.getAVFile(StringUtils.picPhotograph);
                        moreMineData.add(cardTodoBean);
                    }catch (Exception ex){
                        continue;
                    }

                }
                mRefresh.setRefreshing(false);
                myMineListViewAdapter.setDatas(moreMineData);
                mineListview.setAdapter(myMineListViewAdapter);
            }
        });
    }


    /**
     * void
     * 初始化布局界面
     */
    private void initView() {

        Intent intent = getIntent();
        cardType = intent.getStringExtra(StringUtils.typeTodo);
        title = intent.getStringExtra(cardType);

        mFunctionViewPager = (NoScrollViewPager) findViewById(R.id.vp_function_viewpager);
        mibQuit = (ImageButton) findViewById(R.id.ib_quit_user_edit);
        mibAdd = (ImageButton) findViewById(R.id.ib_add_function);
        mtvType = (TextView) findViewById(R.id.tv_type_function);
        mtvType.setText(title);
        mtvDiscover = (TextView) findViewById(R.id.tv_discover_function);
        mtvMine = (TextView) findViewById(R.id.tv_mine_function);
        //设置点击监听事件
        mtvDiscover.setOnClickListener(this);
        mtvMine.setOnClickListener(this);
        mibQuit.setOnClickListener(this);
        mibAdd.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
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
            case R.id.ib_quit_user_edit:
                if (recordDeleteNum > 0) {
                    mListener.onRefresh();
                }
                finish();
                break;
            case R.id.ib_add_function:
                Intent intent = new Intent(this, SetUpNanActivity.class);
                intent.putExtra("titleBarText", title);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (recordDeleteNum > 0) {
            mListener.onRefresh();
        }
        super.onBackPressed();
    }

    /**
     * 设置一个监听 用来调用云端的服务器下载图片
     */
    public interface onRefreshListener {
        void onRefresh();
    }

    private static onRefreshListener mListener;

    public static void setOnRefreshListener(onRefreshListener listener) {
        mListener = listener;
    }
}
