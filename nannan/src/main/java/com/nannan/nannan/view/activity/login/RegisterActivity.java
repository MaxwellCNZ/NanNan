package com.nannan.nannan.view.activity.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SignUpCallback;
import com.nannan.nannan.R;
import com.nannan.nannan.utils.SharedPreferenceUtils;
import com.nannan.nannan.utils.StringUtils;
import com.nannan.nannan.utils.UIUtils;
import com.nannan.nannan.view.activity.MainActivity;
import com.nannan.nannan.view.activity.NanNanApplication;

/**
 * Created by MaxwellCNZ on 2017/3/4.
 */

public class RegisterActivity extends Activity {

    private EditText metPhone;
    private EditText metPassword;
    private Button mbtnRegister;
    private CheckBox mcbRegister;
    private TextView mtvRegister;
    private TextView mtvLogin;
    private AVUser user;
    private SharedPreferenceUtils sp;
    private EditText metNickName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        sp = new SharedPreferenceUtils();
        initView();
        initData();
    }

    /**
     * 数据充填
     */
    private void initData() {
        mcbRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mcbRegister.isChecked()) {
                    UIUtils.setTextColor4TextView(mtvRegister, R.color.MasterColor);
                    //同意 可注册
                    mbtnRegister.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String nickName = metNickName.getText().toString().trim();
                            String password = metPassword.getText().toString().trim();
                            String phone = metPhone.getText().toString().trim();
                            if (StringUtils.isEmpty(nickName) || StringUtils.isEmpty(password) || StringUtils.isEmpty(phone)){
                                UIUtils.toast4Shot("所有栏目不能为空！");
                                return;
                            }

                            attemptRegister(nickName, password, phone);
                        }
                    });
                    UIUtils.setTextColor4Button(mbtnRegister, R.color.TextColorBlack);
                } else {
                    UIUtils.setTextColor4TextView(mtvRegister, R.color.HostBlackColor3);
                    //不同意协议  无法注册
                    mbtnRegister.setClickable(false);
                    UIUtils.setTextColor4Button(mbtnRegister, R.color.TextColorWhite3);
                }
            }
        });
        mtvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UIUtils.getContext(), DealActivity.class);
                startActivity(intent);
            }
        });
        mtvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterActivity.this.finish();
            }
        });
    }

    /**
     * 初始化界面
     */
    private void initView() {
        metPhone = (EditText) findViewById(R.id.et_register_phone);
        metPassword = (EditText) findViewById(R.id.et_register_password);
        metNickName = (EditText) findViewById(R.id.et_register_nickName);
        mbtnRegister = (Button) findViewById(R.id.btn_register);
        mcbRegister = (CheckBox) findViewById(R.id.cb_register);
        mtvRegister = (TextView) findViewById(R.id.tv_register);
        mtvLogin = (TextView) findViewById(R.id.tv_register_login);
    }

    @Override
    public void onBackPressed() {
        sp.clearData();
        AVUser.logOut();// 清除缓存用户对象
        super.onBackPressed();
    }

    /**
     * 注册
     * @param nickName
     * @param password
     * @param phone
     */
    private void attemptRegister(String nickName, String password, String phone) {
        // 新建 AVUser 对象实例
        user = new AVUser();
        user.setUsername(phone);// 设置用户名
        user.setPassword(password);// 设置密码
        user.put(StringUtils.nickname, nickName);
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(AVException e) {
                if (UIUtils.hasException(e)){
                    UIUtils.toast4Shot("发送失败，该用户已注册");
                    return;
                }
                UIUtils.toast4Shot("注册成功");
                //登录
                NanNanApplication.destoryActivity("LoginActivity");
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(intent);
                RegisterActivity.this.finish();
            }
        });
    }
}
