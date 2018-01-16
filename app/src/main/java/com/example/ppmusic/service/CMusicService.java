package com.example.ppmusic.service;

import java.util.ArrayList;
import java.util.List;

import com.example.ppmusic.MusicLoader;
import com.example.ppmusic.MyApp;
import com.example.ppmusic.PreferencesService;
import com.example.ppmusic.R;
import com.example.ppmusic.activities.MainActivity;
import com.example.ppmusic.base.MyMediaPlayer;
import com.example.ppmusic.bean.MusicInfo;
import com.example.ppmusic.utils.DBUtils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RemoteViews;
import android.widget.Toast;

public class CMusicService extends Service {

	private static final String TAG = "com.huan.NATURE_SERVICE";
	public static final String MUSICS = "com.example.nature.MUSIC_LIST";
	public static final String NATURE_SERVICE = "com.example.nature.NatureService";
	public static final String ACTION_UPDATE_PROGRESS = "musicplayer.UPDATE_PROGRESS";
	public static final String ACTION_UPDATE_DURATION = "musicplayer.UPDATE_DURATION";
	public static final String ACTION_UPDATE_CURRENT_MUSIC = "musicplayer.UPDATE_CURRENT_MUSIC";

	public MyMediaPlayer mediaPlayer;
	/** 是否在播放 */
	public boolean isPlaying = false;
	/** 是否第一次播放 */
	private boolean isFirst = true;
	/** 耳机插入状态， true 为插入，false 为没插入 */
	private boolean hasHeadphones = false;//
	/** 播放进度 **/
	public int current_position;
	/** 播放歌曲的索引值 **/
	public int current_index;
	/** 歌曲在数据库中的id ***/
	private int current_id = 0;
	public MusicInfo current_musicInfo;
	public List<MusicInfo> current_music_list = new ArrayList<MusicInfo>();

	private NatureBinder natureBinder;

	public int currentMode = 2; // default sequence playing

	public static final String[] MODE_DESC = { "单曲循环", "随机播放", "顺序播放" };

	public static final int MODE_ONE_LOOP = 0;
	public static final int MODE_RANDOM = 1;
	public static final int MODE_SEQUENCE = 2;

	public void onCreate() {
		// current_music_list = MusicLoader.instance(getContentResolver())
		// .getMusicList();
		/** 初始化播放列表 **/
		current_music_list.clear();
		current_music_list = DBUtils.queryAllDB(getBaseContext());
		initMediaPlayer();
		super.onCreate();
		/** 初始化播放模式 **/
		currentMode = MyApp.getPreferencesService().getInt("CurrentMode", 2);
		/** 初始化播放进度 **/
		current_position = MyApp.getPreferencesService().getInt(
				"CurrentPosition", 0);
		/** 初始化当前歌曲 **/
		current_id = MyApp.getPreferencesService().getInt("MusicId", 1);
		MusicInfo musicInfo = DBUtils.queryById(getBaseContext(), current_id);
		setCurrent_musicInfo(musicInfo);

		// 给耳机广播绑定响应的过滤器
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("android.intent.action.HEADSET_PLUG");
		HeadsetReceiver headsetReceiver = new HeadsetReceiver();
		registerReceiver(headsetReceiver, intentFilter);

		/** 初始化通知栏 **/
		initNotificationReceiver();
	}

	public void onDestroy() {
		MyApp.getPreferencesService().save("MusicId",
				(int) current_musicInfo.getId());
		MyApp.getPreferencesService().save("CurrentPosition", current_position);
		if (mediaPlayer != null) {
			mediaPlayer.release();
			mediaPlayer = null;
		}
	}

