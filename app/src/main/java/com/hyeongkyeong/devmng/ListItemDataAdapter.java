package com.hyeongkyeong.devmng;

/**
 * Created by hkseo on 2017-02-19.
 */

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

public class ListItemDataAdapter extends BaseAdapter {

    private static final String TAG = "ListItemAdapter";

    private Context mContext;

    private List<ListItemData> mItems = new ArrayList<ListItemData>();

    public ListItemDataAdapter(Context context) {

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
        // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴. : 필수 구현
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ListItemDataView itemView;
        //listitem 레이아웃을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            itemView = new ListItemDataView(mContext, mItems.get(position));
        } else {
            itemView = (ListItemDataView) convertView;

            itemView.setText(0, mItems.get(position).getData(0));
            itemView.setText(1, mItems.get(position).getData(1));
            itemView.setText(2, mItems.get(position).getData(2));
        }

        return itemView;
    }

    public void clear() {

        mItems.clear();
    }

    public void setListItems(List<ListItemData> lit) {
        mItems = lit;
    }

    public boolean areAllItemsSelectable() {

        return false;
    }

    public boolean isSelectable(int position) {
        try {
            return mItems.get(position).isSelectable();
        } catch (IndexOutOfBoundsException ex) {
            return false;
        }
    }

    public void addItem(ListItemData it) {

        mItems.add(it);
    }
}