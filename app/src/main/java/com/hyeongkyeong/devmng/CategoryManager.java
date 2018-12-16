package com.hyeongkyeong.devmng;

import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hkseo on 2017-03-18.
 */


public class CategoryManager {
    private static final String TAG = "CategoryManager";

    //createCategoryTable()
    //
    public static boolean createCategoryTable() throws IOException {
        boolean result = false;

            DatabaseHelper.openDatabase();
            if(DatabaseHelper.db!=null){
            try {
                DatabaseHelper.db.execSQL("DROP TABLE IF EXISTS " + DatabaseHelper.category_table_name );
                DatabaseHelper.db.execSQL("CREATE TABLE IF NOT EXISTS " + DatabaseHelper.category_table_name + "("
                        + " _id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
                        + " name TEXT NOT NULL, "
                        + " tablename TEXT, "
                        + " datafile TEXT, "
                        +"UNIQUE(_id, name))");

                Log.d(TAG,"### success ### "+DatabaseHelper.category_table_name+" 테이블 잘 생성됨.");
            } catch (SQLiteException ex) {
                Log.d(TAG,"### error ### "+DatabaseHelper.category_table_name+" 테이블 생성 중 Exception 발생:"+ex.getMessage());
                result = false;
            }

            File f_category_file = new File(DatabaseHelper.category_file);
            if(f_category_file.exists()) {
                try {
                    FileInputStream fIn = new FileInputStream(f_category_file);
                    BufferedReader myReader = new BufferedReader(new InputStreamReader(fIn, "euc-kr"));
                    String aLine = "";

                    List<String> category_data = new ArrayList<String>();
                    int index=0;
                    while ((aLine = myReader.readLine()) != null) {
                        if(!aLine.equals("")) {
                            category_data.add(aLine);
                            index++;
                        }
                    }

                    IntroActivity.setProgress_fulltime(index);

                    int recordCount = 0;
                    for(int i=0;i<category_data.size();i++){
                        boolean isInserted = insertCategory(category_data.get(i));
                        if (isInserted) {
                            IntroActivity.updateProgress(i);
                            recordCount++;
                        }
                    }

                    myReader.close();
                    System.out.println("### log ### category.dat 읽기 성공(count: " + recordCount+")");
                    result = true;

                } catch (Exception ex) {
                    ex.printStackTrace();
                    Log.d(TAG, "### error ### Create Table Exception : " + ex.toString());
                    result = false;
                }
            }
            else{
                Log.d(TAG, "### error ### " + "category.dat 파일이 없습니다. 빈 파일을 생성합니다.");
                f_category_file.createNewFile();
                FileOutputStream fileOutputStream = new FileOutputStream(f_category_file);
                OutputStreamWriter OutputStreamWriter = new OutputStreamWriter(fileOutputStream, "euc-kr");
                BufferedWriter bufferedWriter = new BufferedWriter(OutputStreamWriter);
                bufferedWriter.write("\n");
                bufferedWriter.close();
                result = false;
            }

            DatabaseHelper.closeDatabase();
            result = true;
        }else{
            result = false;
        }
        return result;

    }

    //insertCategory
    //카테고리가 생성되면 데이터파일명과 DB 테이블명을 정의한다.
    //카테고리 테이블에 카테고리 정보(카테고리이름, 데이터파일명, 장비 테이블명)을 등록한다.
    //장비 테이블을 생성한다.(이미 csv 데이터파일이 존재하는 경우 해당 데이터를 장비 테이블에 등록한다.)
    public static boolean insertCategory(String name){
        boolean result = false;
        String mDatafile = DatabaseHelper.app_root_dir + File.separator + name + File.separator + "data.csv";
        String mTablename = name.toUpperCase();
        mTablename = mTablename.replace(' ','_');

        if(makeFileStructure(name, mTablename, mDatafile)){
            result = true;
        }else{
            result = false;
        }

        if ((name != null)&& (mTablename!= null)&&(mDatafile != null) ){
            DatabaseHelper.db.execSQL( "INSERT INTO " + DatabaseHelper.category_table_name + "(name, tablename, datafile) VALUES (" +
                    "'" + name + "'," +
                    "'" + mTablename + "'," +
                    "'" + mDatafile + "')");
            Log.d(TAG, "[" + name+", " + mTablename+ ", " + mDatafile +"]");
            result = true;
        } else {
            Log.d(TAG, "### error ### 입력 category 이름이 유효하지 않습니다. -> name: "+ name+", tablename: "+mTablename+", datafile: "+mDatafile);
            result = false;
        }

        DatabaseHelper.createTableFromCSV(mDatafile, mTablename);

        return result;
    }

