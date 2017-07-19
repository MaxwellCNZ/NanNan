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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.DeleteCallback;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;
import com.makeramen.roundedimageview.RoundedImageView;
import com.nannan.nannan.R;
import com.nannan.nannan.bean.CommentTodoBean;
import com.nannan.nannan.utils.SelectUtils;
import com.nannan.nannan.utils.StringUtils;
import com.nannan.nannan.utils.UIUtils;
import com.nannan.nannan.view.activity.ReplyActivity;
import com.nannan.nannan.view.activity.user.UserActivity;
import com.nannan.nannan.view.adapter.MyListViewAdapter;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import static com.nannan.nannan.view.holder.HomeHolder.LOADING_FAILED;
import static com.nannan.nannan.view.holder.HomeHolder.LOADING_SUCCEED;
import static com.nannan.nannan.view.holder.HomeHolder.getPic4Dir;

/**
 * Created by MaxwellCNZ on 2017/5/5.
 */

public class CommentHolder extends BaseHolder <CommentTodoBean>{

    private final boolean isReply;
    private final ListView listView;
    private final Activity activity;
    private final MyListViewAdapter myListViewAdapter;
    private RoundedImageView mrivIcon;
    private TextView mtvName;
    private TextView mtvData;
    private TextView mtvComment;
    private String iconThumbnailUrl;
    private String avFileId;
    private ImageButton mibUpvote;
    private TextView mtvUpvoteNum;
    private ImageButton mibReply;
    private ImageButton mibReport;
    private View inflate;
    private CommentTodoBean data;

    public CommentHolder(MyListViewAdapter myListViewAdapter, Activity activity, ListView listView, boolean b) {
        this.listView = listView;
        this.myListViewAdapter = myListViewAdapter;
        this.activity = activity;
        this.isReply = b;
    }

    @Override
    public View initView() {
        inflate = UIUtils.inflate(R.layout.item_comment);
        mrivIcon = (RoundedImageView) inflate.findViewById(R.id.riv_icon_comment);
        mtvName = (TextView) inflate.findViewById(R.id.tv_name_comment);
        mtvData = (TextView) inflate.findViewById(R.id.tv_data_comment);
        mtvComment = (TextView) inflate.findViewById(R.id.tv_comment_content);
        mibUpvote = (ImageButton) inflate.findViewById(R.id.ib_upvote_comment);
        mibReport = (ImageButton) inflate.findViewById(R.id.ib_report_comment);
        mtvUpvoteNum = (TextView) inflate.findViewById(R.id.tv_upvote_num_comment);
        mibReply = (ImageButton) inflate.findViewById(R.id.ib_comment_reply);
        return inflate;
    }

    @Override
    protected void fillView(final CommentTodoBean data) {
        this.data = data;
        mibReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPop();
            }
        });
        if (isReply){
            mibReply.setVisibility(View.GONE);
        }else {
            mibReply.setVisibility(View.VISIBLE);
            mibReply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(UIUtils.getContext(), ReplyActivity.class);
                    intent.putExtra(StringUtils.commentTodoId, data.commentTodo.getObjectId());
                    intent.putExtra(StringUtils.cardTodoId, data.cardTodoId);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    UIUtils.getContext().startActivity(intent);
                }
            });
        }
        mrivIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UIUtils.getContext(), UserActivity.class);
                intent.putExtra(StringUtils.avUserId, data.avUser.getObjectId());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                UIUtils.getContext().startActivity(intent);
            }
        });
        mtvName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UIUtils.getContext(), UserActivity.class);
                intent.putExtra(StringUtils.avUserId, data.avUser.getObjectId());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                UIUtils.getContext().startActivity(intent);
            }
        });
        //点赞逻辑
        mtvUpvoteNum.setText(data.upvoteNum + "");
        mibUpvote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (data.hasUpvote){
                    data.hasUpvote = false;
                    mibUpvote.setImageResource(R.drawable.vector_upvote_normal);
                    //点赞次数保存到服务器
                    data.commentTodo.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(AVException e) {
                            if (UIUtils.hasException(e)) {
                                return;
                            }
                            data.commentTodo.increment(StringUtils.upVote, -1);
                            data.commentTodo.setFetchWhenSave(true);
                            data.commentTodo.saveInBackground();
                            data.upvoteNum += -1;
                            mtvUpvoteNum.setText(data.upvoteNum + "");

                        }
                    });
                }else {
                    data.hasUpvote = true;
                    mibUpvote.setImageResource(R.drawable.vector_upvote_pressed);
                    //点赞次数保存到服务器
                    data.commentTodo.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(AVException e) {
                            if (UIUtils.hasException(e)) {
                                return;
                            }
                            data.commentTodo.increment(StringUtils.upVote, 1);
                            data.commentTodo.setFetchWhenSave(true);
                            data.commentTodo.saveInBackground();
                            data.upvoteNum += 1;
                            mtvUpvoteNum.setText(data.upvoteNum + "");
                        }
                    });
                }
            }
        });
        mtvComment.setText(data.strComment);
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

        mtvData.setText(UIUtils.millisecs2DateString(data.millisecsData));

        if (StringUtils.isEmpty(data.nickName)) {
            mtvName.setText("喃喃");
        } else {
            mtvName.setText(data.nickName);
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
        final TextView tvReport = (TextView) view.findViewById(R.id.tv_report);
        final PopupWindow popupWindow = new PopupWindow(view, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        if (data.avUser.getObjectId().equals(UIUtils.avUser.getObjectId())){
            tvDelete.setVisibility(View.VISIBLE);
            tvReport.setVisibility(View.GONE);
            tvDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //删除
                    if (isReply){
                        //直接删除
                        data.commentTodo.deleteEventually();
                        myListViewAdapter.getDatas().remove(getPosition());
                        myListViewAdapter.notifyDataSetChanged();
                    }else {
                        //删除评论
                        data.commentTodo.deleteEventually();
                        //删除回复
                        AVQuery<AVObject> queryReply = new AVQuery<>(StringUtils.replyTodo);
                        queryReply.whereContains(StringUtils.cardTodoId, data.cardTodoId);
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
                        myListViewAdapter.getDatas().remove(getPosition());
                        myListViewAdapter.notifyDataSetChanged();
                    }
                    popupWindow.dismiss();
                }
            });
        }else {
            tvDelete.setVisibility(View.GONE);
            tvReport.setVisibility(View.VISIBLE);
            tvReport.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //举报
                    showDialog4Report();
                    popupWindow.dismiss();
                }
            });
        }

        popupWindow.setOutsideTouchable(true);
        popupWindow.setTouchable(true);
        popupWindow.showAtLocation(listView, Gravity.LEFT | Gravity.TOP, UIUtils.getWindowWidth() - UIUtils.dip2px(160), y + UIUtils.dip2px(7));
    }

    private void showDialog4Report() {
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
                if (StringUtils.isEmpty(strContent)) {
                    UIUtils.toast4Shot("举报内容不能为空");
                    return;
                }
                AVObject reportTodo = new AVObject(StringUtils.reportTodo);
                if (isReply){
                    reportTodo.put(StringUtils.replyTodo, data.commentTodo);
                }else {
                    reportTodo.put(StringUtils.commentTodo, data.commentTodo);
                }
                reportTodo.put(StringUtils.strComment, strContent);
                reportTodo.saveEventually();
                dialog.dismiss();
                UIUtils.toast4Shot("感谢您为喃喃做的贡献");
            }
        });
    }

    ///////////////////网络获取icon///////////////////////
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
