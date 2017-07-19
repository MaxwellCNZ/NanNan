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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.SaveCallback;
import com.makeramen.roundedimageview.RoundedImageView;
import com.nannan.nannan.R;
import com.nannan.nannan.bean.HomeCardTodoBean;
import com.nannan.nannan.utils.SelectUtils;
import com.nannan.nannan.utils.StringUtils;
import com.nannan.nannan.utils.UIUtils;
import com.nannan.nannan.view.activity.CommentActivity;
import com.nannan.nannan.view.activity.pager.ShowImageActivity;
import com.nannan.nannan.view.activity.user.UserActivity;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.nannan.nannan.utils.StringUtils.avUserPointer;
import static com.nannan.nannan.utils.StringUtils.typeNum;
import static com.nannan.nannan.utils.UIUtils.avUser;
import static com.nannan.nannan.view.holder.HomeHolder.LOADING_FAILED;
import static com.nannan.nannan.view.holder.HomeHolder.LOADING_SUCCEED;
import static com.nannan.nannan.view.holder.HomeHolder.getPic4Dir;

/**
 * Created by MaxwellCNZ on 2017/4/13.
 */

public class FunctionHolder extends BaseHolder<HomeCardTodoBean> {

    private final ListView listView;
    private final boolean isCollection;
    private final Activity activity;
    //    private final MyListViewAdapter lvAdapter;
    private RoundedImageView mrivIcon;
    private TextView mtvName;
    private ImageView mivFailedLoaded;
    private TextView mtvData;
    private ImageButton mibMore;
    private ProgressBar mpbPicLoading;
    private TextView mtvIntroduce;
    private ImageView mivPicture;
    private View inflate;
    private HomeCardTodoBean data;
    private String thumbnailUrl;
    private String avFileId;
    private String iconThumbnailUrl;
    private TextView mtvDescribeInit;
    private TextView mtvDescribeClick;
    private TextView mtvOpen;
    private ImageButton mibComment;
    private LinearLayout mllShare;
    private TextView mtvShareName;
    private ImageView mivType;
    private ImageButton mibUpvote;

    public FunctionHolder(Activity activity, ListView listView, boolean isCollection) {
        this.activity = activity;
        this.listView = listView;
        this.isCollection = isCollection;
    }

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
        mibComment = (ImageButton) inflate.findViewById(R.id.ib_comment_hostItem_home);
        mtvOpen = (TextView) inflate.findViewById(R.id.tv_open);
        mtvOpen.setBackground(SelectUtils.getSelector(UIUtils.getColorId(R.color.Transparency), UIUtils.getColorId(R.color.BackgroundColor2), 0));
        mllShare = (LinearLayout) inflate.findViewById(R.id.ll_share);
        mtvShareName = (TextView) inflate.findViewById(R.id.tv_share_name);

