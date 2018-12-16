package com.hyeongkyeong.devmng;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.IOException;

/**
 * Created by hkseo on 2017-03-05.
 */

public class AddItemActivity extends AppCompatActivity {
    //protected String calling_class = "";
    private String this_category;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.additem_page);

        Intent intent = getIntent();
        if (intent != null) {
            this_category = intent.getStringExtra("category");
        }

    }
    public void onAddItemButtonClicked(View v) throws IOException {
        String device_data = "";
        String floor_Data = "";
        String Location_Data = "";

        EditText device_in = (EditText)findViewById(R.id.DeviceNameToAdd);
        EditText floor_in = (EditText)findViewById(R.id.FloorToAdd);
        EditText location_in = (EditText)findViewById(R.id.LocationToAdd);

        device_data = device_in.getText().toString();
        floor_Data = floor_in.getText().toString();
        Location_Data = location_in.getText().toString();

        if(device_data.isEmpty()||floor_Data.isEmpty()||Location_Data.isEmpty()){
            Toast.makeText(getApplicationContext(), "장비명 , 층수, 위치는 기본적으로 입력해야 합니다."+Location_Data, Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(getApplicationContext(), "장비명: " + device_data + " / 층수: " + floor_Data + " / 위치: " + Location_Data + " 가 추가 되었습니다.", Toast.LENGTH_LONG).show();
            DatabaseHelper.openDatabase();

            DatabaseHelper.insertDataWithCSV(CategoryManager.getDataFile(this_category), CategoryManager.getTableName(this_category), device_data + "," + floor_Data + "," + Location_Data+",,");

            DatabaseHelper.closeDatabase();
            finish();
        }
    }
}
