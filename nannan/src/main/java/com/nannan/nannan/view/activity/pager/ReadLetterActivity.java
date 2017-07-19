package com.nannan.nannan.view.activity.pager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.nannan.nannan.R;
import com.nannan.nannan.utils.SharedPreferenceUtils;
import com.nannan.nannan.utils.StatusBarUtils;
import com.nannan.nannan.utils.StringUtils;
import com.nannan.nannan.view.activity.user.LetterActivity;
import com.nannan.nannan.view.activity.user.UserActivity;

/**
 * Created by MaxwellCNZ on 2017/5/8.
 */

public class ReadLetterActivity extends Activity {

    private ImageButton mibQuit;
    private TextView mtvName;
    private TextView mtvReply;
    private EditText metRead;
    private String nickName;
    private String avUserId;
    private SharedPreferenceUtils sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_letter_read);
        StatusBarUtils.setWindowStatusBarColor(this, R.color.MasterColor2);
        sp = new SharedPreferenceUtils();
        Intent intent = getIntent();
        avUserId = intent.getStringExtra(StringUtils.avUserId);
        nickName = intent.getStringExtra(StringUtils.nickname);
        initView();
    }

    private void initView() {
        mibQuit = (ImageButton) findViewById(R.id.ib_quit_letter_read);
        mtvName = (TextView) findViewById(R.id.tv_letter_read_name);
        mtvReply = (TextView) findViewById(R.id.tv_letter_read_reply);
        metRead = (EditText) findViewById(R.id.et_content_letter_read);

        mtvReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentLetter = new Intent(ReadLetterActivity.this, LetterActivity.class);
                intentLetter.putExtra(StringUtils.avUserId, avUserId);
                intentLetter.putExtra(StringUtils.nickname, nickName);
                startActivity(intentLetter);
                finish();
            }
        });
        mtvName.setText(nickName);
        mtvName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReadLetterActivity.this, UserActivity.class);
                intent.putExtra(StringUtils.avUserId, avUserId);
                startActivity(intent);
            }
        });
        mibQuit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        metRead.setText(sp.getString(StringUtils.letterContent));
    }
}
