/**
 * 
 */

package com.example.ppmusic.views;

import java.util.ArrayList;
import java.util.List;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ppmusic.MyApp;
import com.example.ppmusic.R;
import com.example.ppmusic.helpers.utils.ThemeUtils;
import com.example.ppmusic.ui.adapters.RecentlyAddedAdapter;

/**
 * @author Andrew Neal
 */
public class ViewHolderList_v4 {

	public final ImageView mViewHolderImage, mPeakOne, mPeakTwo,mPeakThree/*
																 * ,
																 * mQuickContextDivider
																 * ,
																 * mQuickContextTip
																 */;

	public CheckBox mMenu;

	public final TextView mViewHolderLineOne;

	public final TextView mViewHolderLineTwo;

	public final FrameLayout mQuickContext;
	public LinearLayout ll_null;
	private boolean isRunning = false;
	private boolean isOpen = false;

	public ViewHolderList_v4(View view, final RecentlyAddedAdapter recentlyAddedAdapter) {
		ll_null = (LinearLayout) view.findViewById(R.id.ll_null);

		mViewHolderImage = (ImageView) view
				.findViewById(R.id.listview_item_image);
		mViewHolderLineOne = (TextView) view
				.findViewById(R.id.listview_item_line_one);
		mViewHolderLineTwo = (TextView) view
				.findViewById(R.id.listview_item_line_two);
		mQuickContext = (FrameLayout) view
				.findViewById(R.id.track_list_context_frame);
		mPeakOne = (ImageView) view.findViewById(R.id.peak_one);
		mPeakTwo = (ImageView) view.findViewById(R.id.peak_two);
		mPeakThree = (ImageView) view.findViewById(R.id.peak_three);
		mMenu = (CheckBox) view.findViewById(R.id.cb_button_more);

		// 菜单选项
		mMenu.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				//Toast.makeText(MyApp.getContext(), arg1?"true":"false", Toast.LENGTH_SHORT).show();
				if(isRunning){
					arg0.setChecked(!arg1);
				}
				if (!arg1) {
					final RelativeLayout rl_root = (RelativeLayout) ((RelativeLayout) ((RelativeLayout) arg0
							.getParent()).getParent());
					final LinearLayout ll_null = (LinearLayout) rl_root
							.findViewById(R.id.ll_null);
					colseSelf(ll_null);
					return;
				} else {
					recentlyAddedAdapter.closeOthers(mMenu);
					isRunning = true;
					final RelativeLayout rl_root = (RelativeLayout) ((RelativeLayout) ((RelativeLayout) arg0
							.getParent()).getParent());
					final RelativeLayout rl_top = (RelativeLayout) rl_root
							.findViewById(R.id.rl_top);

					LinearLayout ll_bottom = (LinearLayout) rl_root
							.findViewById(R.id.ll_bottom);
					ll_bottom.setVisibility(View.VISIBLE);
					final LinearLayout ll_null = (LinearLayout) rl_root
							.findViewById(R.id.ll_null);
					
					ll_null.setVisibility(View.VISIBLE);
					ObjectAnimator animator = ObjectAnimator.ofFloat(rl_top,
							"rotationX", 0, 180, 180);
					animator.addUpdateListener(new AnimatorUpdateListener() {

						@Override
						public void onAnimationUpdate(ValueAnimator animation) {
							float curValue = (float) animation
									.getAnimatedValue();
							LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) ll_null
									.getLayoutParams(); // 取控件textView当前的布局参数
							linearParams.height = (int) ((curValue / 180) * rl_top
									.getHeight());// 控件的高强制设成20
							// linearParams.width = 30;// 控件的宽强制设成30
							ll_null.setLayoutParams(linearParams); // 使设置好的布局参数应用到控件</pre>
							if (curValue == 180.0) {
								ObjectAnimator animator1 = ObjectAnimator
										.ofFloat(rl_top, "rotationX", 180, 0, 0);
								animator1.setDuration(0);
								animator1
										.addUpdateListener(new AnimatorUpdateListener() {
											@Override
											public void onAnimationUpdate(
													ValueAnimator arg0) {
												isRunning = false;
												isOpen = true;
											}
										});
								animator1.start();
							}
							Log.d("qijian", "curValue:" + curValue);
						}
					});

					AnimatorSet animatorSet = new AnimatorSet();
					animatorSet.playTogether(animator);
					animatorSet.setDuration(1000);
					animatorSet.start();
				}
			}

			
		});

		// mQuickContextDivider =
		// (ImageView)view.findViewById(R.id.quick_context_line);
		// mQuickContextTip =
		// (ImageView)view.findViewById(R.id.quick_context_tip);

		// Theme chooser
		ThemeUtils.setTextColor(view.getContext(), mViewHolderLineOne,
				"list_view_text_color");
		ThemeUtils.setTextColor(view.getContext(), mViewHolderLineTwo,
				"list_view_text_color");
		// ThemeUtils.setBackgroundColor(view.getContext(),
		// mQuickContextDivider,
		// "list_view_quick_context_menu_button_divider");
	}
	private void colseSelf(LinearLayout ll_current) {
		LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) ll_current
				.getLayoutParams(); // 取控件textView当前的布局参数
		linearParams.height = 0;// 控件的高强制设成20
		ll_null.setLayoutParams(linearParams);
		ll_null.setVisibility(View.GONE);

		isOpen = false;
		isRunning = false;
	}
}
