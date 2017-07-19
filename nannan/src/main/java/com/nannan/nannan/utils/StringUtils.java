package com.nannan.nannan.utils;


import android.os.Environment;

import java.text.SimpleDateFormat;
import java.util.Date;

public class StringUtils {
    //global 字符串
    public static String hasUserInfo = "hasUserInfo";

    public static String nickname_default = "nickname_default";
    public static String describe_default = "describe_default";
    public static String nickname = "nickname";
    public static String gender = "gender";
    public static String introduce = "introduce";
    public static String woman = "woman";
    public static String man = "man";
    public static String hint = "hint";

    public static String HEAD_SHOW_jpg = "head_show.jpg";
    public static String ICON_SHOW_jpg = "icon_show.jpg";
    public static String uploading_jpg = "uploading.jpg";
    public static String uploadingColumn_jpg = "uploadingColumn.jpg";
    public static String thumbnailIconPic_jpg = "thumbnailIconPic.jpg";
//	public static String thumbnailImage_jpg = "thumbnailImage.jpg";

    public static String headShow = "headShow";
    public static String iconShow = "iconShow";

    public static String strContent = "strContent";
    public static String picPhotograph = "picPhotograph";

    public static String artCardTodo = "artCardTodo";
    public static String artCardTodo_avUserPointer = "artCardTodo.avUserPointer";
    public static String learnCardTodo = "learnCardTodo";
    public static String learnCardTodo_avUserPointer = "learnCardTodo.avUserPointer";
    public static String leisureCardTodo = "leisureCardTodo";
    public static String leisureCardTodo_avUserPointer = "leisureCardTodo.avUserPointer";
    public static String mindsCardTodo = "mindsCardTodo";
    public static String mindsCardTodo_avUserPointer = "mindsCardTodo.avUserPointer";
    public static String sportCardTodo = "sportCardTodo";
    public static String sportCardTodo_avUserPointer = "sportCardTodo.avUserPointer";
    public static String musicCardTodo = "musicCardTodo";
    public static String musicCardTodo_avUserPointer = "musicCardTodo.avUserPointer";
    public static String _avUserPointer = ".avUserPointer";

    public static String cardMiddleTable = "cardMiddleTable";
//    public static String cardTodo = "cardTodo";

    public static String createdAt = "createdAt";
    public static String updatedAt = "updatedAt";
    public static String iconAVObjectId = "iconAVObjectId";
    public static String typeNum = "typeNum";
    public static String initDefault = "initDefault";
    public static String avUserPointer = "avUserPointer";
    public static String avUser = "avUser";
    public static String followee = "followee";
    public static String follower = "follower";
    public static String friendsDynamic = "friendsDynamic";
    public static String todoCollect = "todoCollect";
    public static String typeTodo = "typeTodo";
    public static String avUserId = "avUserId";
    public static String follow = "follow";
    public static String followerType = "关注 ta 的人";
    public static String followeeType = "ta 关注的人";
    public static String share_tor = "share_tor";
    public static String upVote = "upVote";
    public static String isUserActivity = "isUserActivity";
    public static String strComment = "strComment";
    public static String cardTodoId = "cardTodoId";
    public static String cardPointer = "cardPointer";
    public static String commentTodo = "commentTodo";
    public static String commentTodoId = "commentTodoId";
    public static String replyTodo = "replyTodo";
    public static String strReply = "strReply";
    public static String commentTodoPointer = "commentTodoPointer";
    public static String letterTodo = "letterTodo";
    public static String sendAVUser = "sendAVUser";
    public static String receiverAVUser = "receiverAVUser";
    public static String letterContent = "letterContent";
    public static String hasRead = "hasRead";
    public static String deleteSend = "deleteSend";
    public static String deleteReceiver = "deleteReceiver";
    public static String mobilePhoneVerified = "mobilePhoneVerified";
    public static String columnTodo = "columnTodo";
    public static String strTitle = "strTitle";
    public static String columnDynamic = "columnDynamic";
    public static String reportTodo = "reportTodo";
    public static String onLine = "onLine";

    /**
     * 将id转为图片格式
     * @param id
     * @return
     */
    public static String avObjectIdTransitionPicForm(String id) {
        return id + ".jpg";
    }

    /**
     * 判断字符串是否有值，如果为null或者是空字符串或者只有空格或者为"null"字符串，则返回true，否则则返回false
     */
    public static boolean isEmpty(String value) {
        if (value != null && !"".equalsIgnoreCase(value.trim())
                && !"null".equalsIgnoreCase(value.trim())) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 密码要求 至少8位， 必须包含字母和数字
     *
     * @param password
     * @return
     */
    public static boolean isPasswordValid(String password) {
        return password.length() > 8 && password.matches("(?i)[^a-z]*[a-z]+[^a-z]*");
    }

    ////////////////////////获取String类 名称或地址/////////////////////////


    //以系统时间为文件名
    public static String getFileName4System_jpg() {
        return new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".jpg";
    }

    //以系统时间为文件名
    public static String getFileName4System_MP4() {
        return new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".mp4";
    }

    //以系统时间为文件名
    public static String getFileName4System_3gp() {
        return new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".3gp";
    }

    //    Context.getFileDir();
// 获取内置存储下的文件目录，可以用来保存不能公开给其他应用的一些敏感数据如用户个人信息

    //    Context.getCacheDir();
// 获取内置存储下的缓存目录，可以用来保存一些缓存文件如图片，
// 当内置存储的空间不足时将系统自动被清除

    //设置录像 SD卡地址
    public static String getVideoPath() {
        return Environment.getExternalStorageDirectory().getPath() + "/NanNan/video";
    }

    //设置拍摄照片 SD卡地址
    public static String getPhotographPath() {
        return Environment.getExternalStorageDirectory().getPath() + "/NanNan/photograph";
    }

    //设置我的图片 SD卡地址
    public static String getPicturePath() {
        return Environment.getExternalStorageDirectory().getPath() + "/NanNan" + "/" + UIUtils.avUser.getUsername() + "/image";
    }

    //设置我下载的图片 SD卡地址
    public static String getDownloadPicturePath() {
        return Environment.getExternalStorageDirectory().getPath() + "/NanNan/downloadPic";
    }

    //设置我的缓存图片 SD卡地址
    public static String getCachePicturePath() {
        return Environment.getExternalStorageDirectory().getPath() + "/NanNan/cache";
    }
}