    //makeFileStructure
    //카테고리가 생성되면 폴더구조와 데이터파일을 생성해주는 메소드
    //데이터 파일이 이미 있으면, 새로 생성하지 않는다.
    public static boolean makeFileStructure(String name, String tablename, String datafile){
        boolean result = false;
        File f_category = new File( DatabaseHelper.app_root_dir + File.separator + name);
        File f_picture = new File( DatabaseHelper.app_root_dir + File.separator + name + File.separator + "Pictures");
        File f_rm = new File( DatabaseHelper.app_root_dir + File.separator + name + File.separator + "RM");
        File f_history = new File( DatabaseHelper.app_root_dir + File.separator + name + File.separator + "History");
        File f_datafile = new File(datafile);
        if(f_category.exists()!=true)f_category.mkdir();
        if(f_picture.exists()!=true)f_picture.mkdir();
        if(f_rm.exists()!=true)f_rm.mkdir();
        if(f_history.exists()!=true)f_history.mkdir();
        if(f_datafile.exists()!=true){
            try {
                f_datafile.createNewFile();
                FileOutputStream fileOutputStream = new FileOutputStream(f_datafile);
                OutputStreamWriter OutputStreamWriter = new OutputStreamWriter(fileOutputStream, "euc-kr");
                BufferedWriter bufferedWriter = new BufferedWriter(OutputStreamWriter);
                bufferedWriter.write("DeviceName,Floor,Location,Pictures,RM,History");
                bufferedWriter.write("\n");
                bufferedWriter.close();
                result = true;
            } catch (Exception ex) {
                ex.printStackTrace();
                Log.d(TAG, "### error ### Category 파일 구조 생성 Exception : " + ex.toString());
                result = false;
            }
        }
        return result;
    }

    //UpdateCategoryFile
    //DB에서 카테고리 데이터를 읽어와 category.dat 파일에 쓰는 메소드
    public static boolean updateCategoryFile() throws IOException {
        boolean result = false;
        File f_category_file = new File(DatabaseHelper.category_file);
        if(f_category_file.exists()) {
            FileOutputStream fileOutputStream = new FileOutputStream(f_category_file);
            OutputStreamWriter OutputStreamWriter = new OutputStreamWriter(fileOutputStream, "euc-kr");
            BufferedWriter bufferedWriter = new BufferedWriter(OutputStreamWriter);

            Cursor cursor = DatabaseHelper.db.rawQuery("SELECT name FROM "+DatabaseHelper.category_table_name, null);
            int recordCount = cursor.getCount();
            int nameCol = cursor.getColumnIndex("name");

            for(int i=0;i<recordCount;i++) {
                cursor.moveToNext();
                String nameData = cursor.getString(nameCol);
                bufferedWriter.write(nameData);
                bufferedWriter.write("\n");
            }
            bufferedWriter.close();
        }
        return result;
    }


    //카테고리 삭제하기
    public static boolean deleteCategory(String name_to_delete) throws IOException {
        boolean result = false;

        String aSQL = "DELETE FROM " + DatabaseHelper.category_table_name + " WHERE name = '"+name_to_delete+"'";

        if(DatabaseHelper.db==null) DatabaseHelper.openDatabase();

        try {
            DatabaseHelper.db.execSQL(aSQL);
            Log.d(TAG,"### success ### 카테고리 삭제됨. name: "+name_to_delete);
            result = true;
        } catch (SQLiteException ex){
            Log.d(TAG,"### error ### 카테고리 삭제 중 Exception 발생:"+ex.getMessage());
            result = false;
        }

        //category.dat 파일에 카테고리 데이터 업데이트 하기
        updateCategoryFile();

        DatabaseHelper.closeDatabase();

        return result;
    }

