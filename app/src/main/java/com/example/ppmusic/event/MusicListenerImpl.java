package com.example.ppmusic.event;

public class MusicListenerImpl implements IMusicListener {

	public void musicEvent(MusicEvent event) {
		if (event.getEventType() == MusicEvent.MusicEventType.onMusicChanged) {
            System.out.println("logs changed");
        } else {
            System.out.println("tags changed");
        }
	}

}
