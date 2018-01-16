package com.example.ppmusic.event;

public class MusicMainTest {
    public static void main(String[] args) {
        MusicManager manager = new MusicManager();
        manager.addMusicListener(new MusicListenerImpl());// 给门1增加监听器
        // 切换音乐
        manager.onMusicChanged();
        System.out.println("换歌");
        //播放
        manager.onStartMusic();
        //暂停
        manager.onStopMusic();
    }
}
