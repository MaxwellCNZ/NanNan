package com.nannan.nannan.view.holder;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.nannan.nannan.R;
import com.nannan.nannan.bean.AVUserInfo;
import com.nannan.nannan.utils.StringUtils;
import com.nannan.nannan.utils.UIUtils;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.nannan.nannan.view.holder.HomeHolder.LOADING_FAILED;
import static com.nannan.nannan.view.holder.HomeHolder.LOADING_SUCCEED;
import static com.nannan.nannan.view.holder.HomeHolder.getPic4Dir;

/**
 * Created by MaxwellCNZ on 2017/4/20.
 */

public class AVUserInfoHolder extends BaseHolder<AVUserInfo> {

    private RoundedImageView rivIcon;
    private TextView tvName;
    private TextView tvIntroduce;
    private String iconThumbnailUrl;
    private String avFileId;

    @Override
    public View initView() {
        View inflate = UIUtils.inflate(R.layout.item_follow);
        rivIcon = (RoundedImageView) inflate.findViewById(R.id.riv_icon_follow);
        tvName = (TextView) inflate.findViewById(R.id.tv_name_follow);
        tvIntroduce = (TextView) inflate.findViewById(R.id.tv_introduce_follow);
        return inflate;
    }

    @Override
    protected void fillView(final AVUserInfo data) {
        if (!StringUtils.isEmpty(data.nickName)){
            tvName.setText(data.nickName);
        }else {
            tvName.setText("喃喃");
        }
        if (!StringUtils.isEmpty(data.introduce)){
            tvIntroduce.setText(data.introduce);
        }else {
            tvIntroduce.setText("ta 还没有任何介绍哦~");
        }
        if (data.iconAVFile != null) {
            String iconUri = StringUtils.getCachePicturePath() + "/" +
                    StringUtils.avObjectIdTransitionPicForm(data.iconAVFile.getObjectId());
            Bitmap iconBitmap = getPic4Dir(iconUri);
            iconThumbnailUrl = data.iconAVFile.getThumbnailUrl(true, 100, 100);
            if (iconBitmap != null) {
                //存在图片
                rivIcon.setImageBitmap(iconBitmap);
            } else {
                avFileId = data.iconAVFile.getObjectId();
                getNetIcon(iconThumbnailUrl, avFileId);
            }
        } else {
            rivIcon.setImageResource(R.mipmap.deer);
        }
    }

    private Handler iconHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == LOADING_SUCCEED) {
                Bitmap bitmap = (Bitmap) msg.obj;
                rivIcon.setImageBitmap(bitmap);
            } else if (msg.what == LOADING_FAILED) {
                rivIcon.setImageResource(R.mipmap.deer);
            }
        }
    };
    private void getNetIcon(final String iconUrl, final String avFileId) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    URL url = new URL(iconUrl);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET"); // 设置请求方法为GET
                    conn.setReadTimeout(5 * 1000); // 设置请求过时时间为5秒
                    int code = conn.getResponseCode();

                    Message msg = new Message();
                    if (code == 200) {
                        InputStream inputStream = conn.getInputStream();
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        UIUtils.savePic2AppointDir(bitmap, StringUtils.getCachePicturePath(), StringUtils.avObjectIdTransitionPicForm(avFileId));
//                        miv.setImageBitmap(bitmap);
                        msg.what = LOADING_SUCCEED;
                        msg.obj = bitmap;
                    } else {
                        msg.what = LOADING_FAILED;
                    }
                    iconHandler.sendMessage(msg);
                } catch (Exception e) {
                    rivIcon.setImageResource(R.mipmap.deer);
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
