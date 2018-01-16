package com.example.ppmusic.event;

import java.util.EventListener;

/**
* 定义监听接口，负责监听DoorEvent事件
*/
public interface IMusicListener extends EventListener {
    public void musicEvent(MusicEvent event);
}
