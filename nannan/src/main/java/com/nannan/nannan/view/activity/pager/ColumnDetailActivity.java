package com.nannan.nannan.view.activity.pager;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.DeleteCallback;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.SaveCallback;
import com.makeramen.roundedimageview.RoundedImageView;
import com.nannan.nannan.R;
import com.nannan.nannan.bean.ColumnTodoBeen;
import com.nannan.nannan.utils.SelectUtils;
import com.nannan.nannan.utils.StatusBarUtils;
import com.nannan.nannan.utils.StringUtils;
import com.nannan.nannan.utils.UIUtils;
import com.nannan.nannan.view.activity.CommentActivity;
import com.nannan.nannan.view.activity.user.UserActivity;
import com.nannan.nannan.view.widget.ParallaxListView;

import java.util.Arrays;
import java.util.List;

import static android.R.layout.simple_list_item_1;
import static com.nannan.nannan.utils.StringUtils.avUserPointer;
import static com.nannan.nannan.utils.StringUtils.columnTodo;
import static com.nannan.nannan.utils.StringUtils.typeNum;
import static com.nannan.nannan.utils.UIUtils.avUser;
import static com.nannan.nannan.view.holder.HomeHolder.getPic4Dir;

/**
 * Created by MaxwellCNZ on 2017/5/13.
 */

public class ColumnDetailActivity extends Activity {

