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
import com.example.ppmusic.adapter.SongListAdapter02;
import com.example.ppmusic.base.MyBaseFragment;
import com.example.ppmusic.bean.MusicInfo;
import com.example.ppmusic.bean.SingerInfo;
import com.example.ppmusic.utils.IntentUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author CGQ
 * 
 */
public class Fragment_SongList02 extends MyBaseFragment implements OnClickListener, OnItemClickListener {

	MainActivity mainActivity;

	ImageView main_image;
	GridView gv_singer;
	List<MusicInfo> musicList = new ArrayList<MusicInfo>();
	List<SingerInfo> singerList = new ArrayList<SingerInfo>();

	private SongListAdapter02 sadapter;

	@Override
	public View setContentUI(LayoutInflater inflater, ViewGroup container) {
		return inflater.inflate(R.layout.music_list, container, false);
	}

	@Override
	public void init() {
		musicList.clear();
		musicList.addAll(MusicListActivity.musicList_current);
		for(int i=0;i<musicList.size();i++){
			boolean isContain = false;
			for (int j = 0; j < singerList.size(); j++) {
				if(singerList.get(j).getName().equals(musicList.get(i).getArtist())){
					isContain = true;
					SingerInfo singerInfo = singerList.get(j);
					List<MusicInfo> mlist = new ArrayList<MusicInfo>();
					mlist =	singerInfo.getMusiclist();
					mlist.add(musicList.get(i));
					singerInfo.setMusiclist(mlist);
					singerList.set(j,singerInfo);
					break;
				}
			}
			if(!isContain){
				SingerInfo singerInfo = new SingerInfo();
				List<MusicInfo> mlist = new ArrayList<MusicInfo>();
				mlist.add(musicList.get(i));
				//singerInfo.setName(musicList.get(i).getArtist());
				singerInfo.setMusiclist(mlist);
				singerList.add(singerInfo);
			}
		}
		GridView gv_singer = (GridView) rootView.findViewById(R.id.gv_singer);//
		gv_singer.setVisibility(View.VISIBLE);
		sadapter = new SongListAdapter02(getActivity(), singerList);
		gv_singer.setAdapter(sadapter);
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
