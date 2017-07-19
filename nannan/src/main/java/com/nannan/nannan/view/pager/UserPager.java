package com.nannan.nannan.view.pager;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.SystemClock;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.FollowCallback;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.GetDataCallback;
import com.makeramen.roundedimageview.RoundedImageView;
import com.nannan.nannan.R;
import com.nannan.nannan.bean.HomeCardTodoBean;
import com.nannan.nannan.utils.SharedPreferenceUtils;
import com.nannan.nannan.utils.StringUtils;
import com.nannan.nannan.utils.UIUtils;
import com.nannan.nannan.view.activity.pager.ShowImageActivity;
import com.nannan.nannan.view.activity.user.LetterActivity;
import com.nannan.nannan.view.activity.user.UserActivity;
import com.nannan.nannan.view.activity.user.UserFollowActivity;
import com.nannan.nannan.view.activity.user.UserFunctionActivity;
import com.nannan.nannan.view.adapter.MyListViewAdapter;
import com.nannan.nannan.view.holder.BaseHolder;
import com.nannan.nannan.view.holder.FunctionHolder;
import com.nannan.nannan.view.holder.MoreHolder;
import com.nannan.nannan.view.widget.MySRLayout;
import com.nannan.nannan.view.widget.ParallaxListView;
import com.nannan.nannan.view.widget.WaterWaveImageButton;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.R.layout.simple_list_item_1;
import static com.nannan.nannan.utils.StringUtils.avUserId;
import static com.nannan.nannan.utils.StringUtils.nickname;
import static com.nannan.nannan.view.pager.HomePager.getLongHeight;
import static com.nannan.nannan.view.pager.HomePager.getShortHeight;
import static com.nannan.nannan.view.pager.HomePager.typeNum2cardTodoType;

/**
 * Created by MaxwellCNZ on 2017/5/6.
 */

