package com.nannan.nannan.view.pager;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.SystemClock;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.GetDataCallback;
import com.nannan.nannan.R;
import com.nannan.nannan.bean.HomeCardTodoBean;
import com.nannan.nannan.utils.SelectUtils;
import com.nannan.nannan.utils.SharedPreferenceUtils;
import com.nannan.nannan.utils.StringUtils;
import com.nannan.nannan.utils.UIUtils;
import com.nannan.nannan.view.activity.MainActivity;
import com.nannan.nannan.view.activity.pager.ColumnActivity;
import com.nannan.nannan.view.activity.pager.FunctionActivity;
import com.nannan.nannan.view.activity.pager.SetUpNanActivity;
import com.nannan.nannan.view.activity.pager.UserEditActivity;
import com.nannan.nannan.view.adapter.MyListViewAdapter;
import com.nannan.nannan.view.holder.BaseHolder;
import com.nannan.nannan.view.holder.HomeHolder;
import com.nannan.nannan.view.holder.MoreHolder;
import com.nannan.nannan.view.net.AVObjectNet;
import com.nannan.nannan.view.widget.MySRLayout;
import com.nannan.nannan.view.widget.ParallaxListView;
import com.nannan.nannan.view.widget.WaterWaveImageButton;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static com.nannan.nannan.utils.StringUtils.gender;
import static com.nannan.nannan.utils.StringUtils.introduce;
import static com.nannan.nannan.utils.StringUtils.nickname;
import static com.nannan.nannan.utils.UIUtils.avUser;

/**
 * Created by MaxwellCNZ on 2017/3/7.
 */

public class HomePager extends BasePager implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    //从pager中启动startActivityForResult必须获取到所依赖的activity，不能是uiutils.getcontext()!!!
    private ParallaxListView mHZListView;
    private ImageView mivHeadView;
    private ImageView mivBackground;
    private WaterWaveImageButton mwwivAdd;
    private PopupWindow mPopupWindow;
    private ImageView mrivIcon;
    private boolean isIconExists;
    private boolean isHeadExists;
    private WaterWaveImageButton mwwivGameFilm;
    private WaterWaveImageButton mwwivColume;
    private WaterWaveImageButton mwwivWorkStudy;
    private WaterWaveImageButton mwwivShareDiscuss;
    private WaterWaveImageButton mwwivCostumeCate;
    private WaterWaveImageButton mwwivSportTravel;
    private WaterWaveImageButton mwwivMusicArt;
    private ImageButton mibEdit;
    private AlertDialog dialog;
    private SharedPreferenceUtils sp;
    private TextView mtvNickname;
    private TextView mtvDescribe;
    private ImageView mivGender;
    private ArrayList<HomeCardTodoBean> moreData;
    private ArrayList<HomeCardTodoBean> cardTodoBeens;
    private static MyListViewAdapter<HomeCardTodoBean> myListViewAdapter;
    private SwipeRefreshLayout msrlPull;
    private final MainActivity mActivity;
    private LinearLayout.LayoutParams mParams;
    //    private boolean hasSetMenu;
    private TextView mtvUpvoteNum;
    private int upVoteNum = 0;

    public HomePager(MainActivity activity) {
        super();
        mActivity = activity;
        UserEditActivity.setOnChangeUserInfoListener(new UserEditActivity.onChangeUserInfoListener() {
            @Override
            public void onChange() {
                setUserInfo();
            }
        });
        FunctionActivity.setOnRefreshListener(new FunctionActivity.onRefreshListener() {
            @Override
            public void onRefresh() {
                getDataFromServerAllCardTodo();
            }
        });
        mActivity.setOnAlbumOrPhotographBackgroundListener(new MainActivity.onAlbumOrPhotographBackgroundListener() {
            @Override
            public void onChangeBackground(Bitmap bitmap) {
                if (isIcon) {
                    //设置图标
                    mrivIcon.setImageBitmap(bitmap);
                } else {
                    //设置背景
                    mivBackground.setImageBitmap(bitmap);
                }
            }
        });
    }

    /**
     * 初始化数据
     */
    @Override
    public void initData() {

    }

    /**
     * 从网络获取数据
     */
    private void getDataFromServerAllCardTodo() {
        cardTodoBeens = new ArrayList<>();
        myListViewAdapter = new MyListViewAdapter<HomeCardTodoBean>(cardTodoBeens, HomePager.this) {
            @Override
            protected void onLoadMore(final MoreHolder moreHolder) {
                loadMore(moreHolder);
            }

            @Override
            public BaseHolder getHolder() {
                return new HomeHolder(myListViewAdapter, mHZListView, mActivity);
            }
        };
        mHZListView.setAdapter(myListViewAdapter);

    }

    /**
     * 获取homepager的适配器
     */
    public static MyListViewAdapter getMyListViewAdapter() {
        return myListViewAdapter;
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
//                query.selectKeys(Arrays.asList(StringUtils.cardTodo));
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
                        cardTodoBean.cardMiddle = object;
                        String typeTodo = typeNum2cardTodoType(typeNum);
                        AVObject cardTodo = object.getAVObject(typeTodo);
                        cardTodoBean.upVoteNum = cardTodo.getInt(StringUtils.upVote);
                        cardTodoBean.avUserPointer = avUser;
                        cardTodoBean.cardTodo = cardTodo;
                        cardTodoBean.typeTodo = typeTodo;
                        cardTodoBean.strContent = cardTodo.getString(StringUtils.strContent);
                        //将时间转为毫秒
                        cardTodoBean.millisecsData = cardTodo.getCreatedAt().getTime();
                        cardTodoBean.nickName = sp.getString(nickname);
                        cardTodoBean.introduce = sp.getString(introduce);
                        cardTodoBean.iconUri = StringUtils.getCachePicturePath() + "/" +
                                StringUtils.avObjectIdTransitionPicForm(sp.getString(StringUtils.iconAVObjectId));
                        cardTodoBean.picAVFile = cardTodo.getAVFile(StringUtils.picPhotograph);
                        moreData.add(cardTodoBean);
                    } catch (Exception ex) {
                        continue;
                    }
                }
                getmProgressBarListener().setProgressBar(moreData, moreHolder);
