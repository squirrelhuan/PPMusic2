/**
 * 
 */

package com.example.ppmusic.ui.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ppmusic.R;
import com.example.ppmusic.activities.MusicDetailActivity;
import com.example.ppmusic.activities.QuickQueue;
import com.example.ppmusic.cache.ImageInfo;
import com.example.ppmusic.helpers.utils.ApolloUtils;
import com.example.ppmusic.helpers.utils.MusicUtils;
import com.example.ppmusic.helpers.utils.ThemeUtils;
import com.example.ppmusic.service.ApolloService;
import com.example.ppmusic.ui.widgets.BottomActionBar;

//import static com.example.ppmusic.activities.MainActivity.rl_for_cd;
import static com.example.ppmusic.constants.Constants.SIZE_THUMB;
import static com.example.ppmusic.constants.Constants.SRC_FIRST_AVAILABLE;
import static com.example.ppmusic.constants.Constants.TYPE_ALBUM;
import static com.example.ppmusic.service.ApolloService.SHUFFLE_NONE;

/**
 * @author Andrew Neal
 */
public class PPBottomActionBarFragment extends Fragment {

	private ImageButton mPrev, mNext,mSelectCD;
	private CheckBox mPlay;
	private BottomActionBar mBottomActionBar;
	private TextView tv_music_MaxTime, tv_music_currentTime;

	// Progress
	private SeekBar mProgress;
	// Album art
	private ImageView mAlbumArt;

