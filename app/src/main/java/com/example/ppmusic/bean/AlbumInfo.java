package com.example.ppmusic.bean;

import java.util.List;
/**
 * 专辑model
 * @author CGQ
 *
 */
public class AlbumInfo {

	private String name;
	private List<MusicInfo> musiclist;
	private int count;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public List<MusicInfo> getMusiclist() {
		return musiclist;
	}
	public void setMusiclist(List<MusicInfo> musiclist) {
		this.musiclist = musiclist;
	}
	public int getCount() {
		count = musiclist.size();
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	
	
}
