package com.example.ppmusic.bean;

import java.util.List;
/**
 * 歌手实体类
 * @author Administrator
 *
 */
public class SingerInfo {

	private String name;
	private int count;
	
	private List<MusicInfo> musiclist;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getCount() {
		return musiclist.size();
	}

	public void setCount(int count) {
		this.count = count;
	}

	public List<MusicInfo> getMusiclist() {
		return musiclist;
	}

	public void setMusiclist(List<MusicInfo> musiclist) {
		this.musiclist = musiclist;
	}
	
	
}
