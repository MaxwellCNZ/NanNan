package com.nannan.nannan.view.activity.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

import com.avos.avoscloud.AVUser;
import com.nannan.nannan.R;
import com.nannan.nannan.utils.SharedPreferenceUtils;
import com.nannan.nannan.utils.UIUtils;
import com.nannan.nannan.view.activity.MainActivity;

public class SplashActivity extends Activity {

    private TextView mtvWelcome;
    private SharedPreferenceUtils sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        sp = new SharedPreferenceUtils();
        mtvWelcome = (TextView) findViewById(R.id.tv_splash_welcome);
        //在此处保存user对象会返回userId

        initAnimation();
    }

    /**
     * 初始化动画
     */
    private void initAnimation() {
        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
        alphaAnimation.setDuration(2000);
        alphaAnimation.setFillAfter(true);

        ScaleAnimation scaleAnimation = new ScaleAnimation(0, 1, 0, 1, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(2000);
        scaleAnimation.setFillAfter(true);

        TranslateAnimation translateAnimation = new TranslateAnimation(0, 0, 200, 0);
        translateAnimation.setDuration(2000);
        translateAnimation.setFillAfter(true);

        AnimationSet animationSet = new AnimationSet(false);
        animationSet.addAnimation(alphaAnimation);
        animationSet.addAnimation(scaleAnimation);
        animationSet.addAnimation(translateAnimation);
        animationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                new Thread() {
                    @Override
                    public void run() {
                        SystemClock.sleep(1000);
//                        if (sp.getBoolean("isLogin")){
//                            Intent intent = new Intent(UIUtils.getContext(), MainActivity.class);
//                            startActivity(intent);
//                        }else {
//                            Intent intent = new Intent(UIUtils.getContext(), LoginActivity.class);
//                            startActivity(intent);
//                        }
                        directLogin();
                        finish();
//                        overridePendingTransition(0, 0);
                    }
                }.start();

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mtvWelcome.startAnimation(animationSet);

    }

    /**
     * 当前用户直接登录
     */
    private void directLogin() {
        AVUser currentUser = AVUser.getCurrentUser();
        if (currentUser != null) {
            // 跳转到首页
            Intent intent = new Intent(UIUtils.getContext(), MainActivity.class);
            startActivity(intent);
        } else {
            //缓存用户对象为空时，可打开用户注册界面…
            Intent intent = new Intent(UIUtils.getContext(), LoginActivity.class);
            startActivity(intent);
        }
    }
}
