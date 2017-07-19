package com.nannan.nannan.view.pager;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.feedback.FeedbackAgent;
import com.nannan.nannan.R;
import com.nannan.nannan.utils.SelectUtils;
import com.nannan.nannan.utils.SharedPreferenceUtils;
import com.nannan.nannan.utils.StringUtils;
import com.nannan.nannan.utils.UIUtils;
import com.nannan.nannan.view.activity.login.ChangePasswordActivity;
import com.nannan.nannan.view.activity.login.LoginActivity;

import java.io.File;

/**
 * Created by MaxwellCNZ on 2017/3/7.
 */

public class MorePager extends BasePager {

    private SharedPreferenceUtils sp;
    private final Activity mActivity;
    private RelativeLayout mrlClear;
    private RelativeLayout mrlAbout;
    private RelativeLayout mrlFeedback;
    private TextView mtvLogout;
    private File file;
    private TextView mtvCacheSize;
    private RelativeLayout mrlChange;

    public MorePager(final Activity activity) {
        super();
        mActivity = activity;
        mtvLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sp.clearData();
                AVUser.logOut();// 清除缓存用户对象
                Intent intent = new Intent(mActivity, LoginActivity.class);
                mActivity.startActivity(intent);
                mActivity.finish();
            }
        });
        mrlClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIUtils.deleteAllFiles(file);
                UIUtils.toast4Shot("缓存清除成功");
                mtvCacheSize.setText(UIUtils.getFormatSize(UIUtils.getFolderSize(file)));
            }
        });
        mrlAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        mrlFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FeedbackAgent agent = new FeedbackAgent(UIUtils.getContext());
                agent.startDefaultThreadActivity();
            }
        });
        mrlChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //修改密码
                Intent intent = new Intent(activity, ChangePasswordActivity.class);
                intent.putExtra(StringUtils.onLine, true);
                activity.startActivity(intent);
            }
        });
    }

    @Override
    public View initView() {
        sp = new SharedPreferenceUtils();
        View inflate = UIUtils.inflate(R.layout.pager_more);
        mrlClear = (RelativeLayout) inflate.findViewById(R.id.rl_clear);
        mrlAbout = (RelativeLayout) inflate.findViewById(R.id.rl_about);
        mrlFeedback = (RelativeLayout) inflate.findViewById(R.id.rl_feedback);
        mrlChange = (RelativeLayout) inflate.findViewById(R.id.rl_change);
        mtvLogout = (TextView) inflate.findViewById(R.id.tv_logout);
        mtvCacheSize = (TextView) inflate.findViewById(R.id.tv_cache_size);

        mrlClear.setBackground(SelectUtils.getSelector(UIUtils.getColorId(R.color.WaveBackground), UIUtils.getColorId(R.color.TextColorHint), 0));
        mrlAbout.setBackground(SelectUtils.getSelector(UIUtils.getColorId(R.color.WaveBackground), UIUtils.getColorId(R.color.TextColorHint), 0));
        mrlFeedback.setBackground(SelectUtils.getSelector(UIUtils.getColorId(R.color.WaveBackground), UIUtils.getColorId(R.color.TextColorHint), 0));
        mrlChange.setBackground(SelectUtils.getSelector(UIUtils.getColorId(R.color.WaveBackground), UIUtils.getColorId(R.color.TextColorHint), 0));

        file = new File(StringUtils.getCachePicturePath());
        mtvCacheSize.setText(UIUtils.getFormatSize(UIUtils.getFolderSize(file)));
        return inflate;
    }

    @Override
    public void initData() {

    }
}
