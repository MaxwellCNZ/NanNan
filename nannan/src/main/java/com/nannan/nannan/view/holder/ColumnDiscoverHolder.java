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
import android.widget.EditText;
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
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.GetDataCallback;
import com.avos.avoscloud.GetFileCallback;
import com.avos.avoscloud.SaveCallback;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.nannan.nannan.utils.StringUtils.avUserPointer;
import static com.nannan.nannan.utils.StringUtils.columnTodo;
import static com.nannan.nannan.utils.StringUtils.typeNum;
import static com.nannan.nannan.utils.UIUtils.avUser;
import static com.nannan.nannan.view.holder.HomeHolder.LOADING_FAILED;
import static com.nannan.nannan.view.holder.HomeHolder.LOADING_SUCCEED;
import static com.nannan.nannan.view.holder.HomeHolder.getPic4Dir;

/**
 * Created by MaxwellCNZ on 2017/5/10.
 */

public class ColumnDiscoverHolder extends BaseHolder<ColumnTodoBeen> {

    private final ListView listView;
    private final boolean isCollection;
    private final MyListViewAdapter myListViewAdapter;
    private final Activity activity;
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
    private LinearLayout mllShare;
    private TextView mtvShareName;

    public ColumnDiscoverHolder(Activity activity, MyListViewAdapter myListViewAdapter, ListView listView, boolean isCollection) {
        this.activity = activity;
        this.myListViewAdapter = myListViewAdapter;
        this.listView = listView;
        this.isCollection = isCollection;
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
        mllShare = (LinearLayout) inflate.findViewById(R.id.ll_share);
        mtvShareName = (TextView) inflate.findViewById(R.id.tv_share_name);
        return inflate;
    }

