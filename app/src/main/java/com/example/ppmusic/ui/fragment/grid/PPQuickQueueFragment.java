/**
 * 
 */

package com.example.ppmusic.ui.fragment.grid;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.BaseColumns;
import android.provider.MediaStore.Audio;
import android.provider.MediaStore.Audio.AudioColumns;
import android.provider.MediaStore.MediaColumns;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.example.ppmusic.NowPlayingCursor;
import com.example.ppmusic.R;
import com.example.ppmusic.activities.MainActivity;
import com.example.ppmusic.adapter.MusicAdapter;
import com.example.ppmusic.adapter.MusicFormAdapter;
import com.example.ppmusic.bean.MusicInfo;
import com.example.ppmusic.helpers.AddIdCursorLoader;
import com.example.ppmusic.helpers.utils.MusicUtils;
import com.example.ppmusic.interfaces.FilterListener;
import com.example.ppmusic.service.ApolloService;
import com.example.ppmusic.ui.adapters.Adapter_PhysicalCharacteristics;
import com.example.ppmusic.ui.adapters.PPQuickQueueAdapter;
import com.example.ppmusic.ui.adapters.QuickQueueAdapter;
import com.example.ppmusic.view.custom.CustomListView;

import java.util.ArrayList;
import java.util.List;

/**
 * 歌单详细
 * @author
 */
