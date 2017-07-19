package com.nannan.nannan.view.net;

import android.graphics.Bitmap;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.DeleteCallback;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.SaveCallback;
import com.nannan.nannan.utils.BitmapUtils;
import com.nannan.nannan.utils.SharedPreferenceUtils;
import com.nannan.nannan.utils.StringUtils;
import com.nannan.nannan.utils.UIUtils;

import static com.nannan.nannan.utils.UIUtils.avUser;

/**
 * Created by MaxwellCNZ on 2017/3/20.
 */

public class AVObjectNet {
    final static SharedPreferenceUtils sp = new SharedPreferenceUtils();
    final static String saveThumbnailPicPath = StringUtils.getCachePicturePath();
    static String fileName = "";

    final static BitmapUtils bitmapUtils = new BitmapUtils().setOnBitmapFinishedListener(new BitmapUtils.onBitmapFinished() {
        @Override
        public void getBitmap(Bitmap bitmap) {
            save2Dir(bitmap);
        }
    });


    ///////////////////////////针对图片的上传icon和head////////////////////////

    //todo 最坏的情况下就是服务器上多了一个文件

    /**
     * 上传文件
     */
    public static void upLoadingAVFile2IconAndHead(final AVFile avFile, final String pictureType) {
        //上传文件
        avFile.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                if (UIUtils.hasException(e)) {
                    sp.putBoolean("isSave_" + pictureType, false);
                    return;
                }
                sp.putBoolean("isSave_" + pictureType, true);
                //先删除在绑定
                deleteAVFileOnLine( pictureType, avFile);
            }
        });
    }

    /**
     * 删除之前在云端上存储的文件
     *
     * @param pictureType
     * @param avFile
     */
    public static void deleteAVFileOnLine( final String pictureType, final AVFile avFile) {
        avUser.fetchInBackground(new GetCallback<AVObject>() {
            @Override
            public void done(AVObject avObject, AVException e) {
                final AVFile perAVFile = avUser.getAVFile(pictureType);
                if (perAVFile != null) {
                    perAVFile.deleteInBackground(new DeleteCallback() {
                        @Override
                        public void done(AVException e) {
                            boundAVFileOnLine(pictureType, avFile);
                        }
                    });
                } else {
                    boundAVFileOnLine(pictureType, avFile);
                }
            }
        });
    }

    /**
     * 绑定上传的文件和用户
     *
     * @param pictureType
     * @param avFile
     */
    public static void boundAVFileOnLine( final String pictureType, AVFile avFile) {
        avUser.put(pictureType, avFile);
        avUser.saveInBackground();
        //绑定完后，获取icon的缩略图
        if (pictureType.equals(StringUtils.iconShow)) {
//            UIUtils.print("上传去拿缩略图");
            sp.putString(StringUtils.iconAVObjectId, avFile.getObjectId());
            AVObjectNet.saveThumbnailPic2Dir(avFile, 100, 100);
        }

    }

    /**
     * 获取缩略图
     */
    public static void saveThumbnailPic2Dir(AVFile avFile, int width, int height) {

        String thumbnailUrl = avFile.getThumbnailUrl(true, width, height);
        fileName = StringUtils.avObjectIdTransitionPicForm(avFile.getObjectId());
        BitmapUtils.getByte2BitmapOfThumbnailPic(thumbnailUrl);
    }

    /**
     * 保存到指定的文件夹下
     */
    private static void save2Dir(Bitmap bitmap) {
        UIUtils.savePic2AppointDir(bitmap, saveThumbnailPicPath, fileName);

    }


    //////////////////////针对用户信息//////////////////////////

    /**
     * 上传用户信息
     *
     * @param nickname
     * @param des
     */
    public static void uploadingUserInfo(String nickname, String des, String sexType) {
        avUser.put(StringUtils.nickname, nickname);
        avUser.put(StringUtils.introduce, des);
        avUser.put(StringUtils.gender, sexType);
        avUser.saveEventually();
    }

    //上传文件并且获得网络地址url
//    public static String upLoadingAndGetNetUrl(final AVFile avFile){
//
//        avFile.saveInBackground(new SaveCallback() {
//            @Override
//            public void done(AVException e) {
//                if (UIUtils.hasException(e)){
//                    return ;
//                }
//                avFile.getUrl();
//            }
//        });
//
//    }

}