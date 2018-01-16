package com.example.ppmusic.fragment;

import android.R.anim;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ppmusic.MyApp;
import com.example.ppmusic.R;
import com.example.ppmusic.activities.MainActivity;
import com.example.ppmusic.activities.SettingActivity;
import com.example.ppmusic.base.MyBaseFragment;
import com.example.ppmusic.bean.MusicInfo;
import com.example.ppmusic.cache.ImageInfo;
import com.example.ppmusic.constants.Constants;
import com.example.ppmusic.helpers.utils.ApolloUtils;
import com.example.ppmusic.helpers.utils.MusicUtils;
import com.example.ppmusic.helpers.utils.ThemeUtils;
import com.example.ppmusic.helpers.utils.VisualizerUtils;
import com.example.ppmusic.service.ApolloService;
import com.example.ppmusic.ui.widgets.VisualizerView;
import com.example.ppmusic.utils.IntentUtil;
import com.example.ppmusic.view.custom.ImageView_CD;
import com.example.ppmusic.view.impl.DefaultLrcBuilder;
import com.example.ppmusic.view.impl.LrcRow;
import com.example.ppmusic.view.impl.LrcView;
import com.example.ppmusic.views.ILrcBuilder;
import com.example.ppmusic.views.ILrcView;
import com.example.ppmusic.views.ILrcViewListener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.example.ppmusic.constants.Constants.SIZE_THUMB;
import static com.example.ppmusic.constants.Constants.SRC_FIRST_AVAILABLE;
import static com.example.ppmusic.constants.Constants.TYPE_ALBUM;
import static com.example.ppmusic.service.ApolloService.SHUFFLE_NONE;

public class MainFragment2 extends MyBaseFragment implements OnClickListener {

	private int TIME = 1000;
	private boolean isManual = false;
	private MusicInfo currentMusic = null;
	MainActivity mainActivity;
	private static int width, height;
	private LayoutParams params = new LayoutParams(0, 0);
	private static Boolean isClick = false;
	private Animation animationTranslate, animationRotate, animationScale,
			animationAlpha;
	private Button buttonCamera, buttonWith, buttonFavorite, buttonMusic,
			buttonThought;

	private ImageButton mPrev, mNext,mRepeat;
	private Toast mToast;

	// 自定义LrcView，用来展示歌词
	ILrcView mLrcView;
	static ImageView_CD iv_music_bar3;
	static ImageView iv_music_bar1, iv_music_bar4;
	private TextView tv_music_name, tv_singer_name;

	@Override
	public View setContentUI(LayoutInflater inflater, ViewGroup container) {
		return inflater.inflate(R.layout.music_1, container, false);
	}

