package com.nannan.nannan.bean;

import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;

/**
 * Created by MaxwellCNZ on 2017/5/5.
 */

public class CommentTodoBean {

    public String strComment;
    public String nickName;
    public AVFile iconAVFile;
    public AVObject commentTodo;
    public AVObject avUser;
    public long millisecsData;
    public boolean hasUpvote;
    public int upvoteNum;
    // commentTodo 与 replyTodo 对应，他俩都有cardTodoId，并且相对应
    //当删除commentTodo时，很容易就把replyTodo删除
    public String cardTodoId;
}
