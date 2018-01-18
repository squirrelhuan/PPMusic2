/**
 *
 */

package com.example.ppmusic.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.AudioManager.*;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.provider.MediaStore.*;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.app.LoaderManager.LoaderCallbacks;
import android.widget.RelativeLayout;

import com.example.ppmusic.NowPlayingCursor;
import com.example.ppmusic.R;
import com.example.ppmusic.adapter.MusicFormAdapter;
import com.example.ppmusic.bean.MusicCollection;
import com.example.ppmusic.bean.MusicInfo;
import com.example.ppmusic.helpers.AddIdCursorLoader;
import com.example.ppmusic.helpers.utils.MusicUtils;
import com.example.ppmusic.interfaces.FilterListener;
import com.example.ppmusic.service.ApolloService;
import com.example.ppmusic.ui.adapters.Adapter_PhysicalCharacteristics;
import com.example.ppmusic.ui.fragment.grid.PPQuickQueueFragment;

import java.util.ArrayList;
import java.util.List;

import static com.example.ppmusic.constants.Constants.EXTERNAL;
import static com.example.ppmusic.constants.Constants.MIME_TYPE;
import static com.example.ppmusic.constants.Constants.PLAYLIST_FAVORITES;
import static com.example.ppmusic.constants.Constants.PLAYLIST_QUEUE;


/**
 * 快速查找
 *
 * @author
 */
