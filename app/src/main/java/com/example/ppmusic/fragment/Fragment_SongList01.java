package com.example.ppmusic.fragment;

import static com.example.ppmusic.constants.Constants.NUMWEEKS;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import com.example.ppmusic.MusicLoader;
import com.example.ppmusic.MyApp;
import com.example.ppmusic.NowPlayingCursor;
import com.example.ppmusic.R;
import com.example.ppmusic.activities.MainActivity;
import com.example.ppmusic.activities.MusicListActivity;
import com.example.ppmusic.adapter.PullToZoomListView;
import com.example.ppmusic.adapter.SongListAdapter01;
import com.example.ppmusic.base.MyBaseFragment;
import com.example.ppmusic.bean.MusicInfo;
import com.example.ppmusic.helpers.AddIdCursorLoader;
import com.example.ppmusic.helpers.CopyOfAddIdCursorLoader;
import com.example.ppmusic.helpers.utils.MusicUtils;
import com.example.ppmusic.service.ApolloService;
import com.example.ppmusic.ui.adapters.RecentlyAddedAdapter;
import com.example.ppmusic.utils.DBUtils;
import com.example.ppmusic.utils.DisplayUtil;
import com.example.ppmusic.utils.IntentUtil;
import com.example.ppmusic.adapter.baseView.SwipeMenuListView;
import com.example.ppmusic.adapter.pullrefresh.PullToRefreshLayout;
import com.example.ppmusic.adapter.pullrefresh.PullToRefreshLayout.OnRefreshListener;
import com.example.ppmusic.adapter.sortlistview.GroupMemberBean;
import com.example.ppmusic.adapter.baseView.SwipeMenu;
import com.example.ppmusic.adapter.baseView.SwipeMenuCreator;
import com.example.ppmusic.adapter.baseView.SwipeMenuItem;
import com.example.ppmusic.adapter.baseView.SwipeMenuListView.OnMenuItemClickListener;

import android.app.AlertDialog;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.MediaStore.Audio;
import android.provider.MediaStore.MediaColumns;
import android.provider.MediaStore.Audio.AudioColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

/**
 * 
 * @author CGQ
 * 
 */