	// Notify if repeat or shuffle changes
	private Toast mToast;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.bottom_action_bar_paopao,
				container);
		mBottomActionBar = new BottomActionBar(getActivity());
		mAlbumArt = (ImageView) root.findViewById(R.id.audio_player_album_art);
		mAlbumArt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				Bundle bundle = new Bundle();
				bundle.putString("Title", artistName);
				// IntentUtil.jump(getActivity(), MusicListActivity.class,
				// bundle);
				Intent intent = new Intent();
				intent.setClass(getActivity(), MusicDetailActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
						| Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.putExtras(bundle);
				getActivity().startActivity(intent);
			}
		});

		mPrev = (ImageButton) root.findViewById(R.id.bottom_action_bar_previous);
		mPrev.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (MusicUtils.mService == null)
					return;
				try {
					if (MusicUtils.mService.position() < 2000) {
						MusicUtils.mService.prev();
					} else {
						MusicUtils.mService.seek(0);
						MusicUtils.mService.play();
					}
				} catch (RemoteException ex) {
					ex.printStackTrace();
				}
			}
		});

		mPlay = (CheckBox) root.findViewById(R.id.bottom_action_bar_play);
		mPlay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				doPauseResume();
			}
		});


		mNext = (ImageButton) root.findViewById(R.id.bottom_action_bar_next);
		mNext.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (MusicUtils.mService == null)
					return;
				try {
					MusicUtils.mService.next();
				} catch (RemoteException ex) {
					ex.printStackTrace();
				}
			}
		});

		mSelectCD = (ImageButton) root.findViewById(R.id.bottom_select_cd_open);
		mSelectCD.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
			//	rl_for_cd.setVisibility(View.VISIBLE);
				//toggleShuffle();
				//cycleRepeat();
				Context context = getActivity();
				getActivity().startActivity(new Intent(context, QuickQueue.class));
				//设置切换动画，从右边进入，左边退出
				getActivity().overridePendingTransition(R.anim.bottom_up, R.anim.bottom_down);
			}
		});

		// ThemeUtils.setImageButton(getActivity(), mPrev, "apollo_previous");
		// ThemeUtils.setImageButton(getActivity(), mNext, "apollo_next");

		mProgress = (SeekBar) root.findViewById(R.id.sb_music_process);
		if (mProgress instanceof SeekBar) {
			SeekBar seeker = mProgress;
			seeker.setOnSeekBarChangeListener(mSeekListener);
		}
		mProgress.setMax(1000);
		ThemeUtils.setProgessDrawable(getActivity(), mProgress,
				"apollo_seekbar_background");

		// mTrackName = (TextView)root.findViewById(R.id.audio_player_track);
		mTotalTime = (TextView) root.findViewById(R.id.audio_player_total_time);
		mCurrentTime = (TextView) root
				.findViewById(R.id.audio_player_current_time);
		return root;
	}

	// Where we are in the track
	private long mDuration, mLastSeekEventTime, mPosOverride = -1,
			mStartSeekPos = 0;

	private boolean mFromTouch, paused = false;
	/**
	 * Drag to a specfic duration
	 */
	private final OnSeekBarChangeListener mSeekListener = new OnSeekBarChangeListener() {
		@Override
		public void onStartTrackingTouch(SeekBar bar) {
			mLastSeekEventTime = 0;
			mFromTouch = true;
		}

		@Override
		public void onProgressChanged(SeekBar bar, int progress,
				boolean fromuser) {
			if (!fromuser || (MusicUtils.mService == null))
				return;
			long now = SystemClock.elapsedRealtime();
			if ((now - mLastSeekEventTime) > 250) {
				mLastSeekEventTime = now;
				mPosOverride = mDuration * progress / 1000;
				try {
					MusicUtils.mService.seek(mPosOverride);
				} catch (RemoteException ex) {
					ex.printStackTrace();
				}

				if (!mFromTouch) {
					refreshNow();
					mPosOverride = -1;
				}
			}
		}

		@Override
		public void onStopTrackingTouch(SeekBar bar) {
			mPosOverride = -1;
			mFromTouch = false;
		}
	};

	// Total and current time
	private TextView mTotalTime, mCurrentTime;

	/**
	 * @return current time
	 */
	private long refreshNow() {
		if (MusicUtils.mService == null)
			return 500;
		try {
			long pos = mPosOverride < 0 ? MusicUtils.mService.position()
					: mPosOverride;
			long remaining = 1000 - (pos % 1000);
			if ((pos >= 0) && (mDuration > 0)) {
				mCurrentTime.setText(MusicUtils.makeTimeString(getActivity(),
						pos / 1000));

				if (MusicUtils.mService.isPlaying()) {
					mCurrentTime.setVisibility(View.VISIBLE);
					mCurrentTime.setTextColor(getResources().getColor(
							R.color.white));
					// Theme chooser
					ThemeUtils.setTextColor(getActivity(), mCurrentTime,
							"audio_player_text_color");
				} else {
					// blink the counter
					int col = mCurrentTime.getCurrentTextColor();
					mCurrentTime.setTextColor(col == getResources().getColor(
							R.color.white) ? getResources().getColor(
							R.color.transparent_white_cc) : getResources()
							.getColor(R.color.white));
					remaining = 500;
					// Theme chooser
					ThemeUtils.setTextColor(getActivity(), mCurrentTime,
							"audio_player_text_color");
				}

				mProgress.setProgress((int) (1000 * pos / mDuration));
			} else {
				mCurrentTime.setText("--:--");
				mProgress.setProgress(1000);
			}
			return remaining;
		} catch (RemoteException ex) {
			ex.printStackTrace();
		}
		return 500;
	}

	/**
	 * Update the list as needed
	 */
	private final BroadcastReceiver mMediaStatusReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (mBottomActionBar != null) {
				mBottomActionBar.updateBottomActionBar(getActivity());
			}
			mHandler.sendMessage(mHandler.obtainMessage(UPDATEINFO));
		}
	};

	@Override
	public void onStart() {
		super.onStart();
		IntentFilter filter = new IntentFilter();
		filter.addAction(ApolloService.PLAYSTATE_CHANGED);
		filter.addAction(ApolloService.META_CHANGED);
		getActivity().registerReceiver(mMediaStatusReceiver, filter);

		IntentFilter f = new IntentFilter();
		f.addAction(ApolloService.PLAYSTATE_CHANGED);
		f.addAction(ApolloService.META_CHANGED);
		getActivity().registerReceiver(mStatusListener, new IntentFilter(f));

		long next = refreshNow();
		queueNextRefresh(next);

		// WeakReference<VisualizerView> mView = new
		// WeakReference<VisualizerView>((VisualizerView)root.findViewById(R.id.visualizerView));
		// VisualizerUtils.updateVisualizerView(mView);
	}

	@Override
	public void onStop() {
		getActivity().unregisterReceiver(mMediaStatusReceiver);
		super.onStop();
	}

	/**
	 * Play and pause music
	 */
	private void doPauseResume() {
		try {
			if (MusicUtils.mService != null) {
				if (MusicUtils.mService.isPlaying()) {
					MusicUtils.mService.pause();
				} else {
					MusicUtils.mService.play();
				}
			}
		} catch (RemoteException ex) {
			ex.printStackTrace();
		}
	}

	// Handler
	private static final int REFRESH = 1, UPDATEINFO = 2;
	/**
	 * Update everything as the meta or playstate changes
	 */
	private final BroadcastReceiver mStatusListener = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(ApolloService.META_CHANGED))
				mHandler.sendMessage(mHandler.obtainMessage(UPDATEINFO));
		}
	};

	/**
	 * @param delay
	 */
	private void queueNextRefresh(long delay) {
		if (!paused) {
			Message msg = mHandler.obtainMessage(REFRESH);
			mHandler.removeMessages(REFRESH);
			mHandler.sendMessageDelayed(msg, delay);
		}
	}

	/**
	 * We need to refresh the time via a Handler
	 */
	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case REFRESH:
				long next = refreshNow();
				queueNextRefresh(next);
				break;
			case UPDATEINFO:
				updateMusicInfo();
				break;
			default:
				break;
			}
		}
	};



	String artistName;

	/**
	 * Update what's playing
	 */
	private void updateMusicInfo() {
		if (MusicUtils.mService == null) {
			return;
		}

		String artistName = MusicUtils.getArtistName();
		this.artistName = artistName;
		String albumName = MusicUtils.getAlbumName();
		String trackName = MusicUtils.getTrackName();
		String albumId = String.valueOf(MusicUtils.getCurrentAlbumId());
		mDuration = MusicUtils.getDuration();
		mTotalTime.setText(MusicUtils.makeTimeString(getActivity(),
				mDuration / 1000));

		ImageInfo mInfo = new ImageInfo();
		mInfo.type = TYPE_ALBUM;
		mInfo.size = SIZE_THUMB;
		mInfo.source = SRC_FIRST_AVAILABLE;
		mInfo.data = new String[] { albumId, artistName, albumName };

		// ImageProvider.getInstance(getActivity()).loadImage(mAlbumArt, mInfo);
		//searchMusic(trackName);
		// Theme chooser
		ThemeUtils.setTextColor(getActivity(), mTotalTime,
				"audio_player_text_color");
		mPlay.setSelected(MusicUtils.isPlaying());
	}
