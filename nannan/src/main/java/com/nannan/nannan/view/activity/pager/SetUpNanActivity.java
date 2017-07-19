package com.nannan.nannan.view.activity.pager;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;
import com.nannan.nannan.R;
import com.nannan.nannan.bean.HomeCardTodoBean;
import com.nannan.nannan.utils.BitmapUtils;
import com.nannan.nannan.utils.PathFromUriUtil;
import com.nannan.nannan.utils.ProgressBarUtils;
import com.nannan.nannan.utils.SelectUtils;
import com.nannan.nannan.utils.SharedPreferenceUtils;
import com.nannan.nannan.utils.StatusBarUtils;
import com.nannan.nannan.utils.StringUtils;
import com.nannan.nannan.utils.UIUtils;
import com.nannan.nannan.view.net.NetAsyncTask;
import com.nannan.nannan.view.pager.HomePager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import static com.nannan.nannan.utils.UIUtils.avUser;
import static com.nannan.nannan.view.activity.MainActivity.checkDirPath;

/**
 * Created by MaxwellCNZ on 2017/3/22.
 */

public class SetUpNanActivity extends Activity implements View.OnClickListener {

    private static final int RESULT_PICK = 101;//从相册中选择
    private static final int RESULT_CAPTURE = 100;//从相机中选择

