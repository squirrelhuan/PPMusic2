package com.example.ppmusic.adapter;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.ppmusic.FormatHelper;
import com.example.ppmusic.MyApp;
import com.example.ppmusic.R;
import com.example.ppmusic.bean.MusicInfo;

import java.util.ArrayList;
import java.util.List;

public class SongListAdapter01 extends BaseAdapter {

	// 帧动画
	private AnimationDrawable mPeakOneAnimation, mPeakTwoAnimation,
			mPeakThreeAnimation;
	// private WeakReference<ViewHolderList> holderReference;

	List<MusicInfo> mList;
	Context mContext;
	LayoutInflater mInflater;
	ListView listView;

	public SongListAdapter01(Context mContext, List<MusicInfo> mList,
			ListView lv_songs) {
		this.mContext = mContext;
		this.mInflater = LayoutInflater.from(mContext);
		this.mList = mList;
		this.listView = lv_songs;
		listView.setOnScrollListener(new OnScrollListener() {

			public void onScrollStateChanged(AbsListView arg0, int arg1) {
				closeOthers();
			}

			public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {

			}
		});
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	private List<View> vlist = new ArrayList<View>();
	@SuppressWarnings("ResourceType")
	@Override
	public View getView(int position, View convertView, final ViewGroup parent) {
		ViewHolder viewHolder = null;
		// if (convertView == null) {
		viewHolder = new ViewHolder();
		convertView = mInflater.inflate(R.layout.item_song_list01, parent,
				false);

		viewHolder.ll_null = (LinearLayout) convertView
				.findViewById(R.id.ll_null);
		viewHolder.iv_button_more = (ImageView) convertView
				.findViewById(R.id.iv_button_more);
		viewHolder.tv_title = (TextView) convertView
				.findViewById(R.id.tv_title);
		viewHolder.tv_artist = (TextView) convertView
				.findViewById(R.id.tv_artist);
		viewHolder.tv_duration = (TextView) convertView
				.findViewById(R.id.tv_duration);
		viewHolder.mPeakOne = (ImageView) convertView
				.findViewById(R.id.peak_one);
		viewHolder.mPeakTwo = (ImageView) convertView
				.findViewById(R.id.peak_two);
		viewHolder.mPeakThree = (ImageView) convertView
				.findViewById(R.id.peak_three);
		convertView.setTag(viewHolder);

		boolean isExit = false;
		for (int i = 0; i < vlist.size(); i++) {
			if (vlist.get(i).getTag().equals(convertView.getTag())) {
				isExit = true;
				break;
			}
		}
		if (!isExit) {
			vlist.add(convertView);
		}
		/*
		 * } else { viewHolder = (ViewHolder) convertView.getTag(); }
		 */
		MusicInfo eModel = mList.get(position);
		viewHolder.tv_title.setText(eModel.getTitle());
		viewHolder.tv_artist.setText(eModel.getArtist());
		viewHolder.tv_duration.setText(FormatHelper.formatDuration(eModel
				.getDuration()));

		viewHolder.mPeakOne.setImageResource(R.anim.peak_meter_1);
		viewHolder.mPeakTwo.setImageResource(R.anim.peak_meter_2);
		viewHolder.mPeakThree.setImageResource(R.anim.peak_meter_3);
		mPeakOneAnimation = (AnimationDrawable) viewHolder.mPeakOne
				.getDrawable();
		mPeakTwoAnimation = (AnimationDrawable) viewHolder.mPeakTwo
				.getDrawable();
		mPeakThreeAnimation = (AnimationDrawable) viewHolder.mPeakThree
				.getDrawable();
		if (MyApp.getNatureBinder().getCurrentMusic() != null) {
			if (MyApp.getNatureBinder().getCurrentMusic().getUrl()
					.equals(mList.get(position).getUrl())) {
				viewHolder.mPeakOne.setVisibility(View.VISIBLE);
				viewHolder.mPeakTwo.setVisibility(View.VISIBLE);
				viewHolder.mPeakThree.setVisibility(View.VISIBLE);
				if (MyApp.getNatureBinder().getStatus()) {
					mPeakOneAnimation.start();
					mPeakTwoAnimation.start();
					mPeakThreeAnimation.start();
				} else {
					mPeakOneAnimation.stop();
					mPeakTwoAnimation.stop();
					mPeakThreeAnimation.stop();
				}
			}
		}
		// 菜单选项
		viewHolder.iv_button_more.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (isRunning) {
					return;
				}

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
				if (isOpen) {
					if (ll_current.equals(ll_null)) {
						closeOthers();
						return;
					} else {
						closeOthers();
					}
				}
				ll_current = ll_null;
				ll_null.setVisibility(View.VISIBLE);
				ObjectAnimator animator = ObjectAnimator.ofFloat(rl_top,
						"rotationX", 0, 180, 180);
				animator.addUpdateListener(new AnimatorUpdateListener() {
					@Override
					public void onAnimationUpdate(ValueAnimator animation) {
						float curValue = (float) animation.getAnimatedValue();
						LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) ll_null
								.getLayoutParams(); // 取控件textView当前的布局参数
						linearParams.height = (int) ((curValue / 180) * rl_top
								.getHeight());// 控件的高强制设成20
						// linearParams.width = 30;// 控件的宽强制设成30
						ll_null.setLayoutParams(linearParams); // 使设置好的布局参数应用到控件</pre>
						if (curValue == 180.0) {
							ObjectAnimator animator1 = ObjectAnimator.ofFloat(
									rl_top, "rotationX", 180, 0, 0);
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
		});

		/*
		 * if (eModel.getImage() != null) {
		 * MyApp.imageLoader.displayImage(MyApp.getNetConstants().SERVER_URL +
		 * eModel.getImage(), viewHolder.image_head); } String imageUrls =
		 * mList.get(position).getImageUrl();
		 */

		return convertView;
	}

	private void closeOthers() {
		if (ll_current == null) {
			return;
		}
		for (int i = 0; i < vlist.size(); i++) {
			LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) ll_current
					.getLayoutParams(); // 取控件textView当前的布局参数
			linearParams.height = 0;// 控件的高强制设成20
			((ViewHolder) (vlist.get(i).getTag())).ll_null
					.setLayoutParams(linearParams);
			((ViewHolder) (vlist.get(i).getTag())).ll_null
					.setVisibility(View.GONE);
		}
		isOpen = false;
		isRunning = false;
	}

	private LinearLayout ll_current = null;
	private boolean isRunning = false;
	private boolean isOpen = false;

	class ViewHolder {
		LinearLayout ll_null;
		ImageView image_head, iv_button_more, mPeakOne, mPeakTwo, mPeakThree;
		TextView tv_title, tv_artist, tv_duration;
		boolean isOpen = false;
	}

	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

}
