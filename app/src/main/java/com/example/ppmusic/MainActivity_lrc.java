package com.example.ppmusic;

import java.io.IOException;
import java.util.List;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;

public class MainActivity_lrc extends Activity {
	private WordView mWordView;
	private List mTimeList;
	private MediaPlayer mPlayer;

	@SuppressLint("SdCardPath")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.music_1);

		mWordView = (WordView) findViewById(R.id.text);

		mPlayer = new MediaPlayer();
		mPlayer.reset();
		LrcHandle lrcHandler = new LrcHandle();
		try {
			lrcHandler.readLRC("/sdcard/����ȥ����.lrc");
			mTimeList = lrcHandler.getTime();
			mPlayer.setDataSource("/sdcard/����ȥ����.mp3");
			mPlayer.prepare();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
		final Handler handler = new Handler();
		mPlayer.start();
		new Thread(new Runnable() {
			int i = 0;

			@Override
			public void run() {
				while (mPlayer.isPlaying()) {
					handler.post(new Runnable() {

						@Override
						public void run() {
							mWordView.invalidate();
						}
					});
					try {
						int j = (Integer) mTimeList.get(i + 1)
								- (Integer) mTimeList.get(i);
						Thread.sleep(j);
					} catch (InterruptedException e) {
					}
					i++;
					if (i == mTimeList.size() - 1) {
						mPlayer.stop();
						break;
					}
				}
			}
		}).start();
	}
}