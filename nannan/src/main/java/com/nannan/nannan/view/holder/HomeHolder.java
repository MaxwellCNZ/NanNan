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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.DeleteCallback;
import com.avos.avoscloud.FindCallback;
import com.makeramen.roundedimageview.RoundedImageView;
import com.nannan.nannan.R;
import com.nannan.nannan.bean.HomeCardTodoBean;
import com.nannan.nannan.utils.SelectUtils;
import com.nannan.nannan.utils.StringUtils;
import com.nannan.nannan.utils.UIUtils;
import com.nannan.nannan.view.activity.CommentActivity;
import com.nannan.nannan.view.activity.pager.ShowImageActivity;
import com.nannan.nannan.view.activity.user.UserActivity;
import com.nannan.nannan.view.adapter.MyListViewAdapter;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import static com.nannan.nannan.utils.StringUtils.typeTodo;

/**
 * Created by MaxwellCNZ on 2017/4/4.
 */

public class HomeHolder extends BaseHolder<HomeCardTodoBean> {

    private final MyListViewAdapter lvAdapter;
    private final ListView listView;
    private final Activity activity;
    private RoundedImageView mrivIcon;
    private TextView mtvName;
    private TextView mtvData;
    private ImageButton mibMore;
    private TextView mtvIntroduce;
    private ImageView mivPicture;
    private TextView mtvDescribeInit;
    private ImageButton mibComment;
    private LinearLayout.LayoutParams mParams;

    public static final int LOADING_SUCCEED = 0;
    public static final int LOADING_FAILED = 1;
    private ProgressBar mpbPicLoading;
    private String thumbnailUrl;
    private ImageView mivFailedLoaded;
    private String avFileId;
    //    private TextView mtvMore;
    private View inflate;
    private HomeCardTodoBean data;
    private TextView mtvDescribeClick;
    private TextView mtvOpen;
    private ImageView mivType;
    private ImageButton mibUpvote;
    private TextView mtvUpvoteNum;

    public HomeHolder(MyListViewAdapter myListViewAdapter, ListView mHZListView, Activity activity) {
        this.lvAdapter = myListViewAdapter;
        this.listView = mHZListView;
        this.activity = activity;
    }
//    private RelativeLayout mrlHome;


    @Override
    public View initView() {
        inflate = UIUtils.inflate(R.layout.item_host_home);
        mrivIcon = (RoundedImageView) inflate.findViewById(R.id.riv_icon_hostItem_home);
        mtvName = (TextView) inflate.findViewById(R.id.tv_name_hostItem_home);
        mivFailedLoaded = (ImageView) inflate.findViewById(R.id.iv_loading_failed_item);
        mtvData = (TextView) inflate.findViewById(R.id.tv_data_hostItem_home);
        mibMore = (ImageButton) inflate.findViewById(R.id.ib_more_hostItem_home);
        mpbPicLoading = (ProgressBar) inflate.findViewById(R.id.pb_pic_loading);
        mtvIntroduce = (TextView) inflate.findViewById(R.id.tv_introduce_hostItem_home);
        mivPicture = (ImageView) inflate.findViewById(R.id.iv_picture_hostItem_home);
        mtvDescribeInit = (TextView) inflate.findViewById(R.id.tv_describe_hostItem_init);
        mtvDescribeClick = (TextView) inflate.findViewById(R.id.tv_describe_hostItem_click);
        mtvOpen = (TextView) inflate.findViewById(R.id.tv_open);
        mibComment = (ImageButton) inflate.findViewById(R.id.ib_comment_hostItem_home);
        mtvOpen.setBackground(SelectUtils.getSelector(UIUtils.getColorId(R.color.Transparency), UIUtils.getColorId(R.color.BackgroundColor2), 0));

        mivType = (ImageView) inflate.findViewById(R.id.iv_type_hostItem_home);
        mibUpvote = (ImageButton) inflate.findViewById(R.id.ib_upvote_hostItem_home);
        mtvUpvoteNum = (TextView) inflate.findViewById(R.id.tv_upvote_num_hostItem_home);
        mtvUpvoteNum.setVisibility(View.VISIBLE);
        return inflate;
    }