public class MusicQuickList extends Activity implements LoaderCallbacks<Cursor> {

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.quick_queue_paopao);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Control Media volume
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        initView();
        initSelectCD();
    }

    private ListView lvSongs;
    private MusicFormAdapter adapter_music_list;
    private Cursor mCursor;

    private void initView() {
        lvSongs = (ListView) findViewById(R.id.lvSongs);
        adapter_music_list = new MusicFormAdapter(musicList, MusicQuickList.this, this, new FilterListener() {
            // 回调方法获取过滤后的数据
            public void getFilterData(final List<MusicInfo> list) {
                // 这里可以拿到过滤后数据，所以在这里可以对搜索后的数据进行操作
                Log.e("TAG", "接口回调成功");
                lvSongs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        try {
                            if (mCursor instanceof NowPlayingCursor) {
                                if (MusicUtils.mService != null) {
                                    MusicUtils.setQueuePosition(list.get(i).getPosition());
                                    return;
                                }
                            }
                            MusicUtils.playAll(MusicQuickList.this, mCursor, list.get(i).getPosition());
                            MusicQuickList.this.finish();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
        lvSongs.setAdapter(adapter_music_list);
        lvSongs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    if (mCursor instanceof NowPlayingCursor) {
                        if (MusicUtils.mService != null) {
                            MusicUtils.setQueuePosition(i);
                            return;
                        }
                    }
                    MusicUtils.playAll(MusicQuickList.this, mCursor, i);
                    MusicQuickList.this.finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        //歌单
        getLoaderManager().initLoader(0, null, this);
        //歌曲
        getLoaderManager().initLoader(SongSheet_Current_Tag, null, this);
    }

    public static final int SongSheet_Current_Tag = -1;
    private int screenWidth = 0;
    private ViewPager viewPager;
    private Adapter_PhysicalCharacteristics adater;

    private void initSelectCD() {
        // 獲取屏幕寬度
        DisplayMetrics dm = getResources().getDisplayMetrics();
        screenWidth = dm.widthPixels;
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        //viewPager.setPageMargin(20);
        viewPager.setOffscreenPageLimit(1);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                screenWidth / 2, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(screenWidth / 5, 0, screenWidth / 5, 0);
        // viewPager.setLayoutParams(params);

        adater = new Adapter_PhysicalCharacteristics(MusicQuickList.this, musicCollections);
        viewPager.setAdapter(adater);

        viewPager.setPageTransformer(false, new ScaleTransformer());
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Bundle bundle = new Bundle();
                bundle.putString("ID", musicCollections.get(position).getId() + "");
                //歌曲
                int i = (int) musicCollections.get(position).getId();
                if (getLoaderManager().getLoader(i) == null) {
                    getLoaderManager().initLoader(i, bundle, MusicQuickList.this);
                } else {
                    getLoaderManager().restartLoader(i, bundle, MusicQuickList.this);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private List<MusicInfo> musicList = new ArrayList<>();
    private List<MusicCollection> musicCollections = new ArrayList<>();
    int size;

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = null;
        Uri uri;
        String sortOrder;
        StringBuilder selection = new StringBuilder();
        long[] mNowPlaying = MusicUtils.getQueue();
        switch (i) {
            case 0://加载歌单
                uri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
                sortOrder = MediaStore.Audio.Playlists.DEFAULT_SORT_ORDER;
                selection = new StringBuilder();
                if (bundle != null) {
                    selection.append(BaseColumns._ID);
                    selection.append(" = " + bundle.get("ID") + " ");
                }
                CursorLoader cursorLoader = new CursorLoader(MusicQuickList.this, uri, projection, selection.toString(), null, sortOrder);
                return cursorLoader;
            case SongSheet_Current_Tag://加载最近歌曲
                projection = new String[]{
                        BaseColumns._ID, MediaStore.MediaColumns.TITLE, MediaStore.Audio.AudioColumns.ALBUM, MediaStore.Audio.AudioColumns.ARTIST,
                };
                sortOrder = MediaStore.Audio.Media.DEFAULT_SORT_ORDER;
                uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                if (mNowPlaying.length == 0)
                    return null;
                selection = new StringBuilder();
                selection.append(BaseColumns._ID + " IN (");
                if (mNowPlaying == null || mNowPlaying.length <= 0)
                    return null;
                for (long queue_id : mNowPlaying) {
                    selection.append(queue_id + ",");
                }
                size = mNowPlaying.length;
                selection.deleteCharAt(selection.length() - 1);
                selection.append(")");

                // size += data.getColumnCount();
                int length = 80;
                DisplayMetrics dm = new DisplayMetrics();
                MusicQuickList.this.getWindowManager().getDefaultDisplay().getMetrics(dm);
                float density = dm.density;
                int gridviewWidth = (int) (size * (length + 4) * density);
                int itemWidth = (int) (length * density);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(gridviewWidth, LinearLayout.LayoutParams.FILL_PARENT);

                return new AddIdCursorLoader(MusicQuickList.this, uri, projection, selection.toString(), null,
                        sortOrder);
            default:
                projection = new String[]{
                        BaseColumns._ID, MediaColumns.TITLE, Audio.AudioColumns.ALBUM, Audio.AudioColumns.ARTIST
                };
                StringBuilder where = new StringBuilder();
                sortOrder = Audio.Media.DEFAULT_SORT_ORDER;
                where.append(Audio.AudioColumns.IS_MUSIC + "=1").append(" AND " + MediaColumns.TITLE + " != ''");
                where = new StringBuilder();
                where.append(Audio.AudioColumns.IS_MUSIC + "=1");
                where.append(" AND " + MediaColumns.TITLE + " != ''");
                switch (i) {
                    case (int) PLAYLIST_QUEUE:
                        uri = Audio.Media.EXTERNAL_CONTENT_URI;
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
                    case (int) PLAYLIST_FAVORITES:
                        long favorites_id = MusicUtils.getFavoritesId(MusicQuickList.this);
                        projection = new String[]{
                                Audio.Playlists.Members._ID, Audio.Playlists.Members.AUDIO_ID,
                                MediaColumns.TITLE, Audio.AudioColumns.ALBUM, Audio.AudioColumns.ARTIST
                        };
                        uri = Audio.Playlists.Members.getContentUri(EXTERNAL, favorites_id);
                        sortOrder = Audio.Playlists.Members.DEFAULT_SORT_ORDER;
                        break;
                    default:
                        if (i < 0)
                            return null;
                        projection = new String[]{
                                Audio.Playlists.Members._ID, Audio.Playlists.Members.AUDIO_ID,
                                MediaColumns.TITLE, Audio.AudioColumns.ALBUM, Audio.AudioColumns.ARTIST,
                                Audio.AudioColumns.DURATION
                        };

                        uri = Audio.Playlists.Members.getContentUri(EXTERNAL, i);
                        sortOrder = Audio.Playlists.Members.DEFAULT_SORT_ORDER;
                        break;
                }
                return new CursorLoader(MusicQuickList.this, uri, projection, where.toString(), null, sortOrder);
        }

    }

    // Audio columns
    public static int mTitleIndex, mAlbumIndex, mArtistIndex, mMediaIdIndex, mAlbumIdIndex;

    // Aduio columns
    public static int mPlaylistNameIndex, mPlaylistIdIndex;

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data == null) {
            return;
        }
        //加载歌单
        if (loader.getId() == 0) {
            mPlaylistIdIndex = data.getColumnIndexOrThrow(BaseColumns._ID);
            mPlaylistNameIndex = data.getColumnIndexOrThrow(MediaStore.Audio.PlaylistsColumns.NAME);
            //mPlaylistAdapter.changeCursor(data);
            mCursor = data;

            musicCollections.clear();
            MusicCollection musicInfo1 = new MusicCollection();
            musicInfo1.setId(-1);
            musicInfo1.setName("播放列表");
            musicCollections.add(musicInfo1);
            while (data.moveToNext()) {
                MusicCollection musicInfo = new MusicCollection();
                musicInfo.setId(data.getLong(mPlaylistIdIndex));
                musicInfo.setName(data.getString(mPlaylistNameIndex));
                musicCollections.add(musicInfo);
            }
            adater.notifyDataSetChanged();
        } else {
            mMediaIdIndex = data.getColumnIndexOrThrow(BaseColumns._ID);
            mTitleIndex = data.getColumnIndexOrThrow(MediaStore.MediaColumns.TITLE);
            mArtistIndex = data.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ARTIST);
            mAlbumIndex = data.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ALBUM);
            //mAlbumIdIndex = data.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ALBUM_ID);
            //mQuickQueueAdapter.changeCursor(data);
            mCursor = data;

            musicList.clear();
            int index = 0;
            while (data.moveToNext()) {
                MusicInfo musicInfo = new MusicInfo();
                musicInfo.setId(data.getLong(mMediaIdIndex));
                musicInfo.setArtist(data.getString(mArtistIndex));
                musicInfo.setTitle(data.getString(mTitleIndex));
                // musicInfo.setAlbum(data.getString(mAlbumIndex));
                musicInfo.setPosition(index);
                musicList.add(musicInfo);
                index++;
            }

            adapter_music_list.setSongSheetId(loader.getId());
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (adapter_music_list != null)
            adapter_music_list.notifyDataSetChanged();
    }

    /**
     * Update the list as needed
     */
    private final BroadcastReceiver mMediaStatusReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (adapter_music_list != null) {
                adapter_music_list.notifyDataSetChanged();
            }
        }

    };

    /**
     * @param
     */
    public void removePlaylistItem(int songSheet_id, long id) {
        //mCursor.moveToPosition(which);
        //long id = mCursor.getLong(mMediaIdIndex);
        if (songSheet_id == PLAYLIST_FAVORITES) {
            MusicUtils.removeFromFavorites(MusicQuickList.this, id);
            Log.i("FAVORITES","songSheet_id="+songSheet_id+",id="+id+"");
        } else {
            MusicUtils.removeTrack(id);
            Log.i("NO_____FAVORITES","songSheet_id="+songSheet_id+",id="+id+"");
        }
        reloadQueueCursor(songSheet_id);
        //mGridView.invalidateViews();
    }

    /**
     * Reload the queue after we remove a track
     */
    private void reloadQueueCursor(int mPlaylistId) {
        if (mPlaylistId == SongSheet_Current_Tag) {//正在播放列表
            String[] projection = new String[]{
                    BaseColumns._ID, MediaStore.MediaColumns.TITLE, MediaStore.Audio.AudioColumns.ALBUM, MediaStore.Audio.AudioColumns.ARTIST,
            };
            StringBuilder selection = new StringBuilder();
            Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            String sortOrder = MediaStore.Audio.Media.DEFAULT_SORT_ORDER;
            uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            long[] mNowPlaying = MusicUtils.getQueue();
            if (mNowPlaying.length == 0)
                return;
            selection = new StringBuilder();
            selection.append(BaseColumns._ID + " IN (");
            if (mNowPlaying == null || mNowPlaying.length <= 0)
                return;
            for (long queue_id : mNowPlaying) {
                selection.append(queue_id + ",");
            }
            selection.deleteCharAt(selection.length() - 1);
            selection.append(")");

            mCursor = MusicUtils.query(MusicQuickList.this, uri, projection, selection.toString(), null,
                    sortOrder);
            // mQuickQueueAdapter.changeCursor(mCursor);
            int index = 0;
            musicList.clear();
            while (mCursor.moveToNext()) {
                MusicInfo musicInfo = new MusicInfo();
                musicInfo.setId(mCursor.getLong(mMediaIdIndex));
                musicInfo.setArtist(mCursor.getString(mArtistIndex));
                musicInfo.setTitle(mCursor.getString(mTitleIndex));
                musicInfo.setAlbum(mCursor.getString(mAlbumIndex));
                musicInfo.setPosition(index);
                musicList.add(musicInfo);
                index++;
            }
        } else {
            MusicUtils.getSongListForPlaylist(MusicQuickList.this, mPlaylistId);
            String[] projection = new String[]{
                    BaseColumns._ID, MediaColumns.TITLE, MediaStore.Audio.AudioColumns.ALBUM, MediaStore.Audio.AudioColumns.ARTIST
            };
            StringBuilder where = new StringBuilder();
            String sortOrder = MediaStore.Audio.Media.DEFAULT_SORT_ORDER;
            where.append(MediaStore.Audio.AudioColumns.IS_MUSIC + "=1").append(" AND " + MediaStore.MediaColumns.TITLE + " != ''");
            Uri uri = Audio.Media.EXTERNAL_CONTENT_URI;
            where = new StringBuilder();
            where.append(Audio.AudioColumns.IS_MUSIC + "=1");
            where.append(" AND " + MediaColumns.TITLE + " != ''");
            switch ((int) mPlaylistId) {
                case (int) PLAYLIST_QUEUE:
                    uri = Audio.Media.EXTERNAL_CONTENT_URI;
                    long[] mNowPlaying = MusicUtils.getQueue();
                    if (mNowPlaying.length == 0)
                        return;
                    where = new StringBuilder();
                    where.append(BaseColumns._ID + " IN (");
                    if (mNowPlaying == null || mNowPlaying.length <= 0)
                        return;
                    for (long queue_id : mNowPlaying) {
                        where.append(queue_id + ",");
                    }
                    where.deleteCharAt(where.length() - 1);
                    where.append(")");
                    break;
                case (int) PLAYLIST_FAVORITES:
                    long favorites_id = MusicUtils.getFavoritesId(this);
                    projection = new String[]{
                            Audio.Playlists.Members._ID, Audio.Playlists.Members.AUDIO_ID,
                            MediaColumns.TITLE, Audio.AudioColumns.ALBUM, Audio.AudioColumns.ARTIST
                    };
                    uri = Audio.Playlists.Members.getContentUri(EXTERNAL, favorites_id);
                    sortOrder = Audio.Playlists.Members.DEFAULT_SORT_ORDER;
                    break;
                default:
                    projection = new String[]{
                            Audio.Playlists.Members._ID, Audio.Playlists.Members.AUDIO_ID,
                            MediaColumns.TITLE, Audio.AudioColumns.ALBUM, Audio.AudioColumns.ARTIST,
                            Audio.AudioColumns.DURATION
                    };

                    uri = Audio.Playlists.Members.getContentUri(EXTERNAL, mPlaylistId);
                    sortOrder = Audio.Playlists.Members.DEFAULT_SORT_ORDER;
                    break;
            }

            mCursor = MusicUtils.query(MusicQuickList.this, uri, projection, where.toString(), null, sortOrder);
            musicList.clear();
            int index = 0;
            while (mCursor.moveToNext()) {
                MusicInfo musicInfo = new MusicInfo();
                musicInfo.setId(mCursor.getLong(mMediaIdIndex));
                musicInfo.setArtist(mCursor.getString(mArtistIndex));
                musicInfo.setTitle(mCursor.getString(mTitleIndex));
                musicInfo.setAlbum(mCursor.getString(mAlbumIndex));
                musicInfo.setPosition(index);
                musicList.add(musicInfo);
                index++;
            }

        }
        adapter_music_list.setSongSheetId(mPlaylistId);
        //adapter_music_list.notifyDataSetChanged();
    }

    @Override
    public void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ApolloService.META_CHANGED);
        filter.addAction(ApolloService.QUEUE_CHANGED);
        MusicQuickList.this.registerReceiver(mMediaStatusReceiver, filter);
    }

    @Override
    public void finish() {
        super.finish();
        //设置切换动画，从右边进入，左边退出
        overridePendingTransition(R.anim.orignal, R.anim.orignal);
    }


    public class ScaleTransformer implements ViewPager.PageTransformer {
        private static final float MIN_SCALE = 0.70f;
        private static final float MIN_ALPHA = 0.5f;

        @Override
        public void transformPage(View page, float position) {
            if (position < -1 || position > 1) {
                page.setAlpha(MIN_ALPHA);
                page.setScaleX(MIN_SCALE);
                page.setScaleY(MIN_SCALE);
            } else if (position <= 1) { // [-1,1]
                float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
                if (position < 0) {
                    float scaleX = 1 + 0.4f * position;
                    Log.d("google_lenve_fb", "transformPage: scaleX:" + scaleX);
                    page.setScaleX(scaleX);
                    page.setScaleY(scaleX);
                } else {
                    float scaleX = 1 - 0.4f * position;
                    page.setScaleX(scaleX);
                    page.setScaleY(scaleX);
                }
                page.setAlpha(MIN_ALPHA + (scaleFactor - MIN_SCALE) / (1 - MIN_SCALE) * (1 - MIN_ALPHA));
                //tv_description.setAlpha(0.7f + (scaleFactor - 0.7f) / (1 - 0.7f) * (1 - 0.7f));
            }
        }
    }

}
