package com.nannan.nannan.view.pager;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.nannan.nannan.R;
import com.nannan.nannan.bean.LetterBean;
import com.nannan.nannan.utils.SelectUtils;
import com.nannan.nannan.utils.SharedPreferenceUtils;
import com.nannan.nannan.utils.StringUtils;
import com.nannan.nannan.utils.UIUtils;
import com.nannan.nannan.view.activity.pager.ReadLetterActivity;
import com.nannan.nannan.view.adapter.MyListViewAdapter;
import com.nannan.nannan.view.holder.BaseHolder;
import com.nannan.nannan.view.holder.LetterHolder;
import com.nannan.nannan.view.holder.MoreHolder;

import java.util.ArrayList;
import java.util.List;

import static com.nannan.nannan.utils.StringUtils.letterTodo;

/**
 * Created by MaxwellCNZ on 2017/5/8.
 */

public class ReceiverPager extends BasePager implements SwipeRefreshLayout.OnRefreshListener {

    private final Activity activity;
    private SwipeRefreshLayout msrlRefresh;
    private ListView listView;
    private MyListViewAdapter<LetterBean> myListViewAdapter;
    private ArrayList<LetterBean> moreData;
    private SharedPreferenceUtils sp;

    public ReceiverPager(Activity activity) {
        this.activity = activity;
    }

