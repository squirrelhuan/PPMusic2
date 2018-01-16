package com.example.ppmusic.manager;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.ppmusic.activities.LauncherActivity;

/**
 * 系统权限管理
 * Created by huan on 2017/11/14.
 */

public class PermissionManager {

    public static Activity activity;
    private static AlertDialog dialog; // 要申请的权限
    private static String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static OnCheckPermissionListener omCheckPermissionListener;

    public static void chekPermission(Context context, String[] permissions, OnCheckPermissionListener listener) {
        activity = (Activity) context;
        omCheckPermissionListener = listener;
        // 版本判断。当手机系统大于 23 时，才有必要去判断权限是否获取
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            // 检查该权限是否已经获取
            boolean useful = true;
            for (String str : permissions) {
                int i = ContextCompat.checkSelfPermission(context, str);
                // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
                if (i != PackageManager.PERMISSION_GRANTED) {
                    useful = false;
                }
            }

            if (!useful) {
                // 如果没有授予该权限，就去提示用户请求
                //listener.onPassed();
                showDialogTipUserRequestPermission(context, permissions, listener);
            } else {
                listener.onPassed();
            }
        } else {
            listener.onPassed();
        }
    }

    // 提示用户该请求权限的弹出框
    private static void showDialogTipUserRequestPermission(final Context context, final String[] permission, final OnCheckPermissionListener listener) {

        dialog = new AlertDialog.Builder(context)
                .setTitle("存储权限不可用")
                .setMessage("请开启权限才能使用此功能")
                .setPositiveButton("立即开启", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        startRequestPermission(context, permission);
                        chekPermission(context, permission, listener);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                       ((Activity) context).finish();
                        PermissionManager.chekPermission(context, permission, listener);
                    }
                }).setCancelable(false).show();
    }

    // 开始提交请求权限
    private static void startRequestPermission(final Context context, String[] permissions) {
        ActivityCompat.requestPermissions((Activity) context, permissions, 321);
        for (String permission : permissions) {
            Log.d("CGQ", permission + "返回值：" + ActivityCompat.checkSelfPermission(context, permission));
        }
    }

// 提示用户去应用设置界面手动开启权限

    public static void showDialogTipUserGoToAppSettting(final Context context) {

        dialog = new AlertDialog.Builder(context)
                .setTitle("存储权限不可用")
                .setMessage("请在-应用设置-权限-中，允许泡泡音乐使用存储权限来保存用户数据")
                .setPositiveButton("立即开启", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 跳转到应用设置界面
                        goToAppSetting(context);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((Activity) context).finish();
                    }
                }).setCancelable(false).show();
    }

    // 跳转到当前应用的设置界面
    private static void goToAppSetting(final Context context) {
        Intent intent = new Intent();

        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
        intent.setData(uri);
        ((Activity) context).startActivityForResult(intent, 123);
    }

    public static void onActivityResult(int requestCode) {
        if (requestCode == 123) {

            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // 检查该权限是否已经获取
                int i = ContextCompat.checkSelfPermission(activity, permissions[0]);
                // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
                if (i != PackageManager.PERMISSION_GRANTED) {
                    // 提示用户应该去应用设置界面手动开启权限
                    PermissionManager.showDialogTipUserGoToAppSettting(activity);
                } else {
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    Toast.makeText(activity, "权限获取成功", Toast.LENGTH_SHORT).show();
                    omCheckPermissionListener.onPassed();
                }
            }
        }
    }

    public static void onRequestPermissionsResult(int requestCode,@NonNull int[] grantResults) {
        if (requestCode == 321) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    // 判断用户是否 点击了不再提醒。(检测该权限是否还可以申请)
                    boolean b = activity.shouldShowRequestPermissionRationale(permissions[0]);
                    if (!b) {
                        // 用户还是想用我的 APP 的
                        // 提示用户去应用设置界面手动开启权限
                        PermissionManager.showDialogTipUserGoToAppSettting(activity);
                    } else{
                        activity.finish();
                    }
                } else {
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    Toast.makeText(activity, "权限获取成功", Toast.LENGTH_SHORT).show();
                    omCheckPermissionListener.onPassed();
                }
            }
        }
    }

    /**
     *
     */
    public interface OnCheckPermissionListener {
        void onPassed();//通过
        void onNoPassed();//不通过
    }
}