    /**
     * 评论逻辑
     */
    private void commentLogic() {
        mibComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UIUtils.getContext(), CommentActivity.class);
                intent.putExtra(StringUtils.cardTodoId, data.cardTodo.getObjectId());
                intent.putExtra(typeTodo, data.typeTodo);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                UIUtils.getContext().startActivity(intent);
            }
        });
    }

    @Override
    protected void fillView(final HomeCardTodoBean data) {
        this.data = data;
        mrivIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UIUtils.getContext(), UserActivity.class);
                intent.putExtra(StringUtils.avUserId, UIUtils.avUser.getObjectId());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                UIUtils.getContext().startActivity(intent);
            }
        });
        mtvName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UIUtils.getContext(), UserActivity.class);
                intent.putExtra(StringUtils.avUserId, UIUtils.avUser.getObjectId());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                UIUtils.getContext().startActivity(intent);
            }
        });

        int typeId = UIUtils.choiceType(data.typeTodo);
        mivType.setImageResource(typeId);
        mibUpvote.setImageResource(R.drawable.vector_upvote_pressed);
        mtvUpvoteNum.setText(data.upVoteNum + "");
        commentLogic();
        mibMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPop();
            }
        });
        mpbPicLoading.setVisibility(View.VISIBLE);
        if (StringUtils.isEmpty(data.nickName)) {
            mtvName.setText("喃喃");
        } else {
            mtvName.setText(data.nickName);
        }
        mtvData.setText(UIUtils.millisecs2DateString(data.millisecsData));
        if (StringUtils.isEmpty(data.introduce)) {
            mtvIntroduce.setVisibility(View.GONE);
        } else {
            mtvIntroduce.setVisibility(View.VISIBLE);
            mtvIntroduce.setText(data.introduce);
        }

        if (!StringUtils.isEmpty(data.iconUri)) {
            Bitmap iconBitmap = getPic4Dir(data.iconUri);
//            UIUtils.print(data.iconUri);
            if (iconBitmap != null) {
                //存在图片
                mrivIcon.setImageBitmap(iconBitmap);
            } else {
                mrivIcon.setImageResource(R.mipmap.deer);
            }
        } else {
            mrivIcon.setImageResource(R.mipmap.deer);
        }

        if (data.picAVFile != null) {
            mivPicture.setVisibility(View.VISIBLE);
            final String picUri = StringUtils.getCachePicturePath() + "/" +
                    StringUtils.avObjectIdTransitionPicForm(data.picAVFile.getObjectId());
            Bitmap picBitmap = getPic4Dir(picUri);
            thumbnailUrl = data.picAVFile.getThumbnailUrl(true, 380, 380);
            if (picBitmap != null) {
                mpbPicLoading.setVisibility(View.GONE);
                mivPicture.setImageBitmap(picBitmap);
            } else {
                //获取缩略图
                avFileId = data.picAVFile.getObjectId();
                getNetPic(thumbnailUrl, avFileId);
            }
            mivPicture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(UIUtils.getContext(), ShowImageActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("picUri", picUri);
                    intent.putExtra("thumbnailUrl", thumbnailUrl);
                    intent.putExtra("avFileId", data.picAVFile.getObjectId());
                    UIUtils.getContext().startActivity(intent);
                }
            });
        } else {
            mpbPicLoading.setVisibility(View.GONE);
            mivPicture.setVisibility(View.GONE);
        }

        if (!StringUtils.isEmpty(data.strContent)) {
            mtvDescribeInit.setText(data.strContent);
            mtvDescribeClick.setText(data.strContent);

            mtvOpen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (data.isOpen) {
                        //关闭
                        data.isOpen = false;
                        mtvOpen.setText("展开全文");
                        mtvDescribeInit.setVisibility(View.VISIBLE);
                        mtvDescribeClick.setVisibility(View.GONE);
                    } else {
                        //打开
                        data.isOpen = true;
                        mtvOpen.setText("收起全文");
                        mtvDescribeInit.setVisibility(View.GONE);
                        mtvDescribeClick.setVisibility(View.VISIBLE);
                    }
                }
            });
            if (!data.isOpen) {
                mtvDescribeClick.setVisibility(View.GONE);
                mtvDescribeInit.setVisibility(View.VISIBLE);
                if (!data.isInit) {
                    data.isInit = true;
                    mtvDescribeInit.post(new Runnable() {
                        @Override
                        public void run() {
                            int lineCount = mtvDescribeInit.getLineCount();
                            if (lineCount > 3) {
                                data.shouldFold = true;
                                mtvOpen.setVisibility(View.VISIBLE);
                            } else {
                                data.shouldFold = false;
                                mtvOpen.setVisibility(View.GONE);
                            }
                        }

                    });
                } else {
                    if (data.shouldFold) {
                        mtvOpen.setVisibility(View.VISIBLE);
                    } else {
                        mtvOpen.setVisibility(View.GONE);
                    }
                }
            } else {
                if (data.shouldFold) {
                    mtvOpen.setVisibility(View.VISIBLE);
                } else {
                    mtvOpen.setVisibility(View.GONE);
                }
                mtvDescribeClick.setVisibility(View.VISIBLE);
                mtvDescribeInit.setVisibility(View.GONE);
            }
        } else {
            mtvOpen.setVisibility(View.GONE);
            mtvDescribeInit.setVisibility(View.GONE);
            mtvDescribeClick.setVisibility(View.GONE);
        }

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

    /**
     * 弹出删除对话框
     */
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
        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (data.picAVFile != null) {
                    data.picAVFile.deleteEventually();
                }
                //删除好友动态里面的内容， 先删除次要的再删除主要的
                deleteDynamic4Friends();
                //删除评论
                AVQuery<AVObject> query = new AVQuery<>(StringUtils.commentTodo);
                query.whereEqualTo(data.typeTodo, data.cardTodo);
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
                queryReply.whereContains(StringUtils.cardTodoId, data.cardTodo.getObjectId());
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
        tvNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }

    /**
     * 好友动态也要删除
     * 收藏夹也要删
     */
    private void deleteDynamic4Friends() {
        AVQuery<AVObject> query = new AVQuery<>(StringUtils.friendsDynamic);
        query.whereEqualTo(data.typeTodo, data.cardTodo);
//        Log.i("我的", data.typeTodo +  "typeTodo" + "---" + data.cardTodo.getObjectId() + "getObjectId");
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
                        AVObject cardTodo = AVObject.createWithoutData(data.typeTodo, data.cardTodo.getObjectId());
                        AVObject cardMiddle = AVObject.createWithoutData(StringUtils.cardMiddleTable, data.cardMiddle.getObjectId());
                        cardTodo.deleteEventually();
                        cardMiddle.deleteEventually();
                        AVQuery<AVObject> query = new AVQuery<>(StringUtils.todoCollect);
                        query.whereEqualTo(data.typeTodo, data.cardTodo);
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
                                        lvAdapter.getDatas().remove(getPosition());
                                        lvAdapter.notifyDataSetChanged();
                                        if (mListener != null) {
                                            mListener.onRecord();
                                        }
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == LOADING_SUCCEED) {
                Bitmap bitmap = (Bitmap) msg.obj;
                mpbPicLoading.setVisibility(View.GONE);
                mivPicture.setImageBitmap(bitmap);
            } else if (msg.what == LOADING_FAILED) {
                mpbPicLoading.setVisibility(View.GONE);
                mivFailedLoaded.setVisibility(View.VISIBLE);
                mivFailedLoaded.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mpbPicLoading.setVisibility(View.VISIBLE);
                        mivFailedLoaded.setVisibility(View.GONE);
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
                        msg.what = LOADING_SUCCEED;
                        msg.obj = bitmap;
                    } else {
                        msg.what = LOADING_FAILED;
                    }
                    handler.sendMessage(msg);
                } catch (Exception e) {
                    mpbPicLoading.setVisibility(View.GONE);
                    mivFailedLoaded.setVisibility(View.VISIBLE);
                    UIUtils.toast4Long("图片加载异常！");
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * 从本地获取图片
     *
     * @param picUri
     * @return
     */
    public static Bitmap getPic4Dir(String picUri) {
//        UIUtils.print(picUri + "-----");
        File file = new File(picUri);
        //存在文件，从本地获取
        if (file.exists()) {
            return BitmapFactory.decodeFile(picUri, null);
        }
        return null;
    }

    /**
     * 设置一个监听 用来调用云端的服务器下载图片
     */
    public interface onRecordDeleteNumListener {
        void onRecord();
    }

    private static onRecordDeleteNumListener mListener;

    public HomeHolder setOnRecordDeleteNumListener(onRecordDeleteNumListener listener) {
        mListener = listener;
        return this;
    }

}
