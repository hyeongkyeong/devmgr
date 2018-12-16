package com.hyeongkyeong.devmng;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private  CategoryListAdapter adapter;
    private ListView CategoryListView;
    private EditText editSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 리스트뷰 객체 참조
        CategoryListView = (ListView) findViewById(R.id.main_listview);
        // 어댑터 객체 생성
        adapter = new CategoryListAdapter(this);

        //모든 데이터 LisbView에 출력하기

        //database 열기
        DatabaseHelper.mContext = getApplicationContext();
        DatabaseHelper.openDatabase();

        //모든 데이터 LisbView에 출력하기
        Cursor cursor = DatabaseHelper.queryTableAllData(DatabaseHelper.category_table_name);
        int recordCount = cursor.getCount();
        adapter.clear();

        int nameCol = cursor.getColumnIndex("name");

        for (int i = 0; i < recordCount; i++) {
            cursor.moveToNext();
            String nameData = cursor.getString(nameCol);
            adapter.addItem(new ListItemData(nameData));
        }
        adapter.addItem(new ListItemData("+"));
        CategoryListView.setAdapter(adapter);

        //database 닫기
        DatabaseHelper.closeDatabase();

        //아이템 하나 클릭했을 때
        CategoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                Log.d(TAG,"###### CategoryListView.setOnItemClickListener 호출");

                if(position==parent.getLastVisiblePosition()){
                    Intent intent = new Intent(getApplicationContext(), AddCategoryActivity.class);
                    startActivity(intent);
                }else {

                    Intent intent = new Intent(getApplicationContext(), ItemlistActivity.class);
                    ListItemData category_data = (ListItemData) adapter.getItem(position);
                    intent.putExtra("category", category_data.getData());
                    startActivity(intent);
                }
            }
        });

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Intent intent = new Intent(MainActivity.this, MainActivity.class);  //your class
        startActivity(intent);
        finish();
    }


    public void onConfigurationButtonClicked(View v){
        Toast.makeText(getApplicationContext(), "설정 버튼을 클릭했습니다.", Toast.LENGTH_LONG).show();
    }
}
