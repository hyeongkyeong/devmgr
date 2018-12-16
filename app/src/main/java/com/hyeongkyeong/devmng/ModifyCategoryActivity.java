package com.hyeongkyeong.devmng;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

/**
 * Created by hkseo on 2017-03-05.
 */

public class ModifyCategoryActivity extends AppCompatActivity {
    protected String this_category = "";
    private EditText EditText_category_name;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modifycategory_page);

        intent = getIntent();
        if (intent != null) {
            this_category = intent.getStringExtra("category");
        }

        EditText_category_name = (EditText)findViewById(R.id.category_name_to_modify);
        EditText_category_name.setText(this_category);



    }
    public void onModifyCategoryButtonClicked(View v) throws IOException {

        String category_name_to_modify;
        EditText edittext_category_name = (EditText)findViewById(R.id.category_name_to_modify);

        category_name_to_modify = edittext_category_name.getText().toString();

        DatabaseHelper.openDatabase();
        CategoryManager.modifyCategory(this_category,category_name_to_modify);
        DatabaseHelper.closeDatabase();
        Toast.makeText(getApplicationContext(), category_name_to_modify + "로 변경 되었습니다.", Toast.LENGTH_LONG).show();

        //Intent intent = new Intent(this, ItemlistActivity.class);  //your class
        Intent result_data = getIntent();
        result_data.putExtra("result", "modify");
        result_data.putExtra("category", category_name_to_modify);
        setResult(1,result_data);
        finish();

    }
}
