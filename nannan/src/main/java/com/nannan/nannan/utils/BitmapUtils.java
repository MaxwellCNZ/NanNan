package com.nannan.nannan.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;

public class BitmapUtils {
    /**
     * 图片等比例压缩
     *
     * @param filePath
     * @param reqWidth  期望的宽
     * @param reqHeight 期望的高
     * @return
     */
    public static Bitmap decodeSampledBitmap(String filePath, int reqWidth,
                                             int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }

    /**
     * 计算InSampleSize
     * 宽的压缩比和高的压缩比的较小值  取接近的2的次幂的值
     * 比如宽的压缩比是3 高的压缩比是5 取较小值3  而InSampleSize必须是2的次幂，取接近的2的次幂4
     *
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and
            // width
            final int heightRatio = Math.round((float) height
                    / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will
            // guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            int ratio = heightRatio < widthRatio ? heightRatio : widthRatio;
            // inSampleSize只能是2的次幂  将ratio就近取2的次幂的值
            if (ratio < 3)
                inSampleSize = ratio;
            else if (ratio < 6.5)
                inSampleSize = 4;
            else if (ratio < 8)
                inSampleSize = 8;
            else
                inSampleSize = ratio;
        }

        return inSampleSize;
    }

    /**
     * 图片缩放到指定宽高
     * <p>
     * 非等比例压缩，图片会被拉伸
     *
     * @param bitmap 源位图对象
     * @param w      要缩放的宽度
     * @param h      要缩放的高度
     * @return 新Bitmap对象
     */
    public static Bitmap zoomBitmap(Bitmap bitmap, int w, int h) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) w / width);
        float scaleHeight = ((float) h / height);
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap newBmp = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);
        return newBmp;
    }

    ////bitmap转成字符串string与String字符串转换为bitmap图片格式////////////

    /**
     * 图片转成string
     *
     * @param bitmap
     * @return
     */
    public static String convertIconToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();// outputstream
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, baos);
        byte[] appicon = baos.toByteArray();// 转为byte数组
        return Base64.encodeToString(appicon, Base64.DEFAULT);

    }

    /**
     * string转成bitmap
     *
     * @param st
     */
    public static Bitmap convertStringToIcon(String st) {
        // OutputStream out;
        Bitmap bitmap = null;
        try {
            // out = new FileOutputStream("/sdcard/aa.jpg");
            byte[] bitmapArray;
            bitmapArray = Base64.decode(st, Base64.DEFAULT);
            bitmap =
                    BitmapFactory.decodeByteArray(bitmapArray, 0,
                            bitmapArray.length);
            // bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            return bitmap;
        } catch (Exception e) {
            return null;
        }
    }
    ////bitmap转drawable与drawable转换为bitmap图片格式////////////

    public static Bitmap drawable2Bitmap(Drawable drawable) {
        if (drawable != null) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            return bitmapDrawable.getBitmap();
        } else {
            return null;
        }
    }

    public static Drawable bitmap2Drawable(Bitmap bitmap) {
        return new BitmapDrawable(UIUtils.getContext().getResources(), bitmap);
    }

    ////bitmap转byte[]与byte[]转换为bitmap图片格式////////////

    public static byte[] bitmap2byte(Bitmap bitmap) {
        int bytes = bitmap.getByteCount();
        ByteBuffer buf = ByteBuffer.allocate(bytes);
        bitmap.copyPixelsToBuffer(buf);
        byte[] byteArray = buf.array();
        return byteArray;
    }

    public static Bitmap byte2Bitmap(byte[] bytes) {
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    /////////////////////网络获取bitmap图片///////////////////////////
    /**
     * 获取网络uri中的bitmap图片
     *
     * @return
     */
    public static void getByte2BitmapOfThumbnailPic(String uri) {
        getNetPic(uri);
    }

    private static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bitmap bitmap = (Bitmap) msg.obj;
            mListener.getBitmap(bitmap);
        }
    };

    /**
     * 从网络获取缩略图的byre数据
     */
    public static void getNetPic(final String uri) {

        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    URL url = new URL(uri);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET"); // 设置请求方法为GET
                    conn.setReadTimeout(5 * 1000); // 设置请求过时时间为5秒
                    int code = conn.getResponseCode();
                    if (code == 200) {
                        InputStream inputStream = conn.getInputStream();
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
//                        4.将获取的图片Bitmap通过handler发送给主线程,主线程设置给当前view.
                        Message msg = Message.obtain();
                        msg.obj = bitmap;
                        handler.sendMessage(msg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
    /**
     * 监听bitmap的传递，给AVObjectNet使用
     */
    public interface onBitmapFinished {
        void getBitmap(Bitmap bitmap);
    }

    private static onBitmapFinished mListener;

    public BitmapUtils setOnBitmapFinishedListener(onBitmapFinished listener) {
        mListener = listener;
        return this;
    }

}