    private String titleBarText;
    private ImageButton mibQuit;
    private ImageButton mibType;
    private ImageButton mibIssue;
    private EditText metType;
    private RelativeLayout mrlTitle;
    private String[] types;
    private PopupWindow mPopupWindow;
    private ListView mListView;
    private Intent intent;
    private EditText metContent;
    private ImageView mivPicture;
    private File tempFile;
    private Bitmap bitmap;
    private String todoType;
    private String typeNum;
    private HomeCardTodoBean homeCardTodoBean;
    private SharedPreferenceUtils sp;
    private TextView mtvAddPicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_nan);
        sp = new SharedPreferenceUtils();
        intent = getIntent();
        titleBarText = intent.getStringExtra("titleBarText");
        StatusBarUtils.setWindowStatusBarColor(this, R.color.MasterColor2);
        initView();
        initData();

    }

    private void initData() {
        types = new String[]{"艺术", "学习", "闲暇", "脑洞", "运动", "音乐"};
    }

    private void initView() {
        mibQuit = (ImageButton) findViewById(R.id.ib_quit_user_edit);
        mibType = (ImageButton) findViewById(R.id.ib_type_setup);
        mibIssue = (ImageButton) findViewById(R.id.ib_issue_setup);
        metType = (EditText) findViewById(R.id.et_type_setup);
        mrlTitle = (RelativeLayout) findViewById(R.id.rl_title_bar_setup);
        metContent = (EditText) findViewById(R.id.et_content_setup);
        mivPicture = (ImageView) findViewById(R.id.iv_picture_setup);
        mtvAddPicture = (TextView) findViewById(R.id.tv_add_picture);
        if (!StringUtils.isEmpty(titleBarText)) {
            metType.setText(titleBarText);
        }
        //注册监听事件
        mibQuit.setOnClickListener(this);
        mibType.setOnClickListener(this);
        mibIssue.setOnClickListener(this);
        metType.setOnClickListener(this);
        mivPicture.setOnClickListener(this);

        if (!UIUtils.hasNetwork) {
            metContent.setHint("没有网络，最好别写，否则你会后悔的！！");
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_quit_user_edit:
                String content = metContent.getText().toString().trim();
                boolean isEmptyPicture = mivPicture.getDrawable() == null;
                //文字或图片存在 弹出提示
                if (!StringUtils.isEmpty(content) || !isEmptyPicture) {
                    showDialog4Exit();
                } else {
                    finish();
                }
                break;
            case R.id.ib_issue_setup:
                //首先判断内容是否为空，然后决定发布卡片
                if (!UIUtils.hasNetwork) {
                    UIUtils.toast4Shot("没有网呀 QAQ");
                    return;
                }
                String strType = metType.getText().toString();
                String strContent = metContent.getText().toString().trim();
                boolean hasPicture = mivPicture.getDrawable() != null;
                if (StringUtils.isEmpty(strType)) {
                    UIUtils.toast4Shot("请选择分类呀");
                    return;
                }
                if (!StringUtils.isEmpty(strContent) || hasPicture) {
                    //将类型转化
                    todoType = transformType2English(strType);
                    issueTheCard(strContent, hasPicture);
                } else {
                    UIUtils.toast4Shot("你还没有任何内容呀");
                }
                break;
            case R.id.ib_type_setup:
                showTypePopupWindow();
                break;
            case R.id.et_type_setup:
                showTypePopupWindow();
                break;
            case R.id.iv_picture_setup:
                //设置相册获取 和 拍照获取
                if (!UIUtils.hasSdcard) {
                    UIUtils.toast4Shot("sd卡未加载！");
                    return;
                }
                showPicturePopupWindow();
                break;
            default:
                break;
        }
    }

    /**
     * 发布卡片
     *
     * @param strContent
     * @param hasPicture
     */
    private void issueTheCard(final String strContent, final boolean hasPicture) {

        new NetAsyncTask() {
            Dialog dialog;
            AVObject cardTodo;
            AVFile avFile;

            @Override
            public void perTask() {
                dialog = ProgressBarUtils.showLoadingProgressBar(SetUpNanActivity.this);
                dialog.show();
            }

            @Override
            public void doinBack(final Handler handler) {

                homeCardTodoBean = new HomeCardTodoBean();
                homeCardTodoBean.typeTodo = todoType;
                homeCardTodoBean.strContent = strContent;
                homeCardTodoBean.nickName = sp.getString(StringUtils.nickname);
                homeCardTodoBean.introduce = sp.getString(StringUtils.introduce);
                homeCardTodoBean.iconUri = StringUtils.getCachePicturePath() + "/" +
                        StringUtils.avObjectIdTransitionPicForm(sp.getString(StringUtils.iconAVObjectId));
                //上传文件
                cardTodo = new AVObject(todoType);
                cardTodo.put(StringUtils.avUserPointer, avUser);
                cardTodo.put(StringUtils.strContent, strContent);
                if (hasPicture) {
                    try {
                        avFile = AVFile.withAbsoluteLocalPath(StringUtils.uploading_jpg, StringUtils.getPicturePath() + "/" + StringUtils.uploading_jpg);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    if (avFile != null) {
                        avFile.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(AVException e) {
                                if (e != null) {
                                    homeCardTodoBean.picAVFile = null;
                                    handler.sendMessage(handler.obtainMessage(NetAsyncTask.MESSAGE_FAILED));
                                    UIUtils.toast4Shot("文件上传错误，发布失败啊 TAT");
                                    return;
                                }
                                cardTodo.put(StringUtils.picPhotograph, avFile);
                                homeCardTodoBean.picAVFile = avFile;
                                //设置中间表
                                setMiddleTable(cardTodo, handler);
                            }
                        });
                    }
                } else {
                    //设置中间表  在此期间，他会自动保存cardTodo！！
                    homeCardTodoBean.picAVFile = null;
                    setMiddleTable(cardTodo, handler);
                }

            }

            @Override
            public void postTask(int messageWhat) {
                // 主线程睡眠，会暂停主界面所有的活动，比如会让progressbar暂停旋转
//                SystemClock.sleep(1500);
                dialog.dismiss();
                if (messageWhat == NetAsyncTask.MESSAGE_SUCCEED) {
                    finish();
                }
            }
        }.execute();
    }

    /**
     * 设置中间表
     *
     * @param cardTodo
     * @param handler
     */
    private void setMiddleTable(final AVObject cardTodo, final Handler handler) {
        final AVObject middleTable = new AVObject(StringUtils.cardMiddleTable);
        middleTable.put(todoType, cardTodo);
        middleTable.put(StringUtils.typeNum, typeNum);
        middleTable.put(StringUtils.avUser, avUser);
        middleTable.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                if (UIUtils.hasException(e)) {
                    handler.sendMessage(handler.obtainMessage(NetAsyncTask.MESSAGE_FAILED));
                    return;
                }
                homeCardTodoBean.millisecsData = System.currentTimeMillis();
                homeCardTodoBean.cardTodo = cardTodo;
                homeCardTodoBean.cardMiddle = middleTable;
                handler.sendMessage(handler.obtainMessage(NetAsyncTask.MESSAGE_SUCCEED));
                HomePager.getMyListViewAdapter().getDatas().add(0, homeCardTodoBean);
                HomePager.getMyListViewAdapter().notifyDataSetChanged();

                //创建一个新表，用来存储好友动态
                saveDynamic4Friends(cardTodo);
            }
        });
    }

    /**
     * 设置好友动态
     *
     * @param cardTodo
     */
    private void saveDynamic4Friends(final AVObject cardTodo) {
        AVQuery<AVUser> followerQuery = AVUser.followerQuery(avUser.getObjectId(), AVUser.class);
        followerQuery.findInBackground(new FindCallback<AVUser>() {
            @Override
            public void done(List<AVUser> list, AVException e) {
                if (UIUtils.hasException(e)) {
                    return;
                }
                ArrayList<AVObject> avObjects = new ArrayList<>();
                //也要把自己放进去，在好友动态中也要显示自己的内容
                AVObject friendsDynamic = new AVObject(StringUtils.friendsDynamic);
                friendsDynamic.put(todoType, cardTodo);
                friendsDynamic.put(StringUtils.typeNum, typeNum);
                friendsDynamic.put(StringUtils.follower, avUser);
                friendsDynamic.put(StringUtils.share_tor, avUser);
                avObjects.add(friendsDynamic);
                for (AVUser object : list) {
                    friendsDynamic = new AVObject(StringUtils.friendsDynamic);
                    friendsDynamic.put(todoType, cardTodo);
                    friendsDynamic.put(StringUtils.typeNum, typeNum);
                    friendsDynamic.put(StringUtils.share_tor, avUser);
                    friendsDynamic.put(StringUtils.follower, object);
                    avObjects.add(friendsDynamic);
                }
                AVObject.saveAllInBackground(avObjects);
            }
        });
    }

    private String transformType2English(String strType) {
        String type = "";
        switch (strType) {
            case "艺术":
                typeNum = "1";
                type = StringUtils.artCardTodo;
                break;
            case "学习":
                typeNum = "2";
                type = StringUtils.learnCardTodo;
                break;
            case "闲暇":
                typeNum = "3";
                type = StringUtils.leisureCardTodo;
                break;
            case "脑洞":
                typeNum = "4";
                type = StringUtils.mindsCardTodo;
                break;
            case "运动":
                typeNum = "5";
                type = StringUtils.sportCardTodo;
                break;
            case "音乐":
                typeNum = "6";
                type = StringUtils.musicCardTodo;
                break;
            default:
                break;
        }
        return type;
    }

    /**
     * 图片选择
     */
    private void showPicturePopupWindow() {
        View inflate = UIUtils.inflate(R.layout.popwindow_video);
        TextView tvOnline = (TextView) inflate.findViewById(R.id.tv_online_setup);
        TextView tvLocation = (TextView) inflate.findViewById(R.id.tv_location_setup);
        tvOnline.setBackground(SelectUtils.getSelector(UIUtils.getColorId(R.color.TextColorWhite), UIUtils.getColorId(R.color.MasterColor2), 0));
        tvLocation.setBackground(SelectUtils.getSelector(UIUtils.getColorId(R.color.TextColorWhite), UIUtils.getColorId(R.color.MasterColor2), 0));
        tvLocation.setText("本地相册");
        tvOnline.setText("在线拍照");
        final PopupWindow popupWindow = new PopupWindow(inflate, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setTouchable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setAnimationStyle(R.style.pop_down_up_anim_style);
        popupWindow.showAtLocation(getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);

        tvOnline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //开启照相功能
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                tempFile = new File(checkDirPath(StringUtils.getPhotographPath()), StringUtils.getFileName4System_jpg());
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
                startActivityForResult(intent, RESULT_CAPTURE);
                popupWindow.dismiss();
            }
        });
        tvLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //开启相册功能
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(Intent.createChooser(intent, "请选择图片"), RESULT_PICK);
                popupWindow.dismiss();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {

            case RESULT_PICK:
                //从相册中选择
                if (resultCode == RESULT_OK && data != null) {
                    Uri uri = data.getData();
                    String bitmapPath = PathFromUriUtil.getRealFilePathFromUri(UIUtils.getContext(), uri);
                    bitmap = BitmapUtils.decodeSampledBitmap(bitmapPath, 720, 1280);
                    bitmap = save2SDCard(bitmap);
                    mivPicture.setImageBitmap(bitmap);
                    mtvAddPicture.setVisibility(View.GONE);
                }
                break;
            case RESULT_CAPTURE:
                //照相选择
                if (resultCode == RESULT_OK) {
                    Uri uri = Uri.fromFile(tempFile);
                    String bitmapPath = PathFromUriUtil.getRealFilePathFromUri(UIUtils.getContext(), uri);
                    bitmap = BitmapUtils.decodeSampledBitmap(bitmapPath, 720, 1280);
                    bitmap = save2SDCard(bitmap);
                    mivPicture.setImageBitmap(bitmap);
                    mtvAddPicture.setVisibility(View.GONE);
                }
                break;
        }

    }

    /**
     * 将图片保存到sdcard中才能生成图片，
     * bitmap文件上传有点大，转成图片会小一些
     */
    public static Bitmap save2SDCard(Bitmap bitmap) {
        File file = new File(checkDirPath(StringUtils.getPicturePath()), StringUtils.uploading_jpg);
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
        return bitmap;
    }

    @Override
    public void onBackPressed() {
        String content = metContent.getText().toString().trim();
        boolean isEmptyPicture = mivPicture.getDrawable() == null;
        //文字或图片存在 弹出提示
        if (!StringUtils.isEmpty(content) || !isEmptyPicture) {
            showDialog4Exit();
        } else {
            finish();
        }
    }

    /**
     * 销毁该activity时是否提醒：不会保存内容
     */
    private void showDialog4Exit() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View inflate = UIUtils.inflate(R.layout.dialog_exit_setup);
        TextView tvRemind = (TextView) inflate.findViewById(R.id.tv_remind_dialog_setup);
        TextView tvOk = (TextView) inflate.findViewById(R.id.tv_ok_dialog_home);
        TextView tvNo = (TextView) inflate.findViewById(R.id.tv_no_dialog_home);
        tvRemind.setBackgroundColor(UIUtils.getColorId(R.color.MasterColor2));
        tvOk.setBackground(SelectUtils.getSelector(UIUtils.getColorId(R.color.MasterColor2), UIUtils.getColorId(R.color.MasterColor), 0));
        tvNo.setBackground(SelectUtils.getSelector(UIUtils.getColorId(R.color.MasterColor2), UIUtils.getColorId(R.color.MasterColor), 0));
        final AlertDialog dialog = builder.create();
        dialog.setView(inflate);
        dialog.show();
        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tvNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    /**
     * 初始化PopupWindow
     */
    private void showTypePopupWindow() {
        mListView = new ListView(UIUtils.getContext());
        mListView.setAdapter(new ArrayAdapter<String>(UIUtils.getContext(), R.layout.item_type_setup, R.id.tv_item_type_setup, types));

        mPopupWindow = new PopupWindow(mListView, metType.getWidth(), LinearLayout.LayoutParams.WRAP_CONTENT, true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setTouchable(true);
//        mPopupWindow.setAnimationStyle(R.style.pop_up_down_anim_style);
        //不用动画财TMD是真爱啊！！！
        mPopupWindow.showAsDropDown(metType, 0, 0);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                metType.setText(types[position]);
                mPopupWindow.dismiss();
            }
        });
    }
}
