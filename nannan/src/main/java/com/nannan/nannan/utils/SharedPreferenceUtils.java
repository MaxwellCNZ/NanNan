package com.nannan.nannan.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by MaxwellCNZ on 2017/2/2.
 */

public class SharedPreferenceUtils {
    private static String fileName = "config";
    private static SharedPreferences sp;

    /**
     * 删除sp文件
     */
    public void clearData() {
        sp.edit().clear().commit();
    }

    public SharedPreferenceUtils(){
        sp = UIUtils.getContext().getSharedPreferences(fileName, Context.MODE_PRIVATE);
    }

    public void putBoolean(String name, boolean value) {
        sp.edit().putBoolean(name, value).commit();
    }

    public boolean getBoolean(String name) {
        return sp.getBoolean(name, false);
    }


    /**
     * 放入数据
     */
    public void putString(String name, String value) {
            sp.edit().putString(name, value).commit();
    }
    /**
     * 获取数据
     */
    public String getString(String name) {
        return sp.getString(name, "");
    }
//    /**
//     * 数据转换成集合
//     */
//    public ArrayList<String> data2List(String name){
//        return new ArrayList<>(Arrays.asList(getString(name).split(",")));
//    }
}
