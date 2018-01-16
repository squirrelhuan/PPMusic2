package com.example.ppmusic.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.ppmusic.R;
import com.example.ppmusic.activities.MainActivity;
import com.example.ppmusic.activities.MusicListActivity;
import com.example.ppmusic.activities.SearchActivity;
import com.example.ppmusic.base.MyBaseFragment;
import com.example.ppmusic.utils.IntentUtil;

import java.util.ArrayList;
import java.util.HashMap;

public class MainFragment1 extends MyBaseFragment implements OnClickListener,
		OnItemClickListener {

	MainActivity mainActivity;

	LinearLayout ll_search;
	ImageView main_image;
	public Adapter adapter;

	@Override
	public View setContentUI(LayoutInflater inflater, ViewGroup container) {
		return inflater.inflate(R.layout.music_0, container, false);
	}

	@Override
	public void init() {
		mainActivity = (MainActivity) getActivity();

		ll_search = (LinearLayout) findViewById(R.id.ll_search);
		ll_search.setOnClickListener(this);

		GridView gridview1 = (GridView) rootView.findViewById(R.id.gridview1);//
		ArrayList<HashMap<String, Object>> ItemList = new ArrayList<HashMap<String, Object>>();

		HashMap<String, Object> map_0 = new HashMap<String, Object>();
		map_0.put("Image", R.drawable.mymusic_icon_allsongs_normal);
		map_0.put("Tag", "本地歌曲");
		ItemList.add(map_0);
		HashMap<String, Object> map_1 = new HashMap<String, Object>();
		map_1.put("Image", R.drawable.mymusic_icon_download_normal);
		map_1.put("Tag", "下载歌曲");
		ItemList.add(map_1);
		HashMap<String, Object> map_2 = new HashMap<String, Object>();
		map_2.put("Image", R.drawable.mymusic_icon_history_normal);
		map_2.put("Tag", "最近播放");
		ItemList.add(map_2);
		HashMap<String, Object> map_3 = new HashMap<String, Object>();
		map_3.put("Image", R.drawable.mymusic_icon_favorite_normal);
		map_3.put("Tag", "我喜欢");
		ItemList.add(map_3);
		HashMap<String, Object> map_4 = new HashMap<String, Object>();
		map_4.put("Image", R.drawable.mymusic_icon_mv_normal);
		map_4.put("Tag", "下载mv");
		ItemList.add(map_4);
		HashMap<String, Object> map_5 = new HashMap<String, Object>();
		map_5.put("Image", R.drawable.mymusic_icon_recognizer_normal);
		map_5.put("Tag", "听歌识曲");
		ItemList.add(map_5);

		adapter = new SimpleAdapter(getActivity(), ItemList,
				R.layout.home_item_style, new String[] { "Image", "Tag" },
				new int[] { R.id.ItemImage, R.id.ItemText });
		gridview1.setAdapter((ListAdapter) adapter);
		gridview1.setOnItemClickListener(this);

	}

	int test = 0;
	long testTime = System.currentTimeMillis();

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_search:
			IntentUtil.jump(getActivity(), SearchActivity.class, null);
			break;

		default:
			break;
		}
	}

	public void showToast(String str) {
		Toast.makeText(getActivity(), str, Toast.LENGTH_LONG).show();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Bundle bundle = new Bundle();
		switch (position) {
		case 0:
			bundle.putString("Title", "本地歌曲");
			break;
		case 1:
			bundle.putString("Title", "下载歌曲");
			break;
		case 2:
			bundle.putString("Title", "最近播放");
			break;
		case 3:
			bundle.putString("Title", "我喜欢");
			break;
		case 4:
			bundle.putString("Title", "下载mv");
			break;
		case 5:
			bundle.putString("Title", "听歌识曲");
			break;

		default:
			break;
		}
		IntentUtil.jump(getActivity(), MusicListActivity.class, bundle);

	}


}