    //카테고리 수정하기
    public static boolean modifyCategory(String before_category_name, String after_category_name) throws IOException {
        boolean result = false;

        String before_tablename=getTableName(before_category_name);
        String before_datafile=getDataFile(before_category_name);

        String after_datafile = DatabaseHelper.app_root_dir + File.separator + after_category_name + File.separator + "data.csv";
        String after_tablename = after_category_name.toUpperCase();
        after_tablename = after_tablename.replace(' ','_');

        String aSQL = "UPDATE " + DatabaseHelper.category_table_name
                + " SET name='"+ after_category_name + "',"
                + "tablename='"+ after_tablename + "',"
                + "datafile='"+ after_datafile + "'"
                + " WHERE name='" + before_category_name + "'";

        try {
            DatabaseHelper.db.execSQL(aSQL);
            Log.d(TAG,"### success ### 카테고리 수정됨. 변경 후 카테고리명: "+after_category_name);
            Log.d(TAG,"### success ### 카테고리 수정됨. 변경 후 테이블이름: "+after_tablename);
            Log.d(TAG,"### success ### 카테고리 수정됨. 변경 후 데이터파일이름: "+after_datafile);
            result = true;
        } catch (SQLiteException ex) {
            Log.d(TAG,"### error ### 카테고리 DB 수정 중 Exception 발생:"+ex.getMessage());
            result = false;
        }

        //category.dat 파일에 카테고리 데이터 업데이트 하기
        updateCategoryFile();

        File f_before_datafile = new File(before_datafile);
        File f_after_datafile = new File(after_datafile);
        f_before_datafile.renameTo(f_after_datafile);

        File f_before_category_directory = new File(DatabaseHelper.app_root_dir + File.separator + before_category_name);
        File f_after_category_directory = new File(DatabaseHelper.app_root_dir + File.separator + after_category_name);
        f_before_category_directory.renameTo(f_after_category_directory);



        try {
            DatabaseHelper.db.execSQL("ALTER TABLE " + before_tablename + " RENAME TO "+after_category_name);
            result = true;
        } catch (SQLiteException ex) {
            Log.d(TAG,"### error ### 장비 테이블명 변경 중 Exception 발생:"+ex.getMessage());
            result = false;
        }

        return result;
    }



    public static String getDataFile(String name){
        String datafile = "";
        String aSQL = "SELECT datafile"+" FROM "+DatabaseHelper.category_table_name+" where name='"+name+"'";

        if(DatabaseHelper.db==null) DatabaseHelper.openDatabase();

        Cursor cursor = DatabaseHelper.db.rawQuery(aSQL, null);
        cursor.moveToFirst();
        int index=cursor.getColumnIndex("datafile");
        datafile = cursor.getString(index);

        //DatabaseHelper.closeDatabase();

        return (datafile);
    }


    public static String getTableName(String name){
        String tablename = "";
        String aSQL = "SELECT tablename"+" FROM "+DatabaseHelper.category_table_name+" where name='"+name+"'";

        if(DatabaseHelper.db==null) DatabaseHelper.openDatabase();

        Cursor cursor = DatabaseHelper.db.rawQuery(aSQL, null);

        cursor.moveToFirst();
        int index=cursor.getColumnIndex("tablename");
        tablename = cursor.getString(index);

        //DatabaseHelper.closeDatabase();

        return (tablename);

    }


    public static String getPictureDirectory(String name){
        String PictureDirectory="";
        PictureDirectory=DatabaseHelper.app_root_dir + File.separator + name + File.separator + "Pictures";
        return (PictureDirectory);
    }


    public static String getRMDirectory(String name){
        String PictureDirectory="";
        PictureDirectory=DatabaseHelper.app_root_dir + File.separator + name + File.separator + "RM";
        return (PictureDirectory);
    }


    public static String getHistoryDirectory(String name){
        String PictureDirectory="";
        PictureDirectory=DatabaseHelper.app_root_dir + File.separator + name + File.separator + "History";
        return (PictureDirectory);
    }

}