public class Fragment_SongList01 extends MyBaseFragment implements
		LoaderCallbacks<Cursor>, OnClickListener, OnItemClickListener {

	MainActivity mainActivity;

	// Cursor 游标
	private Cursor mCursor;
	// Audio columns 音频字段
	public static int mTitleIndex, mAlbumIndex, mAlbumIdIndex, mArtistIndex,
			mMediaIdIndex;

	ImageView main_image;
	ListView lv_songs;
	List<MusicInfo> musicList_current = new ArrayList<MusicInfo>();

	//private SongListAdapter01 sadapter;// Adapter
	private RecentlyAddedAdapter mRecentlyAddedAdapter;
	private Bundle bundle;
	private String title = "本地音乐";

	// Bundle
	public Fragment_SongList01() {
	}

/*	public Fragment_SongList01(Bundle args) {
		setArguments(args);
	}*/

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		lv_songs = (ListView) rootView.findViewById(R.id.lv_songs);
		lv_songs.setVisibility(View.VISIBLE);
		lv_songs.setOnCreateContextMenuListener(this);
		lv_songs.setOnItemClickListener(this);
		// Adapter
		mRecentlyAddedAdapter = new RecentlyAddedAdapter(getActivity(),
				R.layout.item_song_list02 /* R.layout.listview_items */, null,
				new String[] {}, new int[] {}, 0, lv_songs);

		lv_songs.setAdapter(mRecentlyAddedAdapter);
		// Important!
		getLoaderManager().initLoader(0, null, this);
	}

	@Override
	public View setContentUI(LayoutInflater inflater, ViewGroup container) {
		bundle = getActivity().getIntent().getExtras();
		if (bundle.containsKey("Title")) {
			title = bundle.getString("Title");
		}
		return inflater.inflate(R.layout.music_list, container, false);
	}

	@Override
	public void init() {
		musicList_current.clear();
		musicList_current.addAll(MusicListActivity.musicList_current);
		lv_songs = (ListView) rootView.findViewById(R.id.lv_songs);

		// ListView lv_songs = (ListView)
		// rootView.findViewById(R.id.lv_songs);//
		// lv_songs.setVisibility(View.VISIBLE);
		// sadapter = new SongListAdapter01(getActivity(), musicList_current,
		// lv_songs);
		// lv_songs.setAdapter(sadapter);
		// lv_songs.setOnItemClickListener(this);
		PullToZoomListView lv = null;
		// LayoutInflater inflater = getActivity().getLayoutInflater();
		// View view = inflater.inflate(R.layout., null);
		// lv.getHeaderView().setImageResource(R.drawable.audio);
		// init2();
		// lv_list_menu.setAdapter(sadapter);
	}

	int test = 0;
	long testTime = System.currentTimeMillis();

	@Override
	public void onClick(View v) {
		Bundle bundle = new Bundle();

	}

	@Override
    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        if (mCursor instanceof NowPlayingCursor) {
            if (MusicUtils.mService != null) {
                MusicUtils.setQueuePosition(position);
                return;
            }
        }
        MusicUtils.playAll(getActivity(), mCursor, position);
      //MyApp.natureBinder.startPlay(musicList_current.get(position));
    }

	/********************************************************************************************************/

	/**
	 * Update the list as needed
	 */
	private final BroadcastReceiver mMediaStatusReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (lv_songs != null) {
				mRecentlyAddedAdapter.notifyDataSetChanged();
			}
		}
	};

	@Override
	public void onStart() {
		super.onStart();
		IntentFilter filter = new IntentFilter();
		filter.addAction(ApolloService.META_CHANGED);
		filter.addAction(ApolloService.PLAYSTATE_CHANGED);
		getActivity().registerReceiver(mMediaStatusReceiver, filter);
	}

	@Override
	public void onStop() {
		getActivity().unregisterReceiver(mMediaStatusReceiver);
		super.onStop();
	}

	@Override
	public android.support.v4.content.Loader<Cursor> onCreateLoader(int arg0,
			Bundle id) {
		String[] projection = new String[] { BaseColumns._ID,
				MediaColumns.TITLE, AudioColumns.ALBUM, AudioColumns.ARTIST };
		StringBuilder where = new StringBuilder();
		String sortOrder = MediaColumns.DATE_ADDED + " DESC";
		Uri uri = Audio.Media.EXTERNAL_CONTENT_URI;
		int X = MusicUtils.getIntPref(getActivity(), NUMWEEKS, 5) * 3600 * 24 * 7;
		where = new StringBuilder();
		where.append(MediaColumns.TITLE + " != ''");
		where.append(" AND " + AudioColumns.IS_MUSIC + "=1");
		where.append(" AND " + MediaColumns.DATE_ADDED + ">"
				+ (System.currentTimeMillis() / 1000 - X));
		return new CopyOfAddIdCursorLoader(getActivity(), uri, projection,
				where.toString(), null, sortOrder);

		// return new RecentlyAddedLoader(getActivity());
	}

	@Override
	public void onLoadFinished(
			android.support.v4.content.Loader<Cursor> loader, Cursor data) {
		// Check for database errors
		if (data == null) {
			return;
		}

		mMediaIdIndex = data.getColumnIndexOrThrow(BaseColumns._ID);
		mTitleIndex = data.getColumnIndexOrThrow(MediaColumns.TITLE);
		mArtistIndex = data.getColumnIndexOrThrow(AudioColumns.ARTIST);
		mAlbumIndex = data.getColumnIndexOrThrow(AudioColumns.ALBUM);
		mAlbumIdIndex = data.getColumnIndexOrThrow(AudioColumns.ALBUM_ID);
		mRecentlyAddedAdapter.changeCursor(data);
		mCursor = data;
	}

	@Override
	public void onLoaderReset(android.support.v4.content.Loader<Cursor> arg0) {
		if (mRecentlyAddedAdapter != null) {
			mRecentlyAddedAdapter.changeCursor(null);
		}
	}
}