        mivType = (ImageView) inflate.findViewById(R.id.iv_type_hostItem_home);
        mibUpvote = (ImageButton) inflate.findViewById(R.id.ib_upvote_hostItem_home);
//        mtvUpvoteNum = (TextView) inflate.findViewById(R.id.tv_upvote_num_hostItem_home);
        return inflate;
    }

    @Override
    protected void fillView(final HomeCardTodoBean data) {
        this.data = data;
        int typeId = UIUtils.choiceType(data.typeTodo);
        mivType.setImageResource(typeId);
        if (avUser.getObjectId().equals(data.avUser.getObjectId())) {
            mibMore.setVisibility(View.INVISIBLE);
        }else {
            mibMore.setVisibility(View.VISIBLE);
        }
        if (!data.hasChecked) {
            mibUpvote.setImageResource(R.drawable.vector_upvote_normal);
        } else {
            mibUpvote.setImageResource(R.drawable.vector_upvote_pressed);
        }

        mibUpvote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!data.hasChecked) {
                    data.hasChecked = true;
                    mibUpvote.setImageResource(R.drawable.vector_upvote_pressed);

                    //点赞次数保存到服务器
                    data.cardTodo.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(AVException e) {
                            if (UIUtils.hasException(e)) {
                                return;
                            }
                            data.cardTodo.increment(StringUtils.upVote, 1);
                            data.cardTodo.setFetchWhenSave(true);
                            data.cardTodo.saveInBackground();
                        }
                    });

                } else {
                    data.hasChecked = false;
                    mibUpvote.setImageResource(R.drawable.vector_upvote_normal);
                    //点赞次数保存到服务器
                    data.cardTodo.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(AVException e) {
                            if (UIUtils.hasException(e)) {
                                return;
                            }
                            data.cardTodo.increment(StringUtils.upVote, -1);
                            data.cardTodo.setFetchWhenSave(true);
                            data.cardTodo.saveInBackground();
                        }
                    });
                }
            }
        });

        if (data.sharetor != null) {
            if (!data.sharetor.getObjectId().equals(data.avUser.getObjectId())) {
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
                        if (!StringUtils.isEmpty(shareName)){
                            mtvShareName.setText(shareName);
                        }else {
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
        commentLogic();
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

    /**
     * 评论逻辑
     */
    private void commentLogic() {
        mibComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UIUtils.getContext(), CommentActivity.class);
                intent.putExtra(StringUtils.cardTodoId, data.cardTodo.getObjectId());
                intent.putExtra(StringUtils.typeTodo, data.typeTodo);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                UIUtils.getContext().startActivity(intent);
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

    ///////////////////网络获取图片///////////////////////
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
                data.collectionCard.deleteEventually();
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
                avQuery.whereEqualTo(data.typeTodo, data.cardTodo);
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
                reportTodo.put(data.typeTodo, data.cardTodo);
                reportTodo.put(StringUtils.strComment, strContent);
                reportTodo.saveEventually();
                dialog.dismiss();
                UIUtils.toast4Shot("感谢您为喃喃做的贡献");
            }
        });

    }

    /**
     * 分享逻辑
     */
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
                AVObject friendsDynamic = new AVObject(StringUtils.friendsDynamic);
                friendsDynamic.put(data.typeTodo, data.cardTodo);
                friendsDynamic.put(StringUtils.typeNum, transformType2English(data.typeTodo));
                friendsDynamic.put(StringUtils.share_tor, avUser);
                friendsDynamic.put(StringUtils.follower, avUser);
                avObjects.add(friendsDynamic);
                for (AVUser object : list) {
                    friendsDynamic = new AVObject(StringUtils.friendsDynamic);
                    friendsDynamic.put(data.typeTodo, data.cardTodo);
                    friendsDynamic.put(StringUtils.typeNum, transformType2English(data.typeTodo));
                    friendsDynamic.put(StringUtils.share_tor, avUser);
                    friendsDynamic.put(StringUtils.follower, object);
                    avObjects.add(friendsDynamic);
                }
                AVObject.saveAllInBackground(avObjects);
                UIUtils.toast4Shot("分享成功");
            }
        });
    }

    /**
     * 收藏逻辑
     */
    private void collectLogic() {
        AVObject object = new AVObject(StringUtils.todoCollect);
        object.put(data.typeTodo, data.cardTodo);
        object.put(typeNum, transformType2English(data.typeTodo));
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

    private String transformType2English(String strType) {
        String typeNum = null;
        switch (strType) {
            case "artCardTodo":
                typeNum = "1";
                break;
            case "learnCardTodo":
                typeNum = "2";
                break;
            case "leisureCardTodo":
                typeNum = "3";
                break;
            case "mindsCardTodo":
                typeNum = "4";
                break;
            case "sportCardTodo":
                typeNum = "5";
                break;
            case "musicCardTodo":
                typeNum = "6";
                break;
            default:
                break;
        }
        return typeNum;
    }
}
