package com.example.ppmusic.adapter.popupWindows;

import com.example.ppmusic.R;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class popupwindows_01 {
	
	private PopupWindow pop = null;
	private LinearLayout ll_popup;
	
	
	private PopupWindow getView(final Context context ){

		pop = new PopupWindow(context);
		View view =  LayoutInflater.from(context).inflate(R.layout.item_popupwindows, null);

		ll_popup = (LinearLayout) view.findViewById(R.id.ll_popup);
		pop.setWidth(LayoutParams.MATCH_PARENT);
		pop.setHeight(LayoutParams.WRAP_CONTENT);
		pop.setBackgroundDrawable(new BitmapDrawable());
		pop.setFocusable(true);
		pop.setOutsideTouchable(true);
		pop.setContentView(view);

		RelativeLayout parent = (RelativeLayout) view.findViewById(R.id.parent);
		TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
		tv_title.setText("请选择获取视频文件的方式！");
		Button btn_camera = (Button) view.findViewById(R.id.item_popupwindows_camera);
		btn_camera.setText("录制");
		Button btn_file = (Button) view.findViewById(R.id.item_popupwindows_Photo);
		btn_file.setText("从文件中选取");
		parent.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				pop.dismiss();
				ll_popup.startAnimation(AnimationUtils.loadAnimation(context, R.anim.scaling_big));
			}
		});
		//拍射
		btn_camera.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				//takeVideo();
			}
		});
		//文件选取
		btn_file.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				
			}
		});
		pop.showAtLocation(view, Gravity.BOTTOM, 0, 0);
		ll_popup.startAnimation(AnimationUtils.loadAnimation(context, R.anim.scaling_big));
		return pop;
	}
	
}
