package com.hyeongkyeong.devmng;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 하나의 List 아이뎀을 그려주는 클래스입니다.
 */
public class ListItemDataView extends LinearLayout {

    private static final String TAG = "ListItemView";

    private TextView TextView01;
    private TextView TextView02;
    private TextView TextView03;
    public ListItemDataView(Context context, ListItemData aItem) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.listitem, this, true);

        // Set Text 01
        TextView01 = (TextView) findViewById(R.id.NameText);
        TextView01.setText(aItem.getData(0));

        // Set Text 02
        TextView02 = (TextView) findViewById(R.id.FloorText);
        TextView02.setText(aItem.getData(1));

        // Set Text 03
        TextView03 = (TextView) findViewById(R.id.LocationText);
        TextView03.setText(aItem.getData(2));

    }
    public void setText(int index, String data) {
        if (index == 0) {
            TextView01.setText(data);
        } else if (index == 1) {
            TextView02.setText(data);
        } else if (index == 2) {
            TextView03.setText(data);
        } else {
            throw new IllegalArgumentException();
        }
    }
}
