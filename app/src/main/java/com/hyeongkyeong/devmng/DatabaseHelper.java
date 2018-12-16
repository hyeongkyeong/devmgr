package com.hyeongkyeong.devmng;

/**
 * Created by hkseo on 2017-02-19.
 */

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class DatabaseHelper{

    private static final String TAG = "DatabaseHelper";

    public static Context mContext;
    public static SQLiteDatabase db;
    public static String category_table_name="MAIN_CATEGORY";
    public static String app_root_dir = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "DevMgr";
    public static String database_file = app_root_dir+ File.separator+"DevMgr.db";
    public static String category_file = app_root_dir+ File.separator+ "category.txt";

    public DatabaseHelper() {
    }


    public static void openDatabase() {

        try {
            db = SQLiteDatabase.openDatabase(database_file, null, SQLiteDatabase.OPEN_READWRITE+SQLiteDatabase.CREATE_IF_NECESSARY);
            Log.d(TAG,"### success ### Database가 open되었음.");
        } catch (SQLiteException ex) {
            Log.d(TAG,"### error ### Database가 open 중 Exception 발생: "+ex.getMessage());
        }
    }

    public static void closeDatabase() {
        if(db!=null) {
            try {
                // close database
                db.close();
                db = null;
                Log.d(TAG, "### success ### Database가 close 되었음.:");
            } catch (Exception ext) {
                ext.printStackTrace();
                Log.d(TAG, "### error ### Database가 close 중 Exception 발생:" + ext.toString());
            }
        }
    }



    public static void createTable(String table_name) {

        //if(db==null) openDatabase();
        if(db!=null) {
            try {
                db.execSQL("DROP TABLE IF EXISTS " + table_name);
                db.execSQL("CREATE TABLE " + table_name + "("
                        + " _id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
                        + " name TEXT NOT NULL, "
                        + " floor TEXT, "
                        + " location TEXT, "
                        + " dev_picture TEXT, "
                        + " dev_RM TEXT, "
                        + "UNIQUE(_id, name))");

                Log.d(TAG, "### success ### Table(" + table_name + ") 잘 생성됨.");
            } catch (SQLiteException ex) {
                Log.d(TAG, "### error ### Table(" + table_name + ") 생성 중 Exception 발생:" + ex.getMessage());
            }
        }
        //closeDatabase();
    }



    public static boolean createTableFromCSV(String CSV_file_name, String table_name) {
        boolean result = false;
        String headline="";

        File fCSV = new File(CSV_file_name);
        if(fCSV.exists()==true) {
            try {
                FileInputStream fIn = new FileInputStream(fCSV);
                BufferedReader myReader = new BufferedReader(new InputStreamReader(fIn, "euc-kr"));
                String aLine = "";

                createTable(table_name);

                int count = 0;
                int recordCount = 0;
                headline = myReader.readLine();
                if(db==null) openDatabase();
                while ((aLine = myReader.readLine()) != null) {
                    if(!aLine.equals("")) {
                        System.out.print("### Log ### Data " + count + ":");
                        boolean isInserted = insertData(table_name, aLine);
                        if (isInserted) {
                            recordCount++;
                        }
                        count++;
                    }
                }
                //closeDatabase();
                myReader.close();
                System.out.println("### log ### Done reading " + CSV_file_name + " --> " + count + " lines, " + recordCount + " records.");
                result = true;

            } catch (Exception ex) {
                ex.printStackTrace();
                Log.d(TAG, "### error ### Create Table Exception : " + ex.toString());
                result = false;
            }
        }
        else{
            Log.d(TAG, "### error ### " + CSV_file_name + "파일이 없습니다.");
            result = false;
        }



        return result;
    }


    public static boolean insertData(String table_name, String aLine) {

        boolean result = false;
        String[] tokens =  aLine.split("\\,", -1);  //aLine.split(",", -1);
        if (tokens != null){ // && tokens.length > 7) {
            db.execSQL( "insert into " + table_name + "(name, floor, location, dev_picture, dev_RM) values (" +
                    "'" + tokens[0] + "'," +
                    "'" + tokens[1] + "'," +
                    "'" + tokens[2] + "'," +
                    "'" + tokens[3] + "'," +
                    "'" + tokens[4] + "')");
            System.out.println("[" + tokens[0]+", " + tokens[1]+ ", " + tokens[2] + "," + tokens[3] +","+tokens[4]+"]");
            result = true;
        } else {
            Log.d(TAG, "### error ### 입력 라인이 유효하지 않습니다. -> "+ aLine);
            result = false;
        }

        return result;
    }

    public static boolean insertDataWithCSV(String CSV_file_name, String table_name, String aLine) throws IOException {

        boolean result = false;
        if(db==null) openDatabase();

        //Database에 데이터 추가하기
        if(insertData(table_name, aLine)) {
            //CSV 파일에 추가된 데이터 업데이트 하기
            result = update_to_CSV(table_name, CSV_file_name);
        }

        closeDatabase();

        return result;
    }

    //모든 데이터 출력하기
    public static Cursor queryTableAllData(String table_name){
        Cursor outCursor=null;
        if(db!=null) {
            try {
                outCursor = db.rawQuery("SELECT * FROM " + table_name, null);
                Log.d(TAG, "### success ### 모든 데이터읽어오기");
            } catch (Exception ex) {
                ex.printStackTrace();
                Log.d(TAG, "### error ### queryTableAllData()에서 Exception : " + ex.toString());
            }
        }

        return (outCursor);
    }

    //검색된 데이터 출력 하기
    public static Cursor queryTableFindData(String table_name, String strSearchWord ) {
        String aSQL = "SELECT *" + " FROM "+ table_name + " WHERE name LIKE ?";
        String[] args = {"%"+strSearchWord+"%"};
        Cursor outCursor = db.rawQuery(aSQL, args);
        Log.d(TAG,"### success ### 검색한 데이터읽어오기");
        return (outCursor);
    }

    public static String getPictureFromName(String table_name, String name ) {
        String aSQL = "SELECT dev_picture"+" FROM "+table_name+" where name='"+name+"'";
        String pictures = "";
        Cursor cursor = db.rawQuery(aSQL, null);

        cursor.moveToFirst();
        int pictureCol=cursor.getColumnIndex("dev_picture");
        pictures = cursor.getString(pictureCol);

        return (pictures);
    }

    public static String getRMFromName(String table_name, String name ) {
        String aSQL = "SELECT dev_RM"+" FROM "+table_name+" where name='"+name+"'";
        String pictures = "";
        Cursor cursor = db.rawQuery(aSQL, null);

        cursor.moveToFirst();
        int pictureCol=cursor.getColumnIndex("dev_RM");
        pictures = cursor.getString(pictureCol);

        return (pictures);
    }

    //데이터 수정하기
    public static boolean updateData(String CSV_file_name, String table_name, ListItemData data_to_update ) throws IOException {
        boolean result = false;
        int id = data_to_update.mId;
        String name = data_to_update.mData[0];
        String floor = data_to_update.mData[1];
        String location = data_to_update.mData[2];
        String pictures = data_to_update.mData[3];
        String rm = data_to_update.mData[4];

        String aSQL = "UPDATE " + table_name
                + " SET name='"+ name + "',"
                + "floor='"+ floor + "',"
                + "location='"+ location + "'"
                + " WHERE _id='" + id + "'";

        try {
            db.execSQL(aSQL);
            Log.d(TAG,"### success ### Data 수정됨. 장비명: "+name+", 층수: "+floor+", 장소: "+location);
            result = true;
        } catch (SQLiteException ex) {
            Log.d(TAG,"### error ### Data 수정 중 Exception 발생:"+ex.getMessage());
            result = false;
        }

        //CSV 파일에 수정된 데이터 업데이트 하기
        update_to_CSV(table_name,CSV_file_name);

        return result;
    }

    public static boolean addPictureData(String calling_class, String table_name, int id, String image_file) throws IOException {
        boolean result = false;

        //기존 사진 정보 가져오기
        Cursor cursor = db.rawQuery("SELECT dev_picture" + " FROM "+ table_name + " WHERE _id='"+id+"'", null);
        int pictureCol=0;
        String before_pictures="";
        String aSQL = "";
        cursor.moveToFirst();
        pictureCol=cursor.getColumnIndex("dev_picture");
        before_pictures = cursor.getString(pictureCol);
        if(before_pictures.equals("")){    // 이전에 사진이 없는 경우
            aSQL = "UPDATE "+table_name+" SET dev_picture='"+image_file+"'"+" WHERE _id='"+id+"'";
        }else {                             // 이전에 사진이 있는 경우

            aSQL = "UPDATE "+table_name+" SET dev_picture='"+before_pictures+"|"+image_file+"'"+" WHERE _id='"+id+"'";
        }

        try {
            db.execSQL(aSQL);
            Log.d(TAG,"### success ### 장비 사진 DB 수정됨. 사진: "+image_file);
            result = true;
        } catch (SQLiteException ex) {
            Log.d(TAG,"### error ### Data 수정 중 Exception 발생:"+ex.getMessage());result = false;
        }

        //CSV 파일에 수정된 데이터 업데이트 하기
        String CSV_file_name = CategoryManager.getDataFile(calling_class);
        update_to_CSV(table_name,CSV_file_name);

        return result;
}



    public static boolean addRMData(String calling_class, String table_name, int id, String image_file) throws IOException {
        boolean result = false;

        //기존 사진 정보 가져오기
        Cursor cursor = db.rawQuery("SELECT dev_RM" + " FROM "+ table_name + " WHERE _id='"+id+"'", null);
        int pictureCol=0;
        String before_pictures="";
        String aSQL = "";
        cursor.moveToFirst();
        pictureCol=cursor.getColumnIndex("dev_RM");
        before_pictures = cursor.getString(pictureCol);
        if(before_pictures.equals("")){    // 이전에 사진이 없는 경우
            aSQL = "UPDATE "+table_name+" SET dev_RM='"+image_file+"'"+" WHERE _id='"+id+"'";
        }else {                             // 이전에 사진이 있는 경우
            aSQL = "UPDATE "+table_name+" SET dev_RM='"+before_pictures+"|"+image_file+"'"+" WHERE _id='"+id+"'";
        }

        try {
            db.execSQL(aSQL);
            Log.d(TAG,"### success ### 장비 사진 DB 수정됨. 사진: "+image_file);
            result = true;
        } catch (SQLiteException ex) {
            Log.d(TAG,"### error ### Data 수정 중 Exception 발생:"+ex.getMessage());result = false;
        }

        //CSV 파일에 수정된 데이터 업데이트 하기
        String CSV_file_name = CategoryManager.getDataFile(calling_class);
        update_to_CSV(table_name,CSV_file_name);

        return result;
    }

    public static boolean updatePictureData(String calling_class, String table_name, int id, String image_files) throws IOException {
        boolean result = false;
        image_files=image_files.replace(',','|');
        String aSQL = "";
        aSQL = "UPDATE "+table_name+" SET dev_picture='"+image_files+"'"+" WHERE _id='"+id+"'";
        try {
            db.execSQL(aSQL);
            Log.d(TAG,"### success ### 장비 사진 DB 수정됨. 사진: "+image_files);
            result = true;
        } catch (SQLiteException ex) {
            Log.d(TAG,"### error ### Data 수정 중 Exception 발생:"+ex.getMessage());result = false;
        }

        //CSV 파일에 수정된 데이터 업데이트 하기
        String CSV_file_name = CategoryManager.getDataFile(calling_class);
        update_to_CSV(table_name,CSV_file_name);

        return result;
    }

    public static boolean updateRMData(String calling_class, String table_name, int id, String image_files) throws IOException {
        boolean result = false;
        image_files=image_files.replace(',','|');
        String aSQL = "";
        aSQL = "UPDATE "+table_name+" SET dev_RM='"+image_files+"'"+" WHERE _id='"+id+"'";
        try {
            db.execSQL(aSQL);
            Log.d(TAG,"### success ### 장비 사진 DB 수정됨. 사진: "+image_files);
            result = true;
        } catch (SQLiteException ex) {
            Log.d(TAG,"### error ### Data 수정 중 Exception 발생:"+ex.getMessage());result = false;
        }

        //CSV 파일에 수정된 데이터 업데이트 하기
        String CSV_file_name = CategoryManager.getDataFile(calling_class);
        update_to_CSV(table_name,CSV_file_name);

        return result;
    }


    //데이터 삭제하기
    public static boolean deleteData(String CSV_file_name, String table_name, int id_to_delete ) throws IOException {
        boolean result = false;

        String aSQL = "DELETE FROM " + table_name + " WHERE _id = "+id_to_delete+"";

        if(db==null) openDatabase();

        try {
            db.execSQL(aSQL);
            Log.d(TAG,"### success ### Data 삭제됨. ID: "+id_to_delete);
            result = true;
        } catch (SQLiteException ex){
            Log.d(TAG,"### error ### Data 삭제 중 Exception 발생:"+ex.getMessage());
            result = false;
        }

        //CSV 파일에 삭제된 데이터 업데이트 하기
        update_to_CSV(table_name,CSV_file_name);

        closeDatabase();

        return result;
    }

    public static boolean  update_to_CSV(String table_name, String CSV_file_name) throws IOException {
        boolean result = false;

        File fCSV = new File(CSV_file_name);
        if(fCSV.exists()==true){
            fCSV.delete();
        }

        if(fCSV.createNewFile()){
            try {
                //FileOutputStream fOut = new FileOutputStream(fCSV, true);
                FileOutputStream fileOutputStream = new FileOutputStream(fCSV);
                OutputStreamWriter OutputStreamWriter = new OutputStreamWriter(fileOutputStream, "euc-kr");
                BufferedWriter bufferedWriter = new BufferedWriter(OutputStreamWriter);

                if(db==null) openDatabase();

                Cursor cursor = queryTableAllData(table_name);
                int recordCount = cursor.getCount();
                int idCol = cursor.getColumnIndex("_id");
                int nameCol = cursor.getColumnIndex("name");
                int floorCol = cursor.getColumnIndex("floor");
                int locationCol = cursor.getColumnIndex("location");
                int pictureCol = cursor.getColumnIndex("dev_picture");
                int rmCol = cursor.getColumnIndex("dev_RM");

                bufferedWriter.write("DeviceName,Floor,Location,Pictures,RM,History");
                bufferedWriter.write("\n");

                for(int i=0;i<recordCount;i++) {
                    cursor.moveToNext();

                    String nameData = cursor.getString(nameCol);
                    String floorData = cursor.getString(floorCol);
                    String locationData = cursor.getString(locationCol);
                    String pictureData = cursor.getString(pictureCol);
                    String rmData = cursor.getString(rmCol);

                    String aLine = nameData+","+floorData+","+locationData+","+pictureData+","+rmData;
                    bufferedWriter.write(aLine);
                    bufferedWriter.write("\n");
                }
                bufferedWriter.close();

                closeDatabase();
                result = true;

            } catch (Exception ex) {
                ex.printStackTrace();
                Log.d(TAG, "### error ### CSV 파일 업데이트 Exception : " + ex.toString());
                result = false;
            }
        }

        return result;
    }


    //데이터베이스의 데이터를 ListView에 추가
    public static ListItemDataAdapter CurtorToListItemAdapter(Cursor inCursor, ListItemDataAdapter in_adapter) {

        int recordCount = inCursor.getCount();
        ListItemDataAdapter adapter = in_adapter;
        adapter.clear();

        int idCol = inCursor.getColumnIndex("_id");
        int nameCol = inCursor.getColumnIndex("name");
        int floorCol = inCursor.getColumnIndex("floor");
        int locationCol = inCursor.getColumnIndex("location");
        int pictureCol = inCursor.getColumnIndex("dev_picture");
        int rmCol = inCursor.getColumnIndex("dev_RM");

        for (int i = 0; i < recordCount; i++) {
            inCursor.moveToNext();
            int idData = inCursor.getInt(idCol);
            String nameData = inCursor.getString(nameCol);
            String floorData = inCursor.getString(floorCol);
            String locationData = inCursor.getString(locationCol);
            String pictureData = inCursor.getString(pictureCol);
            String rmData = inCursor.getString(rmCol);

            adapter.addItem(new ListItemData(idData, nameData, floorData, locationData, pictureData, rmData));
        }
        return adapter;
    }

}