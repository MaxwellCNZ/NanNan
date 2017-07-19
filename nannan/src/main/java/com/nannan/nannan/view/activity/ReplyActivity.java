package com.nannan.nannan.view.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

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
import com.nannan.nannan.view.pager.ReplyPager;
import com.nannan.nannan.view.widget.NoScrollViewPager;

import java.util.ArrayList;

import static com.nannan.nannan.utils.UIUtils.avUser;

/**
 * Created by MaxwellCNZ on 2017/5/6.
 */

public class ReplyActivity extends Activity {

    private TextView mtvTitle;
    private AVObject commentTodo;
    private ImageButton mibQuite;
    private EditText metContent;
    private ImageButton mibSend;
    private NoScrollViewPager mvpComment;
    private ArrayList<BasePager> pagers;
    private SharedPreferenceUtils sp;
    private ReplyPager replyPager;
    private String cardTodoId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtils.setWindowStatusBarColor(this, R.color.MasterColor2);
        setContentView(R.layout.activity_comment);
        Intent intent = getIntent();
        String commentTodoId = intent.getStringExtra(StringUtils.commentTodoId);
        cardTodoId = intent.getStringExtra(StringUtils.cardTodoId);
        commentTodo = AVObject.createWithoutData(StringUtils.commentTodo, commentTodoId);

        sp = new SharedPreferenceUtils();
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
                if (StringUtils.isEmpty(content)) {
                    UIUtils.toast4Shot("内容不能为空！");
                    return;
                }
                issueTheReply(content);
            }
        });
    }

    private void issueTheReply(final String content) {
        new NetAsyncTask() {
            private CommentTodoBean commentTodoBean;
            Dialog dialog;

            @Override
            public void perTask() {
                dialog = ProgressBarUtils.showLoadingProgressBar(ReplyActivity.this);
                dialog.show();
            }

            @Override
            public void doinBack(final Handler handler) {
                commentTodoBean = new CommentTodoBean();
                commentTodoBean.upvoteNum = 0;
                commentTodoBean.strComment = content;
                commentTodoBean.avUser = avUser;
                commentTodoBean.nickName = sp.getString(StringUtils.nickname);
                commentTodoBean.iconAVFile = avUser.getAVFile(StringUtils.iconShow);

                final AVObject replyTodo = new AVObject(StringUtils.replyTodo);
                replyTodo.put(StringUtils.strReply, content);
                replyTodo.put(StringUtils.avUser, avUser);
                replyTodo.put(StringUtils.cardTodoId, cardTodoId);
                replyTodo.put(StringUtils.commentTodoPointer, commentTodo);
                replyTodo.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(AVException e) {
                        if (UIUtils.hasException(e)) {
                            handler.sendMessage(handler.obtainMessage(NetAsyncTask.MESSAGE_FAILED));
                            return;
                        }
                        commentTodoBean.commentTodo = replyTodo;
                        commentTodoBean.millisecsData = System.currentTimeMillis();
                        handler.sendMessage(handler.obtainMessage(NetAsyncTask.MESSAGE_SUCCEED));
                    }
                });
            }

            @Override
            public void postTask(int messageWhat) {
                dialog.dismiss();
                if (messageWhat == NetAsyncTask.MESSAGE_SUCCEED) {
                    replyPager.getMyListViewAdapter().getDatas().add(0, commentTodoBean);
                    replyPager.getMyListViewAdapter().notifyDataSetChanged();
                    metContent.setText("");
                }
            }
        }.execute();

    }

    private void initView() {
        mtvTitle = (TextView) findViewById(R.id.tv_comment_title);
        mtvTitle.setText("回复");
        mibQuite = (ImageButton) findViewById(R.id.ib_quit_comment);
        metContent = (EditText) findViewById(R.id.et_comment_content);
        metContent.setHint("回复(少于200字)");
        mibSend = (ImageButton) findViewById(R.id.ib_comment_send);
        mvpComment = (NoScrollViewPager) findViewById(R.id.vp_comment_viewpager);
        pagers = new ArrayList<>();
        replyPager = new ReplyPager(this, commentTodo);
        pagers.add(replyPager);
        mvpComment.setAdapter(new FillPagerAdapterUtils(pagers));
        pagers.get(0).initData();
    }
}