public class UserPager extends BasePager implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    private final AVObject avUser;
    private final UserActivity mActivity;
    private ImageView mivBackground;
    private ParallaxListView mHZListView;
    private MySRLayout msrlPull;
    private ImageView mivHeadView;
    private ImageView mivGender;
    private RoundedImageView mrivIcon;
    private TextView mtvNickname;
    private TextView mtvIntroduce;
    private Button mbtnFollow;
    private LinearLayout mllFollowee;
    private LinearLayout mllFollower;
    private WaterWaveImageButton mwwivLetter;
    private TextView mtvFolloweeNum;
    private TextView mtvFollowerNum;
    private WaterWaveImageButton mwwivArt;
    private WaterWaveImageButton mwwivLearn;
    private WaterWaveImageButton mwwivLeisure;
    private WaterWaveImageButton mwwivMind;
    private WaterWaveImageButton mwwivSport;
    private WaterWaveImageButton mwwivMusic;
    private WaterWaveImageButton mwwivColumn;
    private LinearLayout.LayoutParams mParams;
    private AVFile avFileIcon;
    private AVFile avFileHead;
    private boolean hasAttention;
    private String introduce;
    private String filePathHead;
    private String filePathIcon;
    private MyListViewAdapter<HomeCardTodoBean> myListViewAdapter;
    private ArrayList<HomeCardTodoBean> moreData;
    private final SharedPreferenceUtils sp;

    public UserPager(AVObject avUser, UserActivity userActivity) {
        this.avUser = avUser;
        this.mActivity = userActivity;
        sp = new SharedPreferenceUtils();
        //初始化界面设置用户数据
        setUserInfo();
        initPhoto();
        setFollowState();
        setUpvoteNum();
        mwwivLetter.setOnClickListener(this);
        mwwivArt.setOnClickListener(this);
        mwwivLearn.setOnClickListener(this);
        mwwivLeisure.setOnClickListener(this);
        mwwivMind.setOnClickListener(this);
        mwwivSport.setOnClickListener(this);
        mwwivMusic.setOnClickListener(this);
        mwwivColumn.setOnClickListener(this);
    }

    @Override
    public View initView() {
        View inflate = UIUtils.inflate(R.layout.pager_home);
        mivBackground = (ImageView) inflate.findViewById(R.id.iv_head_home_background);
        mHZListView = (ParallaxListView) inflate.findViewById(R.id.pl_ListView);
        msrlPull = (MySRLayout) inflate.findViewById(R.id.srl_pull_refresh_home);
        msrlPull.setOnRefreshListener(this);
        msrlPull.setColorSchemeResources(R.color.Pink, R.color.Purple, R.color.Brown,
                R.color.Orange, R.color.Green, R.color.Cyan, R.color.WaterBlue);
        msrlPull.setDistanceToTriggerSync(400);// 设置手指在屏幕下拉多少距离会触发下拉刷新
        msrlPull.setProgressBackgroundColorSchemeResource(R.color.BackgroundColor2);
        msrlPull.setSize(SwipeRefreshLayout.LARGE);

        View mHeadView = UIUtils.inflate(R.layout.item_head_home);
        mivHeadView = (ImageView) mHeadView.findViewById(R.id.iv_head_home);

        View mSecondView = UIUtils.inflate(R.layout.item_user_secend);
        mivGender = (ImageView) mSecondView.findViewById(R.id.iv_gender_user);
        mrivIcon = (RoundedImageView) mSecondView.findViewById(R.id.riv_icon_user);
        mtvNickname = (TextView) mSecondView.findViewById(R.id.tv_nickname_user);
        mtvIntroduce = (TextView) mSecondView.findViewById(R.id.tv_introduce_user);

        View mThirdView = UIUtils.inflate(R.layout.item_user_third);
        mbtnFollow = (Button) mThirdView.findViewById(R.id.btn_follow);
        mllFollowee = (LinearLayout) mThirdView.findViewById(R.id.ll_followee);
        mllFollower = (LinearLayout) mThirdView.findViewById(R.id.ll_follower);
        mwwivLetter = (WaterWaveImageButton) mThirdView.findViewById(R.id.wwiv_letter);
        mtvFolloweeNum = (TextView) mThirdView.findViewById(R.id.tv_followeeNum);
        mtvFollowerNum = (TextView) mThirdView.findViewById(R.id.tv_followerNum);

        View mFourthView = UIUtils.inflate(R.layout.item_user_forth);
        mwwivArt = (WaterWaveImageButton) mFourthView.findViewById(R.id.wwiv_art);
        mwwivLearn = (WaterWaveImageButton) mFourthView.findViewById(R.id.wwiv_learn);
        mwwivLeisure = (WaterWaveImageButton) mFourthView.findViewById(R.id.wwiv_leisure);
        mwwivMind = (WaterWaveImageButton) mFourthView.findViewById(R.id.wwiv_mind);
        mwwivSport = (WaterWaveImageButton) mFourthView.findViewById(R.id.wwiv_sport);
        mwwivMusic = (WaterWaveImageButton) mFourthView.findViewById(R.id.wwiv_music);
        mwwivColumn = (WaterWaveImageButton) mFourthView.findViewById(R.id.wwiv_column_home);

        View mFifthView = UIUtils.inflate(R.layout.item_fourth_home);

        mHZListView.addHeaderView(mHeadView);
        mHZListView.addHeaderView(mSecondView);
        mHZListView.addHeaderView(mThirdView);
        mHZListView.addHeaderView(mFourthView);
        mHZListView.addHeaderView(mFifthView);

        mHZListView.setParallaxImageView(mivHeadView, mivBackground);
        mHZListView.setAdapter(new ArrayAdapter<String>(UIUtils.getContext(), simple_list_item_1));

        /**
         * 给listView添加滚动事件监听
         */
        mHZListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            //是否是最后一个item
            boolean atBottom = false;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

                switch (scrollState) {
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:

                        if (atBottom) {
                            if (moreHolder.getData() == MoreHolder.STATE_MORE_ERROR) {
                                moreHolder.setData(MoreHolder.STATE_MORE_MORE, 0);
                                loadMore(moreHolder);
                            }
                        }

                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
//                        Log.i("info", "摁住滚的状态..");
                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
//                        Log.i("info", "飞的状态..");
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (totalItemCount != 0 && firstVisibleItem + visibleItemCount == totalItemCount) {
//                    Log.i("info", "到底了...."+totalItemCount);
                    //说明到底了
                    atBottom = true;
                } else {
                    //没有到底
                    atBottom = false;
                }
            }
        });
        return inflate;
    }

    private void setUpvoteNum() {
        //关注者与被关注着的数量
        AVQuery<AVUser> followeeQuery = AVUser.followeeQuery(avUser.getObjectId(), AVUser.class);
        followeeQuery.findInBackground(new FindCallback<AVUser>() {
            @Override
            public void done(List<AVUser> list, AVException e) {
                if (UIUtils.hasException(e)) {
                    return;
                }
                mtvFolloweeNum.setText(list.size() + "");
            }
        });
        AVQuery<AVUser> followerQuery = AVUser.followerQuery(avUser.getObjectId(), AVUser.class);
        followerQuery.findInBackground(new FindCallback<AVUser>() {
            @Override
            public void done(List<AVUser> list, AVException e) {
                if (UIUtils.hasException(e)) {
                    return;
                }
                mtvFollowerNum.setText(list.size() + "");
            }
        });


        mllFollowee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, UserFollowActivity.class);
                intent.putExtra(avUserId, avUser.getObjectId());
                intent.putExtra(StringUtils.follow, StringUtils.followeeType);
                mActivity.startActivity(intent);
            }
        });
        mllFollower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, UserFollowActivity.class);
                intent.putExtra(avUserId, avUser.getObjectId());
                intent.putExtra(StringUtils.follow, StringUtils.followerType);
                mActivity.startActivity(intent);
            }
        });
    }

    /**
     * 设置关注状态
     */
    private void setFollowState() {
        if (avUser.getObjectId().equals(UIUtils.avUser.getObjectId())) {
            mbtnFollow.setBackgroundResource(R.color.BackgroundColor2);
            mbtnFollow.setText("我自己");
            mbtnFollow.setTextColor(UIUtils.getColorId(R.color.HostBlackColor3));
        } else {
            mwwivLetter.setOnClickListener(this);
            AVQuery<AVUser> followeeQuery = UIUtils.avUser.followeeQuery(UIUtils.avUser.getObjectId(), AVUser.class);
            followeeQuery.whereEqualTo(StringUtils.followee, avUser);
            followeeQuery.findInBackground(new FindCallback<AVUser>() {
                @Override
                public void done(List<AVUser> avObjects, AVException e) {
                    // avObjects 中应当只包含 userC
                    if (UIUtils.hasException(e)) {
                        return;
                    }
                    if (avObjects.size() == 1) {
                        hasAttention = true;
                        mbtnFollow.setBackgroundResource(R.color.BackgroundColor2);
                        mbtnFollow.setText("已关注");
                        mbtnFollow.setTextColor(UIUtils.getColorId(R.color.HostBlackColor3));
                    } else {
                        hasAttention = false;
                        mbtnFollow.setBackgroundResource(R.color.BackgroundColor3);
                        mbtnFollow.setText("未关注");
                        mbtnFollow.setTextColor(UIUtils.getColorId(R.color.HomeCardColor));
                    }
                    mbtnFollow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (hasAttention) {
                                //取消关注
                                UIUtils.avUser.unfollowInBackground(avUser.getObjectId(), new FollowCallback() {
                                    @Override
                                    public void done(AVObject object, AVException e) {
                                        if (UIUtils.hasException(e)) {
                                            return;
                                        }
                                        hasAttention = false;
                                        mbtnFollow.setBackgroundResource(R.color.BackgroundColor3);
                                        mbtnFollow.setText("未关注");
                                        mbtnFollow.setTextColor(UIUtils.getColorId(R.color.HomeCardColor));
                                    }
                                });
                            } else {
                                //关注
                                UIUtils.avUser.followInBackground(avUser.getObjectId(), new FollowCallback() {
                                    @Override
                                    public void done(AVObject object, AVException e) {
                                        if (UIUtils.hasException(e)) {
                                            return;
                                        }
                                        hasAttention = true;
                                        mbtnFollow.setBackgroundResource(R.color.BackgroundColor2);
                                        mbtnFollow.setText("已关注");
                                        mbtnFollow.setTextColor(UIUtils.getColorId(R.color.HostBlackColor3));
                                    }
                                });
                            }
                        }
                    });
                }
            });
        }
    }

    long[] mHits = new long[2];

    private void setUserInfo() {
        avUser.fetchInBackground(new GetCallback<AVObject>() {
            @Override
            public void done(AVObject avObject, AVException e) {
                if (UIUtils.hasException(e)) {
                    return;
                }
                String nickName = avObject.getString(nickname);
                introduce = avObject.getString(StringUtils.introduce);
                String gender = avObject.getString(StringUtils.gender);
                if (gender != null && gender.equals(StringUtils.woman)) {
                    mivGender.setVisibility(View.VISIBLE);
                    mivGender.setImageResource(R.drawable.vector_women);
                } else if (gender != null && gender.equals(StringUtils.man)) {
                    mivGender.setVisibility(View.VISIBLE);
                    mivGender.setImageResource(R.drawable.vector_man);
                } else {
                    mivGender.setVisibility(View.GONE);
                }
                if (!StringUtils.isEmpty(nickName)) {
                    mtvNickname.setText(nickName);
                }
                if (!StringUtils.isEmpty(introduce)) {
                    mtvIntroduce.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
                            mHits[mHits.length - 1] = SystemClock.uptimeMillis();
                            if (mHits[0] >= (SystemClock.uptimeMillis() - 500)) {
                                toggle();
                            }
                        }
                    });

                    mtvIntroduce.setText(introduce);
                    mtvIntroduce.post(new Runnable() {
                        @Override
                        public void run() {
                            // 默认展示3行的高度
                            int shortHeight = getShortHeight(mtvIntroduce, introduce);
                            int longHeight = getLongHeight(mtvIntroduce, introduce);

                            if (longHeight > shortHeight) {
                                mtvIntroduce.setBackgroundColor(UIUtils.getColorId(R.color.BackgroundColor2));
                            } else {
                                mtvIntroduce.setBackgroundColor(Color.TRANSPARENT);
                            }
                            mParams = (LinearLayout.LayoutParams) mtvIntroduce.getLayoutParams();
                            mParams.height = shortHeight;
                            mtvIntroduce.setLayoutParams(mParams);
                        }
                    });
                }
            }
        });
    }

    /**
     * btn按钮的开关与控制
     */
    private boolean isOpen = false;

    private void toggle() {
        int shortHeight = getShortHeight(mtvIntroduce, introduce);
        int longHeight = getLongHeight(mtvIntroduce, introduce);
        ValueAnimator animator = null;
        if (isOpen) {
            // 关闭
            isOpen = false;
            if (longHeight > shortHeight) {// 只有描述信息大于3行,才启动动画
                animator = ValueAnimator.ofInt(longHeight, shortHeight);
                mtvIntroduce.setBackgroundColor(UIUtils.getColorId(R.color.BackgroundColor2));
            }
        } else {
            // 打开
            isOpen = true;
            if (longHeight > shortHeight) {// 只有描述信息大于3行,才启动动画
                animator = ValueAnimator.ofInt(shortHeight, longHeight);
                mtvIntroduce.setBackgroundColor(Color.TRANSPARENT);
            }
        }
        if (animator != null) {// 只有描述信息大于3行,才启动动画
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                @Override
                public void onAnimationUpdate(ValueAnimator arg0) {
                    Integer height = (Integer) arg0.getAnimatedValue();
                    mParams.height = height;
                    mtvIntroduce.setLayoutParams(mParams);
                }

            });
            animator.setDuration(200);
            animator.start();
        }
    }

    private void initPhoto() {
        //看看云端有没有
        avUser.fetchInBackground(new GetCallback<AVObject>() {
            @Override
            public void done(AVObject avObject, AVException e) {
                if (UIUtils.hasException(e)) {
                    return;
                }
                avFileIcon = avUser.getAVFile(StringUtils.iconShow);
                if (avFileIcon != null) {
                    filePathIcon = StringUtils.getCachePicturePath() + "/" + avFileIcon.getObjectId() + StringUtils.ICON_SHOW_jpg;
//                    thumbnailIconUrl = avFileIcon.getThumbnailUrl(true, 380, 380);
                    mrivIcon.setOnClickListener(UserPager.this);
                    File fileIcon = new File(filePathIcon);
                    //存在文件，从本地获取
                    if (fileIcon.exists()) {
                        Bitmap bitmapIcon = BitmapFactory.decodeFile(filePathIcon, null);
                        mrivIcon.setImageBitmap(bitmapIcon);
                    } else {
                        avFileIcon.getDataInBackground(new GetDataCallback() {
                            @Override
                            public void done(byte[] bytes, AVException e) {
                                if (UIUtils.hasException(e)) {
                                    return;
                                }
                                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                mrivIcon.setImageBitmap(bitmap);
                                UIUtils.savePic2AppointDir(bitmap, StringUtils.getCachePicturePath(), avFileIcon.getObjectId() + StringUtils.ICON_SHOW_jpg);
                            }
                        });
                    }
                }
                avFileHead = avUser.getAVFile(StringUtils.headShow);
                if (avFileHead != null) {
                    filePathHead = StringUtils.getCachePicturePath() + "/" + avFileHead.getObjectId() + StringUtils.HEAD_SHOW_jpg;
//                    thumbnailHeadUrl = avFileHead.getThumbnailUrl(true, 380, 380);
                    mivHeadView.setOnClickListener(UserPager.this);
                    File fileHead = new File(filePathHead);
                    if (fileHead.exists()) {
                        Bitmap bitmapIcon = BitmapFactory.decodeFile(filePathHead, null);
                        mivBackground.setImageBitmap(bitmapIcon);
                    } else {
                        avFileHead.getDataInBackground(new GetDataCallback() {
                            @Override
                            public void done(byte[] bytes, AVException e) {
                                if (UIUtils.hasException(e)) {
                                    return;
                                }
                                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                mivBackground.setImageBitmap(bitmap);
                                UIUtils.savePic2AppointDir(bitmap, StringUtils.getCachePicturePath(), avFileHead.getObjectId() + StringUtils.HEAD_SHOW_jpg);
                            }
                        });
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.wwiv_letter:
                if (avUser.getObjectId().equals(UIUtils.avUser.getObjectId())){
                    return;
                }
                Intent intentLetter = new Intent(mActivity, LetterActivity.class);
                intentLetter.putExtra(StringUtils.avUserId, avUser.getObjectId());
                intentLetter.putExtra(StringUtils.nickname, nickname);
                mActivity.startActivity(intentLetter);
                break;
            case R.id.iv_head_home:
                Intent intentHead = new Intent(mActivity, ShowImageActivity.class);
                intentHead.putExtra(StringUtils.isUserActivity, true);
                intentHead.putExtra("picUri", filePathHead);
                mActivity.startActivity(intentHead);
                break;
            case R.id.riv_icon_user:
                Intent intentIcon = new Intent(mActivity, ShowImageActivity.class);
                intentIcon.putExtra(StringUtils.isUserActivity, true);
                intentIcon.putExtra("picUri", filePathIcon);
                mActivity.startActivity(intentIcon);
                break;
            case R.id.wwiv_art:
                Intent intent = new Intent(mActivity, UserFunctionActivity.class);
                intent.putExtra(StringUtils.typeTodo, StringUtils.artCardTodo);
                intent.putExtra(StringUtils.avUserId, avUser.getObjectId());
                mActivity.startActivity(intent);
                break;
            case R.id.wwiv_learn:
                Intent intent2 = new Intent(mActivity, UserFunctionActivity.class);
                intent2.putExtra(StringUtils.typeTodo, StringUtils.learnCardTodo);
                intent2.putExtra(StringUtils.avUserId, avUser.getObjectId());
                mActivity.startActivity(intent2);
                break;
            case R.id.wwiv_leisure:
                Intent intent3 = new Intent(mActivity, UserFunctionActivity.class);
                intent3.putExtra(StringUtils.typeTodo, StringUtils.leisureCardTodo);
                intent3.putExtra(StringUtils.avUserId, avUser.getObjectId());
                mActivity.startActivity(intent3);
                break;
            case R.id.wwiv_mind:
                Intent intent4 = new Intent(mActivity, UserFunctionActivity.class);
                intent4.putExtra(StringUtils.typeTodo, StringUtils.mindsCardTodo);
                intent4.putExtra(StringUtils.avUserId, avUser.getObjectId());
                mActivity.startActivity(intent4);
                break;
            case R.id.wwiv_sport:
                Intent intent5 = new Intent(mActivity, UserFunctionActivity.class);
                intent5.putExtra(StringUtils.typeTodo, StringUtils.sportCardTodo);
                intent5.putExtra(StringUtils.avUserId, avUser.getObjectId());
                mActivity.startActivity(intent5);
                break;
            case R.id.wwiv_music:
                Intent intent6 = new Intent(mActivity, UserFunctionActivity.class);
                intent6.putExtra(StringUtils.typeTodo, StringUtils.musicCardTodo);
                intent6.putExtra(StringUtils.avUserId, avUser.getObjectId());
                mActivity.startActivity(intent6);
                break;
            case R.id.wwiv_column_home:
                Intent intent7 = new Intent(mActivity, UserFunctionActivity.class);
                intent7.putExtra(StringUtils.typeTodo, StringUtils.columnTodo);
                intent7.putExtra(StringUtils.avUserId, avUser.getObjectId());
                mActivity.startActivity(intent7);
                break;
            default:
                break;
        }
    }

    @Override
    public void initData() {

        ArrayList<HomeCardTodoBean> homeCardTodoBeens = new ArrayList<>();
        myListViewAdapter = new MyListViewAdapter<HomeCardTodoBean>(homeCardTodoBeens, this) {
            @Override
            protected void onLoadMore(MoreHolder moreHolder) {
                loadMore(moreHolder);
            }

            @Override
            public BaseHolder getHolder() {
                return new FunctionHolder(mActivity, mHZListView, false);
            }
        };
        mHZListView.setAdapter(myListViewAdapter);
    }

    private void loadMore(final MoreHolder moreHolder) {
        moreData = null;
        this.moreHolder = moreHolder;
        AVQuery<AVObject> query = new AVQuery<>(StringUtils.cardMiddleTable);
        query.whereEqualTo(StringUtils.avUser, avUser);
        query.orderByDescending(StringUtils.createdAt);
        query.limit(3);
        query.skip(myListViewAdapter.getDatasSize());
        query.include(StringUtils.artCardTodo);
        query.include(StringUtils.learnCardTodo);
        query.include(StringUtils.musicCardTodo);
        query.include(StringUtils.sportCardTodo);
        query.include(StringUtils.mindsCardTodo);
        query.include(StringUtils.leisureCardTodo);
        query.include(StringUtils.artCardTodo_avUserPointer);
        query.include(StringUtils.learnCardTodo_avUserPointer);
        query.include(StringUtils.musicCardTodo_avUserPointer);
        query.include(StringUtils.sportCardTodo_avUserPointer);
        query.include(StringUtils.mindsCardTodo_avUserPointer);
        query.include(StringUtils.leisureCardTodo_avUserPointer);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (UIUtils.hasException(e)) {
//                    isLoading = false;
                    getmProgressBarListener().setProgressBar(moreData, moreHolder);
                    return;
                }
                moreData = new ArrayList<>();
                for (AVObject object : list) {
                    try {
                        HomeCardTodoBean cardTodoBean = new HomeCardTodoBean();
                        String typeNum = object.getString(StringUtils.typeNum);
                        cardTodoBean.sharetor = object.getAVObject(StringUtils.share_tor);
                        String typeTodo = typeNum2cardTodoType(typeNum);
                        AVObject cardTodo = object.getAVObject(typeTodo);
                        cardTodoBean.cardTodo = cardTodo;
                        cardTodoBean.typeTodo = typeTodo;
                        cardTodoBean.strContent = cardTodo.getString(StringUtils.strContent);
                        //将时间转为毫秒
                        cardTodoBean.millisecsData = cardTodo.getCreatedAt().getTime();
                        cardTodoBean.picAVFile = cardTodo.getAVFile(StringUtils.picPhotograph);

                        AVObject avUser = cardTodo.getAVObject(StringUtils.avUserPointer);
                        cardTodoBean.avUser = avUser;
                        cardTodoBean.nickName = avUser.getString(StringUtils.nickname);
                        cardTodoBean.introduce = avUser.getString(StringUtils.introduce);
                        cardTodoBean.iconAVFile = avUser.getAVFile(StringUtils.iconShow);

                        moreData.add(cardTodoBean);
                    } catch (Exception ex) {
                        continue;
                    }
                }
                getmProgressBarListener().setProgressBar(moreData, moreHolder);
            }
        });
    }

    @Override
    public void onRefresh() {
//        setFollowState();
        setUpvoteNum();
        AVQuery<AVObject> query = new AVQuery<>(StringUtils.cardMiddleTable);
        query.whereEqualTo(StringUtils.avUser, avUser);
        query.orderByDescending(StringUtils.createdAt);
        query.limit(3);
        query.include(StringUtils.artCardTodo);
        query.include(StringUtils.learnCardTodo);
        query.include(StringUtils.musicCardTodo);
        query.include(StringUtils.sportCardTodo);
        query.include(StringUtils.mindsCardTodo);
        query.include(StringUtils.leisureCardTodo);
        query.include(StringUtils.artCardTodo_avUserPointer);
        query.include(StringUtils.learnCardTodo_avUserPointer);
        query.include(StringUtils.musicCardTodo_avUserPointer);
        query.include(StringUtils.sportCardTodo_avUserPointer);
        query.include(StringUtils.mindsCardTodo_avUserPointer);
        query.include(StringUtils.leisureCardTodo_avUserPointer);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (UIUtils.hasException(e)) {
//                    isLoading = false;
                    msrlPull.setRefreshing(false);
                    return;
                }
                moreData = new ArrayList<>();
                for (AVObject object : list) {
                    try {
                        HomeCardTodoBean cardTodoBean = new HomeCardTodoBean();
                        String typeNum = object.getString(StringUtils.typeNum);
                        cardTodoBean.sharetor = object.getAVObject(StringUtils.share_tor);
                        String typeTodo = typeNum2cardTodoType(typeNum);
                        AVObject cardTodo = object.getAVObject(typeTodo);
                        cardTodoBean.cardTodo = cardTodo;
                        cardTodoBean.typeTodo = typeTodo;
                        cardTodoBean.strContent = cardTodo.getString(StringUtils.strContent);
                        //将时间转为毫秒
                        cardTodoBean.millisecsData = cardTodo.getCreatedAt().getTime();
                        cardTodoBean.picAVFile = cardTodo.getAVFile(StringUtils.picPhotograph);

                        AVObject avUser = cardTodo.getAVObject(StringUtils.avUserPointer);
                        cardTodoBean.avUser = avUser;
                        cardTodoBean.nickName = avUser.getString(StringUtils.nickname);
                        cardTodoBean.introduce = avUser.getString(StringUtils.introduce);
                        cardTodoBean.iconAVFile = avUser.getAVFile(StringUtils.iconShow);

                        moreData.add(cardTodoBean);
                    } catch (Exception ex) {
                        continue;
                    }
                }
                msrlPull.setRefreshing(false);
                myListViewAdapter.setDatas(moreData);
                mHZListView.setAdapter(myListViewAdapter);
            }
        });
    }
}
