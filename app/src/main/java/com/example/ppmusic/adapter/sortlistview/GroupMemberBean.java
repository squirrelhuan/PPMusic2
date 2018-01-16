package com.example.ppmusic.adapter.sortlistview;

import com.example.ppmusic.bean.MusicInfo;

public class GroupMemberBean {

	private String name;   //显示的数据
	private String sortLetters;  //显示数据拼音的首字母
	private MusicInfo musicInfo;  //对应的歌曲信息
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSortLetters() {
		return sortLetters;
	}
	public void setSortLetters(String sortLetters) {
		this.sortLetters = sortLetters;
	}
	public MusicInfo getMusicInfo() {
		return musicInfo;
	}
	public void setMusicInfo(MusicInfo musicInfo) {
		this.musicInfo = musicInfo;
	}
	
}
