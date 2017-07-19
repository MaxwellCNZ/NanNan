package com.nannan.nannan.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVUser;
import com.nannan.nannan.R;
import com.nannan.nannan.bean.BridgeColumn;
import com.nannan.nannan.view.activity.NanNanApplication;

import org.ocpsoft.prettytime.PrettyTime;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.nannan.nannan.view.activity.MainActivity.checkDirPath;

/**
 * Created by MaxwellCNZ on 2017/3/2.
 */

public class UIUtils {

    public static AVUser avUser;
    public static BridgeColumn bridgeColumn;
    public static boolean hasSdcard;
    public static boolean hasNetwork;

    ////////////////////////类型转换//////////////////////////
    public static int choiceType(String typeTodo){
        int typeId = 0;
        switch (typeTodo){
            case "artCardTodo":
                typeId = R.drawable.vector_art;
                break;
            case "learnCardTodo":
                typeId = R.drawable.vector_learn;

                break;
            case "leisureCardTodo":
                typeId = R.drawable.vector_leisure;

                break;
            case "mindsCardTodo":
                typeId = R.drawable.vector_minds;

                break;
            case "sportCardTodo":
                typeId = R.drawable.vector_sports;

                break;
            case "musicCardTodo":
                typeId = R.drawable.vector_music;

                break;
            default:
                break;
        }
        return typeId;
    }
    ////////////////////////卡片标识//////////////////////////
    public static int cardType2Num(String typeTodo){
        int type = 0;
        switch (typeTodo){
            case "artCardTodo":
                type = 1;
                break;
            case "learnCardTodo":
                type = 2;

                break;
            case "leisureCardTodo":
                type = 3;

                break;
            case "mindsCardTodo":
                type = 4;

                break;
            case "sportCardTodo":
                type = 5;

                break;
            case "musicCardTodo":
                type = 6;

                break;
            case "columnTodo":
                type = 7;

                break;
            case "commentTodo":
                type = 8;

                break;
            case "replyTodo":
                type = 9;

                break;
            default:
                break;
        }
        return type;
    }



    public static int getWindowWidth() {
        WindowManager windowManager = (WindowManager) UIUtils.getContext().getSystemService(Context.WINDOW_SERVICE);
        return windowManager.getDefaultDisplay().getWidth();
    }

    public static Context getContext() {
        return NanNanApplication.getContext();
    }

    public static Handler getHandler() {
        return NanNanApplication.getHandler();
    }

    public static int getUIThread() {
        return NanNanApplication.getMainThreadId();
    }


    public static boolean hasException(Exception e) {
        if (e != null) {
            UIUtils.toast4Long(e.getMessage());
            return true;
        }
        return false;
    }

    // /////////////////dip和px转换//////////////////////////

    public static int dip2px(float dip) {
        float density = getContext().getResources().getDisplayMetrics().density;
        return (int) (dip * density + 0.5f);
    }

    public static float px2dip(int px) {
        float density = getContext().getResources().getDisplayMetrics().density;
        return px / density;
    }

    // /////////////////加载布局文件//////////////////////////
    public static View inflate(int id) {
        return View.inflate(getContext(), id, null);
    }

    // /////////////////toast提示//////////////////////////
    public static void toast4Shot(String text) {
        Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
    }

    public static void toast4Long(String text) {
        Toast.makeText(getContext(), text, Toast.LENGTH_LONG).show();
    }

    public static void print(String str) {
        System.out.println(str);
    }

    // /////////////////获取资源文件中的内容//////////////////////////
    public static void setTextColor4TextView(TextView tv, int id) {
        tv.setTextColor(getContext().getResources().getColor(id));
    }

    public static void setTextColor4Button(Button btn, int id) {
        btn.setTextColor(getContext().getResources().getColor(id));
    }

    /**
     * 从资源文件中获取string
     */
    public static String getString(int id) {
        return getContext().getResources().getString(id);
    }