/*
	public void searchMusic(String nameORsinger) {

		RequestParams params = new RequestParams();
		params.put("all", nameORsinger);
		params.put("ft", "music");
		params.put("itemset", "web_2013");
		params.put("client", "kt");
		params.put("pn", "1");
		params.put("rn", "1");
		params.put("rformat", "json");
		params.put("encoding", "utf8");
		// ?q=周杰伦&page=1&size=3
		// showToast("参数:"+params);
		MyApp.getNet().getReq(NetConstants.SERVER_KUWO_MUSIC_URL, params,
				new AsyncHttpResponseHandler() {
					@Override
					public void onFailure(int arg0, Header[] arg1, byte[] arg2,
							Throwable arg3) {
						if (arg2 != null) {
							// String data = new String(arg2);
							// showToast("失败"*//*:" + data*//*);
						}
						// showToast("失败..." +"state:"+arg0);
					}

					@Override
					public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
						if (arg2 != null) {
							Log.d("CGQ", new String(arg2));
							Result_KuWo root = JSON.parseObject(arg2,
									Result_KuWo.class);
							int musicNumber = Integer
									.valueOf(root.getAbsList().get(0)
											.getMUSICRID()
											.replace("MUSIC_", ""));
							searchMusicImage(musicNumber);
							// Toast.makeText(getActivity(), +"",
							// Toast.LENGTH_SHORT).show();
							// showToast("成功"+root.getAbsList().get(1).getMUSICRID()+"条");
							*//*
							 * musicList.addAll(root.getData());
							 * sadapter.notifyDataSetChanged();
							 *//*
							// showToast("成功"+root.getRows()+"条");
						}
						// showToast("成功...");
					}
				});
	}*/

	String img_url = "";

	/*public void searchMusicImage(int rid) {
		// &rid=[可为空]from=pc&json=[json数据排放方式,一般为1]&version=1&width=[写真宽度]&height=[写真高度]
		// ?type=rid_pic&pictype=url&size=[图片大小,一般为70]&rid=[歌曲id]
		RequestParams params = new RequestParams();
		params.put("type", "rid_pic");
		params.put("pictype", "url");
		params.put("size", 70);
		params.put("rid", rid);
		// ?q=周杰伦&page=1&size=3
		// showToast("参数:"+params);
		MyApp.getNet().getReq(NetConstants.SERVER_KUWO_IMAGE_HEAD_URL, params,
				new AsyncHttpResponseHandler() {
					@Override
					public void onFailure(int arg0, Header[] arg1, byte[] arg2,
							Throwable arg3) {
						if (arg2 != null) {
							// String data = new String(arg2);
							// showToast("失败"*//*:" + data*//*);
						}
						// showToast("失败..." +"state:"+arg0);
					}

					@Override
					public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
						if (arg2 != null) {
							Log.d("CGQ", new String(arg2));
							img_url = new String(arg2);
							MyApp.imageLoader.displayImage(img_url, mAlbumArt);
							// Toast.makeText(getActivity(), new String(arg2),
							// Toast.LENGTH_SHORT).show();
							// Result_KuWo root = JSON.parseObject(arg2,
							// Result_KuWo.class);
							// Toast.makeText(getActivity(), +"",
							// Toast.LENGTH_SHORT).show();
							// showToast("成功"+root.getAbsList().get(1).getMUSICRID()+"条");
							*//*
							 * musicList.addAll(root.getData());
							 * sadapter.notifyDataSetChanged();
							 *//*
							// showToast("成功"+root.getRows()+"条");
						}
						// showToast("成功...");
					}
				});
	}*/
}