    private static final int TIME_ANIMATION = 300;
    private LinearLayout mllBarOver;
    private RelativeLayout mrlBarDown;
    private ImageView mivColumnPic;
    private ParallaxListView mHZListView;
    private ImageButton mibQuit;
    private ImageButton mibMore;
    private ImageButton mibComment;
    private ImageButton mibCollect;
    private ImageButton mibUpvote;
    private TextView mtvTitleBar;
    private TextView mtvUpvoteNum;
    private ImageView mivHeadView;
    private RelativeLayout mrlUserInfo;
    private RoundedImageView mrivIcon;
    private TextView mtvNickname;
    private TextView mtvIntroduce;
    private TextView mtvData;
    private TextView mtvTitle;
    private EditText metContent;
    private ColumnTodoBeen bridgeColumn;
    private int viewSlop;
    private int barStateHeight = 0;
    private boolean isCollect;
    private int upVoteNum;
    private TextView mtvTitleTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_column);
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            barStateHeight = getResources().getDimensionPixelSize(resourceId);
        }
        UIUtils.printLog(barStateHeight + "---");
        viewSlop = ViewConfiguration.get(UIUtils.getContext()).getScaledTouchSlop();
        StatusBarUtils.setWindowStatusBarColor(this, R.color.MasterColor2);
        bridgeColumn = UIUtils.bridgeColumn.getData();
        initView();
        initData();
    }

    private void initData() {
        mtvTitleBar.setText(bridgeColumn.strTitle);
        mtvNickname.setText(bridgeColumn.nickname);
        mtvIntroduce.setText(bridgeColumn.introduce);
        mtvData.setText(UIUtils.millisecs2DateString(bridgeColumn.millisecsData));
        mtvTitle.setText(bridgeColumn.strTitle);
        mtvTitleTest.setText(bridgeColumn.strTitle);
        metContent.setText(bridgeColumn.strContent);

        //获取点赞数
        bridgeColumn.columnTodo.fetchInBackground(new GetCallback<AVObject>() {
            @Override
            public void done(AVObject avObject, AVException e) {
                if (UIUtils.hasException(e)) {
                    return;
                }
                upVoteNum = avObject.getInt(StringUtils.upVote);
                mtvUpvoteNum.setText(upVoteNum + "");
            }
        });

        Bitmap bitmap = UIUtils.getPic2AppointDir(bridgeColumn.picAVFile.getObjectId());
        if (bitmap != null) {
            //本地存在该原图
            mivColumnPic.setImageBitmap(bitmap);
        } else {
            mivColumnPic.setImageResource(R.mipmap.background_tree);
        }
        if (bridgeColumn.iconAVFile != null) {
            String iconUri = StringUtils.getCachePicturePath() + "/" +
                    StringUtils.avObjectIdTransitionPicForm(bridgeColumn.iconAVFile.getObjectId());
            Bitmap iconBitmap = getPic4Dir(iconUri);
            if (iconBitmap != null) {
                //存在图片
                mrivIcon.setImageBitmap(iconBitmap);
            } else {
                mrivIcon.setImageResource(R.mipmap.deer);
            }
        } else {
            mrivIcon.setImageResource(R.mipmap.deer);
        }

        mibUpvote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!bridgeColumn.hasChecked) {
                    bridgeColumn.hasChecked = true;
                    mibUpvote.setImageResource(R.drawable.vector_upvote_pressed);

                    //点赞次数保存到服务器
                    bridgeColumn.columnTodo.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(AVException e) {
                            if (UIUtils.hasException(e)) {
                                return;
                            }
                            mtvUpvoteNum.setText(++upVoteNum + "");
                            bridgeColumn.columnTodo.increment(StringUtils.upVote, 1);
                            bridgeColumn.columnTodo.setFetchWhenSave(true);
                            bridgeColumn.columnTodo.saveInBackground();
                        }
                    });

                } else {
                    bridgeColumn.hasChecked = false;
                    mibUpvote.setImageResource(R.drawable.vector_upvote_normal);
                    //点赞次数保存到服务器
                    bridgeColumn.columnTodo.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(AVException e) {
                            if (UIUtils.hasException(e)) {
                                return;
                            }
                            mtvUpvoteNum.setText(--upVoteNum + "");
                            bridgeColumn.columnTodo.increment(StringUtils.upVote, -1);
                            bridgeColumn.columnTodo.setFetchWhenSave(true);
                            bridgeColumn.columnTodo.saveInBackground();
                        }
                    });
                }
            }
        });

        mibComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UIUtils.getContext(), CommentActivity.class);
                intent.putExtra(StringUtils.cardTodoId, bridgeColumn.columnTodo.getObjectId());
                intent.putExtra(StringUtils.typeTodo, StringUtils.columnTodo);
                ColumnDetailActivity.this.startActivity(intent);
            }
        });

        //判断是否已经收藏过了
        AVQuery<AVObject> avQuery1 = new AVQuery<>(StringUtils.todoCollect);
        avQuery1.whereEqualTo(avUserPointer, avUser);
        AVQuery<AVObject> avQuery = new AVQuery<>(StringUtils.todoCollect);
        avQuery.whereEqualTo(columnTodo, bridgeColumn.columnTodo);
        AVQuery<AVObject> query = AVQuery.and(Arrays.asList(avQuery1, avQuery));
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (UIUtils.hasException(e)) {
                    UIUtils.toast4Shot("网络异常");
                    mibCollect.setOnClickListener(null);
                    mibCollect.setImageResource(R.drawable.vector_column_normal);
                    return;
                }
                if (list.size() >= 1) {
                    bridgeColumn.todoColection = list.get(0);
                    isCollect = true;
                    mibCollect.setImageResource(R.drawable.vector_column_pressed);
                } else {
                    isCollect = false;
                    mibCollect.setImageResource(R.drawable.vector_column_normal);
                }
            }
        });
        mibCollect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCollect) {
                    //取消收藏
                    isCollect = false;
                    bridgeColumn.todoColection.deleteEventually();
                    mibCollect.setImageResource(R.drawable.vector_column_normal);
                    UIUtils.toast4Shot("取消收藏");
                } else {
                    //添加收藏
                    final AVObject object = new AVObject(StringUtils.todoCollect);
                    object.put(columnTodo, bridgeColumn.columnTodo);
                    object.put(typeNum, "7");
                    object.put(avUserPointer, avUser);
                    object.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(AVException e) {
                            if (UIUtils.hasException(e)) {
                                UIUtils.toast4Shot("收藏失败");
                                return;
                            }
                            isCollect = true;
                            bridgeColumn.todoColection = object;
                            mibCollect.setImageResource(R.drawable.vector_column_pressed);
                            UIUtils.toast4Shot("收藏成功");
                        }
                    });

                }
            }
        });
        mrlUserInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ColumnDetailActivity.this, UserActivity.class);
                intent.putExtra(StringUtils.avUserId, bridgeColumn.avUserPointer.getObjectId());
                ColumnDetailActivity.this.startActivity(intent);
            }
        });
        mtvTitleBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHZListView.smoothScrollToPosition(0);
            }
        });
        mibQuit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mibMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPop();
            }
        });
        metContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isToolHide) {
                    onShow();
                } else {
                    onHide();
                }
            }
        });
        mivHeadView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isToolHide) {
                    onShow();
                } else {
                    onHide();
                }
            }
        });
    }

    private void showPop() {
        View view = UIUtils.inflate(R.layout.popwindow_host_item_edit);
        final TextView tvDelete = (TextView) view.findViewById(R.id.tv_delete);
        final TextView tvReport = (TextView) view.findViewById(R.id.tv_report);
        final PopupWindow popupWindow = new PopupWindow(view, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        if (bridgeColumn.avUserPointer.getObjectId().equals(avUser.getObjectId())) {
            tvDelete.setVisibility(View.VISIBLE);
            tvReport.setVisibility(View.GONE);
            tvDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //删除
                    showDialog4Delete();
                    popupWindow.dismiss();
                }
            });
        } else {
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
        popupWindow.showAtLocation(mllBarOver, Gravity.LEFT | Gravity.TOP, UIUtils.getWindowWidth() - UIUtils.dip2px(160), 5);


    }

    /**
     * 举报对话框
     */
    private void showDialog4Report() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
                reportTodo.put(StringUtils.columnTodo, bridgeColumn.columnTodo);
                reportTodo.put(StringUtils.strComment, strContent);
                reportTodo.saveEventually();
                dialog.dismiss();
                UIUtils.toast4Shot("感谢您为喃喃做的贡献");
            }
        });


    }

    /**
     * 删除对话框
     */
    private void showDialog4Delete() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
                bridgeColumn.picAVFile.deleteEventually();
                //删除好友动态里面的内容， 先删除次要的再删除主要的
                deleteDynamic4Friends();
                //删除评论
                AVQuery<AVObject> query = new AVQuery<>(StringUtils.commentTodo);
                query.whereEqualTo(StringUtils.columnTodo, bridgeColumn.columnTodo);
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
                queryReply.whereContains(StringUtils.cardTodoId, bridgeColumn.columnTodo.getObjectId());
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
        query.whereEqualTo(StringUtils.columnTodo, bridgeColumn.columnTodo);
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
                        bridgeColumn.columnTodo.deleteEventually();
                        AVQuery<AVObject> query = new AVQuery<>(StringUtils.todoCollect);
                        query.whereEqualTo(StringUtils.columnTodo, bridgeColumn.columnTodo);
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
                                        if (mRefreshAdapterListener != null) {
                                            mRefreshAdapterListener.onRefresh();
                                        }
                                        finish();
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });
    }

    private void initView() {
        mllBarOver = (LinearLayout) findViewById(R.id.ll_column_barOver);
        mrlBarDown = (RelativeLayout) findViewById(R.id.rl_column_barDown);
        mivColumnPic = (ImageView) findViewById(R.id.iv_column_pic);
        mHZListView = (ParallaxListView) findViewById(R.id.pl_column_ListView);
        mibQuit = (ImageButton) findViewById(R.id.ib_quit_column);
        mibMore = (ImageButton) findViewById(R.id.ib_more_column);
        mibComment = (ImageButton) findViewById(R.id.ib_comment_column);
        mibCollect = (ImageButton) findViewById(R.id.ib_collect_column);
        mibUpvote = (ImageButton) findViewById(R.id.ib_upvote_column);
        mtvTitleBar = (TextView) findViewById(R.id.tv_title_column);
        mtvUpvoteNum = (TextView) findViewById(R.id.tv_upvote_num_column);

        View mHeadView = UIUtils.inflate(R.layout.item_head_home_column);
        View mSecondView = UIUtils.inflate(R.layout.item_column_second);

        mivHeadView = (ImageView) mHeadView.findViewById(R.id.iv_head_home);

        mrlUserInfo = (RelativeLayout) mSecondView.findViewById(R.id.rl_user);
        mrivIcon = (RoundedImageView) mSecondView.findViewById(R.id.riv_icon_column);
        mtvNickname = (TextView) mSecondView.findViewById(R.id.tv_nickname_column);
        mtvIntroduce = (TextView) mSecondView.findViewById(R.id.tv_introduce_column);
        mtvData = (TextView) mSecondView.findViewById(R.id.tv_data_column);
        mtvTitle = (TextView) mSecondView.findViewById(R.id.tv_title_column);
        mtvTitleTest = (TextView) mSecondView.findViewById(R.id.tv_title_column_test);
        metContent = (EditText) mSecondView.findViewById(R.id.et_column_content);
        metContent.setKeyListener(null);

        mHZListView.addHeaderView(mHeadView);
        mHZListView.addHeaderView(mSecondView);
        mHZListView.setParallaxImageView(mivHeadView, mivColumnPic);
        mHZListView.setAdapter(new ArrayAdapter<String>(UIUtils.getContext(), simple_list_item_1));
        setBarState();

    }

    private void setBarState() {
        mHZListView.setOnTouchListener(new View.OnTouchListener() {
            private float startY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startY = event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float disY = event.getY() - startY;
                        //垂直方向滑动
                        if (Math.abs(disY) > viewSlop) {
                            //是否向上滑动
                            boolean isUpSlide = disY < 0;

                            //实现底部tools的显示与隐藏
                            if (isUpSlide) {
                                onHide();
                            } else {
                                onShow();
                            }
                        }
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
    }

    private void onShow() {
        if (!isToolHide) {
            return;
        }
        int startY = getWindow().getDecorView()
                .getHeight() - barStateHeight;
        ObjectAnimator anim = ObjectAnimator.ofFloat(mrlBarDown, "y",
                startY, startY - mrlBarDown.getHeight());
        ObjectAnimator anim2 = ObjectAnimator.ofFloat(mllBarOver, "y",
                -mllBarOver.getHeight(), 0);

        anim.setDuration(TIME_ANIMATION);
        anim2.setDuration(TIME_ANIMATION);

        anim.start();
        anim2.start();
        isToolHide = false;
    }

    private boolean isToolHide;

    private void onHide() {
        if (isToolHide) {
            return;
        }
        int startY = getWindow().getDecorView()
                .getHeight() - barStateHeight;
        ObjectAnimator anim = ObjectAnimator.ofFloat(mrlBarDown, "y",
                startY - mrlBarDown.getHeight(), startY);
        ObjectAnimator anim2 = ObjectAnimator.ofFloat(mllBarOver, "y",
                0, -mllBarOver.getHeight());

        anim.setDuration(TIME_ANIMATION);
        anim2.setDuration(TIME_ANIMATION);
        anim.start();
        anim2.start();
        isToolHide = true;
    }


    public interface onRefreshAdapter {
        void onRefresh();
    }

    private static onRefreshAdapter mRefreshAdapterListener;

    public static void setOnRefreshAdapter(onRefreshAdapter listener) {
        mRefreshAdapterListener = listener;
    }
}
