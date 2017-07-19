package com.nannan.nannan.bean;

import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;

/**
 * Created by MaxwellCNZ on 2017/5/10.
 */

public class ColumnTodoBeen {


    public String strContent;
    public AVFile picAVFile;
    public AVUser avUserPointer;
    public long millisecsData;
    public AVFile iconAVFile;
    public String nickname;
    public String strTitle;
    public AVObject columnTodo;
    public AVObject todoColection;
    public AVObject sharetor;
    public boolean wasRead;
    public boolean hasChecked;
    public String introduce;
}
