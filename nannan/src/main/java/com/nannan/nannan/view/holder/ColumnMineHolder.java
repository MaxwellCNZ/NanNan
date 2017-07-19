package com.nannan.nannan.view.holder;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.DeleteCallback;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetDataCallback;
import com.avos.avoscloud.GetFileCallback;
import com.makeramen.roundedimageview.RoundedImageView;
import com.nannan.nannan.R;
import com.nannan.nannan.bean.BridgeColumn;
import com.nannan.nannan.bean.ColumnTodoBeen;
import com.nannan.nannan.utils.SelectUtils;
import com.nannan.nannan.utils.StringUtils;
import com.nannan.nannan.utils.UIUtils;
import com.nannan.nannan.view.activity.pager.ColumnDetailActivity;
import com.nannan.nannan.view.activity.user.UserActivity;
import com.nannan.nannan.view.adapter.MyListViewAdapter;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import static com.nannan.nannan.utils.UIUtils.bridgeColumn;
import static com.nannan.nannan.view.holder.HomeHolder.LOADING_FAILED;
import static com.nannan.nannan.view.holder.HomeHolder.LOADING_SUCCEED;
import static com.nannan.nannan.view.holder.HomeHolder.getPic4Dir;

/**
 * Created by MaxwellCNZ on 2017/5/10.
 */

public class ColumnMineHolder extends BaseHolder<ColumnTodoBeen> {

    private final ListView listView;
    private final Activity activity;
    private final MyListViewAdapter myListViewAdapter;
    private RoundedImageView mrivIcon;
    private TextView mtvNickname;
    private TextView mtvData;
    private TextView mtvTitle;
    private ImageButton mibMore;
    private ImageView mivColumn;
    private String iconThumbnailUrl;
    private String avFileId;
    private View inflate;
    private ColumnTodoBeen data;

    public ColumnMineHolder(MyListViewAdapter myListViewAdapter, Activity activity, ListView listView) {
        this.myListViewAdapter = myListViewAdapter;
        this.listView = listView;
        this.activity = activity;
    }


    @Override
    public View initView() {
        inflate = UIUtils.inflate(R.layout.item_column);
        mrivIcon = (RoundedImageView) inflate.findViewById(R.id.riv_icon_item_column);
        mtvNickname = (TextView) inflate.findViewById(R.id.tv_name_item_column);
        mtvData = (TextView) inflate.findViewById(R.id.tv_data_item_column);
        mtvTitle = (TextView) inflate.findViewById(R.id.tv_item_column_title);
        mibMore = (ImageButton) inflate.findViewById(R.id.ib_more_item_column);
        mivColumn = (ImageView) inflate.findViewById(R.id.iv_item_column_pic);
        return inflate;
    }