    @Override
    protected void fillView(final ColumnTodoBeen data) {
        this.data = data;
        mivColumn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data.wasRead = true;
                ColumnDetailActivity.setOnRefreshAdapter(new ColumnDetailActivity.onRefreshAdapter() {
                    @Override
                    public void onRefresh() {
                        myListViewAdapter.getDatas().remove(getPosition());
                        myListViewAdapter.notifyDataSetChanged();
                    }
                });
                mtvTitle.setTextColor(UIUtils.getColorId(R.color.HostBlackColor3));
                UIUtils.bridgeColumn = new BridgeColumn(data);
                Intent intent = new Intent(UIUtils.getContext(), ColumnDetailActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                UIUtils.getContext().startActivity(intent);
            }
        });
        if (data.wasRead) {
            mtvTitle.setTextColor(UIUtils.getColorId(R.color.HostBlackColor3));
        } else {
            mtvTitle.setTextColor(UIUtils.getColorId(R.color.HostBlackColor));
        }
        mtvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data.wasRead = true;
                ColumnDetailActivity.setOnRefreshAdapter(new ColumnDetailActivity.onRefreshAdapter() {
                    @Override
                    public void onRefresh() {
                        myListViewAdapter.getDatas().remove(getPosition());
                        myListViewAdapter.notifyDataSetChanged();
                    }
                });
                mtvTitle.setTextColor(UIUtils.getColorId(R.color.HostBlackColor3));
                UIUtils.bridgeColumn = new BridgeColumn(data);
                Intent intent = new Intent(UIUtils.getContext(), ColumnDetailActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                UIUtils.getContext().startActivity(intent);
            }
        });

        if (data.sharetor != null) {
            if (!data.sharetor.getObjectId().equals(data.avUserPointer.getObjectId())) {
                mllShare.setVisibility(View.VISIBLE);
                data.sharetor.fetchInBackground(new GetCallback<AVObject>() {
                    @Override
                    public void done(AVObject avObject, AVException e) {
                        if (UIUtils.hasException(e)) {
                            mllShare.setVisibility(View.GONE);
                            UIUtils.toast4Shot("网络出问题了 QAQ ");
                            return;
                        }
                        String shareName = avObject.getString(StringUtils.nickname);
                        if (!StringUtils.isEmpty(shareName)) {
                            mtvShareName.setText(shareName);
                        } else {
                            mtvShareName.setText("喃喃");
                        }

                        mtvShareName.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(UIUtils.getContext(), UserActivity.class);
                                intent.putExtra(StringUtils.avUserId, data.sharetor.getObjectId());
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                UIUtils.getContext().startActivity(intent);
                            }
                        });
                    }
                });
            } else {
                mllShare.setVisibility(View.GONE);
            }
        } else {
            mllShare.setVisibility(View.GONE);
        }

        if (avUser.getObjectId().equals(data.avUserPointer.getObjectId())) {
            mibMore.setVisibility(View.INVISIBLE);
        } else {
            mibMore.setVisibility(View.VISIBLE);
        }

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
        TextView tvCollect = (TextView) view.findViewById(R.id.tv_collect);
        TextView tvCollectCancel = (TextView) view.findViewById(R.id.tv_collect_cancel);
        TextView tvShare = (TextView) view.findViewById(R.id.tv_share);
        TextView tvReport = (TextView) view.findViewById(R.id.tv_report);
        ImageView ivDivide = (ImageView) view.findViewById(R.id.iv_divide);
        tvDelete.setVisibility(View.GONE);

        tvShare.setVisibility(View.VISIBLE);
        tvReport.setVisibility(View.VISIBLE);
        ivDivide.setVisibility(View.VISIBLE);
        if (isCollection) {
            tvCollectCancel.setVisibility(View.VISIBLE);
            tvCollect.setVisibility(View.GONE);
        } else {
            tvCollect.setVisibility(View.VISIBLE);
            tvCollectCancel.setVisibility(View.GONE);
        }

        final PopupWindow popupWindow = new PopupWindow(view, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setTouchable(true);
        popupWindow.showAtLocation(listView, Gravity.LEFT | Gravity.TOP, UIUtils.getWindowWidth() - UIUtils.dip2px(160), y + UIUtils.dip2px(7));
        tvCollectCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data.todoColection.deleteEventually();
                popupWindow.dismiss();
                UIUtils.toast4Shot("取消收藏");
            }
        });

        tvReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showReportDialog();
                popupWindow.dismiss();
            }
        });
        tvCollect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //判断是否已经收藏过了
                AVQuery<AVObject> avQuery1 = new AVQuery<>(StringUtils.todoCollect);
                avQuery1.whereEqualTo(StringUtils.avUserPointer, avUser);
                AVQuery<AVObject> avQuery = new AVQuery<>(StringUtils.todoCollect);
                avQuery.whereEqualTo(columnTodo, data.columnTodo);
                AVQuery<AVObject> query = AVQuery.and(Arrays.asList(avQuery1, avQuery));
                query.findInBackground(new FindCallback<AVObject>() {
                    @Override
                    public void done(List<AVObject> list, AVException e) {
                        if (UIUtils.hasException(e)) {
                            UIUtils.toast4Shot("收藏失败");
                            return;
                        }
                        if (list.size() >= 1) {
                            UIUtils.toast4Shot("你已经收藏过了");
                        } else {
                            collectLogic();
                        }
                    }
                });
                popupWindow.dismiss();
            }
        });
        tvShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareLogic();
                popupWindow.dismiss();
            }
        });
    }

    private void showReportDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        View inflate = UIUtils.inflate(R.layout.dialog_report);
        TextView tvOk = (TextView) inflate.findViewById(R.id.tv_ok_report);
        TextView tvNo = (TextView) inflate.findViewById(R.id.tv_no_report);
        final EditText etContent = (EditText) inflate.findViewById(R.id.et_report);

        tvOk.setBackground(SelectUtils.getSelector(UIUtils.getColorId(R.color.MasterColor2), UIUtils.getColorId(R.color.MasterColor), 0));
        tvNo.setBackground(SelectUtils.getSelector(UIUtils.getColorId(R.color.MasterColor2), UIUtils.getColorId(R.color.MasterColor), 0));
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
                String strContent = etContent.getText().toString().trim();
                if (StringUtils.isEmpty(strContent)){
                    UIUtils.toast4Shot("举报内容不能为空");
                    return;
                }
                AVObject reportTodo = new AVObject(StringUtils.reportTodo);
                reportTodo.put(StringUtils.columnTodo, data.columnTodo);
                reportTodo.put(StringUtils.strComment, strContent);
                reportTodo.saveEventually();
                dialog.dismiss();
                UIUtils.toast4Shot("感谢您为喃喃做的贡献");
            }
        });

    }

    private void shareLogic() {
        AVQuery<AVUser> followerQuery = AVUser.followerQuery(avUser.getObjectId(), AVUser.class);
        followerQuery.findInBackground(new FindCallback<AVUser>() {
            @Override
            public void done(List<AVUser> list, AVException e) {
                if (UIUtils.hasException(e)) {
                    UIUtils.toast4Shot("分享失败");
                    return;
                }
                ArrayList<AVObject> avObjects = new ArrayList<>();
                //也要把自己放进去，在好友动态中也要显示自己的内容
                AVObject columnDynamic = new AVObject(StringUtils.columnDynamic);
                columnDynamic.put(StringUtils.columnTodo, data.columnTodo);
                columnDynamic.put(StringUtils.follower, UIUtils.avUser);
                columnDynamic.put(StringUtils.share_tor, UIUtils.avUser);
                avObjects.add(columnDynamic);
                for (AVUser object : list) {
                    columnDynamic = new AVObject(StringUtils.columnDynamic);
                    columnDynamic.put(StringUtils.columnTodo, data.columnTodo);
                    columnDynamic.put(StringUtils.share_tor, UIUtils.avUser);
                    columnDynamic.put(StringUtils.follower, object);
                    avObjects.add(columnDynamic);
                }
                AVObject.saveAllInBackground(avObjects);
                UIUtils.toast4Shot("分享成功");
            }
        });
    }


    private void collectLogic() {
        AVObject object = new AVObject(StringUtils.todoCollect);
        object.put(columnTodo, data.columnTodo);
        object.put(typeNum, "7");
        object.put(avUserPointer, avUser);
        object.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                if (UIUtils.hasException(e)) {
                    UIUtils.toast4Shot("收藏失败");
                    return;
                }
                UIUtils.toast4Shot("收藏成功");
            }
        });
    }
}
