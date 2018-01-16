/**
 * Copyright (c) www.longdw.com
 */

package com.example.ppmusic.utils;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.ppmusic.MyApp;
import com.example.ppmusic.R;


/**
 * 引导页图片，停留若干秒，然后自动消失。
 */
public class SplashScreen {

    public final static int SLIDE_LEFT = 1;
    public final static int SLIDE_UP = 2;
    public final static int FADE_OUT = 3;

    private Dialog splashDialog;

    private Activity activity;

    public SplashScreen(Activity activity) {
        this.activity = activity;
    }

    /**
     * 显示。
     *
     * @param imageResource 图片资源
     * @param animation     消失时的动画效果，取值可以是：SplashScreen.SLIDE_LEFT, SplashScreen.SLIDE_UP, SplashScreen.FADE
     */
    public void show(final int imageResource, final int animation) {
        Runnable runnable = new Runnable() {
            public void run() {
                // Get reference to display
                DisplayMetrics metrics = new DisplayMetrics();
//                Display display = activity.getWindowManager().getDefaultDisplay();

                // Create the layout for the dialog
                final LinearLayout root = new LinearLayout(activity);
                root.setMinimumHeight(metrics.heightPixels);
                root.setMinimumWidth(metrics.widthPixels);
                root.setOrientation(LinearLayout.VERTICAL);
                root.setBackgroundColor(Color.BLACK);
                root.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT, 0.0F));
                final ImageView imageView = new ImageView(activity);
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                root.addView(imageView);
                root.setBackgroundResource(imageResource);
                // root.setBackgroundDrawable(new BitmapDrawable());
                imageView.setBackgroundResource(imageResource);
                if (imageResource == 0) {
                    String picturePath = MyApp.getPreferencesService().getValue(
                            "WelcomepaperUri", null);
                        imageView.setImageDrawable(MyApp.getPictureDrawable("WelcomepaperUri",R.drawable.bg_001));
                }

                // Create and show the dialog
                splashDialog = new Dialog(activity, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
                // check to see if the splash screen should be full screen
                if ((activity.getWindow().getAttributes().flags & WindowManager.LayoutParams.FLAG_FULLSCREEN)
                        == WindowManager.LayoutParams.FLAG_FULLSCREEN) {
                    splashDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                            WindowManager.LayoutParams.FLAG_FULLSCREEN);
                }

                Window window = splashDialog.getWindow();
                switch (animation) {
                    case SLIDE_LEFT:
                        window.setWindowAnimations(R.style.dialog_anim_slide_left);
                        break;
                    case SLIDE_UP:
                        window.setWindowAnimations(R.style.dialog_anim_slide_up);
                        break;
                    case FADE_OUT:
                        window.setWindowAnimations(R.style.dialog_anim_fade_out);
                        break;
                }

                splashDialog.setContentView(root);
                splashDialog.setCancelable(false);
                splashDialog.show();

                // Set Runnable to remove splash screen just in case
                /*final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        removeSplashScreen();
                    }
                }, millis);*/
            }
        };
        activity.runOnUiThread(runnable);
    }

    public void removeSplashScreen() {
        if (splashDialog != null && splashDialog.isShowing()) {
            splashDialog.dismiss();
            splashDialog = null;
        }
    }

}
