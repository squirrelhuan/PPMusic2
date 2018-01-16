package com.example.ppmusic.lrc.gecimi;

public class Result {
private int aid;

private int artist_id;

private int sid;

private String lrc;

private String song;

public void setAid(int aid){
this.aid = aid;
}
public int getAid(){
return this.aid;
}
public void setArtist_id(int artist_id){
this.artist_id = artist_id;
}
public int getArtist_id(){
return this.artist_id;
}
public void setSid(int sid){
this.sid = sid;
}
public int getSid(){
return this.sid;
}
public void setLrc(String lrc){
this.lrc = lrc;
}
public String getLrc(){
return this.lrc;
}
public void setSong(String song){
this.song = song;
}
public String getSong(){
return this.song;
}

}