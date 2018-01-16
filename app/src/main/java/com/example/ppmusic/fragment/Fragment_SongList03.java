package com.example.ppmusic.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.ppmusic.R;
import com.example.ppmusic.activities.MainActivity;
import com.example.ppmusic.activities.MusicListActivity;
import com.example.ppmusic.adapter.AlbumListAdapter03;
import com.example.ppmusic.base.MyBaseFragment;
import com.example.ppmusic.bean.AlbumInfo;
import com.example.ppmusic.bean.MusicInfo;
import com.example.ppmusic.utils.IntentUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author CGQ
 * 
 */
public class Fragment_SongList03 extends MyBaseFragment implements OnClickListener, OnItemClickListener {

	MainActivity mainActivity;

	ImageView main_image;
	GridView gv_singer;
	List<MusicInfo> musicList = new ArrayList<MusicInfo>();
	List<AlbumInfo> albumList = new ArrayList<AlbumInfo>();

	private AlbumListAdapter03 sadapter;

	@Override
	public View setContentUI(LayoutInflater inflater, ViewGroup container) {
		return inflater.inflate(R.layout.music_list, container, false);
	}

	@Override
	public void init() {
		/*musicList.clear();
		musicList.addAll(MusicListActivity.musicList_current);
		for(int i=0;i<musicList.size();i++){
			boolean isContain = false;
			for (int j = 0; j < albumList.size(); j++) {
				if(albumList.get(j).getName().equals(musicList.get(i).getAlbum())){
					isContain = true;
					AlbumInfo albumInfo = albumList.get(j);
					List<MusicInfo> mlist = new ArrayList<MusicInfo>();
					mlist =	albumInfo.getMusiclist();
					mlist.add(musicList.get(i));
					albumInfo.setMusiclist(mlist);
					albumList.set(j,albumInfo);
					break;
				}
			}
			if(!isContain){
				AlbumInfo singerInfo = new AlbumInfo();
				List<MusicInfo> mlist = new ArrayList<MusicInfo>();
				mlist.add(musicList.get(i));
				singerInfo.setName(musicList.get(i).getAlbum());
				singerInfo.setMusiclist(mlist);
				albumList.add(singerInfo);
			}
		}
		GridView gv_singer = (GridView) rootView.findViewById(R.id.gv_album);//
		gv_singer.setVisibility(View.VISIBLE);
		sadapter = new AlbumListAdapter03(getActivity(), albumList);
		gv_singer.setAdapter(sadapter);*/
	}

	int test = 0;
	long testTime = System.currentTimeMillis();

	@Override
	public void onClick(View v) {
		Bundle bundle = new Bundle();
		
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		IntentUtil.jump(getActivity(), MusicListActivity.class, null);
		
	}

	public static void main(String[] args) {
	}
}
