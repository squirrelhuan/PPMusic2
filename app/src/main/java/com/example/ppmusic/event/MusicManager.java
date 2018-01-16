package com.example.ppmusic.event;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

public class MusicManager {
	private Collection listeners;
	private static MusicManager logCateManager = new MusicManager();
	public static MusicManager getInstance(){
		return logCateManager;
	}
	/**
     * 添加事件
     * 
     * @param listener
     *            DoorListener
     */
    public void addMusicListener(IMusicListener listener) {
        if (listeners == null) {
            listeners = new HashSet();
        }
        listeners.add(listener);
    }

    /**
     * 移除事件
     * 
     * @param listener
     *  MusicListener
     */
    public void removeMusicListener(IMusicListener listener) {
        if (listeners == null)
            return;
        listeners.remove(listener);
    }

    /**
     * 歌曲改变事件
     */
    public void onMusicChanged() {
        if (listeners == null)
            return;
        MusicEvent event = new MusicEvent(this, MusicEvent.MusicEventType.onMusicChanged);
        notifyListeners(event);
    }
    
    /**
     * 开始播放事件
     */
    public void onStartMusic() {
        if (listeners == null)
            return;
        MusicEvent event = new MusicEvent(this, MusicEvent.MusicEventType.onMusicStart);
        notifyListeners(event);
    }

    /**
     * 结束播放事件
     */
    public void onStopMusic() {
        if (listeners == null)
            return;
        MusicEvent event = new MusicEvent(this, MusicEvent.MusicEventType.onMusicStop);
        notifyListeners(event);
    }

    /**
     * 通知所有的DoorListener
     */
    private void notifyListeners(MusicEvent event) {
        Iterator iter = listeners.iterator();
        while (iter.hasNext()) {
            IMusicListener listener = (IMusicListener) iter.next();
            listener.musicEvent(event);
        }
    }
}
