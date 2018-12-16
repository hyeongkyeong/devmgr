package com.hyeongkyeong.devmng;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by hkseo on 2017-03-12.
 */

public class Item_Activity extends AppCompatActivity {

    private static final String TAG = "Item_Activity";

    private String this_item;
    private String this_category = "";
    private ListItemData current_item;
    private ActionBar bar;

    Picture_View_Fragment picture_fragment;
    RM_View_Fragment rm_fragment;
    History_View_Fragment history_fragment;

    public static final int REQUEST_MODIFY_ITEM = 1000;
    public static final int REQUEST_IMAGE_CAPTURE = 1001;
    public static final int REQUEST_RM_CAPTURE = 1002;

    private static String temp_imagefile="";

    private int position=0;
    private String msg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"onCreate()");
        Intent intent = getIntent();
        if (intent != null) {
            this_category = intent.getStringExtra("class");
            current_item = new ListItemData(5);
            current_item.mId = intent.getIntExtra("id", 0);
            current_item.mData[0] = intent.getStringExtra("dev_name");
            current_item.mData[1] = intent.getStringExtra("dev_floor");
            current_item.mData[2] = intent.getStringExtra("dev_location");
            current_item.mData[3] = intent.getStringExtra("dev_pictures");
            current_item.mData[4] = intent.getStringExtra("dev_RM");
        }
        this_item=current_item.mData[0];
        bar = getSupportActionBar();
        bar.setTitle(this_item);

        setContentView(R.layout.item_page);

        TextView floor_info = (TextView)findViewById(R.id.item_view_floor);
        TextView location_info = (TextView)findViewById(R.id.item_view_location);
        floor_info.setText("층수: "+current_item.mData[1]);
        location_info.setText("장소: "+current_item.mData[2]);

        picture_fragment = new Picture_View_Fragment(this_category, current_item);
        rm_fragment = new RM_View_Fragment(this_category, current_item);
        history_fragment = new History_View_Fragment(this_category, current_item);

        getSupportFragmentManager().beginTransaction().replace(R.id.container, picture_fragment).commit();

        TabLayout tabs = (TabLayout) findViewById(R.id.tabs);
        tabs.addTab(tabs.newTab().setText("사진"));
        tabs.addTab(tabs.newTab().setText("e-RM"));
        tabs.addTab(tabs.newTab().setText("메모"));

        tabs.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                position = tab.getPosition();
                Log.d(TAG, "선택된 탭 : " + position);

                Fragment selected = null;
                if (position == 0) {
                    selected = picture_fragment;
                } else if (position == 1) {
                    selected = rm_fragment;
                } else if (position == 2) {
                    selected = history_fragment;
                }

                getSupportFragmentManager().beginTransaction().replace(R.id.container, selected).commit();

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }


    @Override
    protected void onRestart() {

        // TODO Auto-generated method stub
        super.onRestart();
        //bar.setTitle(this_item);
        //Intent intent = getIntent(); //your class
        Intent intent = getIntent();
        startActivity(intent);
        finish();
        Log.d(TAG,"onRestart()");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_item, menu);
        Log.d(TAG, "######LOG####### onCreateOptionMenu");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add_picture) {
            add_picture();
            return true;
        }
        else if (id == R.id.action_add_rm) {
            add_rm();
            return true;
        }
        else if (id == R.id.action_modify_item) {
            modify_item();
            return true;
        }
        else if (id == R.id.action_delete_item) {
            AlertDialog dialog = createDialogDeleteItem();
            dialog.show();
            return true;
        }
        else{
            Log.d(TAG, "###### WARNING ####### 선택한 메뉴와 맞는 id를 찾지 못했음");
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            try {
                DatabaseHelper.openDatabase();
                DatabaseHelper.addPictureData(this_category, CategoryManager.getTableName(this_category),current_item.mId,temp_imagefile);
                DatabaseHelper.closeDatabase();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if (requestCode == REQUEST_RM_CAPTURE && resultCode == RESULT_OK) {
            try {
                DatabaseHelper.openDatabase();
                DatabaseHelper.addRMData(this_category, CategoryManager.getTableName(this_category),current_item.mId,temp_imagefile);
                DatabaseHelper.closeDatabase();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if((requestCode==REQUEST_MODIFY_ITEM) && (resultCode==1)){
            finish();
        }
        else{
            Log.d(TAG, "onActivityResult(), requestCode is else");
        }
    }

    private void modify_item(){
        Intent intent = new Intent(this, ModifyItemActivity.class);

        intent.putExtra("class", this_category);
        intent.putExtra("id", current_item.mId);             //아이템 아이디
        intent.putExtra("dev_name", current_item.mData[0]);        //아이템 장비명
        intent.putExtra("dev_floor", current_item.mData[1]);        //아이템 장비층수
        intent.putExtra("dev_location", current_item.mData[2]);        //아이템 장비위치
        intent.putExtra("dev_pictures", current_item.mData[3]);   //아이템 장비 사진 리스트
        intent.putExtra("dev_RM", current_item.mData[4]);   //아이템 장비 사진 리스트
        startActivityForResult(intent,REQUEST_MODIFY_ITEM);

    }

    private void delete_item(){
        try {
            DatabaseHelper.deleteData(CategoryManager.getDataFile(this_category), CategoryManager.getTableName(this_category), current_item.mId);
            Toast.makeText(getApplicationContext(), current_item.mData[0]+" 장비를 삭제하였습니다.", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
        }

        finish();
    }

    private void add_picture(){

        DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        String datetime = df.format(Calendar.getInstance().getTime());

        String imageFileName = current_item.mData[0]+"_"+datetime+".jpg";
        temp_imagefile = imageFileName;

        File file = new File(CategoryManager.getPictureDirectory(this_category)+File.separator+imageFileName);
        Uri photoURI =  FileProvider.getUriForFile(Item_Activity.this,BuildConfig.APPLICATION_ID + ".provider",file);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void add_rm(){

        DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        String datetime = df.format(Calendar.getInstance().getTime());

        String imageFileName = current_item.mData[0]+"_"+datetime+".jpg";
        temp_imagefile = imageFileName;

        File file = new File(CategoryManager.getRMDirectory(this_category)+File.separator+imageFileName);
        Uri photoURI =  FileProvider.getUriForFile(Item_Activity.this,BuildConfig.APPLICATION_ID + ".provider",file);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_RM_CAPTURE);
        }
    }


    public void onLeftArrowClicked(View v){
        if(position==0) {
            picture_fragment.slideRight();
        }
        else if(position==1){
            rm_fragment.slideRight();
        }
        else{
            Log.d(TAG,"onLdetArrowClicked(), selected 에 해당하는 내용 없음");
        }
    }

    public void onRightArrowClicked(View v){
        if(position==0) {
            picture_fragment.slideLeft();
        }
        else if(position==1){
            rm_fragment.slideLeft();
        }
        else{
            Log.d(TAG,"onRightArrowClicked(), selected 에 해당하는 내용 없음");
        }
    }
    public void onAddPictureButtonClicked(View v){
        if(position==0) {
            add_picture();
        }
        else if(position==1){
            add_rm();
        }
        else{
            Log.d(TAG,"onAddPictureButtonClicked(), selected 에 해당하는 내용 없음");
        }

    }
    public void onDeletePictureButtonClicked(View v) {
        AlertDialog dialog = createDialogDeletePicture();
        dialog.show();
    }
    private void deletdPicture(){
        int index = 0;
        String filename_to_remove = "";

        if (position == 0) {
            index = picture_fragment.getCurrentPictureIndex();
            filename_to_remove = picture_fragment.getPictureFromIndex(index);
            File file = new File(CategoryManager.getPictureDirectory(this_category) + File.separator + filename_to_remove);
            picture_fragment.removeCurrentPicture(index);
            file.delete();
            String image_files = picture_fragment.getPictureList();
            Log.d(TAG, "onDeletePictureButtonClicked(), image_files: " + image_files.toString());
            try {
                DatabaseHelper.openDatabase();
                DatabaseHelper.updatePictureData(this_category, CategoryManager.getTableName(this_category), current_item.mId, image_files);
                DatabaseHelper.closeDatabase();
            } catch (IOException e) {
                e.printStackTrace();
            }
            onRestart();
            Toast.makeText(getApplicationContext(), filename_to_remove + "사진을 삭제하였습니다.", Toast.LENGTH_LONG).show();

        } else if (position == 1) {
            index = rm_fragment.getCurrentPictureIndex();
            filename_to_remove = rm_fragment.getPictureFromIndex(index);
            File file = new File(CategoryManager.getRMDirectory(this_category) + File.separator + filename_to_remove);
            rm_fragment.removeCurrentPicture(index);
            file.delete();
            String image_files = rm_fragment.getPictureList();
            Log.d(TAG, "onDeletePictureButtonClicked(), image_files: " + image_files.toString());
            try {
                DatabaseHelper.openDatabase();
                DatabaseHelper.updateRMData(this_category, CategoryManager.getTableName(this_category), current_item.mId, image_files);
                DatabaseHelper.closeDatabase();

            } catch (IOException e) {
                e.printStackTrace();
            }
            onRestart();
            Toast.makeText(getApplicationContext(), filename_to_remove + "사진을 삭제하였습니다.", Toast.LENGTH_LONG).show();

        } else {
            Log.d(TAG, "onDeletePictureButtonClicked(), selected 에 해당하는 내용 없음");
        }
    }


    private AlertDialog createDialogDeleteItem(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("삭제");
        builder.setMessage("이 아이템을 삭제하시겠습니까?");
        builder.setIcon(android.R.drawable.ic_dialog_alert);

        // 예 버튼 설정
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                msg = "예 버튼이 눌렀습니다. " + Integer.toString(whichButton);
                delete_item();
            }
        });

        // 아니오 버튼 설정
        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                msg = "아니오 버튼이 눌렸습니다. " + Integer.toString(whichButton);
            }
        });

        // 빌더 객체의 create() 메소드 호출하면 대화상자 객체 생성
        AlertDialog dialog = builder.create();

        return dialog;

    }

    private AlertDialog createDialogDeletePicture(){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle("삭제");
            builder.setMessage("이 사진을 삭제하시겠습니까?");
            builder.setIcon(android.R.drawable.ic_dialog_alert);

            // 예 버튼 설정
            builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    msg = "예 버튼이 눌렀습니다. " + Integer.toString(whichButton);
                    deletdPicture();
                }
            });

            // 아니오 버튼 설정
            builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    msg = "아니오 버튼이 눌렸습니다. " + Integer.toString(whichButton);
                }
            });

            // 빌더 객체의 create() 메소드 호출하면 대화상자 객체 생성
            AlertDialog dialog = builder.create();

            return dialog;

    }
}
