package com.example.ppmusic;

import java.util.ArrayList;
import java.util.List;

import com.example.ppmusic.bean.MusicInfo;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore.Audio.Media;
import android.util.Log;

public class MusicLoader {
	
	private static final String TAG = "com.example.nature.MusicLoader";
	
	private static List<MusicInfo> musicList = new ArrayList<MusicInfo>();
	
	private static MusicLoader musicLoader;
	
	private static ContentResolver contentResolver;
	
	private Uri contentUri = Media.EXTERNAL_CONTENT_URI;	
	
	private String[] projection = {
			Media._ID,//歌曲编号
			Media.TITLE, //歌曲标题
			Media.DATA,//歌曲文件的路径 
			Media.ALBUM,//歌曲的专辑名
			Media.ARTIST, //歌曲的歌手名
			Media.DURATION,	//歌曲的总播放时长		
			Media.SIZE //歌曲文件的大小
	};
	private String where =  "mime_type in ('audio/mpeg','audio/x-ms-wma') and bucket_display_name <> 'audio' and is_music > 0 " ;
	private String sortOrder = Media.DATA;
	
	public static MusicLoader instance(ContentResolver pContentResolver){
		if(musicLoader == null){
			contentResolver = pContentResolver;
			musicLoader = new MusicLoader();			
		}
		return musicLoader;
	}
	
	private MusicLoader(){
		/*Cursor cursor = contentResolver.query(contentUri, null, null, null, null);
		if(cursor == null){
			Log.v(TAG,"Line(37	)	Music Loader cursor == null.");
		}else if(!cursor.moveToFirst()){
			Log.v(TAG,"Line(39	)	Music Loader cursor.moveToFirst() returns false.");
		}else{			
			int displayNameCol = cursor.getColumnIndex(Media.TITLE);
			int albumCol = cursor.getColumnIndex(Media.ALBUM);
			int idCol = cursor.getColumnIndex(Media._ID);
			int durationCol = cursor.getColumnIndex(Media.DURATION);
			int sizeCol = cursor.getColumnIndex(Media.SIZE);
			int artistCol = cursor.getColumnIndex(Media.ARTIST);
			int urlCol = cursor.getColumnIndex(Media.DATA);			
			do{
				String title = cursor.getString(displayNameCol);
				String album = cursor.getString(albumCol);
				long id = cursor.getLong(idCol);				
				int duration = cursor.getInt(durationCol);
				long size = cursor.getLong(sizeCol);
				String artist = cursor.getString(artistCol);
				String url = cursor.getString(urlCol);
				
				MusicInfo musicInfo = new MusicInfo(id, title);
				musicInfo.setAlbum(album);
				musicInfo.setDuration(duration);
				musicInfo.setSize(size);
				musicInfo.setArtist(artist);
				musicInfo.setUrl(url);
				musicList.add(musicInfo);
				
			}while(cursor.moveToNext());
		}*/
	}
	
	public List<MusicInfo> getMusicList(){
		return musicList;
	}
	
	public Uri getMusicUriById(long id){
		Uri uri = ContentUris.withAppendedId(contentUri, id);
		return uri;
	}	

}
