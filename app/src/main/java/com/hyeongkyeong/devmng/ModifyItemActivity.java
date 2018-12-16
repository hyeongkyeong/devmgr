package com.hyeongkyeong.devmng;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

/**
 * Created by hkseo on 2017-03-05.
 */

public class ModifyItemActivity extends AppCompatActivity {

    private static final String TAG = "ModifyItemActivity";

    protected String calling_class = "";
    protected ListItemData current_item;

    EditText device_in;
    EditText floor_in;
    EditText location_in;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modifiy_item_page);

        Intent intent = getIntent();
        if (intent != null) {
            calling_class = intent.getStringExtra("class");
            current_item = new ListItemData(4);
            current_item.mId = intent.getIntExtra("id", 0);
            current_item.mData[0] = intent.getStringExtra("dev_name");
            current_item.mData[1] = intent.getStringExtra("dev_floor");
            current_item.mData[2] = intent.getStringExtra("dev_location");
        }

        device_in = (EditText)findViewById(R.id.DeviceNameToModify);
        floor_in = (EditText)findViewById(R.id.FloorToModify);
        location_in = (EditText)findViewById(R.id.LocationToModify);

        device_in.setText(current_item.mData[0]);
        floor_in.setText(current_item.mData[1]);
        location_in.setText(current_item.mData[2]);
    }
    public void onModifyItemButtonClicked(View v) throws IOException {
        String name_to_modify;
        String floor_to_modify;
        String Location_to_modify;

        name_to_modify = device_in.getText().toString();
        floor_to_modify = floor_in.getText().toString();
        Location_to_modify = location_in.getText().toString();

        if(name_to_modify.isEmpty()||floor_to_modify.isEmpty()||Location_to_modify.isEmpty()){
            Toast.makeText(getApplicationContext(), "장비명 , 층수, 위치는 기본적으로 입력해야 합니다.", Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(getApplicationContext(), "장비명: " + name_to_modify + "\n층수: " + floor_to_modify + "\n위치: " + Location_to_modify + "\n으로 수정 되었습니다.", Toast.LENGTH_LONG).show();

            DatabaseHelper.openDatabase();

            ListItemData item_data = new ListItemData(current_item.mId, name_to_modify, floor_to_modify, Location_to_modify,"","");
            DatabaseHelper.updateData(CategoryManager.getDataFile(calling_class),CategoryManager.getTableName(calling_class),item_data);

            DatabaseHelper.closeDatabase();

            Intent result_data = getIntent();
            result_data.putExtra("result", "modify");
            result_data.putExtra("item_name", name_to_modify);
            result_data.putExtra("item_floor", floor_to_modify);
            result_data.putExtra("item_location", Location_to_modify);
            setResult(1,result_data);
            finish();


        }
    }
    public void onModifyCancelButtonClicked(View v){
        finish();
    }
}
