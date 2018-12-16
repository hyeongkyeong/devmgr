package com.hyeongkyeong.devmng;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class IntroActivity extends Activity {
    /** Called when the activity is first created. */
    private static final String TAG = "IntroActivity";

    boolean StorageReadPermission = false;
    boolean StorageWritePermission = false;

    private static int TIMER_RUNTIME;
    private static boolean mbActive;
    private static ProgressBar mProgressBar;
    private static TextView LoadingMessage;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(Color.RED);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro_activity);

        checkDangerousPermissions();
        init();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        init();
    }

    private void init(){
        if(StorageReadPermission&&StorageWritePermission) {
            mProgressBar = (ProgressBar) findViewById((R.id.progress_horizontal));
            LoadingMessage = (TextView)findViewById(R.id.LoadingText);

            final Thread timerThread = new Thread() {
                @Override
                public void run() {

                    boolean flag1 = false;
                    boolean flag2 = false;

                    mbActive = true;

                    int waited = 0;

                    try {
                        File f_app_root_dir = new File(DatabaseHelper.app_root_dir);
                        if(!f_app_root_dir.exists()){
                            f_app_root_dir.mkdir();
                        }
                        CategoryManager.createCategoryTable();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    onContinue();
                }

            };
            timerThread.start();
        }
    }



    //권한확인
    private boolean checkDangerousPermissions() {
        boolean result = false;
        int ReadPermissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int WritePermissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        StorageReadPermission = (ReadPermissionCheck == PackageManager.PERMISSION_GRANTED);
        StorageWritePermission = (WritePermissionCheck == PackageManager.PERMISSION_GRANTED);
        if ((StorageReadPermission==true)&&(StorageWritePermission==true)) {
            //Toast.makeText(this, "파일 일기/쓰기 권한이 있습니다.", Toast.LENGTH_LONG).show();
            result = true;
        } else {
            Toast.makeText(this, "파일 일기/쓰기 권한이 없습니다. 권한 요청에 승인을 해주세요.", Toast.LENGTH_LONG).show();
            result = false;
            //if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
            //     Toast.makeText(this, "앱 설정에서 권한을 승인해 주세요.", Toast.LENGTH_LONG).show();
            // }
            // else {
            String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
            ActivityCompat.requestPermissions(this, permissions, 1);
            // }

        }
        return result;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    //Toast.makeText(this, permissions[i] + " 권한이 승인됨.", Toast.LENGTH_LONG).show();
                    init();
                } else {
                    //Toast.makeText(this, permissions[i] + " 권한이 승인되지 않음.", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        }
    }

    public static void updateProgress(final int timePassed) {
        if(null != mProgressBar){

            final int progress = mProgressBar.getMax() * timePassed / TIMER_RUNTIME;
            mProgressBar.setProgress(progress);
        }
    }
    public static void setProgress_fulltime(int time){
        TIMER_RUNTIME = time;
    }


    public void onContinue(){
        Intent intent = new Intent(IntroActivity.this, MainActivity.class);
        startActivity(intent);
        // 뒤로가기 했을경우 안나오도록 없애주기 >> finish!!
        finish();
    }
}

