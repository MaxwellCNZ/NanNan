package com.nannan.nannan.view.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.SaveCallback;
import com.nannan.nannan.R;
import com.nannan.nannan.bean.CommentTodoBean;
import com.nannan.nannan.utils.FillPagerAdapterUtils;
import com.nannan.nannan.utils.ProgressBarUtils;
import com.nannan.nannan.utils.SharedPreferenceUtils;
import com.nannan.nannan.utils.StatusBarUtils;
import com.nannan.nannan.utils.StringUtils;
import com.nannan.nannan.utils.UIUtils;
import com.nannan.nannan.view.net.NetAsyncTask;
import com.nannan.nannan.view.pager.BasePager;
import com.nannan.nannan.view.pager.CommentPager;
import com.nannan.nannan.view.widget.NoScrollViewPager;

import java.util.ArrayList;

import static com.nannan.nannan.utils.UIUtils.avUser;

/**
 * Created by MaxwellCNZ on 2017/4/30.
 */

public class CommentActivity extends Activity{

    private ImageButton mibQuite;
    private EditText metContent;
    private ImageButton mibSend;
    private String cardTodoId;
    private String typeTodo;
    private AVObject cardTodo;
    private NoScrollViewPager mvpComment;
    private ArrayList<BasePager> pagers;
    private SharedPreferenceUtils sp;
    private CommentPager commentPager;
    private CommentTodoBean commentTodoBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtils.setWindowStatusBarColor(this, R.color.MasterColor2);
        setContentView(R.layout.activity_comment);
        sp = new SharedPreferenceUtils();
        Intent intent = getIntent();
        cardTodoId = intent.getStringExtra(StringUtils.cardTodoId);
        typeTodo = intent.getStringExtra(StringUtils.typeTodo);
        cardTodo = AVObject.createWithoutData(typeTodo, cardTodoId);
        initView();
        initData();

    }

    private void initData() {
        mibQuite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //获取数据
        mibSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = metContent.getText().toString();
                if (StringUtils.isEmpty(content)){
                    UIUtils.toast4Shot("内容不能为空！");
                    return;
                }
                issueTheComment(content);
            }
        });
    }

    private void issueTheComment(final String content) {
        new NetAsyncTask() {
            Dialog dialog;

            @Override
            public void perTask() {
                dialog = ProgressBarUtils.showLoadingProgressBar(CommentActivity.this);
                dialog.show();
            }

            @Override
            public void doinBack(final Handler handler) {
                commentTodoBean = new CommentTodoBean();
                commentTodoBean.upvoteNum = 0;
                commentTodoBean.strComment = content;
                commentTodoBean.avUser = avUser;
                commentTodoBean.cardTodoId = cardTodoId;
                commentTodoBean.nickName = sp.getString(StringUtils.nickname);
                commentTodoBean.iconAVFile = avUser.getAVFile(StringUtils.iconShow);

                final AVObject commentTodo = new AVObject(StringUtils.commentTodo);
                commentTodo.put(StringUtils.strComment, content);
                commentTodo.put(StringUtils.avUser, avUser);
                commentTodo.put(typeTodo, cardTodo);
                commentTodo.put(StringUtils.cardTodoId, cardTodoId);
                commentTodo.put(StringUtils.typeNum, UIUtils.cardType2Num(typeTodo));
                commentTodo.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(AVException e) {
                        if (UIUtils.hasException(e)){
                            handler.sendMessage(handler.obtainMessage(NetAsyncTask.MESSAGE_FAILED));
                            return;
                        }
                        commentTodoBean.commentTodo = commentTodo;
                        commentTodoBean.millisecsData = System.currentTimeMillis();
                        handler.sendMessage(handler.obtainMessage(NetAsyncTask.MESSAGE_SUCCEED));
                    }
                });
            }

            @Override
            public void postTask(int messageWhat) {
                dialog.dismiss();
                if (messageWhat == NetAsyncTask.MESSAGE_SUCCEED){
                    commentPager.getMyListViewAdapter().getDatas().add(commentTodoBean);
                    commentPager.getMyListViewAdapter().notifyDataSetChanged();
                    metContent.setText("");
                }
            }
        }.execute();
    }

    private void initView() {
        mibQuite = (ImageButton) findViewById(R.id.ib_quit_comment);
        metContent = (EditText) findViewById(R.id.et_comment_content);
        mibSend = (ImageButton) findViewById(R.id.ib_comment_send);
        mvpComment = (NoScrollViewPager) findViewById(R.id.vp_comment_viewpager);
        pagers = new ArrayList<>();
        commentPager = new CommentPager(this, cardTodo, typeTodo);
        pagers.add(commentPager);
        mvpComment.setAdapter(new FillPagerAdapterUtils(pagers));
        pagers.get(0).initData();
    }

}