//                isLoading = false;
            }
        });
    }

    /**
     * 数字转类型
     *
     * @param typeNum
     */
    public static String typeNum2cardTodoType(String typeNum) {
        String type = "";
        switch (typeNum) {
            case "1":
                type = StringUtils.artCardTodo;
                break;
            case "2":
                type = StringUtils.learnCardTodo;
                break;
            case "3":
                type = StringUtils.leisureCardTodo;
                break;
            case "4":
                type = StringUtils.mindsCardTodo;
                break;
            case "5":
                type = StringUtils.sportCardTodo;
                break;
            case "6":
                type = StringUtils.musicCardTodo;
                break;
            default:
                break;
        }
        return type;
    }

    /**
     * 同步数据并设置用户的基本信息
     */
    private void syncUserInfo() {
        //现网络 后缓存
//        avUser = AVUser.getCurrentUser();
        sp = new SharedPreferenceUtils();
        //同步网络
        avUser.fetchInBackground(new GetCallback<AVObject>() {
            @Override
            public void done(AVObject avObject, AVException e) {
                if (UIUtils.hasException(e)) {
                    setUserInfo();
                    return;
                }
                sp.putBoolean(StringUtils.hasUserInfo, true);
                sp.putString(nickname, avObject.getString(nickname));
                sp.putString(gender, avObject.getString(gender));
                sp.putString(introduce, avObject.getString(introduce));
                setUserInfo();
                //首先判断icon的缩略图是否存在，然后决定是否下载缩略图
                AVFile iconFile = avObject.getAVFile(StringUtils.iconShow);
                if (StringUtils.isEmpty(sp.getString(StringUtils.iconAVObjectId)) && iconFile != null) {
                    sp.putString(StringUtils.iconAVObjectId, iconFile.getObjectId());
                }
                if (iconFile != null) {
                    File file = new File(StringUtils.getCachePicturePath() + "/" + StringUtils.avObjectIdTransitionPicForm(iconFile.getObjectId()));
                    if (!file.exists()) {
                        //如果不存在清重新下载缩略图
//                            UIUtils.print("上传到时候没有拿到缩略图，现在拿到了");
                        sp.putString(StringUtils.iconAVObjectId, iconFile.getObjectId());
                        AVObjectNet.saveThumbnailPic2Dir(iconFile, 100, 100);
                    }
                }
            }
        });
        // 赞的数量
        setUpvoteNum();
    }

    private void setUpvoteNum() {
        upVoteNum = 0;
        AVQuery<AVObject> query = new AVQuery<>(StringUtils.cardMiddleTable);
        query.whereEqualTo(StringUtils.avUser, avUser);
        query.include(StringUtils.artCardTodo);
        query.include(StringUtils.learnCardTodo);
        query.include(StringUtils.musicCardTodo);
        query.include(StringUtils.sportCardTodo);
        query.include(StringUtils.mindsCardTodo);
        query.include(StringUtils.leisureCardTodo);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (UIUtils.hasException(e)) {
                    return;
                }
                for (AVObject object : list) {
                    try {
                        String typeNum = object.getString(StringUtils.typeNum);
                        String typeTodo = typeNum2cardTodoType(typeNum);
                        AVObject cardTodo = object.getAVObject(typeTodo);
                        upVoteNum += cardTodo.getInt(StringUtils.upVote);
                    }catch (Exception ex){
                        continue;
                    }

                }
                AVQuery<AVObject> query = new AVQuery<>(StringUtils.columnTodo);
                query.whereEqualTo(StringUtils.avUserPointer, avUser);
                query.findInBackground(new FindCallback<AVObject>() {
                    @Override
                    public void done(List<AVObject> list, AVException e) {
                        if (UIUtils.hasException(e)){
                            return;
                        }
                        for (AVObject object : list) {
                            try {
                                upVoteNum += object.getInt(StringUtils.upVote);
                            }catch (Exception ex){
                                continue;
                            }
                        }
                        if (upVoteNum >= 1000) {
                            float num = (float) (upVoteNum / 1000.0);
                            DecimalFormat df = new DecimalFormat("0.0");
                            mtvUpvoteNum.setText(df.format(num) + "k");
                        } else {
                            mtvUpvoteNum.setText(upVoteNum + "");
                        }
                    }
                });
            }
        });
    }

    /**
     * 设置用户信息
     */
    private void setUserInfo() {
        if (StringUtils.isEmpty(sp.getString(nickname))) {
            mtvNickname.setText(sp.getString(StringUtils.nickname_default));
        } else {
            mtvNickname.setText(sp.getString(nickname));
        }

        if (sp.getString(gender).equals(StringUtils.woman)) {
            mivGender.setVisibility(View.VISIBLE);
            mivGender.setImageResource(R.drawable.vector_women);
        } else if (sp.getString(gender).equals(StringUtils.man)) {
            mivGender.setVisibility(View.VISIBLE);
            mivGender.setImageResource(R.drawable.vector_man);
        } else {
            mivGender.setVisibility(View.GONE);
        }
        final long[] mHits = new long[2];

        if (StringUtils.isEmpty(sp.getString(introduce))) {
            mtvDescribe.setText(sp.getString(StringUtils.describe_default));
        } else {
            mtvDescribe.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //拷贝数组操作
                    System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
                    mHits[mHits.length - 1] = SystemClock.uptimeMillis();  // 将离开机的时间设置给数组的第二个元素,离开机时间 :毫秒值,手机休眠不算
                    if (mHits[0] >= (SystemClock.uptimeMillis() - 500)) {  // 判断是否多击操作
                        toggle();
                    }
                }
            });
            mtvDescribe.setText(sp.getString(introduce));
            mtvDescribe.post(new Runnable() {
                @Override
                public void run() {
                    // 默认展示3行的高度
                    int shortHeight = getShortHeight(mtvDescribe, sp.getString(introduce));
                    int longHeight = getLongHeight(mtvDescribe, sp.getString(introduce));

                    if (longHeight > shortHeight) {
                        mtvDescribe.setBackgroundColor(UIUtils.getColorId(R.color.BackgroundColor2));
                    } else {
                        mtvDescribe.setBackgroundColor(Color.TRANSPARENT);
                    }
                    mParams = (LinearLayout.LayoutParams) mtvDescribe.getLayoutParams();
                    mParams.height = shortHeight;
                    mtvDescribe.setLayoutParams(mParams);
                }
            });
        }
    }

    /**
     * 获取3行textview的高度
     */
    public static int getShortHeight(TextView textView, String content) {
        int width = textView.getMeasuredWidth();// 宽度
        TextView view = new TextView(UIUtils.getContext());
        view.setText(content);
        view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        view.setMaxLines(3);
        int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(width,
                View.MeasureSpec.EXACTLY);
        int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        view.measure(widthMeasureSpec, heightMeasureSpec);
        return view.getMeasuredHeight();
    }

    /**
     * btn按钮的开关与控制
     */
    private boolean isOpen = false;

    private void toggle() {
        int shortHeight = getShortHeight(mtvDescribe, sp.getString(introduce));
        int longHeight = getLongHeight(mtvDescribe, sp.getString(introduce));
        ValueAnimator animator = null;
        if (isOpen) {
            // 关闭
            isOpen = false;
            if (longHeight > shortHeight) {// 只有描述信息大于3行,才启动动画
                animator = ValueAnimator.ofInt(longHeight, shortHeight);
                mtvDescribe.setBackgroundColor(UIUtils.getColorId(R.color.BackgroundColor2));
            }
        } else {
            // 打开
            isOpen = true;
            if (longHeight > shortHeight) {// 只有描述信息大于3行,才启动动画
                animator = ValueAnimator.ofInt(shortHeight, longHeight);
                mtvDescribe.setBackgroundColor(Color.TRANSPARENT);
            }
        }
        if (animator != null) {// 只有描述信息大于3行,才启动动画
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                @Override
                public void onAnimationUpdate(ValueAnimator arg0) {
                    Integer height = (Integer) arg0.getAnimatedValue();
                    mParams.height = height;
                    mtvDescribe.setLayoutParams(mParams);
                }

            });
            animator.setDuration(200);
            animator.start();
        }


    }

    /**
     * 获取完整textview的高度
     */
    public static int getLongHeight(TextView textView, String content) {
        int width = textView.getMeasuredWidth();
        TextView view = new TextView(UIUtils.getContext());
        view.setText(content);
        view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        // view.setMaxLines(7);

        int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(width,
                View.MeasureSpec.EXACTLY);
        int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        view.measure(widthMeasureSpec, heightMeasureSpec);
        return view.getMeasuredHeight();
    }

    @Override
    public View initView() {
        View mHeadView = UIUtils.inflate(R.layout.item_head_home);
        View inflate = UIUtils.inflate(R.layout.pager_home);
        View mSecondView = UIUtils.inflate(R.layout.item_second_home);
        View mThirdView = UIUtils.inflate(R.layout.item_third_home);
        View mFourthView = UIUtils.inflate(R.layout.item_fourth_home);

        mivBackground = (ImageView) inflate.findViewById(R.id.iv_head_home_background);
        mHZListView = (ParallaxListView) inflate.findViewById(R.id.pl_ListView);
        msrlPull = (MySRLayout) inflate.findViewById(R.id.srl_pull_refresh_home);
        setMenuShow(mHZListView);

        mivHeadView = (ImageView) mHeadView.findViewById(R.id.iv_head_home);

        mrivIcon = (ImageView) mSecondView.findViewById(R.id.riv_second_icon);
        mibEdit = (ImageButton) mSecondView.findViewById(R.id.ib_edit_second);
        mtvNickname = (TextView) mSecondView.findViewById(R.id.tv_nickname_second);
        mtvDescribe = (TextView) mSecondView.findViewById(R.id.tv_describe_second);
        mivGender = (ImageView) mSecondView.findViewById(R.id.iv_gender_second);
        mtvUpvoteNum = (TextView) mSecondView.findViewById(R.id.tv_upvote_support);

        mwwivAdd = (WaterWaveImageButton) mThirdView.findViewById(R.id.wwiv_add_home);
        mwwivColume = (WaterWaveImageButton) mThirdView.findViewById(R.id.wwiv_column_home);
        mwwivGameFilm = (WaterWaveImageButton) mThirdView.findViewById(R.id.wwiv_art);
        mwwivWorkStudy = (WaterWaveImageButton) mThirdView.findViewById(R.id.wwiv_learn);
        mwwivShareDiscuss = (WaterWaveImageButton) mThirdView.findViewById(R.id.wwiv_leisure);
        mwwivCostumeCate = (WaterWaveImageButton) mThirdView.findViewById(R.id.wwiv_mind);
        mwwivSportTravel = (WaterWaveImageButton) mThirdView.findViewById(R.id.wwiv_sport);
        mwwivMusicArt = (WaterWaveImageButton) mThirdView.findViewById(R.id.wwiv_music);

        mHZListView.addHeaderView(mHeadView);
        mHZListView.addHeaderView(mSecondView);
        mHZListView.addHeaderView(mThirdView);
        mHZListView.addHeaderView(mFourthView);

        mrivIcon.setOnClickListener(this);
        mivHeadView.setOnClickListener(this);
        mibEdit.setOnClickListener(this);

        msrlPull.setOnRefreshListener(this);
        mwwivAdd.setOnClickListener(this);
        mwwivColume.setOnClickListener(this);
        mwwivGameFilm.setOnClickListener(this);
        mwwivWorkStudy.setOnClickListener(this);
        mwwivShareDiscuss.setOnClickListener(this);
        mwwivCostumeCate.setOnClickListener(this);
        mwwivSportTravel.setOnClickListener(this);
        mwwivMusicArt.setOnClickListener(this);
        //设置下拉刷新的参数
        msrlPull.setColorSchemeResources(R.color.Pink, R.color.Purple, R.color.Brown,
                R.color.Orange, R.color.Green, R.color.Cyan, R.color.WaterBlue);
        msrlPull.setDistanceToTriggerSync(400);// 设置手指在屏幕下拉多少距离会触发下拉刷新
        msrlPull.setProgressBackgroundColorSchemeResource(R.color.BackgroundColor2);
        msrlPull.setSize(SwipeRefreshLayout.LARGE);

        //初始化界面设置用户数据
        syncUserInfo();

        initPhoto();
        //设置监听
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

        mHZListView.setParallaxImageView(mivHeadView, mivBackground);
        getDataFromServerAllCardTodo();
        return inflate;
    }

    private boolean isIcon = false;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.wwiv_column_home:
                Intent intentColumn = new Intent(UIUtils.getContext(), ColumnActivity.class);
                intentColumn.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                UIUtils.getContext().startActivity(intentColumn);
                break;
            case R.id.ib_edit_second:
                //修改基本资料 dialog来实现
                Intent intent = new Intent(UIUtils.getContext(), UserEditActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                UIUtils.getContext().startActivity(intent);
                break;
            case R.id.riv_second_icon:
                showPopWindowAlbumOrPhotograph();
                isIcon = true;
                break;
            case R.id.iv_head_home:
                //弹出popwindow 选择打开相册 选择图片
                showPopWindowAlbumOrPhotograph();
                isIcon = false;
                break;
            case R.id.tv_album_home:
                //打开相册
                if (!UIUtils.hasSdcard) {
                    UIUtils.toast4Shot("sd卡未加载！");
                    return;
                }
                mPopupWindow.dismiss();
                mListener.onAlbum(isIcon);

                break;
            case R.id.tv_photograph_home:
                //打开相机
                if (!UIUtils.hasSdcard) {
                    UIUtils.toast4Shot("sd卡未加载！");
                    return;
                }
                mPopupWindow.dismiss();
                mListener.onPhotograph(isIcon);

                break;
            case R.id.wwiv_add_home:
                Intent intentWrite = new Intent(mActivity, SetUpNanActivity.class);
//                intentWrite.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mActivity.startActivity(intentWrite);
                break;
            case R.id.wwiv_art:
                Intent artIntent = new Intent(mActivity, FunctionActivity.class);
                artIntent.putExtra(StringUtils.typeTodo, StringUtils.artCardTodo);
                artIntent.putExtra(StringUtils.artCardTodo, "艺术");
                mActivity.startActivity(artIntent);
                break;
            case R.id.wwiv_learn:
                Intent learnIntent = new Intent(mActivity, FunctionActivity.class);
                learnIntent.putExtra(StringUtils.typeTodo, StringUtils.learnCardTodo);
                learnIntent.putExtra(StringUtils.learnCardTodo, "学习");
                mActivity.startActivity(learnIntent);
                break;
            case R.id.wwiv_leisure:
                Intent leisureIntent = new Intent(mActivity, FunctionActivity.class);
                leisureIntent.putExtra(StringUtils.typeTodo, StringUtils.leisureCardTodo);
                leisureIntent.putExtra(StringUtils.leisureCardTodo, "闲暇");
                mActivity.startActivity(leisureIntent);
                break;
            case R.id.wwiv_mind:
                Intent mindIntent = new Intent(mActivity, FunctionActivity.class);
                mindIntent.putExtra(StringUtils.typeTodo, StringUtils.mindsCardTodo);
                mindIntent.putExtra(StringUtils.mindsCardTodo, "脑洞");
                mActivity.startActivity(mindIntent);
                break;
            case R.id.wwiv_sport:
                Intent sportIntent = new Intent(mActivity, FunctionActivity.class);
                sportIntent.putExtra(StringUtils.typeTodo, StringUtils.sportCardTodo);
                sportIntent.putExtra(StringUtils.sportCardTodo, "运动");
                mActivity.startActivity(sportIntent);
                break;
            case R.id.wwiv_music:
                Intent musicIntent = new Intent(mActivity, FunctionActivity.class);
                musicIntent.putExtra(StringUtils.typeTodo, StringUtils.musicCardTodo);
                musicIntent.putExtra(StringUtils.musicCardTodo, "音乐");
                mActivity.startActivity(musicIntent);
                break;
            default:
                break;
        }
    }

    /**
     * 初始化填充图片
     */
    private void initPhoto() {
        //充填图片，如果本地有该用户的图片，就加载本地图片，否则就从网上下载图片并保存填充
        String filePathIcon = StringUtils.getPicturePath() + "/" + StringUtils.ICON_SHOW_jpg;
        String filePathHead = StringUtils.getPicturePath() + "/" + StringUtils.HEAD_SHOW_jpg;

        File fileIcon = new File(filePathIcon);
        File fileHead = new File(filePathHead);

        isIconExists = false;
        isHeadExists = false;
        //存在文件，从本地获取
        if (fileIcon.exists()) {
            isIconExists = true;
            Bitmap bitmapIcon = BitmapFactory.decodeFile(filePathIcon, null);
            mrivIcon.setImageBitmap(bitmapIcon);
        }
        if (fileHead.exists()) {
            isHeadExists = true;
            Bitmap bitmapHead = BitmapFactory.decodeFile(filePathHead, null);
            mivBackground.setImageBitmap(bitmapHead);
        }
        if (!isIconExists || !isHeadExists) {
            //看看云端有没有
            avUser.fetchInBackground(new GetCallback<AVObject>() {
                @Override
                public void done(AVObject avObject, AVException e) {
                    if (UIUtils.hasException(e)) {
                        return;
                    }
                    if (!isIconExists) {
                        AVFile avFileIcon = avUser.getAVFile(StringUtils.iconShow);
//                                    UIUtils.print("过来了吗2");
                        if (avFileIcon != null) {
                            avFileIcon.getDataInBackground(new GetDataCallback() {
                                @Override
                                public void done(byte[] bytes, AVException e) {
                                    if (UIUtils.hasException(e)) {
                                        return;
                                    }
//                                                UIUtils.print("过来了吗3" + bytes.length);
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                    mrivIcon.setImageBitmap(bitmap);
                                    UIUtils.savePic2AppointDir(bitmap, StringUtils.getPicturePath(), StringUtils.ICON_SHOW_jpg);
                                }
                            });
                        }
                    }

                    if (!isHeadExists) {
                        AVFile avFileHead = avUser.getAVFile(StringUtils.headShow);
                        if (avFileHead != null) {
                            avFileHead.getDataInBackground(new GetDataCallback() {
                                @Override
                                public void done(byte[] bytes, AVException e) {
                                    if (UIUtils.hasException(e)) {
                                        return;
                                    }
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                    mivBackground.setImageBitmap(bitmap);
                                    UIUtils.savePic2AppointDir(bitmap, StringUtils.getPicturePath(), StringUtils.HEAD_SHOW_jpg);
                                }
                            });
                        }
                    }
                }
            });
        }
    }

    /**
     * 弹出相册和照相对话框
     */
    private void showPopWindowAlbumOrPhotograph() {
        final View popView = UIUtils.inflate(R.layout.popwindow_picture);
        TextView tvAlbum = (TextView) popView.findViewById(R.id.tv_album_home);
        View tvPhotograph = popView.findViewById(R.id.tv_photograph_home);
        tvAlbum.setBackground(SelectUtils.getSelector(UIUtils.getColorId(R.color.Dialog_normal), UIUtils.getColorId(R.color.Dialog_pressed), 0));
        tvPhotograph.setBackground(SelectUtils.getSelector(UIUtils.getColorId(R.color.Dialog_normal), UIUtils.getColorId(R.color.Dialog_pressed), 0));
        tvAlbum.setOnClickListener(this);
        tvPhotograph.setOnClickListener(this);
        //当你新建一个popupwindow的时候，你所充填的布局宽高不能是自己的，
        // 如下mivHintDialog.getWidth()可以显示布局，但是更改为popView.getWidth()就不行了！！
        mPopupWindow = new PopupWindow(popView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setTouchable(true);
        //弹出PopWindow 是相册还是拍照
        //获取自身的高度, 先绘制popview，然后才执行动画，绘制了内容后才能获取高度，所以动画才会执行在popupWindow.showAtLocation的后面！
        popView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                popView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                int popViewHeight = popView.getHeight();
                TranslateAnimation translateAnimation = new TranslateAnimation(0, 0, -popViewHeight, 0);
                translateAnimation.setDuration(200);
                translateAnimation.setFillAfter(true);
                popView.startAnimation(translateAnimation);
            }
        });

        mPopupWindow.showAtLocation(mivBackground, Gravity.TOP, 0, 0);
    }

    /**
     * 下拉刷新监听器
     */
    @Override
    public void onRefresh() {
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
//                query.selectKeys(Arrays.asList(StringUtils.cardTodo));
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (UIUtils.hasException(e)) {
                    msrlPull.setRefreshing(false);
                    return;
                }
                moreData = new ArrayList<>();
                for (AVObject object : list) {
                    try {
                        HomeCardTodoBean cardTodoBean = new HomeCardTodoBean();
                        String typeNum = object.getString(StringUtils.typeNum);
                        cardTodoBean.avUserPointer = avUser;
                        cardTodoBean.cardMiddle = object;
                        String typeTodo = typeNum2cardTodoType(typeNum);
                        AVObject cardTodo = object.getAVObject(typeTodo);
                        cardTodoBean.upVoteNum = cardTodo.getInt(StringUtils.upVote);
                        cardTodoBean.cardTodo = cardTodo;
                        cardTodoBean.typeTodo = typeTodo;
                        cardTodoBean.strContent = cardTodo.getString(StringUtils.strContent);
                        //将时间转为毫秒
                        cardTodoBean.millisecsData = cardTodo.getCreatedAt().getTime();
                        cardTodoBean.nickName = sp.getString(nickname);
                        cardTodoBean.introduce = sp.getString(introduce);
                        cardTodoBean.iconUri = StringUtils.getCachePicturePath() + "/" +
                                StringUtils.avObjectIdTransitionPicForm(sp.getString(StringUtils.iconAVObjectId));
                        cardTodoBean.picAVFile = cardTodo.getAVFile(StringUtils.picPhotograph);
                        moreData.add(cardTodoBean);
                    }catch (Exception ex){
                        continue;
                    }

                }
                msrlPull.setRefreshing(false);
                myListViewAdapter.setDatas(moreData);
                mHZListView.setAdapter(myListViewAdapter);
            }
        });
    }


    /**
     * 设置一个监听 用来调用打开相册和照相功能
     */
    public interface onAlbumOrPhotographListener {
        void onAlbum(boolean isIcon);

        void onPhotograph(boolean isIcon);

    }

    private onAlbumOrPhotographListener mListener;

    public void setOnAlbumOrPhotographListener(onAlbumOrPhotographListener listener) {
        mListener = listener;
    }

}
