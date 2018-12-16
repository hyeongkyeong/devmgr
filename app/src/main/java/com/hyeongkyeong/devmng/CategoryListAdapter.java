package com.hyeongkyeong.devmng;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by munya on 2017-06-10.
 */

public class CategoryListAdapter extends BaseAdapter {
    private Context mContext;

    private List<ListItemData> mItems = new ArrayList<ListItemData>();

    public CategoryListAdapter(Context context) {
        mContext = context;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CategoryListItemView itemView;
        //listitem 레이아웃을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            itemView = new CategoryListItemView(mContext,mItems.get(position));
        } else {
            itemView = (CategoryListItemView) convertView;
            itemView.setText(mItems.get(position).getData());
        }

        return itemView;
    }

    public void clear() {

        mItems.clear();
    }

    public void addItem(ListItemData it) {

        mItems.add(it);
    }

}
