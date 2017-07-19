package com.nannan.nannan.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import com.nannan.nannan.R;

/**
 * Created by MaxwellCNZ on 2017/3/30.
 */

public class ProgressBarUtils {
    public static Dialog showLoadingProgressBar(Context context) {

        View inflate = UIUtils.inflate(R.layout.dialog_progress);
        // 创建自定义样式的Dialog
        Dialog loadingDialog = new Dialog(context, R.style.loading_dialog);
        // 设置返回键无效
        loadingDialog.setCancelable(false);
        loadingDialog.setContentView(inflate, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        return loadingDialog;
    }

}