    @Override
    public View initView() {
        sp = new SharedPreferenceUtils();
        View inflate = UIUtils.inflate(R.layout.pager_listview);
        msrlRefresh = (SwipeRefreshLayout) inflate.findViewById(R.id.srl_refresh);
        listView = (ListView) inflate.findViewById(R.id.lv_ListView);
        msrlRefresh.setOnRefreshListener(this);
        msrlRefresh.setColorSchemeResources(R.color.MasterColor);
        msrlRefresh.setDistanceToTriggerSync(400);// 设置手指在屏幕下拉多少距离会触发下拉刷新
        msrlRefresh.setProgressBackgroundColorSchemeResource(R.color.BackgroundColor2);
        msrlRefresh.setSize(SwipeRefreshLayout.DEFAULT);
        //长按删除或者清空所有数据
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == myListViewAdapter.getCount() - 1) {
                    return true;
                }
                showDialog4Exit(position);
                return true;
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == myListViewAdapter.getCount() - 1) {
                    return;
                }
                LetterBean item = myListViewAdapter.getItem(position);
                sp.putString(StringUtils.letterContent, item.letterContent);
                Intent intent = new Intent(UIUtils.getContext(), ReadLetterActivity.class);
                intent.putExtra(StringUtils.avUserId, item.userId);
                intent.putExtra(StringUtils.nickname, item.nickName);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                AVObject letterTodo = AVObject.createWithoutData(StringUtils.letterTodo, item.letterTodoId);
                letterTodo.put(StringUtils.hasRead, true);
                letterTodo.saveEventually();
                UIUtils.getContext().startActivity(intent);
                item.hasRead = true;
                myListViewAdapter.notifyDataSetChanged();
            }
        });

        return inflate;
    }

    /**
     * 删除选择
     *
     * @param position
     */
    private void showDialog4Exit(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        View inflate = UIUtils.inflate(R.layout.dialog_exit_setup);
        TextView tvContent = (TextView) inflate.findViewById(R.id.tv_content);
        TextView tvRemind = (TextView) inflate.findViewById(R.id.tv_remind_dialog_setup);
        TextView tvOk = (TextView) inflate.findViewById(R.id.tv_ok_dialog_home);
        TextView tvNo = (TextView) inflate.findViewById(R.id.tv_no_dialog_home);
        tvRemind.setBackgroundColor(UIUtils.getColorId(R.color.MasterColor2));
        tvOk.setBackground(SelectUtils.getSelector(UIUtils.getColorId(R.color.MasterColor2), UIUtils.getColorId(R.color.MasterColor), 0));
        tvNo.setBackground(SelectUtils.getSelector(UIUtils.getColorId(R.color.MasterColor2), UIUtils.getColorId(R.color.MasterColor), 0));
        tvContent.setText("你确定要删除该数据？");
        final AlertDialog dialog = builder.create();
        dialog.setView(inflate);
        dialog.show();
        final LetterBean item = myListViewAdapter.getItem(position);
        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //删除数据
                if (item.deleteSend) {
                    AVObject letterTodo = AVObject.createWithoutData(StringUtils.letterTodo, item.letterTodoId);
                    letterTodo.deleteEventually();
                    myListViewAdapter.getDatas().remove(position);
                    myListViewAdapter.notifyDataSetChanged();
                } else {
                    //形式
                    AVObject letterTodo = AVObject.createWithoutData(StringUtils.letterTodo, item.letterTodoId);
                    letterTodo.put(StringUtils.deleteReceiver, true);
                    letterTodo.saveEventually();
                    myListViewAdapter.getDatas().remove(position);
                    myListViewAdapter.notifyDataSetChanged();
                }
                dialog.dismiss();
            }
        });
        tvNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    @Override
    public void initData() {
        ArrayList<LetterBean> letterBeens = new ArrayList<>();
        myListViewAdapter = new MyListViewAdapter<LetterBean>(letterBeens, this) {
            @Override
            protected void onLoadMore(final MoreHolder moreHolder) {

                moreData = null;
                AVQuery<AVObject> query = new AVQuery<>(letterTodo);
                query.whereEqualTo(StringUtils.receiverAVUser, UIUtils.avUser);
                query.orderByDescending(StringUtils.createdAt);
                query.limit(10);
                query.skip(myListViewAdapter.getDatasSize());
                query.include(StringUtils.sendAVUser);
                query.findInBackground(new FindCallback<AVObject>() {
                    @Override
                    public void done(List<AVObject> list, AVException e) {
                        if (UIUtils.hasException(e)) {
                            getmProgressBarListener().setProgressBar(moreData, moreHolder);
                            return;
                        }
                        moreData = new ArrayList<>();
                        for (AVObject object : list) {
                            try {
//                                if (object.getBoolean(StringUtils.deleteReceiver)){
//                                    continue;
//                                }
                                if (!object.getBoolean(StringUtils.deleteReceiver)) {
                                    LetterBean letterBean = new LetterBean();
                                    letterBean.letterContent = object.getString(StringUtils.letterContent);
                                    letterBean.millisecsData = object.getCreatedAt().getTime();
                                    letterBean.hasRead = object.getBoolean(StringUtils.hasRead);
                                    letterBean.letterTodoId = object.getObjectId();
                                    letterBean.deleteSend = object.getBoolean(StringUtils.deleteSend);
//                                letterBean.deleteReceiver = object.getBoolean(StringUtils.deleteReceiver);
                                    AVUser avUser = object.getAVUser(StringUtils.sendAVUser);
                                    letterBean.nickName = avUser.getString(StringUtils.nickname);
                                    letterBean.iconFile = avUser.getAVFile(StringUtils.iconShow);
                                    letterBean.userId = avUser.getObjectId();
                                    moreData.add(letterBean);
                                }

                            } catch (Exception ex) {
                                continue;
                            }
                        }
                        UIUtils.printLog(moreData.size() + "");
                        getmProgressBarListener().setProgressBar(moreData, moreHolder);
                    }
                });
            }

            @Override
            public BaseHolder getHolder() {
                return new LetterHolder(true);
            }
        };
        listView.setAdapter(myListViewAdapter);
    }

    @Override
    public void onRefresh() {
        ArrayList<LetterBean> letterBeens = new ArrayList<>();
        myListViewAdapter = new MyListViewAdapter<LetterBean>(letterBeens, this) {
            @Override
            protected void onLoadMore(final MoreHolder moreHolder) {

                moreData = null;
                AVQuery<AVObject> query = new AVQuery<>(letterTodo);
                query.whereEqualTo(StringUtils.receiverAVUser, UIUtils.avUser);
                query.orderByDescending(StringUtils.createdAt);
                query.limit(10);
                query.skip(myListViewAdapter.getDatasSize());
                query.include(StringUtils.sendAVUser);
                query.findInBackground(new FindCallback<AVObject>() {
                    @Override
                    public void done(List<AVObject> list, AVException e) {
                        if (UIUtils.hasException(e)) {
                            msrlRefresh.setRefreshing(false);
                            getmProgressBarListener().setProgressBar(moreData, moreHolder);
                            return;
                        }
                        moreData = new ArrayList<>();
                        for (AVObject object : list) {
                            try {
//                                if (object.getBoolean(StringUtils.deleteReceiver)){
//                                    continue;
//                                }
                                if (!object.getBoolean(StringUtils.deleteReceiver)) {
                                    LetterBean letterBean = new LetterBean();
                                    letterBean.letterContent = object.getString(StringUtils.letterContent);
                                    letterBean.millisecsData = object.getCreatedAt().getTime();
                                    letterBean.hasRead = object.getBoolean(StringUtils.hasRead);
                                    letterBean.letterTodoId = object.getObjectId();
                                    letterBean.deleteSend = object.getBoolean(StringUtils.deleteSend);
//                                letterBean.deleteReceiver = object.getBoolean(StringUtils.deleteReceiver);
                                    AVUser avUser = object.getAVUser(StringUtils.sendAVUser);
                                    letterBean.nickName = avUser.getString(StringUtils.nickname);
                                    letterBean.iconFile = avUser.getAVFile(StringUtils.iconShow);
                                    letterBean.userId = avUser.getObjectId();
                                    moreData.add(letterBean);
                                }

                            } catch (Exception ex) {
                                continue;
                            }
                        }
                        msrlRefresh.setRefreshing(false);
                        getmProgressBarListener().setProgressBar(moreData, moreHolder);
                    }
                });
            }

            @Override
            public BaseHolder getHolder() {
                return new LetterHolder(true);
            }
        };
        listView.setAdapter(myListViewAdapter);
    }
}