public class PPQuickQueueFragment extends Fragment implements LoaderCallbacks<Cursor>,
        OnItemClickListener {

    // Adapter
    private PPQuickQueueAdapter mQuickQueueAdapter;

    // Cursor
    private Cursor mCursor;
    private ImageView iv_cd;
    private ImageButton bottom_select_cd_close;

    // Selected position
    private int mSelectedPosition;

    // Options
    private final int PLAY_SELECTION = 0;

    private final int REMOVE = 1;

    // Audio columns
    public static int mTitleIndex, mAlbumIndex, mArtistIndex, mMediaIdIndex, mAlbumIdIndex;

    // Bundle
    public PPQuickQueueFragment() {
    }
    @SuppressLint("ValidFragment")
   public PPQuickQueueFragment(Bundle args) {
        setArguments(args);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // Adapter
        /*mQuickQueueAdapter = new PPQuickQueueAdapter(getActivity(), R.layout.quick_queue_items_paopao, null,
                new String[] {}, new int[] {}, 0);*/

        // Important!
        getLoaderManager().initLoader(0, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    private ListView lvSongs;
    private MusicFormAdapter adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.quick_queue_paopao, container, false);
        //mGridView = (GridView)root.findViewById(R.id.gridview);
        //mGridView.setNumColumns(1);
        iv_cd = new ImageView(getActivity());

        RelativeLayout mQueueHolder = (RelativeLayout)root.findViewById(R.id.quick_queue_holder);
        mQueueHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        mQueueHolder.addView(iv_cd);
        mQueueHolder.setBackgroundColor(getResources().getColor(R.color.transparent_white_33));

        initSelectCD(root);

        lvSongs = (ListView) root.findViewById(R.id.lvSongs);
        /*adapter = new MusicFormAdapter(musicList,getActivity(),this,new FilterListener() {
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
        });*/
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
        return root;
    }

    private int screenWidth = 0;
    private ViewPager viewPager;
    private MotionEvent eventc;
    private boolean isClicked;
    private RelativeLayout container;
    private Adapter_PhysicalCharacteristics adater;
    private void initSelectCD(View root) {
        // 獲取屏幕寬度
        DisplayMetrics dm = getResources().getDisplayMetrics();
        screenWidth = dm.widthPixels;
        viewPager = (ViewPager) root.findViewById(R.id.viewpager);
        //viewPager.setPageMargin(20);
        viewPager.setOffscreenPageLimit(1);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                screenWidth / 2, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(screenWidth / 5, 0, screenWidth / 5, 0);
       // viewPager.setLayoutParams(params);

        List<String> list = new ArrayList<String>();
        list.add("喜欢");
        list.add("海翻天");
        list.add("安静");
       // adater = new Adapter_PhysicalCharacteristics(getActivity(),list);
        viewPager.setAdapter(adater);

        viewPager.setPageTransformer(false, new ScaleTransformer());
        // 将父节点Layout事件分发给viewpager，否则只能滑动中间的一个view对象
       // container = (RelativeLayout) root.findViewById(R.id.ll_container);
      /*  container.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (eventc == null) {
                            eventc = event;
                            isClicked = true;
                        }
                        return viewPager.dispatchTouchEvent(event);
                    case MotionEvent.ACTION_MOVE:
                        if (Math.abs(event.getX() - eventc.getX()) < 60 && Math.abs(event.getY() - eventc.getY()) < 60) {
                        } else {
                            isClicked = false;
                        }
                        return viewPager.dispatchTouchEvent(event);
                    case MotionEvent.ACTION_UP:
                        eventc = null;
                        if (event.getX() < container.getWidth() / 4) {
                            if (isClicked) {
                                //Toast.makeText(PhysicalCharacteristicsActivity.this, "left", Toast.LENGTH_SHORT).show();
                                if (viewPager.getCurrentItem() != 0) {
                                    viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
                                }
                                return false;
                            } else {
                                return viewPager.dispatchTouchEvent(event);
                            }
                        } else if (event.getX() > container.getWidth() * 3 / 4) {
                            if (isClicked) {
                                //Toast.makeText(PhysicalCharacteristicsActivity.this, "right", Toast.LENGTH_SHORT).show();
                                if (viewPager.getCurrentItem() < viewPager.getChildCount() + 1) {
                                    viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                                }
                                return false;
                            } else {
                                return viewPager.dispatchTouchEvent(event);
                            }
                        } else {
                            //Toast.makeText(PhysicalCharacteristicsActivity.this, "center", Toast.LENGTH_SHORT).show();
                            return viewPager.dispatchTouchEvent(event);
                        }
                }
                return viewPager.dispatchTouchEvent(event);
            }
        });
*/
       /* ImageView iv_left = (ImageView) root.findViewById(R.id.iv_toleft);
        ImageView iv_right = (ImageView) root.findViewById(R.id.iv_toright);
        iv_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (viewPager.getCurrentItem() < viewPager.getChildCount() + 1) {
                    viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                }
            }
        });
        iv_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (viewPager.getCurrentItem() != 0) {
                    viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
                }
            }
        });*/
    }

    int size;
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = new String[] {
                BaseColumns._ID, MediaColumns.TITLE, AudioColumns.ALBUM, AudioColumns.ARTIST,
        };
        StringBuilder selection = new StringBuilder();
        Uri uri = Audio.Media.EXTERNAL_CONTENT_URI;
        String sortOrder = Audio.Media.DEFAULT_SORT_ORDER;
        uri = Audio.Media.EXTERNAL_CONTENT_URI;
        long[] mNowPlaying = MusicUtils.getQueue();
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
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        float density = dm.density;
        int gridviewWidth = (int) (size * (length + 4) * density);
        int itemWidth = (int) (length * density);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(gridviewWidth, LinearLayout.LayoutParams.FILL_PARENT);

        return new AddIdCursorLoader(getActivity(), uri, projection, selection.toString(), null,
                sortOrder);
    }

    private List<MusicInfo> musicList = new ArrayList<>();
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Check for database errors
        if (data == null) {
            return;
        }

        mMediaIdIndex = data.getColumnIndexOrThrow(BaseColumns._ID);
        mTitleIndex = data.getColumnIndexOrThrow(MediaColumns.TITLE);
        mArtistIndex = data.getColumnIndexOrThrow(AudioColumns.ARTIST);
        mAlbumIndex = data.getColumnIndexOrThrow(AudioColumns.ALBUM);
        mAlbumIdIndex = data.getColumnIndexOrThrow(AudioColumns.ALBUM_ID);
        //mQuickQueueAdapter.changeCursor(data);
        mCursor = data;

        int index  = 0;
        while (data.moveToNext()) {
            MusicInfo musicInfo = new MusicInfo();
            musicInfo.setId(data.getLong(mMediaIdIndex));
            musicInfo.setArtist(data.getString(mArtistIndex));
            musicInfo.setTitle(data.getString(mTitleIndex));
            musicInfo.setAlbum(data.getString(mAlbumIndex));
            musicInfo.setPosition(index);
            musicList.add(musicInfo);
            index++;
        }

        adapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (mQuickQueueAdapter != null)
            mQuickQueueAdapter.changeCursor(null);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putAll(getArguments() != null ? getArguments() : new Bundle());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        menu.add(0, PLAY_SELECTION, 0, getResources().getString(R.string.play_all));
        menu.add(0, REMOVE, 0, getResources().getString(R.string.remove));

        AdapterContextMenuInfo mi = (AdapterContextMenuInfo)menuInfo;
        mSelectedPosition = mi.position;
        mCursor.moveToPosition(mSelectedPosition);

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
                getActivity().finish();
                break;
            case REMOVE:
                removePlaylistItem(mSelectedPosition);
                break;
            default:
                break;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent,final View v,final int position, long id) {
        final ImageView iv = (ImageView)v.findViewById(R.id.queue_artist_image);
        iv.setEnabled(false);
        iv_cd.setVisibility(View.VISIBLE);
        int[] location = new  int[2];
        // iv.getLocationInWindow(location); //获取在当前窗口内的绝对坐标
        iv.getLocationOnScreen(location);//获取在整个屏幕内的绝对坐标
        iv_cd.setLayoutParams(new RelativeLayout.LayoutParams(iv.getWidth(),iv.getHeight()));
       //p[
        iv_cd.setImageDrawable(((ImageView)iv).getDrawable());
        iv_cd.setX(location [0]);
        iv_cd.setY(location [1]);

        PointF point = MainActivity.getInstance().mainFragment2.getCDBoxPosition();
        // 属性动画移动
        ObjectAnimator y = ObjectAnimator.ofFloat(iv_cd, "y", iv_cd.getY(), point.y-(iv.getHeight()/2));
        ObjectAnimator x = ObjectAnimator.ofFloat(iv_cd, "x", iv_cd.getX(), point.x-(iv.getWidth()/2));

        AnimatorSet animatorSet = new AnimatorSet();

        /**
         * 按需添加
         */
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // TODO Auto-generated method stub
                super.onAnimationEnd(animation);
                if(iv_cd!=null){
                    iv_cd.setVisibility(View.GONE);
                }
                ((ImageView)iv).setEnabled(true);

                if (mCursor instanceof NowPlayingCursor) {
                    if (MusicUtils.mService != null) {
                        MusicUtils.setQueuePosition(position);
                    }
                }
                MusicUtils.playAll(getActivity(), mCursor, position);
                getActivity().finish();
            }
        });
        animatorSet.playTogether(x, y);
        animatorSet.setDuration(200);
        //animatorSet.start();
        iv_cd.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        moveCDICON(event, v);
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        moveCDICON(event, v);
                        break;
                    case MotionEvent.ACTION_UP:
                        moveCDICON(event, v);
                        break;
                }
                return true;
            }
        });

    }


    private int containerWidth;
    private int containerHeight;
    float lastX, lastY;

    private void moveCDICON(MotionEvent event,View v){
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                //iv_parent.removeView(iv);
                if(iv_cd.getParent()!=null){
                    MainActivity.getInstance().rl_root.removeView(iv_cd);
                }
                MainActivity.getInstance().rl_root.addView(iv_cd);
                iv_cd.setVisibility(View.VISIBLE);
                                /*iv.setImageDrawable(iv.getDrawable());
								//iv_cd_tep.setLayoutParams(iv.getLayoutParams());
								iv.setLayoutParams(new RelativeLayout.LayoutParams(iv.getWidth(),iv.getHeight()));*/

                int[] location = new  int[2];
                // iv.getLocationInWindow(location); //获取在当前窗口内的绝对坐标
                iv_cd.getLocationOnScreen(location);//获取在整个屏幕内的绝对坐标
                //location [0]--->x坐标,location [1]--->y坐标
                iv_cd.setX(location [0]);
                iv_cd.setY(location [1]);

                //MyLog.i("scrollx:"+((View)((View)iv.getParent()).getParent()).getScrollX()+"scrolly:"+((View)iv.getParent()).getScrollY()+";x="+((View)iv.getParent()).getX()+";y="+((View)iv.getParent()).getY()+",Left:"+((View)iv.getParent()).getLeft()+",Left:"+((View)iv.getParent()).getRight());
                lastX = event.getRawX();
                lastY = event.getRawY();
            case MotionEvent.ACTION_MOVE:
                //  不要直接用getX和getY,这两个获取的数据已经是经过处理的,容易出现图片抖动的情况
                float distanceX = lastX - event.getRawX();
                float distanceY = lastY - event.getRawY();

                float nextY = iv_cd.getY() - distanceY;
                float nextX = iv_cd.getX() - distanceX;

                // 不能移出屏幕
                if (nextY < 0) {
                    nextY = 0;
                } else if (nextY > containerHeight - iv_cd.getHeight()) {
                    nextY = containerHeight - iv_cd.getHeight();
                }
                if (nextX < 0)
                    nextX = 0;
                else if (nextX > containerWidth - iv_cd.getWidth())
                    nextX = containerWidth - iv_cd.getWidth();

                // 属性动画移动
                ObjectAnimator y = ObjectAnimator.ofFloat(iv_cd, "y", iv_cd.getY(), nextY);
                ObjectAnimator x = ObjectAnimator.ofFloat(iv_cd, "x", iv_cd.getX(), nextX);

                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(x, y);
                animatorSet.setDuration(0);
                animatorSet.start();

                lastX = event.getRawX();
                lastY = event.getRawY();
            case MotionEvent.ACTION_UP:
                break;
        }
    }

    /**
     * @param which
     */
    public void removePlaylistItem(int which) {
        mCursor.moveToPosition(which);
        long id = mCursor.getLong(mMediaIdIndex);
        MusicUtils.removeTrack(id);
        reloadQueueCursor();
        //mGridView.invalidateViews();
    }

    /**
     * Reload the queue after we remove a track
     */
    private void reloadQueueCursor() {
        String[] projection = new String[] {
                BaseColumns._ID, MediaColumns.TITLE, AudioColumns.ALBUM, AudioColumns.ARTIST,
        };
        StringBuilder selection = new StringBuilder();
        Uri uri = Audio.Media.EXTERNAL_CONTENT_URI;
        String sortOrder = Audio.Media.DEFAULT_SORT_ORDER;
        uri = Audio.Media.EXTERNAL_CONTENT_URI;
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

        mCursor = MusicUtils.query(getActivity(), uri, projection, selection.toString(), null,
                sortOrder);
       // mQuickQueueAdapter.changeCursor(mCursor);
        int index  = 0;
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

        adapter.notifyDataSetChanged();
    }

    /**
     * Update the list as needed
     */
    private final BroadcastReceiver mMediaStatusReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (adapter != null) {
                adapter.notifyDataSetChanged();
                // Scroll to the currently playing track in the queue
               /* mGridView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mGridView.setSelection(MusicUtils.getQueuePosition());
                    }
                }, 100);*/
            }
        }

    };

    @Override
    public void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ApolloService.META_CHANGED);
        filter.addAction(ApolloService.QUEUE_CHANGED);
        getActivity().registerReceiver(mMediaStatusReceiver, filter);
    }

    @Override
    public void onStop() {
        getActivity().unregisterReceiver(mMediaStatusReceiver);
        super.onStop();
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
