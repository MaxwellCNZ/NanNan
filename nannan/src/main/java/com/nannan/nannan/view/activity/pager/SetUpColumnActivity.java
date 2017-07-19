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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;
import com.nannan.nannan.R;
import com.nannan.nannan.utils.BitmapUtils;
import com.nannan.nannan.utils.PathFromUriUtil;
import com.nannan.nannan.utils.ProgressBarUtils;
import com.nannan.nannan.utils.SelectUtils;
import com.nannan.nannan.utils.StatusBarUtils;
import com.nannan.nannan.utils.StringUtils;
import com.nannan.nannan.utils.UIUtils;
import com.nannan.nannan.view.net.NetAsyncTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import static com.nannan.nannan.view.activity.MainActivity.checkDirPath;

/**
 * Created by MaxwellCNZ on 2017/5/9.
 */

public class SetUpColumnActivity extends Activity implements View.OnClickListener{

    private ImageView mivPicture;
    private TextView mtvAddPicture;
    private static final int RESULT_PICK = 101;//从相册中选择
    private static final int RESULT_CAPTURE = 100;//从相机中选择
    private File tempFile;
    private Bitmap bitmap;
    private EditText metTitle;
    private AVFile avFile;
    private String content;
    private String title;
    private boolean isEmptyPicture;
    private AVFile avFileColumn;
    private EditText metContent;
    private ImageView mivAddPicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_column);
        StatusBarUtils.setWindowStatusBarColor(this, R.color.MasterColor2);
        initView();
    }

    private void initView() {
        ImageButton mibQuit = (ImageButton) findViewById(R.id.ib_quit_user_edit);
        ImageButton mibIssue =  (ImageButton) findViewById(R.id.ib_issue_setup);
        mivPicture = (ImageView) findViewById(R.id.iv_picture_setup);
        mtvAddPicture = (TextView) findViewById(R.id.tv_add_picture);
        metTitle = (EditText) findViewById(R.id.et_title_column);
        metContent = (EditText) findViewById(R.id.et_content);

        mibQuit.setOnClickListener(this);
        mibIssue.setOnClickListener(this);
        mivPicture.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ib_quit_user_edit:
                content = metContent.getText().toString().trim();
                title = metTitle.getText().toString().trim();
                isEmptyPicture = mivPicture.getDrawable() == null;
                //文字或图片存在 弹出提示
                if (!StringUtils.isEmpty(content) || !isEmptyPicture || !StringUtils.isEmpty(title)) {
                    showDialog4Exit();
                } else {
                    finish();
                }
                break;
            case R.id.ib_issue_setup:
                if (!UIUtils.hasNetwork) {
                    UIUtils.toast4Shot("没有网呀 QAQ");
                    return;
                }
                content = metContent.getText().toString().trim();
                title = metTitle.getText().toString().trim();
                isEmptyPicture = mivPicture.getDrawable() == null;
                if (StringUtils.isEmpty(title)) {
                    UIUtils.toast4Shot("你还没有标题呢！");
                    return;
                }
                if (isEmptyPicture){
                    UIUtils.toast4Shot("你还没有封面图片呢！");
                    return;
                }
                if (StringUtils.isEmpty(content)) {
                    UIUtils.toast4Shot("你还没有任何内容呢！");
                    return;
                }
                issueTheCard(title, content);

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
     * @param title
     * @param content
     */
    private void issueTheCard(final String title, final String content) {
        new NetAsyncTask() {

            Dialog dialog;

            @Override
            public void perTask() {
                dialog = ProgressBarUtils.showLoadingProgressBar(SetUpColumnActivity.this);
                dialog.show();
            }

            @Override
            public void doinBack(final Handler handler) {
                final AVObject columnTodo = new AVObject(StringUtils.columnTodo);
                columnTodo.put(StringUtils.strContent, content);
                columnTodo.put(StringUtils.strTitle, title);
                columnTodo.put(StringUtils.avUserPointer, UIUtils.avUser);
                try {
                    avFileColumn = AVFile.withAbsoluteLocalPath(StringUtils.uploadingColumn_jpg, StringUtils.getPicturePath() + "/" + StringUtils.uploadingColumn_jpg);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                if (avFileColumn != null) {
                    avFileColumn.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(AVException e) {
                            if (e != null) {
                                handler.sendMessage(handler.obtainMessage(NetAsyncTask.MESSAGE_FAILED));
                                UIUtils.toast4Shot("文件上传错误，发布失败啊 TAT");
                                return;
                            }
                            columnTodo.put(StringUtils.picPhotograph, avFileColumn);
                            columnTodo.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(AVException e) {
                                    if (UIUtils.hasException(e)){
                                        handler.sendMessage(handler.obtainMessage(NetAsyncTask.MESSAGE_FAILED));
                                        return;
                                    }
                                    handler.sendMessage(handler.obtainMessage(NetAsyncTask.MESSAGE_SUCCEED));
                                    saveDynamic4Friends(columnTodo);
                                }
                            });
                        }
                    });
                }
            }

            @Override
            public void postTask(int messageWhat) {
                dialog.dismiss();
                if (messageWhat == NetAsyncTask.MESSAGE_SUCCEED) {
                    finish();
                }
            }
        }.execute();

    }

    /**
     * 设置好友动态
     *
     * @param columnTodo
     */
    private void saveDynamic4Friends(final AVObject columnTodo) {
        AVQuery<AVUser> followerQuery = AVUser.followerQuery(UIUtils.avUser.getObjectId(), AVUser.class);
        followerQuery.findInBackground(new FindCallback<AVUser>() {
            @Override
            public void done(List<AVUser> list, AVException e) {
                if (UIUtils.hasException(e)) {
                    return;
                }
                ArrayList<AVObject> avObjects = new ArrayList<>();
                //也要把自己放进去，在好友动态中也要显示自己的内容
                AVObject columnDynamic = new AVObject(StringUtils.columnDynamic);
                columnDynamic.put(StringUtils.columnTodo, columnTodo);
                columnDynamic.put(StringUtils.follower, UIUtils.avUser);
                columnDynamic.put(StringUtils.share_tor, UIUtils.avUser);
                avObjects.add(columnDynamic);
                for (AVUser object : list) {
                    columnDynamic = new AVObject(StringUtils.columnDynamic);
                    columnDynamic.put(StringUtils.columnTodo, columnTodo);
                    columnDynamic.put(StringUtils.share_tor, UIUtils.avUser);
                    columnDynamic.put(StringUtils.follower, object);
                    avObjects.add(columnDynamic);
                }
                AVObject.saveAllInBackground(avObjects);
            }
        });
    }

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
                    bitmap = save2SDCardColumn(bitmap);
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
                    bitmap = save2SDCardColumn(bitmap);
                    mivPicture.setImageBitmap(bitmap);
                    mtvAddPicture.setVisibility(View.GONE);
                }
                break;
        }
    }
    public static Bitmap save2SDCardColumn(Bitmap bitmap) {
        File file = new File(checkDirPath(StringUtils.getPicturePath()), StringUtils.uploadingColumn_jpg);
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
        String title = metTitle.getText().toString().trim();
        boolean isEmptyPicture = mivPicture.getDrawable() == null;
        //文字或图片存在 弹出提示
        if (!StringUtils.isEmpty(content) || !isEmptyPicture || !StringUtils.isEmpty(title)) {
            showDialog4Exit();
        } else {
            finish();
        }
    }
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
}
