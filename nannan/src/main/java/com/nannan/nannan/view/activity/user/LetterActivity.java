package com.nannan.nannan.view.activity.user;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.SaveCallback;
import com.nannan.nannan.R;
import com.nannan.nannan.bean.LetterBean;
import com.nannan.nannan.utils.ProgressBarUtils;
import com.nannan.nannan.utils.SelectUtils;
import com.nannan.nannan.utils.StatusBarUtils;
import com.nannan.nannan.utils.StringUtils;
import com.nannan.nannan.utils.UIUtils;
import com.nannan.nannan.view.net.NetAsyncTask;
import com.nannan.nannan.view.pager.SendPager;

/**
 * Created by MaxwellCNZ on 2017/5/8.
 */
public class LetterActivity extends Activity {

    private ImageButton mibQuit;
    private EditText metLetter;
    private ImageButton mibSend;
    private AVObject avUser;
    private String nickName;
    private AVFile iconFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_letter);
        StatusBarUtils.setWindowStatusBarColor(this, R.color.MasterColor2);
        Intent intent = getIntent();
        String avUserId = intent.getStringExtra(StringUtils.avUserId);
        nickName = intent.getStringExtra(StringUtils.nickname);
        avUser = AVObject.createWithoutData("_User", avUserId);
        avUser.fetchInBackground(new GetCallback<AVObject>() {
            @Override
            public void done(AVObject avObject, AVException e) {
                iconFile = avObject.getAVFile(StringUtils.iconShow);
            }
        });

        initView();
        initData();
    }

    private void initData() {
        mibQuit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = metLetter.getText().toString().trim();
                if (!StringUtils.isEmpty(content)) {
                    showDialog4Exit();
                } else {
                    finish();
                }
            }
        });

        mibSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //发私信的逻辑
                String content = metLetter.getText().toString().trim();
                if (StringUtils.isEmpty(content)) {
                    UIUtils.toast4Shot("内容不能为空哦！");
                    return;
                } else {
                    sendLetterLogic(content);
                }
            }
        });
    }

    /**
     * 发私信
     *
     * @param content
     */
    private void sendLetterLogic(final String content) {

        new NetAsyncTask() {
            Dialog dialog;

            @Override
            public void perTask() {
                dialog = ProgressBarUtils.showLoadingProgressBar(LetterActivity.this);
                dialog.show();
            }

            @Override
            public void doinBack(final Handler handler) {
                final LetterBean letterBean = new LetterBean();
                letterBean.letterContent = content;
                letterBean.deleteSend = false;
                letterBean.deleteReceiver = false;
                letterBean.iconFile = iconFile;
                letterBean.userId = avUser.getObjectId();
                final AVObject letterTodo = new AVObject(StringUtils.letterTodo);
                letterTodo.put(StringUtils.sendAVUser, UIUtils.avUser);
                letterTodo.put(StringUtils.receiverAVUser, avUser);
                letterTodo.put(StringUtils.letterContent, content);
                letterTodo.put(StringUtils.hasRead, false);
                letterTodo.put(StringUtils.deleteSend, false);
                letterTodo.put(StringUtils.deleteReceiver, false);
                letterTodo.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(AVException e) {
                        if (UIUtils.hasException(e)){
                            handler.sendMessage(handler.obtainMessage(NetAsyncTask.MESSAGE_FAILED));
                            return;
                        }
                        letterBean.millisecsData = System.currentTimeMillis();
                        letterBean.letterTodoId = letterTodo.getObjectId();
                        letterBean.nickName = nickName;
                        handler.sendMessage(handler.obtainMessage(NetAsyncTask.MESSAGE_SUCCEED));
                        if (SendPager.getMyListViewAdapter()!= null) {
                            SendPager.getMyListViewAdapter().getDatas().add(0, letterBean);
                            SendPager.getMyListViewAdapter().notifyDataSetChanged();
                        }
                    }
                });
            }

            @Override
            public void postTask(int messageWhat) {
                dialog.dismiss();
                if (messageWhat == NetAsyncTask.MESSAGE_SUCCEED) {
                    finish();
                }else {
                    UIUtils.toast4Shot("发送失败请重试！");
                }
            }
        }.execute();
    }

    /**
     * 弹出对话框，决定是否退出该界面
     */
    private void showDialog4Exit() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View inflate = UIUtils.inflate(R.layout.dialog_exit_setup);
        TextView tvRemind = (TextView) inflate.findViewById(R.id.tv_remind_dialog_setup);
        TextView tvOk = (TextView) inflate.findViewById(R.id.tv_ok_dialog_home);
        TextView tvNo = (TextView) inflate.findViewById(R.id.tv_no_dialog_home);
        tvRemind.setBackgroundColor(UIUtils.getColorId(R.color.MasterColor2));
        tvOk.setBackground(SelectUtils.getSelector(UIUtils.getColorId(R.color.MasterColor2), UIUtils.getColorId(R.color.MasterColor), 0));
        tvNo.setBackground(SelectUtils.getSelector(UIUtils.getColorId(R.color.MasterColor2), UIUtils.getColorId(R.color.MasterColor), 0));
        final AlertDialog dialog = builder.create();
        dialog.setView(inflate);
        dialog.show();
        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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
    public void onBackPressed() {
        String content = metLetter.getText().toString().trim();
        if (!StringUtils.isEmpty(content)) {
            showDialog4Exit();
        } else {
            finish();
        }
    }

    private void initView() {
        mibQuit = (ImageButton) findViewById(R.id.ib_quit_letter);
        metLetter = (EditText) findViewById(R.id.et_content_letter);
        mibSend = (ImageButton) findViewById(R.id.ib_send_letter);
    }
}
