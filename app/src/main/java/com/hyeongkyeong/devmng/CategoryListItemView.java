package com.hyeongkyeong.devmng;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by munya on 2017-06-10.
 */


public class CategoryListItemView extends LinearLayout {
    private static final String TAG = "MainListItemView";

    private TextView textdata;

    public CategoryListItemView(Context context, ListItemData data) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.main_listitem, this, true);

        // Set Text 01
        textdata = (TextView) findViewById(R.id.MainItem);
        textdata.setText(data.getData());

    }
    public void setText(String data) {

        textdata.setText(data);

    }
}
