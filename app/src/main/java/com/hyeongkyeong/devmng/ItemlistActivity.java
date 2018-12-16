package com.hyeongkyeong.devmng;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;

/**
 * Created by munya on 2017-06-11.
 */

public class ItemlistActivity extends AppCompatActivity {
    private static final String TAG = "ItemlistActivity";
    private String this_category;

    private ListItemDataAdapter adapter;
    private ListView DevListView;
    private EditText editSearch;

    private ActionBar bar;
    private Intent intent;

    private String dlgmsg;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG,"onCreate() 호출");
        bar = getSupportActionBar();

        intent = getIntent();
        if (intent != null) {
            this_category = intent.getStringExtra("category");
        }
        bar.setTitle(this_category);
        setContentView(R.layout.itemlist_page);

        printAllItem();

        //TextBox 글자 입력 시 검색
        editSearch = (EditText) findViewById(R.id.EditText_Item_Search);
        editSearch.addTextChangedListener(new TextWatcher() {
                                              @Override
                                              public void onTextChanged(CharSequence s, int start, int before, int count) {
                                                  printSearchedItem();
                                              }
                                              @Override
                                              public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                                  // TODO Auto-generated method stub
                                              }

                                              @Override
                                              public void afterTextChanged(Editable s) {
                                                  // TODO Auto-generated method stub
                                              }
                                          }
        );

        //아이템 하나 클릭했을 때
        DevListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {

                Intent intent = new Intent(getApplicationContext(), Item_Activity.class);

                ListItemData current_item = (ListItemData) adapter.getItem(position);

                intent.putExtra("class", this_category);
                intent.putExtra("id", current_item.mId);             //아이템 아이디
                intent.putExtra("dev_name", current_item.mData[0]);        //아이템 장비명
                intent.putExtra("dev_floor", current_item.mData[1]);        //아이템 장비층수
                intent.putExtra("dev_location", current_item.mData[2]);        //아이템 장비위치
                intent.putExtra("dev_pictures", current_item.mData[3]);   //아이템 장비 사진 리스트
                intent.putExtra("dev_RM", current_item.mData[4]);   //아이템 장비 Risk Manage
                startActivity(intent);
            }
        });


        //추가버튼
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.Add_Item_Button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddItemActivity.class);
                intent.putExtra("category", this_category);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode==1){
            if(data!=null) this_category=data.getStringExtra("category");
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        bar.setTitle(this_category);
        printSearchedItem();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_listitem, menu);
        Log.d(TAG, "######LOG####### onCreateOptionMenu");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
          int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_modify_category_name) {
            modify_category_name();
            return true;
        }
        else if (id == R.id.action_delete_category) {
            AlertDialog dialog = createDialogDeleteCategory();
            dialog.show();
            return true;
        }
        else{
            Log.d(TAG, "###### WARNING ####### 선택한 메뉴와 맞는 메뉴 id를 찾지 못했음");
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean printAllItem(){
        boolean result=false;

        // 리스트뷰 객체 참조
        DevListView = (ListView) findViewById(R.id.ListView_ItemList);
        // 어댑터 객체 생성
        adapter = new ListItemDataAdapter(this);

        //database 열기
        DatabaseHelper.mContext = getApplicationContext();
        DatabaseHelper.openDatabase();


        //모든 데이터 LisbView에 출력하기
        Cursor initial_cursor = DatabaseHelper.queryTableAllData(CategoryManager.getTableName(this_category));
        adapter = DatabaseHelper.CurtorToListItemAdapter(initial_cursor, adapter);
        DevListView.setAdapter(adapter);

        //database 닫기
        DatabaseHelper.closeDatabase();

        return result;
    }

    public boolean printSearchedItem(){
        boolean result=false;
        String strSearch = editSearch.getText().toString();
        String strSearchQuery = strSearch.concat("%");
        //database 열기
        DatabaseHelper.mContext = getApplicationContext();
        DatabaseHelper.openDatabase();
        //검색한 데이터 LisbView에 출력하기
        Cursor cursor = DatabaseHelper.queryTableFindData(CategoryManager.getTableName(this_category), strSearchQuery);
        adapter = DatabaseHelper.CurtorToListItemAdapter(cursor, adapter);
        DevListView.setAdapter(adapter);
        //database 닫기
        DatabaseHelper.closeDatabase();

        return result;
    }

    public void modify_category_name() {

        Intent intent = new Intent(this, ModifyCategoryActivity.class);
        intent.putExtra("category", this_category);
        startActivityForResult(intent,1);

    }

    public boolean delete_category() throws IOException {
        boolean result=false;

        DatabaseHelper.openDatabase();

        if(CategoryManager.deleteCategory(this_category)) {
            result = true;
        }else{
            result = false;
        }
        DatabaseHelper.closeDatabase();

        Toast.makeText(getApplicationContext(), this_category+" 카테고리를 삭제하였습니다.", Toast.LENGTH_LONG).show();
        finish();

        return result;
    }

    private AlertDialog createDialogDeleteCategory(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("삭제");
        builder.setMessage("모든 아이템이 사라집니다. \n정말로 카테고리를 삭제하시겠습니까?");
        builder.setIcon(android.R.drawable.ic_dialog_alert);

        // 예 버튼 설정
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dlgmsg = "예 버튼이 눌렀습니다. " + Integer.toString(whichButton);
                try {
                    delete_category();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        // 아니오 버튼 설정
        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dlgmsg = "아니오 버튼이 눌렸습니다. " + Integer.toString(whichButton);
            }
        });

        // 빌더 객체의 create() 메소드 호출하면 대화상자 객체 생성
        AlertDialog dialog = builder.create();

        return dialog;

    }

}
