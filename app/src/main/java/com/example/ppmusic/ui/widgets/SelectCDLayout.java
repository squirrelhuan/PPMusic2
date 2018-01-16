package com.example.ppmusic.ui.widgets;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.example.ppmusic.R;

/**
 * Created by Administrator on 2017/4/14.
 */

public class SelectCDLayout extends RelativeLayout {

    public SelectCDLayout(Context context) {
        super(context);
    }

    public SelectCDLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SelectCDLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void updateView(Activity activity){
        View selectCDLayout = activity.findViewById(R.id.bottom_action_bar);
        if (selectCDLayout == null) {
            return;
        }
    }

}
