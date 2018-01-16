package com.example.ppmusic.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio;
import android.provider.MediaStore.Audio.AudioColumns;
import android.provider.MediaStore.Audio.Genres;
import android.provider.MediaStore.Audio.Playlists;
import android.provider.MediaStore.MediaColumns;
import android.support.transition.TransitionSet;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v7.widget.Toolbar;
import android.support.transition.AutoTransition;
import android.support.transition.TransitionManager;
import android.support.transition.TransitionSet;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ppmusic.MusicLoader;
import com.example.ppmusic.NowPlayingCursor;
import com.example.ppmusic.R;
import com.example.ppmusic.activities.MainActivity;
import com.example.ppmusic.adapter.EditView_withDeleteButton;
import com.example.ppmusic.adapter.MusicAdapter;
import com.example.ppmusic.adapter.sortlistview.CharacterParser;
import com.example.ppmusic.adapter.sortlistview.ClearEditText;
import com.example.ppmusic.adapter.sortlistview.GroupMemberBean;
import com.example.ppmusic.adapter.sortlistview.PinyinComparator;
import com.example.ppmusic.adapter.sortlistview.SideBar;
import com.example.ppmusic.base.MyBaseFragment;
import com.example.ppmusic.bean.MusicInfo;
import com.example.ppmusic.helpers.utils.MusicUtils;
import com.example.ppmusic.interfaces.FilterListener;
import com.example.ppmusic.service.ApolloService;
import com.example.ppmusic.ui.adapters.TrackAdapter;
import com.example.ppmusic.utils.DBUtils;
import com.example.ppmusic.view.custom.CustomListView;
import com.example.ppmusic.view.custom.CustomScrollView;

import java.util.ArrayList;
import java.util.List;

import static com.example.ppmusic.constants.Constants.EXTERNAL;
import static com.example.ppmusic.constants.Constants.INTENT_ADD_TO_PLAYLIST;
import static com.example.ppmusic.constants.Constants.INTENT_PLAYLIST_LIST;
import static com.example.ppmusic.constants.Constants.MIME_TYPE;
import static com.example.ppmusic.constants.Constants.PLAYLIST_FAVORITES;
import static com.example.ppmusic.constants.Constants.PLAYLIST_QUEUE;

public class MainFragment3 extends MyBaseFragment implements OnClickListener ,LoaderManager.LoaderCallbacks<Cursor>,OnItemClickListener {

	MainActivity mainActivity;

	ImageView main_image;
	private CustomListView lvSongs;
	private List<MusicInfo> musicList = new ArrayList<>();

	private SideBar sideBar;
	private TextView dialog;
	private LinearLayout titleLayout;
	private TextView title;
	private TextView tvNofriends;
	private ClearEditText mClearEditText;

	/**
	 * 上次第一个可见元素，用于滚动时记录标识。
	 */
	private int lastFirstVisibleItem = -1;
	/**
	 * 汉字转换成拼音的类
	 */
	private CharacterParser characterParser;
	private List<GroupMemberBean> SourceDateList;
	/**
	 * 根据拼音来排列ListView里面的数据类
	 */
	private PinyinComparator pinyinComparator;

	EditText tvSearch;
	LinearLayout mSearchLayout;
	CustomScrollView mScrollView;
	boolean isExpand = true;
	ImageView ivImg;
	Toolbar toolbar;
	private TransitionSet mSet;

	// Adapter
	//private TrackAdapter mTrackAdapter;
	private MusicAdapter adapter;
	// Cursor
	private Cursor mCursor;
	// Playlist ID
	private long mPlaylistId = PLAYLIST_QUEUE;
	// Selected position
	private int mSelectedPosition;
	// Used to set ringtone
	private long mSelectedId;
	// Options
	private final int PLAY_SELECTION = 6;
	private final int USE_AS_RINGTONE = 7;
	private final int ADD_TO_PLAYLIST = 8;
	private final int SEARCH = 9;
	private final int REMOVE = 10;
	private boolean mEditMode = false;

	// Audio columns
	public static int mTitleIndex, mAlbumIndex, mArtistIndex, mMediaIdIndex;

	@Override
	public View setContentUI(LayoutInflater inflater, ViewGroup container) {
		return inflater.inflate(R.layout.activity_add_friends, container, false);
	}

