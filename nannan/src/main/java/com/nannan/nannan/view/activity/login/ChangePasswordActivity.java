package com.nannan.nannan.view.activity.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVMobilePhoneVerifyCallback;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogInCallback;
import com.avos.avoscloud.RequestMobileCodeCallback;
import com.avos.avoscloud.UpdatePasswordCallback;
import com.nannan.nannan.R;
import com.nannan.nannan.utils.CountDownUtils;
import com.nannan.nannan.utils.SelectUtils;
import com.nannan.nannan.utils.SharedPreferenceUtils;
import com.nannan.nannan.utils.StringUtils;
import com.nannan.nannan.utils.UIUtils;
import com.nannan.nannan.view.activity.MainActivity;
import com.nannan.nannan.view.activity.NanNanApplication;

/**
 * Created by MaxwellCNZ on 2017/3/5.
 */

public class ChangePasswordActivity extends Activity {

    private EditText metVerify;
    private Button mbtnVerify;
    private Button mbtnChange;
    private EditText metPhone;
    private SharedPreferenceUtils sp;
    private String phone;
    private String verifyCode;
    private EditText metPassword;
    private String password;
    private boolean isOnLine;
    private TextView mtvVerify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changepassword);
        Intent intent = getIntent();
        isOnLine = intent.getBooleanExtra(StringUtils.onLine, false);
        sp = new SharedPreferenceUtils();
        initView();
        initData();
    }

    private void initView() {
        metPhone = (EditText) findViewById(R.id.et_change_phone);
        metVerify = (EditText) findViewById(R.id.et_change_verify);
        metPassword = (EditText) findViewById(R.id.et_change_password);
        mbtnVerify = (Button) findViewById(R.id.btn_verify_num);
        mbtnChange = (Button) findViewById(R.id.btn_change);
        mtvVerify = (TextView) findViewById(R.id.tv_verify);
    }

    private void initData() {
        if (isOnLine){
            mtvVerify.setVisibility(View.VISIBLE);
            mtvVerify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialog4Verify();
                }
            });
        }else {
            mtvVerify.setVisibility(View.GONE);
        }
        metPhone.setText(sp.getString("phone"));
        mbtnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phone = metPhone.getText().toString().trim();
                if (StringUtils.isEmpty(phone)) {
                    UIUtils.toast4Shot("手机号不能为空");
                    return;
                }
                sendCode();
            }
        });
        mbtnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyCode = metVerify.getText().toString();
                password = metPassword.getText().toString().trim();
                if (StringUtils.isEmpty(verifyCode) || StringUtils.isEmpty(password)) {
                    UIUtils.toast4Shot("所有栏目不能为空");
                    return;
                }
                verifyCode();
            }
        });
    }

    private void showDialog4Verify() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View inflate = UIUtils.inflate(R.layout.dialog_verify);
        final EditText etVerify = (EditText) inflate.findViewById(R.id.et_verify);
        final Button btnVerify = (Button) inflate.findViewById(R.id.btn_verify_num);
        TextView mtvVerify = (TextView) inflate.findViewById(R.id.tv_verify);
        mtvVerify.setBackground(SelectUtils.getSelector(UIUtils.getColorId(R.color.MasterColor2), UIUtils.getColorId(R.color.MasterColor), 0));

        final AlertDialog dialog = builder.create();
        dialog.setView(inflate);
        dialog.show();

        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AVUser.requestMobilePhoneVerifyInBackground(UIUtils.avUser.getUsername(), new RequestMobileCodeCallback() {
                    @Override
                    public void done(AVException e) {
                        if(e == null){
                            // 发送成功
                            new CountDownUtils(btnVerify, 60000, 1000).start();
                        } else {
                            UIUtils.toast4Shot("发送失败  该手机号已经验证过了");
                        }
                    }
                });
            }
        });
        mtvVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String verifyCode = etVerify.getText().toString().trim();
                if (StringUtils.isEmpty(verifyCode)){
                    UIUtils.toast4Shot("验证码不能为空");
                    return;
                }
                AVUser.verifyMobilePhoneInBackground(verifyCode, new AVMobilePhoneVerifyCallback() {
                    @Override
                    public void done(AVException e) {
                        if (e == null) {
                            // 验证成功
                            UIUtils.toast4Shot("验证成功");
                            dialog.dismiss();
                        } else {
                            UIUtils.toast4Shot("验证失败");
                            dialog.dismiss();
                        }
                    }
                });
            }
        });
    }

    /**
     * 验证注册码
     *
     */
    private void verifyCode() {
        AVUser.resetPasswordBySmsCodeInBackground(verifyCode, password, new UpdatePasswordCallback() {
            @Override
            public void done(AVException e) {
                if (e == null) {
                    //验证成功 登录
                    attempLogin();
                } else {
                    //验证失败
                    UIUtils.toast4Shot("修改密码失败");
                }
            }
        });
    }

    /**
     * 登录
     */
    private void attempLogin() {
        AVUser.logInInBackground(phone, password, new LogInCallback<AVUser>() {
            @Override
            public void done(AVUser avUser, AVException e) {
                if (e == null){
                    if (isOnLine){
                        ChangePasswordActivity.this.finish();
                    }else {
                        NanNanApplication.destoryActivity("LoginActivity");
                        Intent intent = new Intent(UIUtils.getContext(), MainActivity.class);
                        startActivity(intent);
                        ChangePasswordActivity.this.finish();
                    }
                }else {
                    UIUtils.toast4Shot("登录失败");
                }
            }
        });
    }

    /**
     * 发送验证码
     *
     */
    private void sendCode() {

        AVUser.requestPasswordResetBySmsCodeInBackground(phone, new RequestMobileCodeCallback() {
            @Override
            public void done(AVException e) {
                if (e == null) {
                    //短信发送成功
                    //发送成功 按钮颜色变灰，并且不可点击，还有60秒倒计时
                    new CountDownUtils(mbtnVerify, 60000, 1000).start();
                } else {
                    //短息发送失败
                    UIUtils.toast4Shot("发送失败  该手机号可能未验证");
                }
            }
        });
    }
}
