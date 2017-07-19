package com.nannan.nannan.view.holder;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nannan.nannan.R;
import com.nannan.nannan.utils.UIUtils;

/**
 * Created by MaxwellCNZ on 2017/4/4.
 */

public class MoreHolder extends BaseHolder<Integer> {
    // 加载更多的几种状态
    public static final int STATE_MORE_MORE = 1;// 1. 可以加载更多
    public static final int STATE_MORE_ERROR = 2;// 2. 加载更多失败
    public static final int STATE_MORE_NONE = 3; // 3. 没有更多数据
    public static final int STATE_MORE_HINT = 4; // 4. 隐藏该item
    private LinearLayout mllLoading;
    private TextView mtvLoading;
    private ProgressBar mpbLoading;

    public MoreHolder(boolean hasMore){
        setData(hasMore ? STATE_MORE_MORE : STATE_MORE_NONE, 0);
    }
    @Override
    public View initView() {
        View inflate = UIUtils.inflate(R.layout.item_loading_more);
        mpbLoading = (ProgressBar) inflate.findViewById(R.id.pb_loading_more);
        mllLoading = (LinearLayout) inflate.findViewById(R.id.ll_loading_more);
        mtvLoading = (TextView) inflate.findViewById(R.id.tv_loading_more);
        return inflate;
    }

    @Override
    protected void fillView(Integer data) {
        switch (data.intValue()){
            case STATE_MORE_MORE:
                mllLoading.setVisibility(View.VISIBLE);
                mpbLoading.setVisibility(View.VISIBLE);
                mtvLoading.setText("正在努力加载中...");
                break;
            case STATE_MORE_NONE:
                mllLoading.setVisibility(View.VISIBLE);
                mpbLoading.setVisibility(View.GONE);
                mtvLoading.setText("没有更多数据了");
                break;
            case STATE_MORE_ERROR:
                mllLoading.setVisibility(View.VISIBLE);
                mpbLoading.setVisibility(View.GONE);
                mtvLoading.setText("没网啦  QAQ");
                break;
            case STATE_MORE_HINT:
                mllLoading.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }
}
