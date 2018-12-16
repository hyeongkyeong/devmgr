package com.hyeongkyeong.devmng;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewAnimator;

import com.github.chrisbanes.photoview.PhotoView;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by hkseo on 2017-03-05.
 */

public class RM_View_Fragment extends Fragment implements View.OnTouchListener{

    private static final String TAG = "RM_View_Activity";


    private static String root_dir = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "DevMgr";
    private static String sub_dir = "";

    private ViewAnimator view_animator;
    private Animation to_left;
    private Animation out_left;
    private Animation to_right;
    private Animation out_right;

    private float downX;
    private float upX;

    private String this_category = "";
    private int  mId = 0;
    private String mName="";
    private String mPicturesList="";

    private String full_path_filename;
    private File picture_file;

    private ArrayList picture_files_name;

    public RM_View_Fragment() {
    }

    public RM_View_Fragment(String calling_class, ListItemData in_Data) {

        this_category = calling_class;
        mId = in_Data.mId;  //장비 아이디
        mName = in_Data.mData[0];   //장비 명
        DatabaseHelper.openDatabase();
        mPicturesList = DatabaseHelper.getRMFromName(CategoryManager.getTableName(this_category), mName);  //장비 사진 리스트
        DatabaseHelper.closeDatabase();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.picture_view, container, false);

        String[] input_pictures = mPicturesList.split("\\|", -1);  //aLine.split(",", -1);
        int pictures_count = input_pictures.length;

        sub_dir = CategoryManager.getRMDirectory(this_category);

        //String[] filename = new String[pictures_count];
        PhotoView[] ImageViews = new PhotoView[pictures_count];
        TextView[] TextViews = new TextView[pictures_count];
        LinearLayout[] LinearLayouts = new LinearLayout[pictures_count];

        LinearLayout.LayoutParams layout_params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);

        picture_files_name = new ArrayList(pictures_count);


        for (int i = 0; i < pictures_count; i++) {
            picture_files_name.add(input_pictures[i]);
            //filename[i] = input_pictures[i];
            ImageViews[i] = new PhotoView(getActivity().getApplicationContext());
            ImageViews[i].setLayoutParams(layout_params);
            TextViews[i] = new TextView(getActivity().getApplicationContext());
            LinearLayouts[i] = new LinearLayout(getActivity().getApplicationContext());
            LinearLayouts[i].setOrientation(LinearLayout.VERTICAL);
        }


        view_animator = (ViewAnimator) rootView.findViewById(R.id.PictureViewAnimation);
        view_animator.setOnTouchListener(this);

        to_left = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.to_left);
        out_left = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.out_left);
        to_right = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.to_right);
        out_right = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.out_right);


        String filename;
        for(int i=0;i<pictures_count;i++) {
            filename = (String)picture_files_name.get(i);
            full_path_filename = sub_dir + File.separator + filename;
            picture_file = new File(full_path_filename);
            try {
                if (picture_file.exists() == true) {
                    Bitmap orgImage = BitmapFactory.decodeFile(full_path_filename);
                    ImageViews[i].setImageBitmap(orgImage);
                    TextViews[i].setText(filename);
                    LinearLayouts[i].addView(ImageViews[i], layout_params);
                    view_animator.addView(LinearLayouts[i]);

                }
            } catch (Exception e) {
                Log.d(TAG, "이미지 읽기 실패: " + e.toString());
            }


        }
        Log.d(TAG, "이미지 읽기 결과: " +picture_files_name.toString());
        return rootView;
    }

    public void slideLeft() {
        view_animator.setInAnimation(to_left);
        view_animator.setOutAnimation(out_left);
        view_animator.showNext();
    }

    public void slideRight() {
        view_animator.setInAnimation(to_right);
        view_animator.setOutAnimation(out_right);
        view_animator.showPrevious();
    }

    public int getCurrentPictureIndex(){
        int index = view_animator.getDisplayedChild();
        return index;
    }

    public String getPictureFromIndex(int index){
        String elemnet;
        elemnet=(String)picture_files_name.get(index);
        return elemnet;
    }

    public boolean removeCurrentPicture(int index){
        boolean result = false;
        String elemnet_input;
        String element_removed;
        elemnet_input=(String)picture_files_name.get(index);
        element_removed = (String)picture_files_name.remove(index);
        if(element_removed.equals(elemnet_input))   result = true;
        return result;
    }

    public String getPictureList(){
        String image_files = "";

        for(int i=0;i<picture_files_name.size();i++){
            if(image_files.equals("")) image_files = (String)picture_files_name.get(i);
            else image_files = image_files + "," + (String)picture_files_name.get(i);
        }
        return image_files;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v != view_animator) return false;
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            downX = event.getX();
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            upX = event.getX();
            if (upX < downX) {  //왼쪽 사진으로 이동
                slideLeft();
            } else if (upX > downX) { // 오른쪽 사진으로 이동
                slideRight();
            }
        }
        return true;
    }



}
