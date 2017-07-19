package com.nannan.nannan.view.activity.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogInCallback;
import com.nannan.nannan.R;
import com.nannan.nannan.utils.SharedPreferenceUtils;
import com.nannan.nannan.utils.StringUtils;
import com.nannan.nannan.utils.UIUtils;
import com.nannan.nannan.view.activity.MainActivity;
import com.nannan.nannan.view.activity.NanNanApplication;

/**
 * Created by MaxwellCNZ on 2017/3/2.
 */

public class LoginActivity extends Activity {

    private TextView mtvRegister;
    private TextView mtvVerify;
    private EditText metPhone;
    private EditText metPassword;
    private Button mbtnLogin;
    private SharedPreferenceUtils sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sp = new SharedPreferenceUtils();
        initView();
        initData();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        mtvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NanNanApplication.addDestoryActivity("LoginActivity", LoginActivity.this);
                Intent intent = new Intent(UIUtils.getContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });
        mbtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = metPhone.getText().toString().trim();
                String password = metPassword.getText().toString();
                if (StringUtils.isEmpty(phone) || StringUtils.isEmpty(password)){
                    UIUtils.toast4Shot("用户名和密码不能为空");
                    return;
                }
                attempLogin(phone, password);
            }
        });
        mtvVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NanNanApplication.addDestoryActivity("LoginActivity", LoginActivity.this);
                String phone = metPhone.getText().toString().trim();
                sp.putString("phone", phone);
                Intent intent = new Intent(UIUtils.getContext(), ChangePasswordActivity.class);
                startActivity(intent);
            }
        });
    }
    /**
     * 初始化界面
     */
    private void initView() {
        mtvRegister = (TextView) findViewById(R.id.tv_login_register);
        mtvVerify = (TextView) findViewById(R.id.tv_login_verify);
        metPhone = (EditText) findViewById(R.id.et_login_phone);
        metPassword = (EditText) findViewById(R.id.et_login_password);
        mbtnLogin = (Button) findViewById(R.id.btn_login);
    }

    /**
     * 登录
     * @param phone
     * @param password
     */
    private void attempLogin(String phone, String password) {
        AVUser.logInInBackground(phone, password, new LogInCallback<AVUser>() {
            @Override
            public void done(AVUser avUser, AVException e) {
                if (e == null){
                    Intent intent = new Intent(UIUtils.getContext(), MainActivity.class);
                    startActivity(intent);
                    LoginActivity.this.finish();
                }else {
                    UIUtils.toast4Shot("登录失败");
                }
            }
        });
    }
}
