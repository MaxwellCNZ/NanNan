package com.nannan.nannan.view.holder;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.nannan.nannan.R;
import com.nannan.nannan.bean.LetterBean;
import com.nannan.nannan.utils.StringUtils;
import com.nannan.nannan.utils.UIUtils;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.nannan.nannan.view.holder.HomeHolder.LOADING_FAILED;
import static com.nannan.nannan.view.holder.HomeHolder.LOADING_SUCCEED;
import static com.nannan.nannan.view.holder.HomeHolder.getPic4Dir;

/**
 * Created by MaxwellCNZ on 2017/5/8.
 */

public class LetterHolder extends BaseHolder<LetterBean> {

    private final boolean shouldShowRead;
    private RoundedImageView mrivIcon;
    private TextView mtvName;
    private TextView mtvData;
    private ImageView mivRead;
    private TextView mtvConten;

    public LetterHolder(boolean shouldShowRead) {
        this.shouldShowRead = shouldShowRead;
    }

    @Override
    public View initView() {
        View inflate = UIUtils.inflate(R.layout.item_letter);
        mrivIcon = (RoundedImageView) inflate.findViewById(R.id.riv_icon_letter);
        mtvName = (TextView) inflate.findViewById(R.id.tv_name_letter);
        mtvData = (TextView) inflate.findViewById(R.id.tv_data_letter);
        mivRead = (ImageView) inflate.findViewById(R.id.iv_letter_read);
        mtvConten = (TextView) inflate.findViewById(R.id.tv_content_letter);
        return inflate;
    }

    @Override
    protected void fillView(LetterBean data) {
        if (shouldShowRead){
            if (data.hasRead){
                mivRead.setVisibility(View.VISIBLE);
            }else {
                mivRead.setVisibility(View.INVISIBLE);
            }
        }else {
            mivRead.setVisibility(View.INVISIBLE);
        }

        if (!StringUtils.isEmpty(data.nickName)){
            mtvName.setText(data.nickName);
        }else {
            mtvName.setText("喃喃");
        }
        mtvData.setText(UIUtils.millisecs2DateString(data.millisecsData));
        mtvConten.setText(data.letterContent);
        if (data.iconFile != null) {
            String iconUri = StringUtils.getCachePicturePath() + "/" +
                    StringUtils.avObjectIdTransitionPicForm(data.iconFile.getObjectId());
            Bitmap iconBitmap = getPic4Dir(iconUri);
            String iconThumbnailUrl = data.iconFile.getThumbnailUrl(true, 100, 100);
            if (iconBitmap != null) {
                //存在图片
                mrivIcon.setImageBitmap(iconBitmap);
            } else {
                String avFileId = data.iconFile.getObjectId();
                getNetIcon(iconThumbnailUrl, avFileId);
            }
        } else {
            mrivIcon.setImageResource(R.mipmap.deer);
        }

    }

    private Handler iconHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == LOADING_SUCCEED) {
                Bitmap bitmap = (Bitmap) msg.obj;
                mrivIcon.setImageBitmap(bitmap);
            } else if (msg.what == LOADING_FAILED) {
                mrivIcon.setImageResource(R.mipmap.deer);
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
                    mrivIcon.setImageResource(R.mipmap.deer);
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
