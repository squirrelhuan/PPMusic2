package com.example.ppmusic.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.ppmusic.MyApp;
import com.example.ppmusic.R;
import com.example.ppmusic.manager.PermissionManager;


/**
 * Created by Administrator on 2017/3/30 0030.
 */

public class LauncherActivity extends Activity {

    public final int MSG_FINISH_LAUNCHERACTIVITY = 500;

    private ImageView iv_start_bg;
    // 要申请的权限
    private String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};//, Manifest.permission.CAMERA
    public Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_FINISH_LAUNCHERACTIVITY:
                    PermissionManager.chekPermission(LauncherActivity.this, permissions, new PermissionManager.OnCheckPermissionListener() {
                        @Override
                        public void onPassed() {
                            //跳转到MainActivity，并结束当前的LauncherActivity
                            Intent intent = new Intent(LauncherActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        @Override
                        public void onNoPassed() {
                            Toast.makeText(LauncherActivity.this, "权限不足无法继续使用", Toast.LENGTH_LONG).show();
                        }
                    });

                    break;

                default:
                    break;
            }
        }

        ;
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 不显示系统的标题栏，保证windowBackground和界面activity_main的大小一样，显示在屏幕不会有错位（去掉这一行试试就知道效果了）
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // 注意：添加3秒睡眠，以确保黑屏一会儿的效果明显，在项目应用要去掉这3秒睡眠
        /*try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/

        setContentView(R.layout.activity_launcher);
        iv_start_bg = (ImageView) findViewById(R.id.iv_start_bg);
        iv_start_bg.setImageDrawable(MyApp.getPictureDrawable("WelcomepaperUri", R.drawable.bg_001));
        // 停留3秒后发送消息，跳转到MainActivity
        mHandler.sendEmptyMessageDelayed(MSG_FINISH_LAUNCHERACTIVITY, 2400);
    }

    // 用户权限 申请 的回调方法
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionManager.onRequestPermissionsResult(requestCode,grantResults);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        PermissionManager.onActivityResult(requestCode);

    }

}
