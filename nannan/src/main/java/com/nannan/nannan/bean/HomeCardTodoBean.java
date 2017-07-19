package com.nannan.nannan.bean;

import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;

/**
 * Created by MaxwellCNZ on 2017/4/4.
 */

public class HomeCardTodoBean {
    public String iconUri;
    public String nickName;
    public String introduce;
    public String strContent;
    public String typeTodo;
    public long millisecsData;
    public AVFile picAVFile;
    public AVFile iconAVFile;
    public AVObject avUser;
    public AVObject cardTodo;
    public AVObject collectionCard;
    public AVObject sharetor;
    public AVObject cardMiddle;
    public AVUser avUserPointer;
    public boolean isOpen;
    public boolean shouldFold;
    public boolean isInit;
    public boolean hasChecked;
    public int upVoteNum;
}
