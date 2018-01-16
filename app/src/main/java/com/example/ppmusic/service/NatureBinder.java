package com.example.ppmusic.service;

import android.media.MediaPlayer;
import android.os.Binder;

import com.example.ppmusic.MyApp;
import com.example.ppmusic.bean.MusicInfo;

import java.util.List;

/**
 * Created by squirrel桓 on 2018/1/15.
 */

public class NatureBinder extends Binder {

    private CMusicService cMusicService;
    NatureBinder(CMusicService service){
        cMusicService = service;
    }

    public void startPlay(int index, int position) {
        cMusicService.play(index, position);
    }

    public void startPlay() {
        cMusicService.play();
    }

    public void startPlay(MusicInfo musicInfo) {
        cMusicService.play(musicInfo);
    }

    public void startPlay(List<MusicInfo> mlList, int index, int position) {
        cMusicService.play(mlList, index, position);
    }

    public boolean getStatus() {
        return cMusicService.isPlaying;
    }

    public MusicInfo getCurrentMusic() {
        return cMusicService.current_musicInfo;
    }

    public List<MusicInfo> getCurrentMusicList() {
        return cMusicService.current_music_list;
    }

    public MediaPlayer getMediaPlayer() {
        return cMusicService.mediaPlayer;
    }

    public void stopPlay() {
        cMusicService.pause();
    }

    public void toNext() {
        cMusicService.playNext();
    }

    public void toLast() {
        cMusicService.playLast();
    }

    /**
     * MODE_ONE_LOOP = 1; MODE_ALL_LOOP = 2; MODE_RANDOM = 3; MODE_SEQUENCE
     * = 4;
     */
    public void changeMode() {
        cMusicService.currentMode = MyApp.getPreferencesService()
                .getInt("CurrentMode", 3);
        cMusicService.currentMode = (cMusicService.currentMode + 1) % 3;
        MyApp.getPreferencesService().save("CurrentMode", cMusicService.currentMode);
       // MyLog.v(TAG, "[NatureBinder] changeMode : " + currentMode);
    }

    /**
     * return the current mode MODE_ONE_LOOP = 1; MODE_ALL_LOOP = 2;
     * MODE_RANDOM = 3; MODE_SEQUENCE = 4;
     *
     * @return
     */
    public int getCurrentMode() {
        return MyApp.getPreferencesService().getInt("CurrentMode", 2);
    }

    /**
     * The service is playing the music
     *
     * @return
     */
    public boolean isPlaying() {
        return cMusicService.isPlaying;
    }

    /**
     * Notify Activities to update the current music and duration when
     * current activity changes.
     */
    public void notifyActivity() {
    }

    /**
     * Seekbar changes
     *
     * @param progress
     */
    public void changeProgress(int progress) {
        if (cMusicService.mediaPlayer != null) {
            cMusicService.current_position = progress;
            if (cMusicService.isPlaying) {
                cMusicService.mediaPlayer.seekTo(cMusicService.current_position);
            } else {
                cMusicService.play(cMusicService.current_index, cMusicService.current_position);
            }
        }
    }

    public int getCurrentPosition() {
        return cMusicService.mediaPlayer.getCurrentPosition();
    }

    public int getCurrentIndex() {
        return cMusicService.current_index;
    }

		/*
		 * public int getProgress() { progress =
		 * mediaPlayer.getCurrentPosition() / mediaPlayer.getDuration() * 100;
		 * return progress; }
		 */

    public int getPosition() {
        int process = MyApp.getPreferencesService().getInt(
                "CurrentPosition", 0);
        if (process != 0) {
            return process;
        } else {
            MyApp.getPreferencesService().save("CurrentPosition",
                    cMusicService.mediaPlayer.getCurrentPosition());
            return cMusicService.mediaPlayer.getCurrentPosition();
        }
    }

}
