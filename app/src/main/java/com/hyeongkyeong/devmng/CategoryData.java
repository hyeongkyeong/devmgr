package com.hyeongkyeong.devmng;

import android.os.Environment;

import java.io.File;

/**
 * Created by munya on 2017-06-10.
 */

public class CategoryData {

    public  int mId;
    public  String mName;
    public  String mDatafile;
    public  String mTablename;

    private boolean mSelectable = true;


    public static String app_root_dir = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "DevMgr";

    public CategoryData(){
        this.mId = 0;
        this.mName = "";
        this.mTablename = "";
        this.mDatafile = "";
    }

    public CategoryData(int id, String name){
        this.mId = id;
        this.mName = name;
        this.mTablename = "TABLE_"+ Integer.toString(id);
        this.mDatafile = DatabaseHelper.app_root_dir + File.separator + name+".csv";
    }

    public int getId() {
        return mId;
    }

    public String getName() {
        if (mName == null ) {
            return null;
        }

        return mName;
    }

    public String getDatafile() {
        if (mDatafile == null ) {
            return null;
        }

        return mDatafile;
    }

    public String getTablename() {
        if (mTablename == null ) {
            return null;
        }

        return mTablename;
    }

    public void setData(int id, String name)
    {
        this.mId = id;
        this.mName = name;
        this.mTablename = "TABLE_"+ Integer.toString(id);
        this.mDatafile = DatabaseHelper.app_root_dir + File.separator + name+".csv";
    }
}
