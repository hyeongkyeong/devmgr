package com.hyeongkyeong.devmng;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * Created by hkseo on 2017-03-05.
 */

public class History_View_Fragment extends Fragment {

    private static final String TAG = "History_View_Activity";

    ViewGroup rootView;


    private String mClass = "";
    private String mClassForder="";
    private ListItemData current_item;
    private int  mId = 0;
    private String mName="";

    private String memo_filename="";

    public History_View_Fragment() {
    }

    public  History_View_Fragment(String calling_class, ListItemData in_Data){
        mClass = calling_class;
        current_item = in_Data;
        mId = current_item.mId;  //장비 아이디
        mName = current_item.mData[0];   //장비 명

        memo_filename = DatabaseHelper.app_root_dir + File.separator + mClass + File.separator + "history"+ File.separator + mName + ".txt";
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = (ViewGroup)inflater.inflate(R.layout.history_view, container, false);

        EditText EditText_memo = (EditText) rootView.findViewById(R.id.memo_edittext);
        Button SaveButton = (Button) rootView.findViewById(R.id.Save_History_Button);

        try {
            File fTXT = new File(memo_filename);
            if (fTXT.exists() == true) {

                FileInputStream file_input_stream = new FileInputStream(fTXT);
                InputStreamReader input_stream_reader = new InputStreamReader(file_input_stream, "euc-kr");
                BufferedReader buffered_reader = new BufferedReader(input_stream_reader);

                String aLine;

                while ((aLine = buffered_reader.readLine()) != null) {
                    EditText_memo.append(aLine);
                    EditText_memo.append("\n");
                }

                buffered_reader.close();
            }
        }catch (IOException e) {
            e.printStackTrace();
        }

        SaveButton.setOnClickListener(
                    new Button.OnClickListener(){
                        public void onClick(View v){
                            EditText EditText_memo = (EditText) rootView.findViewById(R.id.memo_edittext);
                            String contents = EditText_memo.getText().toString();

                            try {

                                File fTXT = new File(memo_filename);
                                if (fTXT.exists() == true) {
                                    fTXT.delete();

                                }
                                fTXT.createNewFile();

                                FileOutputStream fileOutputStream = new FileOutputStream(fTXT);
                                OutputStreamWriter OutputStreamWriter = new OutputStreamWriter(fileOutputStream, "euc-kr");
                                BufferedWriter bufferedWriter = new BufferedWriter(OutputStreamWriter);

                                bufferedWriter.write(contents);

                                bufferedWriter.close();

                                Toast.makeText(getActivity().getApplicationContext(), "저장되었습니다.", Toast.LENGTH_LONG).show();


                            } catch (IOException e) {
                                e.printStackTrace();
                            }


                        }
                    }
            );

            return rootView;
    }

}
