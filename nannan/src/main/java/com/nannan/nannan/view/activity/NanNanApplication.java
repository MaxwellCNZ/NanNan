package com.nannan.nannan.view.activity;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.os.Handler;

import com.avos.avoscloud.AVOSCloud;
import com.nannan.nannan.utils.UIUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by MaxwellCNZ on 2017/3/2.
 */

public class NanNanApplication extends Application {

    private static Context context;
    private static Map<String,Activity> destoryMap = new HashMap<>();
    private static Handler handler;
    private static int mainThreadId;

    public static Context getContext() {
        return context;
    }

    /**
     * 添加到销毁队列
     *
     * @param activity 要销毁的activity
     */
    public static void addDestoryActivity(String activityName,Activity activity) {
        destoryMap.put(activityName,activity);
    }
    /**
     *销毁指定Activity
     */
    public static void destoryActivity(String activityName) {
        Set<String> keySet=destoryMap.keySet();
        for (String key:keySet){
            destoryMap.get(key).finish();
        }
    }
    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        handler = new Handler();
        mainThreadId = android.os.Process.myTid();

        AVOSCloud.initialize(this, "J2kb6ha7U9xthVrcCxbpC3xM-gzGzoHsz",
                "WC6iarmGNPVSyP90Cr6M3b84");

        // TODO: 2017/3/18  在应用发布之前，请关闭调试日志，以免暴露敏感数据。
        AVOSCloud.setDebugLogEnabled(true);

        //判断sdcard是否挂载
        UIUtils.hasSdcard = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);

    }

    public static Handler getHandler() {
        return handler;
    }

    public static int getMainThreadId() {
        return mainThreadId;
    }
}
