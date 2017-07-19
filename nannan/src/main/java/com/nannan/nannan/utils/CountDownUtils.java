package com.nannan.nannan.utils;

import android.os.CountDownTimer;
import android.widget.Button;

import com.nannan.nannan.R;

/**
 * Created by MaxwellCNZ on 2017/3/5.
 */

public class CountDownUtils extends CountDownTimer {
    private final Button btn;

    public CountDownUtils(Button btn, long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);//参数依次为总时长,和计时的时间间隔
        this.btn = btn;
    }

    @Override
    public void onFinish() {//计时完毕时触发
        btn.setText("重新验证");
        UIUtils.setTextColor4Button(btn, R.color.TextColorBlack);
        btn.setClickable(true);
    }

    @Override
    public void onTick(long millisUntilFinished) {//计时过程显示
        btn.setClickable(false);
        UIUtils.setTextColor4Button(btn, R.color.TextColorWhite3);
        btn.setText(millisUntilFinished / 1000 + "秒后可重新验证");
    }
}