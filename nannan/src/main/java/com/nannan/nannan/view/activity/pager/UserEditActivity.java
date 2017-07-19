package com.nannan.nannan.view.activity.pager;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.nannan.nannan.R;
import com.nannan.nannan.utils.SharedPreferenceUtils;
import com.nannan.nannan.utils.StatusBarUtils;
import com.nannan.nannan.utils.StringUtils;
import com.nannan.nannan.view.net.AVObjectNet;

/**
 * Created by MaxwellCNZ on 2017/4/25.
 */
public class UserEditActivity extends Activity {

    private ImageButton mibFinish;
    private EditText metName;
    private RadioGroup mrgGender;
    private RadioButton mrbHint;
    private RadioButton mrbMan;
    private RadioButton mrbWoman;
    private EditText metIntroduce;
    private SharedPreferenceUtils sp;
    private String sexType;
    private ImageButton mibQuit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activiity_user_edit);
        sp = new SharedPreferenceUtils();
        initView();
        initData();
    }

    private void initData() {
        if (!StringUtils.isEmpty(sp.getString(StringUtils.nickname))) {
            metName.setText(sp.getString(StringUtils.nickname));
        }
        if (!StringUtils.isEmpty(sp.getString(StringUtils.introduce))) {
            metIntroduce.setText(sp.getString(StringUtils.introduce));
        }
        if (sp.getString(StringUtils.gender).equals(StringUtils.woman)) {
            sexType = StringUtils.woman;
            mrbWoman.setChecked(true);
        } else if (sp.getString(StringUtils.gender).equals(StringUtils.man)) {
            sexType = StringUtils.man;
            mrbMan.setChecked(true);
        } else {
            sexType = StringUtils.hint;
            mrbHint.setChecked(true);
        }

        mrgGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_hint_user_edit:
                        sexType = StringUtils.hint;
                        break;
                    case R.id.rb_woman_user_edit:
                        sexType = StringUtils.woman;
                        break;
                    case R.id.rb_man_user_edit:
                        sexType = StringUtils.man;
                        break;
                    default:
                        break;
                }
            }
        });
        mibFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //修改用户数据
                String nickname = metName.getText().toString().trim();
                String introduce = metIntroduce.getText().toString();
                sp.putString(StringUtils.nickname, nickname);
                sp.putString(StringUtils.introduce, introduce);
                sp.putString(StringUtils.gender, sexType);
                mListener.onChange();
                AVObjectNet.uploadingUserInfo(nickname, introduce, sexType);
                finish();
            }
        });
        mibQuit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private void initView() {
        StatusBarUtils.setWindowStatusBarColor(this, R.color.MasterColor2);
        mibFinish = (ImageButton) findViewById(R.id.ib_finish_user_edit);
        metName = (EditText) findViewById(R.id.et_name_user_edit);
        mrgGender = (RadioGroup) findViewById(R.id.rg_gender_user_edit);
        mrbHint = (RadioButton) findViewById(R.id.rb_hint_user_edit);
        mrbMan = (RadioButton) findViewById(R.id.rb_man_user_edit);
        mrbWoman = (RadioButton) findViewById(R.id.rb_woman_user_edit);
        mibQuit = (ImageButton) findViewById(R.id.ib_quit_user_edit);
        metIntroduce = (EditText) findViewById(R.id.et_introduce_user_edit);
    }
    /**
     * 设置一个监听 用来修改用户信息
     */
    public interface onChangeUserInfoListener {
        void onChange();

    }

    private static onChangeUserInfoListener mListener;

    public static  void setOnChangeUserInfoListener(onChangeUserInfoListener listener) {
        mListener = listener;
    }
}