    // /////////////////设置图片//////////////////////////
//    public static void setSrcDrawable4ImageButton(ImageButton btn, int cardTodoId){
//        btn.setImageResource(cardTodoId);
//    }

    //////////////////从资源文件获取颜色/////////////////////

    /**
     * 返回资源文件中颜色id
     *
     * @param colorId
     * @return
     */
    public static int getColorId(int colorId) {
        return ContextCompat.getColor(getContext(), colorId);
    }

    //////////////////主线程逻辑判断与执行/////////////////////
    public static boolean isRunUIThread() {
        int currentThread = android.os.Process.myTid();
        if (currentThread == getUIThread()) {
            return true;
        }
        return false;
    }

    //在主线程执行
    public static void runOnUIThread(Runnable runnable) {
        if (isRunUIThread()) {
            runnable.run();
        } else {
            getHandler().post(runnable);
        }
    }

    //////////////////保存文件到指定的文件夹下/////////////////////

    /**
     * 保存图片
     *
     * @param bitmap
     * @param dirPath
     * @param fileName
     */
    public static void savePic2AppointDir(Bitmap bitmap, String dirPath, String fileName) {
        File file = new File(checkDirPath(dirPath), fileName);
        if (file.exists()) {
            file.delete();
        }
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //////////////////获取指定的文件/////////////////////

    /**
     * 从本地中获取指定的文件
     * 需要判断是否为空
     *
     * @param avFileId
     * @return
     */
    public static Bitmap getPic2AppointDir(String avFileId) {
        String fileName = StringUtils.avObjectIdTransitionPicForm(avFileId);
        String filePath = StringUtils.getDownloadPicturePath() + "/" + fileName;
        Bitmap bitmap = BitmapFactory.decodeFile(filePath, null);
        return bitmap;
    }


    ///////////////////////////时间戳的转换///////////////////////
    public static PrettyTime prettyTime = new PrettyTime();

    public static String millisecs2DateString(long timestamp) {
        long gap = System.currentTimeMillis() - timestamp;
        if (gap < 1000 * 60 * 60 * 24) {
            String s = prettyTime.format(new Date(timestamp));
            return s.replace(" ", "");
        } else {
            return getDate(new Date(timestamp));
        }
    }

    public static String getDate(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm");
        return format.format(date);
    }
    //////////////文件夹//////////////////
    /**
     * 格式化单位
     * @param size
     * @return
     */
    public static String getFormatSize(double size) {
        double kiloByte = size/1024;
        if(kiloByte < 1) {
            return size + "Byte(s)";
        }

        double megaByte = kiloByte/1024;
        if(megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "KB";
        }

        double gigaByte = megaByte/1024;
        if(gigaByte < 1) {
            BigDecimal result2  = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "MB";
        }

        double teraBytes = gigaByte/1024;
        if(teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "GB";
        }
        BigDecimal result4 = new BigDecimal(teraBytes);
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "TB";
    }
    /**
     * 获取文件夹大小
     * @param file File实例
     * @return long
     */
    public static long getFolderSize(File file){

        long size = 0;
        try {
            java.io.File[] fileList = file.listFiles();
            for (int i = 0; i < fileList.length; i++)
            {
                if (fileList[i].isDirectory())
                {
                    size = size + getFolderSize(fileList[i]);

                }else{
                    size = size + fileList[i].length();

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //return size/1048576;
        return size;
    }

    /**
     * 删除文件夹
     * @param root
     */
    public static void deleteAllFiles(File root) {
        File files[] = root.listFiles();
        if (files != null)
            for (File f : files) {
                if (f.isDirectory()) { // 判断是否为文件夹
                    deleteAllFiles(f);
                    try {
                        f.delete();
                    } catch (Exception e) {
                    }
                } else {
                    if (f.exists()) { // 判断是否存在
                        deleteAllFiles(f);
                        try {
                            f.delete();
                        } catch (Exception e) {
                        }
                    }
                }
            }
    }

    public static void printLog(String str) {
        Log.i("我的", str);
    }
}