	/**
     * Update everything as the meta or playstate changes
     */
    private final BroadcastReceiver mStatusListener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ApolloService.META_CHANGED)){//更新歌曲信息
                mHandler.sendMessage(mHandler.obtainMessage(UPDATEINFO));
            }else if(intent.getAction().equals(ApolloService.PLAYSTATE_CHANGED)){
            	mHandler.sendMessage(mHandler.obtainMessage(UPDATEINFO));
				mHandler.sendMessage(mHandler.obtainMessage(SongChanged));
            }
          //  setShuffleButtonImage();
        }
    };
    

    // Handler
    private static final int REFRESH = 1, UPDATEINFO = 2,UPDATESTATE = 3,SongChanged = 4;
    
    /**
     * We need to refresh the time via a Handler
     */
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case REFRESH:
                    break;
                case UPDATEINFO:
                    updateMusicInfo();
                    break;
                case UPDATESTATE:
                    updateMusicState();
                    break;
				case SongChanged:

					break;
                default:
                    break;
            }
        }
    };

    private boolean isPlaying_old = false;
    
    private String artistName ;
    private String trackName ;
    /**
     * Update what's playing
     */
    private void updateMusicInfo() {
        if (MusicUtils.mService == null) {
            return;
        }

		setRepeatButtonImage();
        String artistName = MusicUtils.getArtistName();
        this.artistName = artistName;
        String albumName = MusicUtils.getAlbumName();
        String trackName = MusicUtils.getTrackName();
        this.trackName = artistName;
        
        String albumId = String.valueOf(MusicUtils.getCurrentAlbumId());
        tv_music_name.setText(trackName);
        tv_singer_name.setText(albumName + " - " + artistName);

        ImageInfo mInfo = new ImageInfo();
        mInfo.type = TYPE_ALBUM;
        mInfo.size = SIZE_THUMB;
        mInfo.source = SRC_FIRST_AVAILABLE;
        mInfo.data = new String[]{ albumId , artistName, albumName };
        
        //ImageProvider.getInstance( getActivity() ).loadImage( mAlbumArt, mInfo );

        // Theme chooser
        ThemeUtils.setTextColor(getActivity(), tv_music_name, "audio_player_text_color");
        ThemeUtils.setTextColor(getActivity(), tv_singer_name, "audio_player_text_color");
        
        if (MusicUtils.isPlaying()) {
			if (!isPlaying_old&&currentMusic!=null) {
				//getLrc(trackName, artistName);
				
			}
			if (iv_music_bar3.getAnimation() == null && !isPlaying_old) {
				isPlaying_old = true;
				animation(0, false);
			}
			// 获取歌曲播放的位置
			final long timePassed = MusicUtils.Position();//MyApp.natureBinder.getCurrentPosition();
			// 滚动歌词
			mLrcView.seekLrcToTime(timePassed);
		} else {
			if (iv_music_bar3.getAnimation() != null && isPlaying_old) {
				isPlaying_old = false;
				animation(1, false);
			}
		}

		if (MusicUtils.mService != null && MusicUtils.getCurrentAudioId() != -1) {
			if (MusicUtils.isFavorite(getActivity(), MusicUtils.getCurrentAudioId())) {
				buttonFavorite.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.record_card_like_s));
			} else {
				buttonFavorite.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.record_card_like_h));
				// Theme chooser
				//ThemeUtils.setActionBarItem(this, favorite, "apollo_favorite_normal");
			}
		}
    }
	
    /**
     * Update state of playing
     */
    private void updateMusicState() {
    	if (MusicUtils.isPlaying()) {
			if (!isPlaying_old) {
				//getLrc(trackName, artistName);
			}
    		if(!isPlaying_old){
    			initLrc();
    		}
			if (iv_music_bar3.getAnimation() == null && !isPlaying_old) {
				isPlaying_old = true;
				animation(0, false);
			}
			// 获取歌曲播放的位置
			final long timePassed = MusicUtils.Position();//MyApp.natureBinder.getCurrentPosition();
			// 滚动歌词
			mLrcView.seekLrcToTime(timePassed);
		} else {
			if (iv_music_bar3.getAnimation() != null && isPlaying_old) {
				isPlaying_old = false;
				animation(1, false);
			}
		}
    }
    
    @Override
    public void onStart() {
        super.onStart();
        IntentFilter f = new IntentFilter();
        f.addAction(ApolloService.PLAYSTATE_CHANGED);
        f.addAction(ApolloService.META_CHANGED);
        getActivity().registerReceiver(mStatusListener, new IntentFilter(f));

        WeakReference<VisualizerView> mView = new WeakReference<VisualizerView>((VisualizerView)rootView.findViewById(R.id.visualizerView));
        VisualizerUtils.updateVisualizerView(mView);

    }

	public static  PointF getCDBoxPosition(){
		PointF p =  new PointF((iv_music_bar4.getX()+(iv_music_bar4.getWidth()/2)),(iv_music_bar4.getY()+(iv_music_bar4.getHeight()/2)));
		return p;
	}
    
	@Override
	public void init() {
		mainActivity = (MainActivity) getActivity();

		tv_music_name = (TextView) rootView.findViewById(R.id.tv_music_name);
		tv_singer_name = (TextView) rootView.findViewById(R.id.tv_singer_name);
		iv_music_bar1 = (ImageView) findViewById(R.id.iv_music_bar1);
		iv_music_bar1.setOnClickListener(this);
		iv_music_bar3 = (ImageView_CD) findViewById(R.id.iv_music_bar3);
		iv_music_bar4 = (ImageView) findViewById(R.id.iv_music_bar4);

		mRepeat = (ImageButton) findViewById(R.id.iv_music_model);
		mRepeat.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setButtonImage();
				//toggleShuffle();
				//cycleRepeat();
			}
		});
		initialButton();

		mLrcView = (ILrcView) findViewById(R.id.lrcView);

		// 设置自定义的LrcView上下拖动歌词时监听
		mLrcView.setListener(new ILrcViewListener() {
			// 当歌词被用户上下拖动的时候回调该方法,从高亮的那一句歌词开始播放
			public void onLrcSeeked(int newPosition, LrcRow row) {
				/*
				 * if (mPlayer != null) { Log.d(TAG, "onLrcSeeked:" + row.time);
				 * mPlayer.seekTo((int) row.time); }
				 */
			}
		});

		handler.postDelayed(runnable, TIME); // 每隔1s执行
	}

	/*private String getLrc(final String title, String singer) {
		RequestParams params = new RequestParams();
		params.put("size", "20");
		// ?q=周杰伦&page=1&size=3
		// showToast("参数:"+params);
		MyApp.getNet().getReq(
				NetConstants.SERVER_GECIMI_LRC_URL + title + "/" + singer,
				null, new AsyncHttpResponseHandler() {

					@Override
					public void onFailure(int arg0, Header[] arg1, byte[] arg2,
							Throwable arg3) {
						if (arg2 != null) {
							// String data = new String(arg2);
							showToast("失败"*//* :" + data *//*);
						}
						showToast("失败..." + "state:" + arg0);
					}

					@Override
					public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
						if (arg2 != null) {
							Root root = JSON.parseObject(arg2, Root.class);
							if (root.getResult().size() > 0) {
								String url = root.getResult().get(0).getLrc();
								String name = "";
								try {
									name = URLDecoder.decode(root.getResult()
											.get(0).getSong(), "UTF-8");
								} catch (UnsupportedEncodingException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								DownloadLrc downloadLrc = new DownloadLrc(url,
										name);
								downloadLrc.start();
								// 解析歌词构造器
								ILrcBuilder builder = new DefaultLrcBuilder();
								// 解析歌词返回LrcRow集合
								// List<LrcRow> rows = builder.getLrcRows(lrc);
								// 将得到的歌词集合传给mLrcView用来展示
								// mLrcView.setLrc(rows);

								// 开始播放歌曲并同步展示歌词
								beginLrcPlay();
								showToast("成功" + root.getCount() + "条");
							} else {
								getLrc(title);
							}
						}
						// String data = new String(arg2);
						// showToast("成功..."+data);
					}
				});

		return null;

	}*/

	/*private String getLrc(String name) {
		RequestParams params = new RequestParams();
		params.put("size", "20");
		// ?q=周杰伦&page=1&size=3
		// showToast("参数:"+params);
		MyApp.getNet().getReq(NetConstants.SERVER_GECIMI_LRC_URL + name, null,
				new AsyncHttpResponseHandler() {

					@Override
					public void onFailure(int arg0, Header[] arg1, byte[] arg2,
							Throwable arg3) {
						if (arg2 != null) {
							// String data = new String(arg2);
							showToast("失败"*//* :" + data *//*);
						}
						showToast("失败..." + "state:" + arg0);
					}

					@Override
					public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
						if (arg2 != null) {
							Root root = JSON.parseObject(arg2, Root.class);
							if (root.getResult().size() > 0) {
								String url = root.getResult().get(0).getLrc();
								String name = "";
								try {
									name = URLDecoder.decode(root.getResult()
											.get(0).getSong(), "UTF-8");
								} catch (UnsupportedEncodingException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								DownloadLrc downloadLrc = new DownloadLrc(url,
										name);
								downloadLrc.start();
								// 解析歌词构造器
								ILrcBuilder builder = new DefaultLrcBuilder();
								// 解析歌词返回LrcRow集合
								// List<LrcRow> rows = builder.getLrcRows(lrc);
								// 将得到的歌词集合传给mLrcView用来展示
								// mLrcView.setLrc(rows);

								// 开始播放歌曲并同步展示歌词
								beginLrcPlay();
								showToast("成功" + root.getCount() + "条");
							}
						}
						// String data = new String(arg2);
						// showToast("成功..."+data);
					}
				});

		return null;

	}*/


	private void setButtonImage(){
		if (MusicUtils.mService == null) {
			return;
		}
		try {
			int mode = MusicUtils.mService.getRepeatMode();
			int shuffle = MusicUtils.mService.getShuffleMode();

			if (mode == ApolloService.REPEAT_ALL&&shuffle == SHUFFLE_NONE) {//顺序播放切换成随机
				MusicUtils.mService.setShuffleMode(ApolloService.SHUFFLE_NORMAL);
				MusicUtils.mService.setRepeatMode(ApolloService.REPEAT_ALL);
				ApolloUtils.showToast(R.string.SHUFFLE, mToast, getActivity());
			}else if (shuffle == ApolloService.SHUFFLE_NORMAL
					|| shuffle == ApolloService.SHUFFLE_AUTO){//随机播放切换成单曲
				MusicUtils.mService.setRepeatMode(ApolloService.REPEAT_CURRENT);
				MusicUtils.mService.setShuffleMode(SHUFFLE_NONE);
				ApolloUtils.showToast(R.string.REPEAT_CURRENT, mToast, getActivity());
			}else if((mode == ApolloService.REPEAT_CURRENT&&shuffle == SHUFFLE_NONE)||mode == ApolloService.REPEAT_NONE){//单曲播放切换成循环
				MusicUtils.mService.setShuffleMode(SHUFFLE_NONE);
				MusicUtils.mService.setRepeatMode(ApolloService.REPEAT_ALL);
				ApolloUtils.showToast(R.string.REPEAT_ALL, mToast, getActivity());
			}

			setRepeatButtonImage();
		} catch (RemoteException ex) {
			ex.printStackTrace();
		}
	}


	/**
	 * Set the repeat images
	 */
	private void setRepeatButtonImage() {
		if (MusicUtils.mService == null)
			return;
		try {
			switch (MusicUtils.mService.getRepeatMode()) {
				case ApolloService.REPEAT_ALL:
					mRepeat.setImageResource(R.drawable.apollo_holo_light_repeat_all);
					break;
				case ApolloService.REPEAT_CURRENT:
					mRepeat.setImageResource(R.drawable.apollo_holo_light_repeat_one);
					break;
				default:
					mRepeat.setImageResource(R.drawable.apollo_holo_light_repeat_normal);
					// Theme chooser
					ThemeUtils.setImageButton(getActivity(), mRepeat,"apollo_repeat_normal");
					break;
			}
			if(MusicUtils.mService.getShuffleMode()!=ApolloService.SHUFFLE_NONE){
				mRepeat.setImageResource(R.drawable.apollo_holo_light_shuffle_on);
			}
		} catch (RemoteException ex) {
			ex.printStackTrace();
		}
	}

	File file = null;
	boolean hasDownload_lrc = false;

	public class DownloadLrc extends Thread {
		String urlStr;
		String name;

		DownloadLrc(String str, String name) {
			this.urlStr = str;
			this.name = name;
		}

		@Override
		public void run() {
			// String urlStr="http://172.17.54.91:8080/download/1.mp3";
			String path = "file";
			String fileName = name + ".lrc";

			OutputStream output = null;
			try {
				/*
				 * 通过URL取得HttpURLConnection 要网络连接成功，需在AndroidMainfest.xml中进行权限配置
				 * <uses-permission android:name="android.permission.INTERNET"
				 * />
				 */
				URL url = new URL(urlStr);
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				conn.connect();
				// 取得inputStream，并将流中的信息写入SDCard

				/*
				 * 写前准备 1.在AndroidMainfest.xml中进行权限配置 <uses-permission
				 * android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
				 * 取得写入SDCard的权限 2.取得SDCard的路径：
				 * Environment.getExternalStorageDirectory() 3.检查要保存的文件上是否已经存在
				 * 4.不存在，新建文件夹，新建文件 5.将input流中的信息写入SDCard 6.关闭流
				 */
				String SDCard = Environment.getExternalStorageDirectory() + "";
				String pathName = Constants.APP_PATH_DOWNLOAD + "/" + fileName;// SDCard
																				// +
																				// "/"+path+"/"+fileName;//文件存储路径

				file = new File(pathName);
				InputStream input = conn.getInputStream();
				if (file.exists()) {
					System.out.println("exits");
					hasDownload_lrc = true;
					current_music_title = name;
					return;
				} else {
					String dir = SDCard + "/" + path;
					new File(dir).mkdir();// 新建文件夹
					file.createNewFile();// 新建文件
					output = new FileOutputStream(file);
					// 读取大文件
					byte[] buffer = new byte[4 * 1024];
					while (input.read(buffer) != -1) {
						output.write(buffer);
					}
					output.flush();
					hasDownload_lrc = true;
					current_music_title = name;
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (output != null) {
						output.close();
					}
					System.out.println("success");
				} catch (IOException e) {
					System.out.println("fail");
					e.printStackTrace();
				}

			}
			super.run();
		}

	}

	public void initLrc() {
		// 从assets目录下读取歌词文件内容
		// String lrc = getFromAssets("test.lrc");
		// lrc = LrcGet.query("心跳", "王力宏");
		String lrc = getFromSDCard(file.getAbsoluteFile().toString());
		// 解析歌词构造器
		ILrcBuilder builder = new DefaultLrcBuilder();
		// 解析歌词返回LrcRow集合
		List<LrcRow> rows = builder.getLrcRows(lrc);
		// 将得到的歌词集合传给mLrcView用来展示
		mLrcView.setLrc(rows);

		// 开始播放歌曲并同步展示歌词
		beginLrcPlay();
		hasDownload_lrc = false;
	}

	public static Handler handler = new Handler();
	Runnable runnable = new Runnable() {

		@Override
		public void run() {
			// handler自带方法实现定时器
			try {
				handler.postDelayed(this, TIME);
				if (isManual) {
					isManual = false;
				} else {
					refreshUI();
					if (hasDownload_lrc) {
						initLrc();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};
	String current_music_title = "";

	private void refreshUI() {
		//Toast.makeText(getActivity(), "refreshUI", Toast.LENGTH_SHORT).show();
		/*currentMusic = MyApp.natureBinder.getCurrentMusic();
		if (currentMusic != null) {
			tv_music_name.setText(currentMusic.getTitle());
			tv_singer_name.setText(currentMusic.getArtist());
		}*/
		if (MusicUtils.isPlaying()/*MyApp.natureBinder.getStatus()*/) {
			if (!isPlaying_old&&currentMusic!=null) {
				//getLrc(currentMusic.getTitle(), currentMusic.getArtist());
			}
			if (iv_music_bar3.getAnimation() == null) {
				animation(0, false);
			}
			// 获取歌曲播放的位置
			final long timePassed = MusicUtils.Position();/*MyApp.natureBinder.getCurrentPosition()*/;
			// 滚动歌词
			mLrcView.seekLrcToTime(timePassed);
		} else {
			if (iv_music_bar3.getAnimation() != null) {
				animation(1, false);
			}
		}
		// animation(1);
	}

	private void doAnimation1(boolean checked) {
		isManual = true;
		if (checked != MyApp.getNatureBinder().getStatus()) {
			if (checked) {
				if (iv_music_bar3.getAnimation() == null) {
					animation(0, true);
				}
			} else {
				if (iv_music_bar3.getAnimation() != null) {
					animation(1, true);
				}
			}
		}
	}

	long testTime = System.currentTimeMillis();

	@Override
	public void onClick(View v) {
		Bundle bundle = new Bundle();
	}

	/**
	 * 
	 * @param st
	 *            0 开始 1结束
	 * @param onlyShow
	 *            是否是只作为数据展示
	 */
	public void animation(int st, final boolean onlyShow) {
		LinearInterpolator lir = new LinearInterpolator();
		if (st == 0) {
			AnimationSet animationSet1 = new AnimationSet(true);
			RotateAnimation rotateAnimation1 = new RotateAnimation(0, 25,
					Animation.RELATIVE_TO_SELF, 0.5f,
					Animation.RELATIVE_TO_SELF, 0.1f);
			rotateAnimation1.setDuration(300);
			rotateAnimation1.setFillAfter(true);
			iv_music_bar1.startAnimation(rotateAnimation1);
			rotateAnimation1.setAnimationListener(new AnimationListener() {

				@Override
				public void onAnimationStart(Animation arg0) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onAnimationRepeat(Animation arg0) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onAnimationEnd(Animation arg0) {
					if (onlyShow) {
						MyApp.getNatureBinder().startPlay();
					}
				}
			});
			RotateAnimation rotateAnimation4 = new RotateAnimation(0, 360,
					Animation.RELATIVE_TO_SELF, 0.5f,
					Animation.RELATIVE_TO_SELF, 0.5f);
			rotateAnimation4.setInterpolator(lir);
			rotateAnimation4.setDuration(20000);
			rotateAnimation4.setRepeatMode(rotateAnimation4.RESTART);
			rotateAnimation4.setRepeatCount(10000);
			iv_music_bar4.startAnimation(rotateAnimation4);
			RotateAnimation rotateAnimation3 = new RotateAnimation(0, -360,
					Animation.RELATIVE_TO_SELF, 0.5f,
					Animation.RELATIVE_TO_SELF, 0.5f);
			rotateAnimation3.setInterpolator(lir);
			rotateAnimation3.setDuration(20000);
			rotateAnimation3.setRepeatMode(rotateAnimation4.RESTART);
			rotateAnimation3.setRepeatCount(1000000);
			iv_music_bar3.startAnimation(rotateAnimation3);
		} else {
			AnimationSet animationSet1 = new AnimationSet(true);
			RotateAnimation rotateAnimation1 = new RotateAnimation(0, 0,
					Animation.RELATIVE_TO_SELF, 0.5f,
					Animation.RELATIVE_TO_SELF, 0.1f);
			animationSet1.addAnimation(rotateAnimation1);
			rotateAnimation1.setDuration(300);
			animationSet1.setAnimationListener(new AnimationListener() {

				@Override
				public void onAnimationStart(Animation arg0) {
					if (onlyShow) {
						MyApp.getNatureBinder().stopPlay();
					}
				}

				@Override
				public void onAnimationRepeat(Animation arg0) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onAnimationEnd(Animation arg0) {

				}
			});
			animationSet1.setFillAfter(true);
			iv_music_bar1.startAnimation(animationSet1);
			iv_music_bar3.clearAnimation();
			iv_music_bar4.clearAnimation();
		}
	}

	/**
	 * 开始播放歌曲并同步展示歌词
	 */
	public void beginLrcPlay() {
		/*
		 * mPlayer = new MediaPlayer(); try {
		 * mPlayer.setDataSource(MusicLoader.instance(getContentResolver())
		 * .getMusicList().get(10).getUrl() getAssets().openFd("test.mp3"
		 * ).getFileDescriptor() ); // 准备播放歌曲监听
		 * mPlayer.setOnPreparedListener(new OnPreparedListener() { // 准备完毕
		 * public void onPrepared(MediaPlayer mp) { mp.start(); if (mTimer ==
		 * null) { mTimer = new Timer(); mTask = new LrcTask();
		 * mTimer.scheduleAtFixedRate(mTask, 0, mPalyTimerDuration); } } }); //
		 * 歌曲播放完毕监听 mPlayer.setOnCompletionListener(new OnCompletionListener() {
		 * public void onCompletion(MediaPlayer mp) { stopLrcPlay(); } }); //
		 * 准备播放歌曲 mPlayer.prepare(); // 开始播放歌曲 mPlayer.start(); } catch
		 * (IllegalArgumentException e) { e.printStackTrace(); } catch
		 * (IllegalStateException e) { e.printStackTrace(); } catch (IOException
		 * e) { e.printStackTrace(); }
		 */

	}

	/**
	 * 从assets目录下读取歌词文件内容
	 * 
	 * @param fileName
	 * @return
	 */
	public String getFromAssets(String fileName) {
		try {
			InputStreamReader inputReader = new InputStreamReader(
					getResources().getAssets().open(fileName));
			BufferedReader bufReader = new BufferedReader(inputReader);
			String line = "";
			String result = "";
			while ((line = bufReader.readLine()) != null) {
				if (line.trim().equals(""))
					continue;
				result += line + "\r\n";
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 从assets目录下读取歌词文件内容
	 * 
	 * @param fileName
	 * @return
	 */
	public String getFromSDCard(String fileName) {
		try {
			File file = new File(fileName);
			InputStream is = new FileInputStream(file);
			InputStreamReader inputReader = new InputStreamReader(is);
			BufferedReader bufReader = new BufferedReader(inputReader);
			String line = "";
			String result = "";
			while ((line = bufReader.readLine()) != null) {
				if (line.trim().equals(""))
					continue;
				result += line + "\r\n";
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * @return Share intent
	 * @throws RemoteException
	 */
	private String shareCurrentTrack() {
		if (MusicUtils.getTrackName() == null || MusicUtils.getArtistName() == null) {

		}

		Intent shareIntent = new Intent();
		String currentTrackMessage = getResources().getString(R.string.now_listening_to) + " "
				+ MusicUtils.getTrackName() + " " + getResources().getString(R.string.by) + " "
				+ MusicUtils.getArtistName();

		shareIntent.setAction(Intent.ACTION_SEND);
		shareIntent.setType("text/plain");
		shareIntent.putExtra(Intent.EXTRA_TEXT, currentTrackMessage);

		startActivity(Intent.createChooser(shareIntent,
				getResources().getString(R.string.share_track_using)));
		return currentTrackMessage;
	}

	private void initialButton() {
		// TODO Auto-generated method stub
		Display display = getActivity().getWindowManager().getDefaultDisplay();
		height = display.getHeight();
		width = display.getWidth();
		Log.v("width  & height is:",
				String.valueOf(width) + ", " + String.valueOf(height));

		params.height = 50;
		params.width = 50;
		// ���ñ߾� (int left, int top, int right, int bottom)
		// params.setMargins(10, height - 98, 0, 0);

		int a = 0;
		int b = 0;
		int c = 0;
		int d = 0;
		int e = 0;
		int sum = 0;

		buttonThought = (Button) findViewById(R.id.button_composer_thought);
		buttonThought.setVisibility(View.INVISIBLE);
		buttonThought.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus && !buttonMusic.isFocused()
						&& !buttonFavorite.isFocused() && !buttonWith.isFocused() && !buttonCamera.isFocused()) {
					iv_music_bar4.performClick();
				}
			}
		});
		// buttonThought.setLayoutParams(params);

		buttonMusic = (Button) findViewById(R.id.button_composer_music);
		buttonMusic.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus && !buttonThought.isFocused()
						&& !buttonFavorite.isFocused() && !buttonWith.isFocused() && !buttonCamera.isFocused()) {
					iv_music_bar4.performClick();
				}
			}
		});
		// buttonMusic.setLayoutParams(params);

		buttonFavorite = (Button) findViewById(R.id.button_composer_place);
		buttonFavorite.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus && !buttonMusic.isFocused()
						&& !buttonThought.isFocused() && !buttonWith.isFocused() && !buttonCamera.isFocused()) {
					iv_music_bar4.performClick();
				}
			}
		});
		// buttonFavorite.setLayoutParams(params);

		buttonWith = (Button) findViewById(R.id.button_composer_with);
		buttonWith.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus && !buttonMusic.isFocused()
						&& !buttonFavorite.isFocused() && !buttonThought.isFocused() && !buttonCamera.isFocused()) {
					iv_music_bar4.performClick();
				}
			}
		});
		// buttonWith.setLayoutParams(params);

		buttonCamera = (Button) findViewById(R.id.button_composer_camera);
		buttonCamera.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus && !buttonMusic.isFocused()
						&& !buttonFavorite.isFocused() && !buttonWith.isFocused() && !buttonThought.isFocused()) {
					iv_music_bar4.performClick();
				}
			}
		});
		buttonCamera.setVisibility(View.INVISIBLE);
		// buttonCamera.setLayoutParams(params);

		iv_music_bar4.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 自身动画
				/*
				 * if(iv_music_bar3.getAnimation()==null){ Animation animation =
				 * new ScaleAnimation(1.0f, 0.95f, 1.0f,
				 * 0.95f,Animation.RELATIVE_TO_SELF, 0.5f,
				 * Animation.RELATIVE_TO_SELF, 0.5f);
				 * animation.setDuration(500);
				 * iv_music_bar3.startAnimation(animation); }
				 * 
				 * if(iv_music_bar4.getAnimation()==null){ Animation animation =
				 * new ScaleAnimation(1.0f, 1.1f, 1.0f,
				 * 1.1f,Animation.RELATIVE_TO_SELF, 0.5f,
				 * Animation.RELATIVE_TO_SELF, 0.5f);
				 * animation.setDuration(500);
				 * iv_music_bar4.startAnimation(animation); }
				 */

				PointF pointF = new PointF(iv_music_bar3.getX()
						+ iv_music_bar3.getWidth() / 2
						- buttonCamera.getWidth() / 2, iv_music_bar3.getY()
						+ iv_music_bar3.getHeight() / 2
						- buttonCamera.getHeight() / 2);
				float radius = iv_music_bar3.getWidth() / 2
						+ buttonCamera.getWidth() / 2;// 半径
				int index = 0;
				int count = 5;
				float angle = 0;

				List<Button> btns = new ArrayList<Button>();
				btns.add(buttonCamera);
				btns.add(buttonWith);
				btns.add(buttonFavorite);
				btns.add(buttonMusic);
				btns.add(buttonThought);

				count = btns.size();
				// TODO Auto-generated method stub
				if (isClick == false) {
					if (btns.get(0).getAnimation() == null
							&& btns.get(1).getAnimation() == null
							&& btns.get(2).getAnimation() == null
							&& btns.get(3).getAnimation() == null
							&& btns.get(4).getAnimation() == null
							&& ((LrcView) mLrcView).getAnimation() == null) {
						/*if (MyApp.natureBinder.getCurrentMusic() != null) {
							buttonFavorite
									.setBackground(getActivity()
											.getResources()
											.getDrawable(
													MyApp.natureBinder
															.getCurrentMusic()
															.isFavourite() ? R.drawable.record_card_like_s
															: R.drawable.record_card_like_h));
						}*/
						isClick = true;

						// buttonDelete.startAnimation(animRotate(-45.0f, 0.5f,
						// 0.45f));
						angle = 180 / (count + 1);
						for (int i = 1; i <= count; i++) {
							index = i;
							int rX = 0;// 相对原点x
							int rY = 0;// 相对原点y
							if (index < 3) {
								rX = -Math.abs((int) (radius * Math.cos(Math
										.toRadians(angle * index))));
								rY = (int) (radius * Math.sin(Math
										.toRadians(angle * index)));
							} else if (index > 3) {
								rX = Math.abs((int) (radius * Math.cos(Math
										.toRadians(angle * index))));
								rY = (int) (radius * Math.sin(Math
										.toRadians(angle * index)));
							} else {
								rX = 0;
								rY = (int) radius;
							}
							int toX = (int) (pointF.x + rX);
							int toY = (int) (pointF.y + Math.abs(rY));
							PointF pointF2 = new PointF(toX, toY);
							if (btns.get(index - 1) == buttonThought
									|| btns.get(index - 1) == buttonCamera) {
								continue;
							}
							btns.get(index - 1).startAnimation(
									animTranslate(pointF, pointF2,
											btns.get(index - 1), 500));

							((LrcView) mLrcView).startAnimation(animTranslate(
									buttonCamera.getHeight(),
									((LrcView) mLrcView), 500));
						}
					} else {
						return;
					}

				} else {

					if (btns.get(0).getAnimation() == null
							&& btns.get(1).getAnimation() == null
							&& btns.get(2).getAnimation() == null
							&& btns.get(3).getAnimation() == null
							&& btns.get(4).getAnimation() == null
							&& ((LrcView) mLrcView).getAnimation() == null) {
						isClick = false;

						// buttonDelete.startAnimation(animRotate(90.0f, 0.5f,
						// 0.45f));

						// buttonDelete.startAnimation(animRotate(-45.0f, 0.5f,
						// 0.45f));
						angle = 180 / (count + 1);
						for (int i = 1; i <= count; i++) {
							index = i;
							int rX = 0;// 相对原点x
							int rY = 0;// 相对原点y
							if (index < count / 2) {
								rX = -Math.abs((int) (radius * Math.cos(Math
										.toRadians(angle * index))));
								rY = (int) (radius * Math.sin(Math
										.toRadians(angle * index)));
							} else if (index > count / 2) {
								rX = Math.abs((int) (radius * Math.cos(Math
										.toRadians(angle * index))));
								rY = (int) (radius * Math.sin(Math
										.toRadians(angle * index)));
							} else {
								rX = 0;
								rY = (int) radius;
							}
							int toX = (int) (pointF.x + rX);
							int toY = (int) (pointF.y + Math.abs(rY));
							PointF pointF2 = new PointF(btns.get(index - 1)
									.getX()
									+ btns.get(index - 1).getWidth()
									/ 2, btns.get(index - 1).getY()
									+ btns.get(index - 1).getHeight() / 2);
							btns.get(index - 1).startAnimation(
									animTranslate(pointF2, pointF,
											btns.get(index - 1), 300));
							((LrcView) mLrcView).startAnimation(animTranslate(
									-buttonCamera.getHeight(),
									((LrcView) mLrcView), 500));
						}
					} else {
						return;
					}

				}
			}
		});
		buttonCamera.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// TODO Auto-generated method stub
				AnimationSet animationSet = new AnimationSet(true);
				animationSet
						.addAnimation(setAnimScale(1.2f, 1.2f, buttonCamera));
				// animationSet.addAnimation(setAnimAlpha(1f, 0f));
				buttonCamera.startAnimation(animationSet);

			}
		});
		buttonWith.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AnimationSet animationSet = new AnimationSet(true);
				animationSet.addAnimation(setAnimScale(1.2f, 1.2f, buttonWith));
				// animationSet.addAnimation(setAnimAlpha(1f, 0f));
				buttonWith.startAnimation(animationSet);
				animationSet.setAnimationListener(new AnimationListener() {

					@Override
					public void onAnimationStart(Animation arg0) {
						// TODO Auto-generated method stub
						shareCurrentTrack();
					}

					@Override
					public void onAnimationRepeat(Animation arg0) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onAnimationEnd(Animation arg0) {
						//IntentUtil.jump(getActivity(), SettingActivity.class,null);
					}
				});
			}
		});
		buttonFavorite.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AnimationSet animationSet = new AnimationSet(true);
				animationSet
						.addAnimation(setAnimScale(1.2f, 1.2f, buttonFavorite));
				// animationSet.addAnimation(setAnimAlpha(1f, 0f));
				buttonFavorite.startAnimation(animationSet);
				animationSet.setAnimationListener(new AnimationListener() {

					@Override
					public void onAnimationStart(Animation arg0) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onAnimationRepeat(Animation arg0) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onAnimationEnd(Animation arg0) {
						MusicUtils.toggleFavorite();
						getActivity().invalidateOptionsMenu();

						if (MusicUtils.mService != null && MusicUtils.getCurrentAudioId() != -1) {
							if (MusicUtils.isFavorite(getActivity(), MusicUtils.getCurrentAudioId())) {
								buttonFavorite.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.record_card_like_s));
							} else {
								buttonFavorite.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.record_card_like_h));
								// Theme chooser
								//ThemeUtils.setActionBarItem(this, favorite, "apollo_favorite_normal");
							}
						}
						/*MusicInfo mInfo = MyApp.getNatureBinder().getCurrentMusic();
						mInfo.setFavourite(mInfo.isFavourite() ? false : true);
						DBUtils.modifyDB(getActivity(), mInfo);*/
					}
				});
				// buttonDelete.startAnimation(setAnimScale(0.0f, 0.0f));
			}
		});
		buttonMusic.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AnimationSet animationSet = new AnimationSet(true);
				animationSet
						.addAnimation(setAnimScale(1.2f, 1.2f, buttonMusic));
				// animationSet.addAnimation(setAnimAlpha(1f, 0f));
				buttonMusic.startAnimation(animationSet);
				animationSet.setAnimationListener(new AnimationListener() {

					@Override
					public void onAnimationStart(Animation arg0) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onAnimationRepeat(Animation arg0) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onAnimationEnd(Animation arg0) {
						IntentUtil.jump(getActivity(), SettingActivity.class,
								null);
					}
				});
			}
		});
		buttonThought.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AnimationSet animationSet = new AnimationSet(true);
				animationSet.addAnimation(setAnimScale(1.2f, 1.2f,
						buttonThought));
				// animationSet.addAnimation(setAnimAlpha(1f, 0f));
				buttonThought.startAnimation(animationSet);
				/*
				 * buttonFavorite .startAnimation(setAnimScale(0.0f, 0.0f,
				 * buttonFavorite)); buttonWith.startAnimation(setAnimScale(0.0f,
				 * 0.0f, buttonWith));
				 * buttonCamera.startAnimation(setAnimScale(0.0f, 0.0f,
				 * buttonCamera)); buttonMusic
				 * .startAnimation(setAnimScale(0.0f, 0.0f, buttonMusic));
				 */
				// buttonDelete.startAnimation(setAnimScale(0.0f, 0.0f));
			}
		});

	}

	protected Animation setAnimScale(float toX, float toY) {
		// TODO Auto-generated method stub
		animationScale = new ScaleAnimation(1f, toX, 1f, toY,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.45f);
		animationScale.setInterpolator(getActivity(),
				anim.accelerate_decelerate_interpolator);
		animationScale.setDuration(500);
		animationScale.setFillAfter(false);

		return animationScale;
	}

	protected Animation setAnimScale(float toX, float toY, final View view) {
		// TODO Auto-generated method stub
		animationScale = new ScaleAnimation(1f, toX, 1f, toY,
				Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f);
		animationScale.setInterpolator(getActivity(),
				anim.accelerate_decelerate_interpolator);
		animationScale.setDuration(200);
		animationScale.setFillAfter(false);
		animationScale.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationRepeat(Animation arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation arg0) {
				view.clearAnimation();
				PointF pointF = new PointF(iv_music_bar3.getX()
						+ iv_music_bar3.getWidth() / 2
						- buttonCamera.getWidth() / 2, iv_music_bar3.getY()
						+ iv_music_bar3.getHeight() / 2
						- buttonCamera.getHeight() / 2);
				float radius = iv_music_bar3.getWidth() / 2
						+ buttonCamera.getWidth() / 2;// 半径
				int index = 0;
				int count = 5;
				float angle = 0;

				List<Button> btns = new ArrayList<Button>();
				btns.add(buttonCamera);
				btns.add(buttonWith);
				btns.add(buttonFavorite);
				btns.add(buttonMusic);
				btns.add(buttonThought);

				count = btns.size();
				if (isClick) {

					if (btns.get(0).getAnimation() == null
							&& btns.get(1).getAnimation() == null
							&& btns.get(2).getAnimation() == null
							&& btns.get(3).getAnimation() == null
							&& btns.get(4).getAnimation() == null
							&& ((LrcView) mLrcView).getAnimation() == null) {
						isClick = false;

						angle = 180 / (count + 1);
						for (int i = 1; i <= count; i++) {
							index = i;
							int rX = 0;// 相对原点x
							int rY = 0;// 相对原点y
							if (index < count / 2) {
								rX = -Math.abs((int) (radius * Math.cos(Math
										.toRadians(angle * index))));
								rY = (int) (radius * Math.sin(Math
										.toRadians(angle * index)));
							} else if (index > count / 2) {
								rX = Math.abs((int) (radius * Math.cos(Math
										.toRadians(angle * index))));
								rY = (int) (radius * Math.sin(Math
										.toRadians(angle * index)));
							} else {
								rX = 0;
								rY = (int) radius;
							}
							int toX = (int) (pointF.x + rX);
							int toY = (int) (pointF.y + Math.abs(rY));
							PointF pointF2 = new PointF(btns.get(index - 1)
									.getX()
									+ btns.get(index - 1).getWidth()
									/ 2, btns.get(index - 1).getY()
									+ btns.get(index - 1).getHeight() / 2);
							btns.get(index - 1).startAnimation(
									animTranslate(pointF2, pointF,
											btns.get(index - 1), 300));
							((LrcView) mLrcView).startAnimation(animTranslate(
									-buttonCamera.getHeight(),
									((LrcView) mLrcView), 500));
						}
					} else {
						return;
					}
				}
			}
		});
		return animationScale;

	}

	protected Animation setAnimAlpha(float fromX, float toX) {
		// TODO Auto-generated method stub
		animationAlpha = new AlphaAnimation(fromX, toX);
		animationAlpha.setInterpolator(getActivity(),
				anim.accelerate_decelerate_interpolator);
		animationAlpha.setDuration(300);
		animationAlpha.setFillAfter(false);
		return animationAlpha;

	}

	protected Animation animRotate(float toDegrees, float pivotXValue,
			float pivotYValue) {
		// TODO Auto-generated method stub
		animationRotate = new RotateAnimation(0, toDegrees,
				Animation.RELATIVE_TO_SELF, pivotXValue,
				Animation.RELATIVE_TO_SELF, pivotYValue);
		animationRotate.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				animationRotate.setFillAfter(true);
			}
		});
		return animationRotate;
	}

	// 移动的动画效果
	/*
	 * TranslateAnimation(float fromXDelta, float toXDelta, float fromYDelta,
	 * float toYDelta)
	 * 
	 * float fromXDelta:这个参数表示动画开始的点离当前View X坐标上的差值；
	 * 
	 * 　　 * float toXDelta, 这个参数表示动画结束的点离当前View X坐标上的差值；
	 * 
	 * 　　 * float fromYDelta, 这个参数表示动画开始的点离当前View Y坐标上的差值；
	 * 
	 * 　　 * float toYDelta)这个参数表示动画开始的点离当前View Y坐标上的差值；
	 */
	protected Animation animTranslate(float toX, float toY, final int lastX,
			final int lastY, final Button button, long durationMillis) {
		// TODO Auto-generated method stub
		animationTranslate = new TranslateAnimation(button.getX()
				+ button.getWidth() / 2, toX, button.getY()
				+ button.getHeight() / 2, toY);
		animationTranslate.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				params = new LayoutParams(0, 0);
				params.height = button.getHeight();
				params.width = button.getWidth();
				// params.setMargins(lastX, lastY, 0, 0);
				button.setX(lastX);
				button.setY(lastY);
				button.setLayoutParams(params);
				button.clearAnimation();
			}
		});
		animationTranslate.setDuration(durationMillis);
		return animationTranslate;
	}

	//
	/*
	 * TranslateAnimation(float fromXDelta, float toXDelta, float fromYDelta,
	 * float toYDelta)
	 * 
	 * float fromXDelta:这个参数表示动画开始的点离当前View X坐标上的差值；
	 * 
	 * 　　 * float toXDelta, 这个参数表示动画结束的点离当前View X坐标上的差值；
	 * 
	 * 　　 * float fromYDelta, 这个参数表示动画开始的点离当前View Y坐标上的差值；
	 * 
	 * 　　 * float toYDelta)这个参数表示动画开始的点离当前View Y坐标上的差值；
	 */
	/**
	 * 移动的动画效果
	 * 
	 * @param pointF
	 *            起始点坐标
	 * @param pointF2
	 *            要移动到的点的坐标
	 * @param button
	 *            要移动的view
	 * @param durationMillis
	 *            动画时长
	 * @return
	 */
	protected Animation animTranslate(PointF pointF, PointF pointF2,
			final View button, long durationMillis) {
		// TODO Auto-generated method stub
		final PointF pointF_2 = pointF2;
		animationTranslate = new TranslateAnimation(0, pointF_2.x - pointF.x,
				0, pointF_2.y - pointF.y);
		animationTranslate.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				/*
				 * params = new LayoutParams(0, 0); params.height =
				 * button.getHeight(); params.width = button.getWidth();
				 */
				// params.setMargins(lastX, lastY, 0, 0);
				button.setX((int) pointF_2.x);
				button.setY((int) pointF_2.y);
				// button.setLayoutParams(params);
				button.clearAnimation();

			}
		});
		animationTranslate.setDuration(durationMillis);
		return animationTranslate;
	}

	/**
	 * 单独在Y轴移动
	 * 
	 * @param y
	 * @param button
	 * @param durationMillis
	 * @return
	 */
	protected Animation animTranslate(final float y, final View button,
			long durationMillis) {

		animationTranslate = new TranslateAnimation(0, 0, 0, y);
		animationTranslate.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				// params.setMargins(lastX, lastY, 0, 0);
				button.setX((int) button.getX());
				button.setY((int) button.getY() + y);
				// button.setLayoutParams(params);
				button.clearAnimation();

			}
		});
		animationTranslate.setDuration(durationMillis);
		return animationTranslate;
	}
}