    @Override
    protected void fillView(final ColumnTodoBeen data) {
        this.data = data;
        mivColumn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColumnDetailActivity.setOnRefreshAdapter(new ColumnDetailActivity.onRefreshAdapter() {
                    @Override
                    public void onRefresh() {
                        myListViewAdapter.getDatas().remove(getPosition());
                        myListViewAdapter.notifyDataSetChanged();
                    }
                });
                bridgeColumn = new BridgeColumn(data);
                Intent intent = new Intent(UIUtils.getContext(), ColumnDetailActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                UIUtils.getContext().startActivity(intent);
            }
        });
        mtvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColumnDetailActivity.setOnRefreshAdapter(new ColumnDetailActivity.onRefreshAdapter() {
                    @Override
                    public void onRefresh() {
                        myListViewAdapter.getDatas().remove(getPosition());
                        myListViewAdapter.notifyDataSetChanged();
                    }
                });
                bridgeColumn = new BridgeColumn(data);
                Intent intent = new Intent(UIUtils.getContext(), ColumnDetailActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                UIUtils.getContext().startActivity(intent);
            }
        });


        mibMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPop();
            }
        });

        mrivIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UIUtils.getContext(), UserActivity.class);
                intent.putExtra(StringUtils.avUserId, data.avUserPointer.getObjectId());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                UIUtils.getContext().startActivity(intent);
            }
        });
        mtvNickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UIUtils.getContext(), UserActivity.class);
                intent.putExtra(StringUtils.avUserId, data.avUserPointer.getObjectId());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                UIUtils.getContext().startActivity(intent);
            }
        });

        mtvData.setText(UIUtils.millisecs2DateString(data.millisecsData));
        mtvNickname.setText(data.nickname);
        mtvTitle.setText(data.strTitle);

        if (data.iconAVFile != null) {
            String iconUri = StringUtils.getCachePicturePath() + "/" +
                    StringUtils.avObjectIdTransitionPicForm(data.iconAVFile.getObjectId());
            Bitmap iconBitmap = getPic4Dir(iconUri);
            iconThumbnailUrl = data.iconAVFile.getThumbnailUrl(true, 100, 100);
            if (iconBitmap != null) {
                //存在图片
                mrivIcon.setImageBitmap(iconBitmap);
            } else {
                avFileId = data.iconAVFile.getObjectId();
                getNetIcon(iconThumbnailUrl, avFileId);
            }
        } else {
            mrivIcon.setImageResource(R.mipmap.deer);
        }

        if (data.picAVFile != null) {
            Bitmap bitmap = UIUtils.getPic2AppointDir(data.picAVFile.getObjectId());
            if (bitmap != null) {
                //本地存在该原图
                mivColumn.setImageBitmap(bitmap);
            } else {
                //需要下载原图
                loadOriginalPic(data.picAVFile.getObjectId());
            }
        } else {
            mivColumn.setImageResource(R.mipmap.background_tree);
        }
    }

    private void loadOriginalPic(final String avFileId) {
        AVFile.withObjectIdInBackground(avFileId, new GetFileCallback<AVFile>() {
            @Override
            public void done(AVFile avFile, AVException e) {
                if (UIUtils.hasException(e)) {
                    mivColumn.setImageResource(R.mipmap.background_tree);
                    return;
                }
                avFile.getDataInBackground(new GetDataCallback() {
                    @Override
                    public void done(byte[] bytes, AVException e) {
                        if (UIUtils.hasException(e)) {
                            mivColumn.setImageResource(R.mipmap.background_tree);
                            return;
                        }
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        mivColumn.setImageBitmap(bitmap);
                        UIUtils.savePic2AppointDir(bitmap, StringUtils.getDownloadPicturePath(), StringUtils.avObjectIdTransitionPicForm(avFileId));
                    }
                });
            }
        });
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

    private void showPop() {
        int[] location = new int[2];//保存x和y坐标的数组
        inflate.getLocationInWindow(location);//获取条目x和y的坐标,同时保存到int[]
        //获取x和y的坐标
        int x = location[0];
        int y = location[1];
        View view = UIUtils.inflate(R.layout.popwindow_host_item_edit);
        final TextView tvDelete = (TextView) view.findViewById(R.id.tv_delete);

        final PopupWindow popupWindow = new PopupWindow(view, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setTouchable(true);
        popupWindow.showAtLocation(listView, Gravity.LEFT | Gravity.TOP, UIUtils.getWindowWidth() - UIUtils.dip2px(160), y + UIUtils.dip2px(7));
        tvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                showDialog4Delete();
            }
        });
    }

    private void showDialog4Delete() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        View inflate = UIUtils.inflate(R.layout.dialog_delete_host_item);
        TextView tvOk = (TextView) inflate.findViewById(R.id.tv_ok_dialog_delete_host_item);
        TextView tvNo = (TextView) inflate.findViewById(R.id.tv_no_dialog_delete_host_item);
        tvOk.setBackground(SelectUtils.getSelector(UIUtils.getColorId(R.color.Cream), UIUtils.getColorId(R.color.Cream3), 0));
        tvNo.setBackground(SelectUtils.getSelector(UIUtils.getColorId(R.color.Cream), UIUtils.getColorId(R.color.Cream3), 0));
        final AlertDialog dialog = builder.create();
        dialog.setView(inflate);
        dialog.show();
        tvNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data.picAVFile.deleteEventually();
                //删除好友动态里面的内容， 先删除次要的再删除主要的
                deleteDynamic4Friends();
                //删除评论
                AVQuery<AVObject> query = new AVQuery<>(StringUtils.commentTodo);
                query.whereEqualTo(StringUtils.columnTodo, data.columnTodo);
                query.findInBackground(new FindCallback<AVObject>() {
                    @Override
                    public void done(List<AVObject> list, AVException e) {
                        if (UIUtils.hasException(e)) {
                            return;
                        }
                        AVObject.deleteAllInBackground(list, new DeleteCallback() {
                            @Override
                            public void done(AVException e) {
                                if (UIUtils.hasException(e)) {
                                    return;
                                }
                            }
                        });
                    }
                });
                //删除回复
                AVQuery<AVObject> queryReply = new AVQuery<>(StringUtils.replyTodo);
                queryReply.whereContains(StringUtils.cardTodoId, data.columnTodo.getObjectId());
                queryReply.findInBackground(new FindCallback<AVObject>() {
                    @Override
                    public void done(List<AVObject> list, AVException e) {
                        if (UIUtils.hasException(e)) {
                            return;
                        }
                        AVObject.deleteAllInBackground(list, new DeleteCallback() {
                            @Override
                            public void done(AVException e) {
                                if (UIUtils.hasException(e)) {
                                    return;
                                }
                            }
                        });
                    }
                });
                dialog.dismiss();
            }
        });
    }
    private void deleteDynamic4Friends() {
        AVQuery<AVObject> query = new AVQuery<>(StringUtils.columnDynamic);
        query.whereEqualTo(StringUtils.columnTodo, data.columnTodo);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (UIUtils.hasException(e)) {
                    return;
                }
                AVObject.deleteAllInBackground(list, new DeleteCallback() {
                    @Override
                    public void done(AVException e) {
                        if (UIUtils.hasException(e)) {
                            return;
                        }
                        data.columnTodo.deleteEventually();
                        AVQuery<AVObject> query = new AVQuery<>(StringUtils.todoCollect);
                        query.whereEqualTo(StringUtils.columnTodo, data.columnTodo);
                        query.findInBackground(new FindCallback<AVObject>() {
                            @Override
                            public void done(List<AVObject> list, AVException e) {
                                if (UIUtils.hasException(e)) {
                                    return;
                                }
                                AVObject.deleteAllInBackground(list, new DeleteCallback() {
                                    @Override
                                    public void done(AVException e) {
                                        if (UIUtils.hasException(e)) {
                                            return;
                                        }
                                        myListViewAdapter.getDatas().remove(getPosition());
                                        myListViewAdapter.notifyDataSetChanged();
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });
    }
}
