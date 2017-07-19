package com.nannan.nannan.view.net;

import android.os.Handler;
import android.os.Message;

/**
 * Created by MaxwellCNZ on 2017/4/3.
 */

public abstract class NetAsyncTask {

    public static final int MESSAGE_SUCCEED = 1;
    public static final int MESSAGE_FAILED = 2;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MESSAGE_SUCCEED:
                    postTask(MESSAGE_SUCCEED);
                    break;
                case MESSAGE_FAILED:
                    postTask(MESSAGE_FAILED);
                    break;
                default:
                    break;
            }
        }
    };

    public abstract void perTask();
    public abstract void doinBack(Handler handler);
    public abstract void postTask(int messageWhat);

    public void execute(){
        perTask();
        new Thread(){
            @Override
            public void run() {
                doinBack(handler);
                /*try {
                    Thread.sleep(1500);
                    handler.sendEmptyMessage(0);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*/
            }

        }.start();
    }

}
