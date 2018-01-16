package com.example.ppmusic.bean;

import android.media.MediaPlayer;

/**
 * 临时音乐播放文件
 * @author Administrator
 *
 */
public class MusicTemp {
	
	private MusicInfo musicInfo;
	//当前歌曲播放位置
	private int position;
	//当前歌曲在列表中索引位置
	private int index;
	//播放状态
	private int status;
	//
	private MediaPlayer mediaPlayer;
	
	public MusicInfo getMusicInfo() {
		return musicInfo;
	}
	public void setMusicInfo(MusicInfo musicInfo) {
		this.musicInfo = musicInfo;
	}
	
	public int getPosition() {
		return position;
	}
	public void setPosition(int position) {
		this.position = position;
		this.mediaPlayer.seekTo(position);
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	
	public MediaPlayer getMediaPlayer() {
		return mediaPlayer;
	}
	public void setMediaPlayer(MediaPlayer mediaPlayer) {
		this.mediaPlayer = mediaPlayer;
	}
	
	
}
