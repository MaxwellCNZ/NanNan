package com.nannan.nannan.view.activity.pager;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.GetDataCallback;
import com.avos.avoscloud.GetFileCallback;
import com.bm.library.PhotoView;
import com.nannan.nannan.R;
import com.nannan.nannan.utils.StringUtils;
import com.nannan.nannan.utils.UIUtils;
import com.nannan.nannan.view.holder.HomeHolder;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.nannan.nannan.view.holder.HomeHolder.LOADING_FAILED;
import static com.nannan.nannan.view.holder.HomeHolder.LOADING_SUCCEED;

/**
 * Created by MaxwellCNZ on 2017/4/10.
 */

public class ShowImageActivity extends Activity {

    //    private ImageView mivShow;
    private ProgressBar mpbShow;
    private ImageButton mibShow;
    private String picUri;
    private String thumbnailUrl;
    private String avFileId;
    private ImageView mivFailed;
    private PhotoView mpvView;
    private FrameLayout mflShow;
    private boolean isUserActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image);
        initView();
        initData();
    }


    private void initData() {
        Intent intent = getIntent();
        isUserActivity = intent.getBooleanExtra(StringUtils.isUserActivity, false);
        picUri = intent.getStringExtra("picUri");
        Bitmap bitmap = HomeHolder.getPic4Dir(picUri);
        mpvView = new PhotoView(ShowImageActivity.this);
        mflShow.addView(mpvView);
        mpvView.enable();
        mpvView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        if (isUserActivity) {
            mibShow.setVisibility(View.GONE);
            if (bitmap != null) {
                mpbShow.setVisibility(View.GONE);
                mpvView.setImageBitmap(bitmap);
            } else {
                mpbShow.setVisibility(View.GONE);
                mpvView.setImageResource(R.mipmap.deer);
            }
        } else {
            thumbnailUrl = intent.getStringExtra("thumbnailUrl");
            avFileId = intent.getStringExtra("avFileId");
            if (bitmap != null) {
                mpbShow.setVisibility(View.GONE);
                mpvView.setImageBitmap(bitmap);
            } else {
                //获取缩略图
                getNetPic(thumbnailUrl, avFileId);
            }
        }
        mibShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //下载原图片并且显示出来
                //在下载之前，先判断本地是否存在该图片
                mpbShow.setVisibility(View.VISIBLE);
                Bitmap bitmap = UIUtils.getPic2AppointDir(avFileId);
                if (bitmap != null) {
                    //本地存在该原图
                    mpvView.setImageBitmap(bitmap);
                    mpbShow.setVisibility(View.GONE);
                } else {
                    //需要下载原图
                    loadOriginalPic();
                }

                //toast提醒保存到了指定的文件下

            }
        });

    }

    private boolean isLoadingOriginalPic = false;

    /**
     * 下载原图
     */
    private void loadOriginalPic() {
        if (!isLoadingOriginalPic) {
            isLoadingOriginalPic = true;
            mpvView.setImageBitmap(null);
            AVFile.withObjectIdInBackground(avFileId, new GetFileCallback<AVFile>() {
                @Override
                public void done(AVFile avFile, AVException e) {
                    if (UIUtils.hasException(e)) {
                        isLoadingOriginalPic = false;
                        mpbShow.setVisibility(View.GONE);
                        mivFailed.setVisibility(View.VISIBLE);
                        mivFailed.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mpbShow.setVisibility(View.VISIBLE);
                                mivFailed.setVisibility(View.GONE);
                                loadOriginalPic();
                            }
                        });
                        return;
                    }
                    avFile.getDataInBackground(new GetDataCallback() {
                        @Override
                        public void done(byte[] bytes, AVException e) {
                            if (UIUtils.hasException(e)) {
                                isLoadingOriginalPic = false;
                                mpbShow.setVisibility(View.GONE);
                                mivFailed.setVisibility(View.VISIBLE);
                                mivFailed.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        mpbShow.setVisibility(View.VISIBLE);
                                        mivFailed.setVisibility(View.GONE);
                                        loadOriginalPic();
                                    }
                                });
                                return;
                            }
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            mpbShow.setVisibility(View.GONE);
                            mpvView.setImageBitmap(bitmap);
                            UIUtils.savePic2AppointDir(bitmap, StringUtils.getDownloadPicturePath(), StringUtils.avObjectIdTransitionPicForm(avFileId));
                            UIUtils.toast4Long("文件已保存在:" + StringUtils.getDownloadPicturePath());
                            isLoadingOriginalPic = false;
                        }
                    });
                }
            });
        }
    }

    private void initView() {
        mpbShow = (ProgressBar) findViewById(R.id.pb_show_loading);
        mibShow = (ImageButton) findViewById(R.id.ib_show_more);
        mivFailed = (ImageView) findViewById(R.id.iv_loading_failed);
        mflShow = (FrameLayout) findViewById(R.id.fl_show);

    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == LOADING_SUCCEED) {
                Bitmap bitmap = (Bitmap) msg.obj;
                mpbShow.setVisibility(View.GONE);
                mpvView.setImageBitmap(bitmap);
            } else if (msg.what == LOADING_FAILED) {
                mpbShow.setVisibility(View.GONE);
                mivFailed.setVisibility(View.VISIBLE);
                mivFailed.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mpbShow.setVisibility(View.VISIBLE);
                        mivFailed.setVisibility(View.GONE);
                        getNetPic(thumbnailUrl, avFileId);
                    }
                });
            }
        }
    };

    /**
     * 从网络上获取图片
     */
    private void getNetPic(final String uri, final String avFileId) {

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

                    Message msg = new Message();
                    if (code == 200) {
                        InputStream inputStream = conn.getInputStream();
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        UIUtils.savePic2AppointDir(bitmap, StringUtils.getCachePicturePath(), StringUtils.avObjectIdTransitionPicForm(avFileId));
//                        miv.setImageBitmap(bitmap);
                        msg.what = HomeHolder.LOADING_SUCCEED;
                        msg.obj = bitmap;
                    } else {
                        msg.what = HomeHolder.LOADING_FAILED;
                    }
                    handler.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