	/**
	 * initialize the MediaPlayer
	 */
	private void initMediaPlayer() {
		mediaPlayer = new MyMediaPlayer() {
			@Override
			public void onPaused() {
				current_position = mediaPlayer.getCurrentPosition();
				MyApp.getPreferencesService().save("MusicId",
						(int) current_musicInfo.getId());
				//mediaPlayer.seekTo(current_position);
				isPlaying = false;
				// 通知状态栏改变
				if (mNotificationManager != null) {
					showButtonNotify();
				}
			}

			@Override
			public void onStarted() {
				MyApp.getPreferencesService().save("MusicId",
						(int) current_musicInfo.getId());
				isPlaying = true;
				// 通知状态栏改变
				if (mNotificationManager != null) {
					showButtonNotify();
				}
			}
		};
		mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		mediaPlayer.setOnPreparedListener(new OnPreparedListener() {
			@Override
			public void onPrepared(MediaPlayer mp) {
				isPlaying = true;
				mediaPlayer.start();
				mediaPlayer.seekTo(current_position);
				showButtonNotify();
			}
		});

		mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				// 播放结束进度归零
				current_position = 0;
				// 判断是否继续播放及播放模式
				if (isPlaying) {
					MyApp.getPreferencesService().save("CurrentPosition", 0);
					switch (currentMode) {
					case MODE_ONE_LOOP:// currentMode = MODE_ONE_LOOP.");
						mediaPlayer.start();
						break;
					case MODE_RANDOM:// currentMode = MODE_RANDOM.");
						play(getRandomPosition(), 0);
						break;
					case MODE_SEQUENCE:// currentMode = MODE_SEQUENCE.");
						if (current_index < current_music_list.size() - 1) {
							playNext();
						} else {
							isPlaying = false;
							mediaPlayer.seekTo(0);
						}
						break;
					default:
						Log.v(TAG, "No Mode selected! How could that be ?");
						break;
					}
				}
				// 通知状态栏改变
				if (mNotificationManager != null) {
					showButtonNotify();
				}
			}
		});
	}

	// 随机歌曲
	private int getRandomPosition() {
		int random = (int) (Math.random() * (current_music_list.size() - 1));
		return random;
	}

	private void sendBrodcast() {
		Intent i = new Intent("com.paopaomusic.android.USER_ACTION");
		sendBroadcast(i);
	}

	/**
	 * 播放不带参数
	 */
	public void play() {
		if (current_musicInfo!=null) {
			if (!isPlaying) {
				if(!isFirst){
					mediaPlayer.start();
				}else{
					try {
						//setCurrent_musicInfo(current_musicInfo);
						mediaPlayer.reset();
						mediaPlayer.setDataSource("http://fs.web.kugou.com//9feeacb1efffcf84ffea81ce1bf17465//58df65f3//G005//M08//05//10//RQ0DAFS8P2uACBc6AEPvvqMvhZg665.mp3"/*current_musicInfo.getUrl()*/);
						mediaPlayer.prepareAsync();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}else{
			if(current_music_list!=null && current_music_list.size()!=0){
				//根据模式选择当前音乐
				switch (currentMode) {
				case MODE_RANDOM:// currentMode = MODE_RANDOM.");
					current_musicInfo = current_music_list.get(getRandomPosition());
					break;
				default:
					current_musicInfo = current_music_list.get(0);
					break;
				}
				try {
					setCurrent_musicInfo(current_musicInfo);
					mediaPlayer.reset();
					mediaPlayer.setDataSource(current_musicInfo.getUrl());
					mediaPlayer.prepareAsync();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}else{
				Toast.makeText(getApplicationContext(), "歌单为空", Toast.LENGTH_SHORT).show();
			}
		}
	}

	public void pause() {
		if (current_musicInfo!=null) {
			if (isPlaying) {
				mediaPlayer.pause();
			}
		}
	}

	public void play(MusicInfo musicInfo) {
		setCurrent_musicInfo(musicInfo);
		mediaPlayer.reset();
		try {
			mediaPlayer.setDataSource(current_musicInfo.getUrl());
			mediaPlayer.prepareAsync();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void play(int index, int position) {
		setCurrent_musicInfo(current_music_list.get(index));
		mediaPlayer.reset();
		try {
			mediaPlayer.setDataSource(current_musicInfo.getUrl());
			mediaPlayer.prepareAsync();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void play(List<MusicInfo> musicInfo, int index, int position) {
		setCurrent_musicInfo(current_music_list.get(index));
		mediaPlayer.reset();
		try {
			mediaPlayer.setDataSource(current_musicInfo.getUrl());
			mediaPlayer.prepareAsync();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean setCurrent_musicInfo(MusicInfo current_musicInfo) {

		// 判断歌曲是否等于当前歌曲
		if (this.current_musicInfo != null
				&& current_musicInfo.getUrl().equals(
						this.current_musicInfo.getUrl())) {
			return true;
		}
		if (current_musicInfo == null) {
			this.current_index = 0;
			this.current_id = 0;
			this.current_position = 0;
			this.current_musicInfo = null;
			return false;
		}
		// 歌曲列表中是否包含此歌曲
		boolean isContains = false;
		for (int i = 0; i < current_music_list.size(); i++) {
			if (current_music_list.get(i).getUrl()
					.equals(current_musicInfo.getUrl())) {
				isContains = true;
				break;
			}
		}
		// 如果包含查到歌曲在歌单里的索引值
		if (isContains) {
			for (int i = 0; i < current_music_list.size(); i++) {
				if (current_music_list.get(i).getUrl()
						.equals(current_musicInfo.getUrl())) {
					this.current_index = i;
					break;
				}
			}
		} else {// 如果不包含往数据库里添加
			DBUtils.insertDB(getApplicationContext(), current_musicInfo);
			current_music_list = DBUtils.queryAllDB(getApplicationContext());
			for (int i = 0; i < current_music_list.size(); i++) {
				if (current_music_list.get(i).getUrl().equals(current_musicInfo.getUrl())) {
					current_musicInfo.setId(current_music_list.get(i).getId());
					this.current_index = i;
					break;
				}
			}
		}
		this.current_id = (int) current_musicInfo.getId();
		this.current_position = current_musicInfo.getPosition();
		this.current_musicInfo = current_musicInfo;
		MyApp.getPreferencesService().save("MusicId", current_id);
		return true;
	}

	// 上一首
	public void playLast() {
		switch (currentMode) {
		case MODE_ONE_LOOP:
			play(current_index, 0);
			break;
		case MODE_SEQUENCE:
			if (current_index - 1 < 0) {
				Toast.makeText(this, "已经是第一首了", Toast.LENGTH_SHORT).show();
			} else {
				play(current_index - 1, 0);
			}
			break;
		case MODE_RANDOM:
			play(getRandomPosition(), 0);
			break;
		}
	}

	// 下一首
	public void playNext() {
		switch (currentMode) {
		case MODE_ONE_LOOP:
			play(current_index, 0);
			break;
		case MODE_SEQUENCE:
			if (current_index + 1 == current_music_list.size()) {
				Toast.makeText(this, "已经是最后一首了", Toast.LENGTH_SHORT).show();
			} else {
				play(current_index + 1, 0);
			}
			break;
		case MODE_RANDOM:
			play(getRandomPosition(), 0);
			break;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		natureBinder = new NatureBinder(this);
		return natureBinder;
	}

	/*********************************** 判断耳机插入状态 *****************************************************************************/
	// 自己定义的广播接收器 判断耳机插入状态
	public class HeadsetReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.hasExtra("state")) {
				if (0 == intent.getIntExtra("state", 0)) {
					hasHeadphones = false;
					pause();
					// Toast.makeText(context, "耳机未插入",
					// Toast.LENGTH_SHORT).show();
				} else if (1 == intent.getIntExtra("state", 0)) {
					hasHeadphones = true;
					// Toast.makeText(context, "耳机已插入",
					// Toast.LENGTH_SHORT).show();
				}
			}
		}

	}

	/*********************************** 通知栏按钮广播 *****************************************************************************/
	/** 按钮：显示自定义通知 */
	private Button btn_show_custom;
	/** 按钮：显示自定义带按钮的通知 */
	private Button btn_show_custom_button;
	/** Notification 的ID */
	int notifyId = 101;
	/** NotificationCompat 构造器 */
	NotificationCompat.Builder mBuilder;
	/** 通知栏按钮广播 */
	public ButtonBroadcastReceiver bReceiver;
	/** 通知栏按钮点击事件对应的ACTION */
	public final static String ACTION_BUTTON = "com.paopaomusic.notifications.intent.action.ButtonClick";

	/** 带按钮的通知栏点击广播接收 */
	public void initNotificationReceiver() {
		mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		bReceiver = new ButtonBroadcastReceiver();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(ACTION_BUTTON);
		registerReceiver(bReceiver, intentFilter);
	}

	public final static String INTENT_BUTTONID_TAG = "ButtonId";
	/** 点击进入主界面 ID */
	public final static int BUTTON_Main_ID = 0;
	/** 上一首 按钮点击 ID */
	public final static int BUTTON_PREV_ID = 1;
	/** 播放/暂停 按钮点击 ID */
	public final static int BUTTON_PALY_ID = 2;
	/** 下一首 按钮点击 ID */
	public final static int BUTTON_NEXT_ID = 3;

	/** Notification管理 */
	public NotificationManager mNotificationManager;

	/**
	 * 广播监听按钮点击时间
	 */
	public class ButtonBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if (action.equals(ACTION_BUTTON)) {
				// 通过传递过来的ID判断按钮点击属性或者通过getResultCode()获得相应点击事件
				int buttonId = intent.getIntExtra(INTENT_BUTTONID_TAG, 0);
				switch (buttonId) {
				case BUTTON_Main_ID:// "主页"
					Toast.makeText(context, "耳机已插入",Toast.LENGTH_SHORT).show();
					Intent intent2 = new Intent(getApplicationContext(),MainActivity.class);
					intent2.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP); 
					getApplication().startActivity(intent2); 
					break;
				case BUTTON_PREV_ID:// "上一首"
					playLast();
					break;
				case BUTTON_PALY_ID:
					if (!isPlaying) {
						play();// "开始播放"
					} else {
						pause();// "已暂停";
					}
					break;
				case BUTTON_NEXT_ID:// "下一首"
					playNext();
					break;
				default:
					break;
				}
			}
		}
	}

	/**
	 * 带按钮的通知栏
	 */
	public void showButtonNotify() {
		if (current_musicInfo == null) {
			return;
		}
		NotificationCompat.Builder mBuilder = new Builder(this);
		RemoteViews mRemoteViews = new RemoteViews(getPackageName(),
				R.layout.view_custom_button);
		mRemoteViews.setImageViewResource(R.id.custom_song_icon,
				R.drawable.ic_launcher);
		// API3.0 以上的时候显示按钮，否则消失
		mRemoteViews.setTextViewText(R.id.tv_custom_song_singer,
				current_musicInfo.getArtist());
		mRemoteViews.setTextViewText(R.id.tv_custom_song_name,
				current_musicInfo.getTitle());
		// 如果版本号低于（3。0），那么不显示按钮
		if (getSystemVersion() <= 9) {
			mRemoteViews.setViewVisibility(R.id.ll_custom_button, View.GONE);
		} else {
			mRemoteViews.setViewVisibility(R.id.ll_custom_button, View.VISIBLE);
			//
			if (isPlaying) {
				mRemoteViews.setImageViewResource(R.id.btn_custom_play,
						R.drawable.btn_pause);
			} else {
				mRemoteViews.setImageViewResource(R.id.btn_custom_play,
						R.drawable.btn_play);
			}
		}

		// 点击的事件处理
		Intent buttonIntent = new Intent(ACTION_BUTTON);
		/* 点击进入主界面事件 */
		buttonIntent.putExtra(INTENT_BUTTONID_TAG, BUTTON_Main_ID);
		//点击的意图ACTION是跳转到Intent
		Intent resultIntent = new Intent(this, MainActivity.class);
		resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		Bundle bundle = new Bundle();
		bundle.putBoolean("FirstLaucher", false);
		resultIntent.putExtras(bundle);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		mRemoteViews.setOnClickPendingIntent(R.id.rel_notify, pendingIntent);
		/* 上一首按钮 */
		buttonIntent.putExtra(INTENT_BUTTONID_TAG, BUTTON_PREV_ID);
		// 这里加了广播，所及INTENT的必须用getBroadcast方法
		PendingIntent intent_prev = PendingIntent.getBroadcast(this, 1,
				buttonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		mRemoteViews.setOnClickPendingIntent(R.id.btn_custom_prev, intent_prev);
		/* 播放/暂停 按钮 */
		buttonIntent.putExtra(INTENT_BUTTONID_TAG, BUTTON_PALY_ID);
		PendingIntent intent_paly = PendingIntent.getBroadcast(this, 2,
				buttonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		mRemoteViews.setOnClickPendingIntent(R.id.btn_custom_play, intent_paly);
		/* 下一首 按钮 */
		buttonIntent.putExtra(INTENT_BUTTONID_TAG, BUTTON_NEXT_ID);
		PendingIntent intent_next = PendingIntent.getBroadcast(this, 3,
				buttonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		mRemoteViews.setOnClickPendingIntent(R.id.btn_custom_next, intent_next);

		mBuilder.setContent(mRemoteViews)
				.setContentIntent(
						getDefalutIntent(Notification.FLAG_ONGOING_EVENT))
				.setWhen(System.currentTimeMillis())// 通知产生的时间，会在通知信息里显示
				.setTicker("正在播放").setPriority(Notification.PRIORITY_DEFAULT)// 设置该通知优先级
				.setOngoing(true).setSmallIcon(R.drawable.ic_launcher);
		Notification notify = mBuilder.build();
		notify.flags = Notification.FLAG_ONGOING_EVENT;
		// 会报错，还在找解决思路
		// notify.contentView = mRemoteViews;
		// notify.contentIntent = PendingIntent.getActivity(this, 0, new
		// Intent(), 0);
		mNotificationManager.notify(200, notify);
	}

	/**
	 * 初始化要用到的系统服务
	 */
	private void initService() {
		mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
	}

	/**
	 * 清除当前创建的通知栏
	 */
	public void clearNotify(int notifyId) {
		mNotificationManager.cancel(notifyId);// 删除一个特定的通知ID对应的通知
		// mNotification.cancel(getResources().getString(R.string.app_name));
	}

	/**
	 * 清除所有通知栏
	 * */
	public void clearAllNotify() {
		mNotificationManager.cancelAll();// 删除你发的所有通知
	}

	/**
	 * @获取默认的pendingIntent,为了防止2.3及以下版本报错
	 * @flags属性: 在顶部常驻:Notification.FLAG_ONGOING_EVENT 点击去除：
	 *           Notification.FLAG_AUTO_CANCEL
	 */
	public PendingIntent getDefalutIntent(int flags) {
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 1,
				new Intent(), flags);
		return pendingIntent;
	}

	/**
	 * 获取当前应用版本号
	 * 
	 * @param context
	 * @return version
	 * @throws Exception
	 */
	public static String getAppVersion(Context context) throws Exception {
		// 获取packagemanager的实例
		PackageManager packageManager = context.getPackageManager();
		// getPackageName()是你当前类的包名，0代表是获取版本信息
		PackageInfo packInfo = packageManager.getPackageInfo(
				context.getPackageName(), 0);
		String versionName = packInfo.versionName;
		return versionName;
	}

	/**
	 * 获取当前系统SDK版本号
	 */
	public static int getSystemVersion() {
		/* 获取当前系统的android版本号 */
		int version = android.os.Build.VERSION.SDK_INT;
		return version;
	}
}