package com.nannan.nannan.view.activity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.GetCallback;
import com.nannan.nannan.R;
import com.nannan.nannan.receiver.NetworkConnectChangedReceiver;
import com.nannan.nannan.utils.BitmapUtils;
import com.nannan.nannan.utils.FillPagerAdapterUtils;
import com.nannan.nannan.utils.PathFromUriUtil;
import com.nannan.nannan.utils.SharedPreferenceUtils;
import com.nannan.nannan.utils.StringUtils;
import com.nannan.nannan.utils.UIUtils;
import com.nannan.nannan.view.activity.pager.ClipHeaderActivity;
import com.nannan.nannan.view.net.AVObjectNet;
import com.nannan.nannan.view.pager.BasePager;
import com.nannan.nannan.view.pager.FollowPager;
import com.nannan.nannan.view.pager.HomePager;
import com.nannan.nannan.view.pager.LetterPager;
import com.nannan.nannan.view.pager.MorePager;
import com.nannan.nannan.view.pager.SquarePager;
import com.nannan.nannan.view.widget.NoScrollViewPager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import static com.nannan.nannan.utils.UIUtils.avUser;

//import android.app.FragmentManager;
//import android.app.FragmentTransaction;

/**
 * Created by MaxwellCNZ on 2017/3/5.
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //    private Button mbtnOout;
    //工具栏是否是隐藏状态
//    private boolean isToolHide;
    //默认的动画时间
    private static final int TIME_ANIMATION = 300;
    private static final int CROP_PHOTO = 102;//调用截图界面
    private static final int RESULT_PICK = 101;//从相册中选择
    private static final int RESULT_CAPTURE = 100;//从相机中选择
    private static final int RESULT_PRIVILEGE = 200;//权限

    //    public String activityType = "MainActivity";
    private LinearLayout mllButton;
    private NoScrollViewPager mvpViewPager;
    private ImageButton mibHome;
    private ImageButton mibDiscover;
    private ImageButton mibAttention;
    private ImageButton mibLetter;
    private ImageButton mibMore;
    private ArrayList<BasePager> mPagers;
    private HomePager mHomePager;
    private static boolean isToolHide = false;
    private boolean isIcon;
    private File tempFile;
    private AVFile avFile;
    private SharedPreferenceUtils sp;
    private SquarePager squarePager;
    private FollowPager followPager;
    //    private SharedPreferenceUtils sp;
//    private FrameLayout flMain;

    //    private ScrollView mflScroller;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UIUtils.printLog("现联网-- main");
        setContentView(R.layout.activity_main);
        /*if (UIUtils.hasSdcard){
            initData(savedInstanceState); //创建相机文件
        }*/
        sp = new SharedPreferenceUtils();
        avUser = AVUser.getCurrentUser();
        initDefaultInfo(); // 初始化默认配置
        initView();
        initViewPager();
        initData();
        registerBroadcast();
    }

    /**
     * 注册广播
     */
    private void registerBroadcast() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(new NetworkConnectChangedReceiver(), filter);
    }

    /**
     * 初始化所有默认配置
     */
    private void initDefaultInfo() {
        //初始化默认配置
        if (!sp.getBoolean(StringUtils.initDefault)){
            sp.putBoolean(StringUtils.initDefault, true);
            sp.putBoolean("isSave_" + StringUtils.iconShow, true);
            sp.putBoolean("isSave_" + StringUtils.headShow, true);
            //设置默认信息
            sp.putString(StringUtils.nickname_default, "喃喃");
            sp.putString(StringUtils.describe_default, "快来介绍你自己吧 : ) ");
            //看看缩略图存在吗
            avUser.fetchInBackground(new GetCallback<AVObject>() {
                @Override
                public void done(AVObject avObject, AVException e) {
                    if (e != null){
                        return;
                    }
                    AVFile iconFile = avObject.getAVFile(StringUtils.iconShow);
                    if (iconFile != null) {
                        File file = new File(StringUtils.getCachePicturePath() + "/" + StringUtils.avObjectIdTransitionPicForm(iconFile.getObjectId()));
                        if (file.exists()) {
                            sp.putString(StringUtils.iconAVObjectId, iconFile.getObjectId());
                        }
                    }
                }
            });
        }
    }

    private void initData() {
        mHomePager.setOnMenuShowListener(new BasePager.onMenuShowListener() {
            @Override
            public void onShow() {
                showTool();
            }

            @Override
            public void onHide() {
                hideTool();
            }
        });
        squarePager.setOnMenuShowListener(new BasePager.onMenuShowListener() {
            @Override
            public void onShow() {
                showTool();
            }

            @Override
            public void onHide() {
                hideTool();
            }
        });
        followPager.setOnMenuShowListener(new BasePager.onMenuShowListener() {
            @Override
            public void onShow() {
                showTool();
            }

            @Override
            public void onHide() {
                hideTool();
            }
        });
        mvpViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                mPagers.get(position).initData();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private void initView() {
//        sp = new SharedPreferenceUtils();
//        mflScroller = (ScrollView) findViewById(R.cardTodoId.scroller);
        mvpViewPager = (NoScrollViewPager) findViewById(R.id.vp_main_viewpager);
        mllButton = (LinearLayout) findViewById(R.id.ll_main_button);

        mibHome = (ImageButton) findViewById(R.id.ib_main_home);
        mibDiscover = (ImageButton) findViewById(R.id.ib_main_discover);
        mibAttention = (ImageButton) findViewById(R.id.ib_main_attention);
        mibLetter = (ImageButton) findViewById(R.id.ib_main_letter);
        mibMore = (ImageButton) findViewById(R.id.ib_main_more);

        mibHome.setOnClickListener(this);
        mibDiscover.setOnClickListener(this);
        mibAttention.setOnClickListener(this);
        mibLetter.setOnClickListener(this);
        mibMore.setOnClickListener(this);
    }

    /**
     * 缓存viewpager面
     */
    private void initViewPager() {
        mPagers = new ArrayList<>();
        mHomePager = new HomePager(this);
        squarePager = new SquarePager(this);
        followPager = new FollowPager();
        mPagers.add(mHomePager);
        mPagers.add(squarePager);
        mPagers.add(followPager);
        mPagers.add(new LetterPager(this));
        mPagers.add(new MorePager(this));
        mvpViewPager.setAdapter(new FillPagerAdapterUtils(mPagers));
        mPagers.get(0).initData();

        mHomePager.setOnAlbumOrPhotographListener(new HomePager.onAlbumOrPhotographListener() {
            @Override
            public void onAlbum(boolean isIcon) {
                //开启相册功能
                MainActivity.this.isIcon = isIcon;
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(Intent.createChooser(intent, "请选择图片"), RESULT_PICK);

            }

            @Override
            public void onPhotograph(boolean isIcon) {
                //开启照相公能
                MainActivity.this.isIcon = isIcon;
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                tempFile = new File(checkDirPath(StringUtils.getPhotographPath()), StringUtils.getFileName4System_jpg());
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
                startActivityForResult(intent, RESULT_CAPTURE);
            }
        });
    }

    /**
     * 为拍的照片检查存放的文件路径是否正确
     */
    public static String checkDirPath(String dirPath) {
        if (TextUtils.isEmpty(dirPath)) {
            return "";
        }

        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dirPath;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RESULT_PRIVILEGE:
                Intent intent = getIntent();
                MainActivity.this.finish();
                startActivity(intent);
                break;
            case RESULT_PICK:
                if (resultCode == RESULT_OK && data != null) {
                    Uri uri = data.getData();
                    //在设置背景之前了解是谁的背景,然后决定是否打开裁剪界面
                    if (isIcon) {
                        //打开截图界面
                        starCropPhoto(uri);
                    } else {
                        //调整图片大小，设置背景
                        mListener.onChangeBackground(imageHeadShow(uri));
                    }
                }
                break;
            case CROP_PHOTO:
                if (resultCode == RESULT_OK && data != null) {
                    Uri uri = data.getData();
                    if (uri == null) {
                        return;
                    }
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mListener.onChangeBackground(bitmap);
                }
                break;
            case RESULT_CAPTURE:
                if (resultCode == RESULT_OK) {
                    Uri uri = Uri.fromFile(tempFile);
                    //在设置背景之前了解是谁的背景,然后决定是否打开裁剪界面
                    if (isIcon) {
                        //打开截图界面
                        starCropPhoto(uri);
                    } else {
                        //调整图片大小，设置背景
                        mListener.onChangeBackground(imageHeadShow(uri));
                    }
                }
                break;
            default:
                break;
        }
    }

    /**
     * 设置头布局的图片展示,并把文件放到了指定文件夹中
     */
    private Bitmap imageHeadShow(Uri uri) {
        String bitmapPath = PathFromUriUtil.getRealFilePathFromUri(UIUtils.getContext(), uri);
        Bitmap bitmap = BitmapUtils.decodeSampledBitmap(bitmapPath, 720, 1280);
        File file = new File(checkDirPath(StringUtils.getPicturePath()), StringUtils.HEAD_SHOW_jpg);
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
//        reFreshHeadAndIcon();
        //上传文件到云端
        //创建文件
        try {
            avFile = AVFile.withAbsoluteLocalPath(StringUtils.HEAD_SHOW_jpg, StringUtils.getPicturePath() + "/" + StringUtils.HEAD_SHOW_jpg);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        //上传文件 绑定文件  删除文件
        AVObjectNet.upLoadingAVFile2IconAndHead(avFile, StringUtils.headShow);
        return bitmap;
    }

    /**
     * 打开截图界面
     *
     * @param uri 原图的Uri
     */
    public void starCropPhoto(Uri uri) {

        if (uri == null) {
            return;
        }
        Intent intent = new Intent();
        intent.setClass(this, ClipHeaderActivity.class);
        intent.setData(uri);
        intent.putExtra("side_length", 200);//裁剪图片宽高
        startActivityForResult(intent, CROP_PHOTO);
    }

    //默认选中的按钮
    private int btn = R.id.ib_main_home;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_main_home:
                colorRecover(btn);
                mibHome.setImageResource(R.drawable.vector_home_pressed);
                btn = R.id.ib_main_home;
                mvpViewPager.setCurrentItem(0, false);//false表示没有滑动效果
                break;
            case R.id.ib_main_discover:
                colorRecover(btn);
                mibDiscover.setImageResource(R.drawable.vector_discover_pressed);
                btn = R.id.ib_main_discover;
                mvpViewPager.setCurrentItem(1, false);
                break;
            case R.id.ib_main_attention:
                colorRecover(btn);
                mibAttention.setImageResource(R.drawable.vector_attention_pressed);
                btn = R.id.ib_main_attention;
                mvpViewPager.setCurrentItem(2, false);
                break;
            case R.id.ib_main_letter:
                colorRecover(btn);
                mibLetter.setImageResource(R.drawable.vector_letter_pressed);
                btn = R.id.ib_main_letter;
                mvpViewPager.setCurrentItem(3, false);
                break;
            case R.id.ib_main_more:
                colorRecover(btn);
                mibMore.setImageResource(R.drawable.vector_more_pressed);
                btn = R.id.ib_main_more;
                mvpViewPager.setCurrentItem(4, false);
                break;
            default:
                break;
        }
    }

    /**
     * 按钮颜色复位
     *
     * @param id
     */
    private void colorRecover(int id) {
        switch (id) {
            case R.id.ib_main_home:
                mibHome.setImageResource(R.drawable.vector_home);
                break;
            case R.id.ib_main_discover:
                mibDiscover.setImageResource(R.drawable.vector_discover);
                break;
            case R.id.ib_main_attention:
                mibAttention.setImageResource(R.drawable.vector_attention);
                break;
            case R.id.ib_main_letter:
                mibLetter.setImageResource(R.drawable.vector_letter);
                break;
            case R.id.ib_main_more:
                mibMore.setImageResource(R.drawable.vector_more);
                break;
            default:
                break;
        }

    }

    /**
     * 显示工具栏
     */
    private void showTool() {

        if (!isToolHide) {
            return;
        }
        //以满屏startY为起点来定位，这样就不会乱了！
        int startY = getWindow().getDecorView()
                .getHeight();
        ObjectAnimator anim = ObjectAnimator.ofFloat(mllButton, "y",
                startY, startY - mllButton.getHeight());
        anim.setDuration(TIME_ANIMATION);
        anim.start();
        isToolHide = false;
    }

    /**
     * 隐藏工具栏
     */
    private void hideTool() {
        if (isToolHide) {
            return;
        }
        int startY = getWindow().getDecorView()
                .getHeight();
        ObjectAnimator anim = ObjectAnimator.ofFloat(mllButton, "y",
                startY - mllButton.getHeight(), startY);
        anim.setDuration(TIME_ANIMATION);
        anim.start();
        isToolHide = true;
    }

//    public FrameLayout getFlMain() {
//        return flMain;
//    }

    /**
     * 设置一个监听 用来调用更改homepager中的背景
     */
    public interface onAlbumOrPhotographBackgroundListener {
        void onChangeBackground(Bitmap bitmap);
    }

    private onAlbumOrPhotographBackgroundListener mListener;

    public void setOnAlbumOrPhotographBackgroundListener(onAlbumOrPhotographBackgroundListener listener) {
        mListener = listener;
    }

    //按两次退出程序
    Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isExit = false;
        }
    };

    private static boolean isExit = false;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void exit() {
        if (!isExit) {
            isExit = true;
            UIUtils.toast4Shot("再按一次退出程序");
            // 利用handler延迟发送更改状态信息
            mHandler.sendEmptyMessageDelayed(0, 2000);
        } else {
            finish();
        }
    }


//    /**
//     * 设置一个监听 用来调用云端的服务器下载图片
//     */
//    public interface onAVFile2PictureListener {
//        void onGetAVFile();
//    }
//
//    private static onAVFile2PictureListener mAVFileListener;
//
//    public void setOnAVfile2PictureListener(onAVFile2PictureListener listener) {
//        mAVFileListener = listener;
//    }

}
