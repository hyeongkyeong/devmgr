package com.hyeongkyeong.devmng;

import android.graphics.drawable.Drawable;

/**
 * ListView에 보여줄 데이터를 추가하는 클래스 입니다.
 */
public class ListItemData {

    private static final String TAG = "ListItemData";

    //private Drawable mIcon;
    public int mId;
    public String[] mData;
    private boolean mSelectable = true;

    public ListItemData() {
        mId=0;
        mData = new String[5];
    }

    public ListItemData(int length) {
        mId = 0;
        mData = new String[length];
    }

    public ListItemData(String str) {
        mData = new String[1];
        mData[0] = str;
    }

    public ListItemData(String[] obj) {

        mData = obj;
    }

    public ListItemData(int id_num, String Data_Col01, String Data_Col02, String Data_Col03, String Data_Col04, String Data_Col05) {
        mId = id_num;

        mData = new String[5];
        mData[0] = Data_Col01;  //장비 이름
        mData[1] = Data_Col02;  //장비 층수
        mData[2] = Data_Col03;  //장비 위치
        mData[3] =  Data_Col04; //장비 사진
        mData[4] =  Data_Col05; //RM 사진
    }

    public boolean isSelectable() {

        return mSelectable;
    }


    public void setSelectable(boolean selectable) {

        mSelectable = selectable;
    }

    public String getData() {
        if (mData == null) {
            return null;
        }

        return mData[0];
    }

    public String getData(int index) {
        if (mData == null || index >= mData.length) {
            return null;
        }

        return mData[index];
    }

    public void setData(String[] obj)
    {

        mData = obj;
    }
/*
    public void setIcon(Drawable icon)
    {

        mIcon = icon;
    }


    public Drawable getIcon() {

        return mIcon;
    }

    public int compareTo(ListItemData other) {
        if (mData != null) {
            String[] otherData = other.getData();
            if (mData.length == otherData.length) {
                for (int i = 0; i < mData.length; i++) {
                    if (!mData[i].equals(otherData[i])) {
                        return -1;
                    }
                }
            } else {
                return -1;
            }
        } else {
            throw new IllegalArgumentException();
        }

        return 0;
    }
    */

}