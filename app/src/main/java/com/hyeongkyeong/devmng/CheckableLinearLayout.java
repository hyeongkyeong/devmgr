package com.hyeongkyeong.devmng;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.LinearLayout;

/**
 * Created by hkseo on 2017-03-08.
 */

public class CheckableLinearLayout extends LinearLayout implements Checkable{
    private static final int[] CHECKED_STATE_SET = {android.R.attr.state_checked};
    private boolean mChecked = false;
    //private static CheckBox listitem_checkbox;
    public CheckableLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        // mIsChecked = false ;
    }

    @Override
    public boolean isChecked() {
        CheckBox cb = (CheckBox) findViewById(R.id.ListItemcheckBox) ;
        //Log.d("CheckableLinearLayout","cb.isChecked()"+cb.isChecked());
        return cb.isChecked() ;
        // return mIsChecked ;

    }

    @Override
    public void toggle() {
        CheckBox cb = (CheckBox) findViewById(R.id.ListItemcheckBox) ;

        setChecked(cb.isChecked() ? false : true) ;
        // setChecked(mIsChecked ? false : true) ;
    }

    @Override
    public void setChecked(boolean checked) {
        CheckBox cb = (CheckBox) findViewById(R.id.ListItemcheckBox) ;

        if (cb.isChecked() != checked) {
            cb.setChecked(checked) ;
        }

        // CheckBox 가 아닌 View의 상태 변경.
    }

    @Override
    public int[] onCreateDrawableState(int extraSpace) {
        final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (isChecked()) {
            mergeDrawableStates(drawableState, CHECKED_STATE_SET);
        }
        return drawableState;
    }


}