	@Override
	public void init() {
		mainActivity = (MainActivity) getActivity();
		
		adapter = new MusicAdapter(musicList,getActivity(),new FilterListener() {
			// 回调方法获取过滤后的数据
			public void getFilterData(final List<MusicInfo> list) {
				// 这里可以拿到过滤后数据，所以在这里可以对搜索后的数据进行操作
				Log.e("TAG", "接口回调成功");
				lvSongs.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
						try{
							if (mCursor instanceof NowPlayingCursor) {
								if (MusicUtils.mService != null) {
									MusicUtils.setQueuePosition(list.get(i).getPosition());
									return;
								}
							}
							MusicUtils.playAll(getActivity(), mCursor, list.get(i).getPosition());
						}catch (Exception e){
							e.printStackTrace();
						}
					}
				});
			}
		});
		lvSongs = (CustomListView) rootView.findViewById(R.id.lvSongs);
		lvSongs.setAdapter(adapter);
		lvSongs.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
					try{
						if (mCursor instanceof NowPlayingCursor) {
							if (MusicUtils.mService != null) {
								MusicUtils.setQueuePosition(i);
								return;
							}
						}
						MusicUtils.playAll(getActivity(), mCursor, i);
					}catch (Exception e){
						e.printStackTrace();
					}
			}
		});
		ArrayList<String> strs = new ArrayList<String>();

		/*if(musicList_current.size()<1){
			Toast.makeText(getActivity(), "songlist", Toast.LENGTH_SHORT).show();
		};

		for (int i = 0; i < musicList_current.size(); i++) {
			strs.add(musicList_current.get(i).getTitle());
		}
		final String[] sss = strs.toArray(new String[strs.size()]);*/
		titleLayout = (LinearLayout) findViewById(R.id.title_layout);
		title = (TextView) this.findViewById(R.id.title_layout_catalog);
		tvNofriends = (TextView) this
				.findViewById(R.id.title_layout_no_friends);

		mClearEditText = (ClearEditText) findViewById(R.id.filter_edit);
		mClearEditText.setFocusable(false);
		// 实例化汉字转拼音类
		characterParser = CharacterParser.getInstance();

		pinyinComparator = new PinyinComparator();

		sideBar = (SideBar) findViewById(R.id.sidrbar);
		dialog = (TextView) findViewById(R.id.dialog);
		sideBar.setTextView(dialog);

		// 设置右侧触摸监听
		sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

			@Override
			public void onTouchingLetterChanged(String s) {
				// 该字母首次出现的位置
				/*int position = adapter.getPositionForSection(s.charAt(0));
				if (position != -1) {
					lvSongs.setSelection(position);
				}*/
			}
		});
		//SourceDateList = filledData(sss,musicList_current);

		// 根据a-z进行排序源数据
		//Collections.sort(SourceDateList, pinyinComparator);

		isEditMode();

		// Adapter
		//mTrackAdapter = new TrackAdapter(getActivity(), R.layout.listview_items, null,
		//		new String[] {}, new int[] {}, 0);
		lvSongs.setOnCreateContextMenuListener(this);

		// Important!
		getLoaderManager().initLoader(0, null, this);

		tvSearch = (EditText) findViewById(R.id.tv_search);
		mSearchLayout = (LinearLayout) findViewById(R.id.ll_search);
		mScrollView = (CustomScrollView) findViewById(R.id.scrollView);
		toolbar = (Toolbar) findViewById(R.id.toolbar);
		ivImg = (ImageView) findViewById(R.id.iv_img);

		//设置toolbar初始透明度为0
		toolbar.getBackground().mutate().setAlpha(0);
		//scrollview滚动状态监听
		mScrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
			@Override
			public void onScrollChanged() {
				//改变toolbar的透明度
				changeToolbarAlpha();
				//滚动距离>=大图高度-toolbar高度 即toolbar完全盖住大图的时候 且不是伸展状态 进行伸展操作
				if (mScrollView.getScrollY() >=ivImg.getHeight() - toolbar.getHeight()  && !isExpand) {
					reduce();
					isExpand = true;
				}
				//滚动距离<=0时 即滚动到顶部时  且当前伸展状态 进行收缩操作
				else if (mScrollView.getScrollY()<=0&& isExpand) {
					expand();
					isExpand = false;
				}
			}
		});

		setListeners();
	}
	private void setListeners() {
		tvSearch.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
									  int count) {
				if(adapter != null){
					adapter.getFilter().filter(s);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
										  int after) {
				// TODO Auto-generated method stub
			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
			}
		});
	}
	long testTime = System.currentTimeMillis();

	@Override
	public void onClick(View v) {
		Bundle bundle = new Bundle();
		
	}
	private void changeToolbarAlpha() {
		int scrollY = mScrollView.getScrollY();
		//快速下拉会引起瞬间scrollY<0
		if(scrollY<0){
			toolbar.getBackground().mutate().setAlpha(0);
			return;
		}
		//计算当前透明度比率
		float radio= Math.min(1,scrollY/(ivImg.getHeight()-toolbar.getHeight()*1f));
		//设置透明度
		toolbar.getBackground().mutate().setAlpha( (int)(radio * 0xFF));
	}


	private void expand() {
		//设置伸展状态时的布局
		tvSearch.setHint("搜索歌曲名称");
		RelativeLayout.LayoutParams LayoutParams = (RelativeLayout.LayoutParams) mSearchLayout.getLayoutParams();
		LayoutParams.width = LayoutParams.MATCH_PARENT;
		LayoutParams.setMargins(dip2px(10), dip2px(10), dip2px(10), dip2px(10));
		mSearchLayout.setLayoutParams(LayoutParams);
		//开始动画
		beginDelayedTransition(mSearchLayout);
	}

	private void reduce() {
		//设置收缩状态时的布局搜索
		tvSearch.setHint("");
		tvSearch.setText("");
		RelativeLayout.LayoutParams LayoutParams = (RelativeLayout.LayoutParams) mSearchLayout.getLayoutParams();
		LayoutParams.width = dip2px(80);
		LayoutParams.setMargins(dip2px(10), dip2px(10), dip2px(10), dip2px(10));
		mSearchLayout.setLayoutParams(LayoutParams);
		//开始动画
		beginDelayedTransition(mSearchLayout);
	}

	void beginDelayedTransition(ViewGroup view) {
		mSet = new AutoTransition();
		mSet.setDuration(300);
		TransitionManager.beginDelayedTransition(view, mSet);
	}

	private int dip2px(float dpVale) {
		final float scale = getResources().getDisplayMetrics().density;
		return (int) (dpVale * scale + 0.5f);
	}
	/**
	 * 为ListView填充数据
	 *
	 * @param date
	 * @param musicList_current2
	 * @return
	 */
	private List<GroupMemberBean> filledData(String[] date, List<MusicInfo> musicList_current2) {
		List<GroupMemberBean> mSortList = new ArrayList<GroupMemberBean>();

		for (int i = 0; i < date.length; i++) {
			GroupMemberBean sortModel = new GroupMemberBean();
			sortModel.setName(date[i]);
			// 汉字转换成拼音
			String pinyin = characterParser.getSelling(date[i]);
			String sortString = pinyin.substring(0, 1).toUpperCase();

			// 正则表达式，判断首字母是否是英文字母
			if (sortString.matches("[A-Z]")) {
				sortModel.setSortLetters(sortString.toUpperCase());
			} else {
				sortModel.setSortLetters("#");
			}
			sortModel.setMusicInfo(musicList_current2.get(i));
			mSortList.add(sortModel);
		}
		return mSortList;

	}

	@Override
	public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
		String str = " != ''";
		if(tvSearch!=null&&tvSearch.getText()!=null&&!tvSearch.getText().toString().trim().equals("")){
			str = " like '"+tvSearch.getText().toString()+"'";
		}
			String[] projection = new String[] {
					BaseColumns._ID, MediaColumns.TITLE, AudioColumns.ALBUM, AudioColumns.ARTIST
			};
			StringBuilder where = new StringBuilder();
			String sortOrder = Audio.Media.DEFAULT_SORT_ORDER;
			where.append(AudioColumns.IS_MUSIC + "=1").append(" AND " + MediaColumns.TITLE +str );
			Uri uri = Audio.Media.EXTERNAL_CONTENT_URI;
			if (getArguments() != null) {
				mPlaylistId = getArguments().getLong(BaseColumns._ID);
				String mimeType = getArguments().getString(MIME_TYPE);
				if (Audio.Playlists.CONTENT_TYPE.equals(mimeType)) {
					where = new StringBuilder();
					where.append(AudioColumns.IS_MUSIC + "=1");
					where.append(" AND " + MediaColumns.TITLE + " != ''");
					switch ((int)mPlaylistId) {
						case (int)PLAYLIST_QUEUE:
							uri = Audio.Media.EXTERNAL_CONTENT_URI;
							long[] mNowPlaying = MusicUtils.getQueue();
							if (mNowPlaying.length == 0)
								return null;
							where = new StringBuilder();
							where.append(BaseColumns._ID + " IN (");
							if (mNowPlaying == null || mNowPlaying.length <= 0)
								return null;
							for (long queue_id : mNowPlaying) {
								where.append(queue_id + ",");
							}
							where.deleteCharAt(where.length() - 1);
							where.append(")");
							break;
						case (int)PLAYLIST_FAVORITES:
							long favorites_id = MusicUtils.getFavoritesId(getActivity());
							projection = new String[] {
									Playlists.Members._ID, Playlists.Members.AUDIO_ID,
									MediaColumns.TITLE, AudioColumns.ALBUM, AudioColumns.ARTIST
							};
							uri = Playlists.Members.getContentUri(EXTERNAL, favorites_id);
							sortOrder = Playlists.Members.DEFAULT_SORT_ORDER;
							break;
						default:
							if (id < 0)
								return null;
							projection = new String[] {
									Playlists.Members._ID, Playlists.Members.AUDIO_ID,
									MediaColumns.TITLE, AudioColumns.ALBUM, AudioColumns.ARTIST,
									AudioColumns.DURATION
							};

							uri = Playlists.Members.getContentUri(EXTERNAL, mPlaylistId);
							sortOrder = Playlists.Members.DEFAULT_SORT_ORDER;
							break;
					}
				} else if (Audio.Genres.CONTENT_TYPE.equals(mimeType)) {
					long genreId = getArguments().getLong(BaseColumns._ID);
					uri = Genres.Members.getContentUri(EXTERNAL, genreId);
					projection = new String[] {
							BaseColumns._ID, MediaColumns.TITLE, AudioColumns.ALBUM,
							AudioColumns.ARTIST
					};
					where = new StringBuilder();
					where.append(AudioColumns.IS_MUSIC + "=1").append(
							" AND " + MediaColumns.TITLE + " != ''");
					sortOrder = Genres.Members.DEFAULT_SORT_ORDER;
				} else {
					if (Audio.Albums.CONTENT_TYPE.equals(mimeType)) {
						long albumId = getArguments().getLong(BaseColumns._ID);
						where.append(" AND " + AudioColumns.ALBUM_ID + "=" + albumId);
						sortOrder = Audio.Media.TRACK + ", " + sortOrder;
					} else if (Audio.Artists.CONTENT_TYPE.equals(mimeType)) {
						sortOrder = MediaColumns.TITLE;
						long artist_id = getArguments().getLong(BaseColumns._ID);
						where.append(" AND " + AudioColumns.ARTIST_ID + "=" + artist_id);
					}
				}
			}
			return new CursorLoader(getActivity(), uri, projection, where.toString(), null, sortOrder);

	}

	@Override
	public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
		// Check for database errors
		if (data == null) {
			return;
		}

		if (getArguments() != null
				&& Playlists.CONTENT_TYPE.equals(getArguments().getString(MIME_TYPE))
				&& (getArguments().getLong(BaseColumns._ID) >= 0 || getArguments().getLong(
				BaseColumns._ID) == PLAYLIST_FAVORITES)) {
			mMediaIdIndex = data.getColumnIndexOrThrow(Playlists.Members.AUDIO_ID);
			mTitleIndex = data.getColumnIndexOrThrow(MediaColumns.TITLE);
			mAlbumIndex = data.getColumnIndexOrThrow(AudioColumns.ALBUM);
			// FIXME
			// mArtistIndex =
			// data.getColumnIndexOrThrow(Playlists.Members.ARTIST);
		} else if (getArguments() != null
				&& Genres.CONTENT_TYPE.equals(getArguments().getString(MIME_TYPE))) {
			mMediaIdIndex = data.getColumnIndexOrThrow(BaseColumns._ID);
			mTitleIndex = data.getColumnIndexOrThrow(MediaColumns.TITLE);
			mArtistIndex = data.getColumnIndexOrThrow(AudioColumns.ARTIST);
			mAlbumIndex = data.getColumnIndexOrThrow(AudioColumns.ALBUM);
		} else if (getArguments() != null
				&& Audio.Artists.CONTENT_TYPE.equals(getArguments().getString(MIME_TYPE))) {
			mTitleIndex = data.getColumnIndexOrThrow(MediaColumns.TITLE);
			// mArtistIndex is "line2" of the ListView
			mArtistIndex = data.getColumnIndexOrThrow(AudioColumns.ALBUM);
		} else if (getArguments() != null
				&& Audio.Albums.CONTENT_TYPE.equals(getArguments().getString(MIME_TYPE))) {
			mMediaIdIndex = data.getColumnIndexOrThrow(BaseColumns._ID);
			mTitleIndex = data.getColumnIndexOrThrow(MediaColumns.TITLE);
			mArtistIndex = data.getColumnIndexOrThrow(AudioColumns.ARTIST);
		} else {
			mMediaIdIndex = data.getColumnIndexOrThrow(BaseColumns._ID);
			mTitleIndex = data.getColumnIndexOrThrow(MediaColumns.TITLE);
			mArtistIndex = data.getColumnIndexOrThrow(AudioColumns.ARTIST);
			mAlbumIndex = data.getColumnIndexOrThrow(AudioColumns.ALBUM);
		}
		int index  = 0;
		while (data.moveToNext()) {
			MusicInfo musicInfo = new MusicInfo();
			musicInfo.setId(data.getLong(MainFragment3.mMediaIdIndex));
			musicInfo.setArtist(data.getString(MainFragment3.mArtistIndex));
			musicInfo.setTitle(data.getString(MainFragment3.mTitleIndex));
			musicInfo.setAlbum(data.getString(MainFragment3.mAlbumIndex));
			musicInfo.setPosition(index);
			musicList.add(musicInfo);
			index++;
		}
		Log.i("CGQ","size="+musicList.size());
		//mTrackAdapter.changeCursor(data);
		//lvSongs.invalidateViews();
		mCursor = data;
		adapter.notifyDataSetChanged();
		mScrollView.smoothScrollTo(0, 0);
	}

	@Override
	public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {
		/*if (mTrackAdapter != null)
			mTrackAdapter.changeCursor(null);*/
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
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
		menu.add(0, PLAY_SELECTION, 0, getResources().getString(R.string.play_all));
		menu.add(0, ADD_TO_PLAYLIST, 0, getResources().getString(R.string.add_to_playlist));
		menu.add(0, USE_AS_RINGTONE, 0, getResources().getString(R.string.use_as_ringtone));
		if (mEditMode) {
			menu.add(0, REMOVE, 0, R.string.remove);
		}
		menu.add(0, SEARCH, 0, getResources().getString(R.string.search));

		AdapterContextMenuInfo mi = (AdapterContextMenuInfo)menuInfo;
		mSelectedPosition = mi.position;
		mCursor.moveToPosition(mSelectedPosition);

		try {
			mSelectedId = mCursor.getLong(mMediaIdIndex);
		} catch (IllegalArgumentException ex) {
			mSelectedId = mi.id;
		}

		String title = mCursor.getString(mTitleIndex);
		menu.setHeaderTitle(title);
		super.onCreateContextMenu(menu, v, menuInfo);
	}

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case PLAY_SELECTION:
                int position = mSelectedPosition;
                MusicUtils.playAll(getActivity(), mCursor, position);
                break;
            case ADD_TO_PLAYLIST: {
                Intent intent = new Intent(INTENT_ADD_TO_PLAYLIST);
                long[] list = new long[] {
                        mSelectedId
                };
                intent.putExtra(INTENT_PLAYLIST_LIST, list);
                getActivity().startActivity(intent);
                break;
            }
            case USE_AS_RINGTONE:
                MusicUtils.setRingtone(getActivity(), mSelectedId);
                break;
            case REMOVE: {
                removePlaylistItem(mSelectedPosition);
                break;
            }
            case SEARCH: {
                MusicUtils.doSearch(getActivity(), mCursor, mTitleIndex);
                break;
            }
            default:
                break;
        }
        return super.onContextItemSelected(item);
    }

    /**
     * @param which
     */
    private void removePlaylistItem(int which) {

        mCursor.moveToPosition(which);
        long id = mCursor.getLong(mMediaIdIndex);
        if (mPlaylistId >= 0) {
            Uri uri = Playlists.Members.getContentUri(EXTERNAL, mPlaylistId);
            getActivity().getContentResolver().delete(uri, Playlists.Members.AUDIO_ID + "=" + id,
                    null);
        } else if (mPlaylistId == PLAYLIST_QUEUE) {
            MusicUtils.removeTrack(id);
            reloadQueueCursor();
        } else if (mPlaylistId == PLAYLIST_FAVORITES) {
            MusicUtils.removeFromFavorites(getActivity(), id);
        }
       // lvSongs.invalidateViews();
    }

    /**
     * Reload the queue after we remove a track
     */
    private void reloadQueueCursor() {
		String str = " != ''";
		if(tvSearch!=null&&tvSearch.getText()!=null&&!tvSearch.getText().toString().trim().equals("")){
			str = " like '"+tvSearch.getText().toString()+"' ";
		}
        if (mPlaylistId == PLAYLIST_QUEUE) {
            String[] cols = new String[] {
                    BaseColumns._ID, MediaColumns.TITLE, MediaColumns.DATA, AudioColumns.ALBUM,
                    AudioColumns.ARTIST, AudioColumns.ARTIST_ID
            };
            StringBuilder selection = new StringBuilder();
            selection.append(AudioColumns.IS_MUSIC + "=1");
            selection.append(" AND " + MediaColumns.TITLE + str);
            //selection.append(" order by if(instr("+MediaColumns.TITLE +",'"+tvSearch.getText().toString()+"') >0,1,0) desc   ");
            Uri uri = Audio.Media.EXTERNAL_CONTENT_URI;
            long[] mNowPlaying = MusicUtils.getQueue();
            if (mNowPlaying.length == 0) {
            }
            selection = new StringBuilder();
            selection.append(BaseColumns._ID + " IN (");
            for (int i = 0; i < mNowPlaying.length; i++) {
                selection.append(mNowPlaying[i]);
                if (i < mNowPlaying.length - 1) {
                    selection.append(",");
                }
            }
            selection.append(")");
            mCursor = MusicUtils.query(getActivity(), uri, cols, selection.toString(), null, MediaColumns.TITLE);
            //mTrackAdapter.changeCursor(mCursor);
			//lvSongs.invalidateViews();
        }
    }

	@Override
	public void onStart() {
		super.onStart();
		IntentFilter filter = new IntentFilter();
		filter.addAction(ApolloService.META_CHANGED);
		filter.addAction(ApolloService.PLAYSTATE_CHANGED);
		getActivity().registerReceiver(mMediaStatusReceiver, filter);
	}
	/**
	 * Update the list as needed
	 */
	private final BroadcastReceiver mMediaStatusReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (lvSongs != null) {
				adapter.notifyDataSetChanged();
				// Scroll to the currently playing track in the queue
				/*if (mPlaylistId == PLAYLIST_QUEUE)
					lvSongs.postDelayed(new Runnable() {
						@Override
						public void run() {
							lvSongs.setSelection(MusicUtils.getQueuePosition());
						}
					}, 100);*/
			}
		}

	};

	/**
	 * Check if we're viewing the contents of a playlist
	 */
	public void isEditMode() {
		if (getArguments() != null) {
			String mimetype = getArguments().getString(MIME_TYPE);
			if (MediaStore.Audio.Playlists.CONTENT_TYPE.equals(mimetype)) {
				mPlaylistId = getArguments().getLong(BaseColumns._ID);
				switch ((int)mPlaylistId) {
					case (int)PLAYLIST_QUEUE:
						mEditMode = true;
						break;
					case (int)PLAYLIST_FAVORITES:
						mEditMode = true;
						break;
					default:
						if (mPlaylistId > 0) {
							mEditMode = true;
						}
						break;
				}
			}
		}
	}
}
