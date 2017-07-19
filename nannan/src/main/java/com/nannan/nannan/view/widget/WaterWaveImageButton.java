package com.nannan.nannan.view.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.ImageButton;

import com.nannan.nannan.R;

/**
 * Created by MaxwellCNZ on 2017/3/11.
 */

public class WaterWaveImageButton extends ImageButton {
    //每次刷新的时间间隔
    private static final int INVALIDATE_DURATION = 15;
    //扩散半径增量
    private static int DIFFUSE_GAP = 10;
    //判断点击和长按的时间
    private static int TAP_TIMEOUT;

    private Paint wavePaint;
    private float startX,startY;
    //控件原点坐标（左上角）
    private int pointX, pointY;
    private int viewWidth;
    private int viewHeight;
    private float maxRadio; //扩散的最大半径
    private long downTime;
    private boolean isPushButton;
    private int shaderRadio; //当前半径

    public WaterWaveImageButton(Context context) {
        super(context);
        initPaint();
    }

    public WaterWaveImageButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaint();
    }

    public WaterWaveImageButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
    }

    /**
     * 初始化画笔
     */
    private void initPaint() {
        TAP_TIMEOUT = ViewConfiguration.getLongPressTimeout();
        wavePaint = new Paint();
        wavePaint.setColor(getResources().getColor(R.color.WaveBackground));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if (downTime == 0){
                    downTime = SystemClock.elapsedRealtime();
                }
                startX = event.getX();
                startY = event.getY();
                isPushButton = true;
                //计算最大半径
                countMaxRadio();
                postInvalidateDelayed(INVALIDATE_DURATION);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if(SystemClock.elapsedRealtime() - downTime < TAP_TIMEOUT){
                    //点击
                    DIFFUSE_GAP = 20;
                    postInvalidate();
                }else{
                    //长按
                    clearData();
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (!isPushButton){
            return;
        }
        //绘制扩散圆形背景
        canvas.save();
        canvas.clipRect(pointX, pointY, pointX + viewWidth, pointY + viewHeight);
        canvas.drawCircle(startX, startY, shaderRadio, wavePaint);
        canvas.restore();
        //直到半径等于最大半径
        if(shaderRadio < maxRadio){
            postInvalidateDelayed(INVALIDATE_DURATION,
                    pointX, pointY, pointX + viewWidth, pointY + viewHeight);
            shaderRadio += DIFFUSE_GAP;
        }else{
            clearData();
        }
    }

    /**
     * 清空数据
     */
    private void clearData() {
        downTime = 0;
        DIFFUSE_GAP = 10;
        isPushButton = false;
        shaderRadio = 0;
        postInvalidate();
    }

    /**
     * 计算控件的最大半径
     */
    private void countMaxRadio(){
        if (viewWidth > viewHeight) {
                maxRadio = viewWidth + 10;
        } else {
                maxRadio = viewHeight + 10;
        }
    }

    /**
     * 获取view控件的长和宽
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.viewWidth = w;
        this.viewHeight = h;
    }
}
