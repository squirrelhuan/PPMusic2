package com.example.ppmusic.bean.TTdongting;

import java.util.List;

public class Data {
private int vip;

private int song_id;

private String song_name;

private int singer_id;

private String singer_name;

private int artist_flag;

private int album_id;

private String album_name;

private int pick_count;

private List<Audition_list> audition_list ;

private List<Url_list> url_list ;

private List<Ll_list> ll_list ;

public void setVip(int vip){
this.vip = vip;
}
public int getVip(){
return this.vip;
}
public void setSong_id(int song_id){
this.song_id = song_id;
}
public int getSong_id(){
return this.song_id;
}
public void setSong_name(String song_name){
this.song_name = song_name;
}
public String getSong_name(){
return this.song_name;
}
public void setSinger_id(int singer_id){
this.singer_id = singer_id;
}
public int getSinger_id(){
return this.singer_id;
}
public void setSinger_name(String singer_name){
this.singer_name = singer_name;
}
public String getSinger_name(){
return this.singer_name;
}
public void setArtist_flag(int artist_flag){
this.artist_flag = artist_flag;
}
public int getArtist_flag(){
return this.artist_flag;
}
public void setAlbum_id(int album_id){
this.album_id = album_id;
}
public int getAlbum_id(){
return this.album_id;
}
public void setAlbum_name(String album_name){
this.album_name = album_name;
}
public String getAlbum_name(){
return this.album_name;
}
public void setPick_count(int pick_count){
this.pick_count = pick_count;
}
public int getPick_count(){
return this.pick_count;
}
public void setAudition_list(List<Audition_list> audition_list){
this.audition_list = audition_list;
}
public List<Audition_list> getAudition_list(){
return this.audition_list;
}
public void setUrl_list(List<Url_list> url_list){
this.url_list = url_list;
}
public List<Url_list> getUrl_list(){
return this.url_list;
}
public void setLl_list(List<Ll_list> ll_list){
this.ll_list = ll_list;
}
public List<Ll_list> getLl_list(){
return this.ll_list;
}

}