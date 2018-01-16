package com.example.ppmusic.base;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import com.example.ppmusic.constants.Constants;
import com.example.ppmusic.constants.NetConstants;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.Surface;
import android.view.SurfaceHolder;

public abstract class MyMediaPlayer extends MediaPlayer {

	@Override
	public void setDisplay(SurfaceHolder sh) {
		// TODO Auto-generated method stub
		super.setDisplay(sh);
	}

	@Override
	public void setSurface(Surface surface) {
		// TODO Auto-generated method stub
		super.setSurface(surface);
	}

	@Override
	public void setVideoScalingMode(int mode) {
		// TODO Auto-generated method stub
		super.setVideoScalingMode(mode);
	}

	@Override
	public void setDataSource(Context context, Uri uri) throws IOException,
			IllegalArgumentException, SecurityException, IllegalStateException {
		// TODO Auto-generated method stub
		super.setDataSource(context, uri);
	}

	@Override
	public void setDataSource(Context context, Uri uri,
			Map<String, String> headers) throws IOException,
			IllegalArgumentException, SecurityException, IllegalStateException {
		

		String scheme = uri.getScheme();
		if (scheme == null || scheme.equals("file")) {

			return;
		}

		AssetFileDescriptor fd = null;
		try {
			ContentResolver resolver = context.getContentResolver();
			fd = resolver.openAssetFileDescriptor(uri, "r");
			if (fd == null) {
				return;
			}
			// Note: using getDeclaredLength so that our behavior is the same
			// as previous versions when the content provider is returning
			// a full file.
			if (fd.getDeclaredLength() < 0) {
				//setDataSource(fd.getFileDescriptor());
			} else {
				String filename = "myfile";
				String string = "Hello world!";
				FileOutputStream outputStream;
				File file = new File(Constants.APP_PATH_DOWNLOAD + filename);
				if (file.exists()) {
					try {
						outputStream = context.openFileOutput(filename,
								Context.MODE_PRIVATE);
						outputStream.write(string.getBytes());
						outputStream.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				setDataSource(fd.getFileDescriptor(), fd.getStartOffset(),
						fd.getDeclaredLength());
			}
			return;
		} catch (SecurityException ex) {
		} catch (IOException ex) {
		} finally {
			if (fd != null) {
				fd.close();
			}
		}
		// TODO Auto-generated method stub
		super.setDataSource(context, uri, headers);
	}

	@Override
	public void setDataSource(String path) throws IOException,
			IllegalArgumentException, SecurityException, IllegalStateException {
		// TODO Auto-generated method stub
		super.setDataSource(path);
	}

	@Override
	public void setDataSource(FileDescriptor fd) throws IOException,
			IllegalArgumentException, IllegalStateException {
		// TODO Auto-generated method stub
		super.setDataSource(fd);
	}

	@Override
	public void setDataSource(FileDescriptor fd, long offset, long length)
			throws IOException, IllegalArgumentException, IllegalStateException {
		// TODO Auto-generated method stub
		super.setDataSource(fd, offset, length);
	}

	@Override
	public void prepare() throws IOException, IllegalStateException {
		// TODO Auto-generated method stub
		super.prepare();
	}

	@Override
	public void prepareAsync() throws IllegalStateException {
		// TODO Auto-generated method stub
		super.prepareAsync();
	}

	@Override
	public void start() throws IllegalStateException {
		// TODO Auto-generated method stub
		onStarted();
		super.start();
	}
    //开始播放事件
	public abstract void onStarted();
	
	@Override
	public void stop() throws IllegalStateException {
		// TODO Auto-generated method stub
		super.stop();
	}

	@Override
	public void pause() throws IllegalStateException {
		onPaused();
		super.pause();
	}
    //暂停事件
	public abstract void onPaused();
	
	@Override
	public void setWakeMode(Context context, int mode) {
		// TODO Auto-generated method stub
		super.setWakeMode(context, mode);
	}

	@Override
	public void setScreenOnWhilePlaying(boolean screenOn) {
		// TODO Auto-generated method stub
		super.setScreenOnWhilePlaying(screenOn);
	}

	@Override
	public int getVideoWidth() {
		// TODO Auto-generated method stub
		return super.getVideoWidth();
	}

	@Override
	public int getVideoHeight() {
		// TODO Auto-generated method stub
		return super.getVideoHeight();
	}

	@Override
	public boolean isPlaying() {
		// TODO Auto-generated method stub
		return super.isPlaying();
	}

	@Override
	public void seekTo(int msec) throws IllegalStateException {
		// TODO Auto-generated method stub
		super.seekTo(msec);
	}

	@Override
	public int getCurrentPosition() {
		// TODO Auto-generated method stub
		return super.getCurrentPosition();
	}

	@Override
	public int getDuration() {
		// TODO Auto-generated method stub
		return super.getDuration();
	}

	@Override
	public void setNextMediaPlayer(MediaPlayer next) {
		// TODO Auto-generated method stub
		super.setNextMediaPlayer(next);
	}

	@Override
	public void release() {
		// TODO Auto-generated method stub
		super.release();
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		super.reset();
	}

	@Override
	public void setAudioStreamType(int streamtype) {
		// TODO Auto-generated method stub
		super.setAudioStreamType(streamtype);
	}

	@Override
	public void setAudioAttributes(AudioAttributes attributes)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		super.setAudioAttributes(attributes);
	}

	@Override
	public void setLooping(boolean looping) {
		// TODO Auto-generated method stub
		super.setLooping(looping);
	}

	@Override
	public boolean isLooping() {
		// TODO Auto-generated method stub
		return super.isLooping();
	}

	@Override
	public void setVolume(float leftVolume, float rightVolume) {
		// TODO Auto-generated method stub
		super.setVolume(leftVolume, rightVolume);
	}

	@Override
	public void setAudioSessionId(int sessionId)
			throws IllegalArgumentException, IllegalStateException {
		// TODO Auto-generated method stub
		super.setAudioSessionId(sessionId);
	}

	@Override
	public int getAudioSessionId() {
		// TODO Auto-generated method stub
		return super.getAudioSessionId();
	}

	@Override
	public void attachAuxEffect(int effectId) {
		// TODO Auto-generated method stub
		super.attachAuxEffect(effectId);
	}

	@Override
	public void setAuxEffectSendLevel(float level) {
		// TODO Auto-generated method stub
		super.setAuxEffectSendLevel(level);
	}

	@Override
	public TrackInfo[] getTrackInfo() throws IllegalStateException {
		// TODO Auto-generated method stub
		return super.getTrackInfo();
	}

	@Override
	public void addTimedTextSource(String path, String mimeType)
			throws IOException, IllegalArgumentException, IllegalStateException {
		// TODO Auto-generated method stub
		super.addTimedTextSource(path, mimeType);
	}

	@Override
	public void addTimedTextSource(Context context, Uri uri, String mimeType)
			throws IOException, IllegalArgumentException, IllegalStateException {
		// TODO Auto-generated method stub
		super.addTimedTextSource(context, uri, mimeType);
	}

	@Override
	public void addTimedTextSource(FileDescriptor fd, String mimeType)
			throws IllegalArgumentException, IllegalStateException {
		// TODO Auto-generated method stub
		super.addTimedTextSource(fd, mimeType);
	}

	@Override
	public void addTimedTextSource(FileDescriptor fd, long offset, long length,
			String mime) throws IllegalArgumentException, IllegalStateException {
		// TODO Auto-generated method stub
		super.addTimedTextSource(fd, offset, length, mime);
	}

	@Override
	public int getSelectedTrack(int trackType) throws IllegalStateException {
		// TODO Auto-generated method stub
		return super.getSelectedTrack(trackType);
	}

	@Override
	public void selectTrack(int index) throws IllegalStateException {
		// TODO Auto-generated method stub
		super.selectTrack(index);
	}

	@Override
	public void deselectTrack(int index) throws IllegalStateException {
		// TODO Auto-generated method stub
		super.deselectTrack(index);
	}

	@Override
	protected void finalize() {
		// TODO Auto-generated method stub
		super.finalize();
	}

	@Override
	public void setOnPreparedListener(OnPreparedListener listener) {
		// TODO Auto-generated method stub
		super.setOnPreparedListener(listener);
	}

	@Override
	public void setOnCompletionListener(OnCompletionListener listener) {
		// TODO Auto-generated method stub
		super.setOnCompletionListener(listener);
	}

	@Override
	public void setOnBufferingUpdateListener(OnBufferingUpdateListener listener) {
		// TODO Auto-generated method stub
		super.setOnBufferingUpdateListener(listener);
	}

	@Override
	public void setOnSeekCompleteListener(OnSeekCompleteListener listener) {
		// TODO Auto-generated method stub
		super.setOnSeekCompleteListener(listener);
	}

	@Override
	public void setOnVideoSizeChangedListener(
			OnVideoSizeChangedListener listener) {
		// TODO Auto-generated method stub
		super.setOnVideoSizeChangedListener(listener);
	}

	@Override
	public void setOnTimedTextListener(OnTimedTextListener listener) {
		// TODO Auto-generated method stub
		super.setOnTimedTextListener(listener);
	}

	@Override
	public void setOnErrorListener(OnErrorListener listener) {
		// TODO Auto-generated method stub
		super.setOnErrorListener(listener);
	}

	@Override
	public void setOnInfoListener(OnInfoListener listener) {
		// TODO Auto-generated method stub
		super.setOnInfoListener(listener);
	}

}
