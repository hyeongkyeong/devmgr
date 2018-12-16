package com.hyeongkyeong.devmng;

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

public class AddCategoryActivity extends AppCompatActivity {
    protected String calling_class = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addcategory_page);

        Intent intent = getIntent();
        if (intent != null) {
            calling_class = intent.getStringExtra("class");
        }

    }
    public void onAddCategoryButtonClicked(View v) throws IOException {

        String category_name;
        EditText edittext_category_name = (EditText)findViewById(R.id.category_name_to_add);

        category_name = edittext_category_name.getText().toString();

        DatabaseHelper.openDatabase();
        CategoryManager.insertCategory(category_name);
        CategoryManager.updateCategoryFile();
        DatabaseHelper.closeDatabase();
        Toast.makeText(getApplicationContext(), category_name + "가 추가 되었습니다.", Toast.LENGTH_LONG).show();
        finish();
    }
}
