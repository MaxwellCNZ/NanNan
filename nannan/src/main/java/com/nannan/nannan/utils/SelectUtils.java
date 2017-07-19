package com.nannan.nannan.utils;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;

/**
 * Created by MaxwellCNZ on 2017/3/24.
 */

public class SelectUtils {

    //获取一个shape对象
    public static GradientDrawable getGradientDrawable(int color, int radius) {
        // xml中定义的shape标签 对应此类
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);// 矩形
        shape.setCornerRadius(radius);// 圆角半径
        shape.setColor(color);// 颜色

        return shape;
    }

    //获取状态选择器
    public static StateListDrawable getSelector(Drawable normal, Drawable press) {
        StateListDrawable selector = new StateListDrawable();
        selector.addState(new int[] { android.R.attr.state_pressed }, press);// 按下图片
        selector.addState(new int[] {}, normal);// 默认图片

        return selector;
    }

    //获取状态选择器
    public static StateListDrawable getSelector(int normalId, int pressId, int radius) {
        GradientDrawable bgNormal = getGradientDrawable(normalId, radius);
        GradientDrawable bgPress = getGradientDrawable(pressId, radius);
        StateListDrawable selector = getSelector(bgNormal, bgPress);
        return selector;
    }
}
