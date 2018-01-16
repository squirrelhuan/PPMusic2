package com.example.ppmusic.ui.adapters;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.AnimationDrawable;
import android.os.RemoteException;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.example.ppmusic.MyApp;
import com.example.ppmusic.R;
import com.example.ppmusic.cache.ImageInfo;
import com.example.ppmusic.fragment.Fragment_SongList01;
import com.example.ppmusic.helpers.utils.MusicUtils;
import com.example.ppmusic.ui.fragment.list.CopyOfRecentlyAddedFragment;
import com.example.ppmusic.views.ViewHolderList_v4;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import static com.example.ppmusic.constants.Constants.SIZE_THUMB;
import static com.example.ppmusic.constants.Constants.SRC_FIRST_AVAILABLE;
import static com.example.ppmusic.constants.Constants.TYPE_ALBUM;


/**
 * @author Andrew Neal
 */
public class RecentlyAddedAdapter extends SimpleCursorAdapter {

	private AnimationDrawable mPeakOneAnimation, mPeakTwoAnimation,
			mPeakThreeAnimation;

	private WeakReference<ViewHolderList_v4> holderReference;

	private Context mContext;

	//private ImageProvider mImageProvider;
	ListView listView;

	public RecentlyAddedAdapter(Context context, int layout, Cursor c,
			String[] from, int[] to, int flags, ListView lv_songs) {
		super(context, layout, c, from, to, flags);
		mContext = context;
		this.listView = lv_songs;
		//mImageProvider = ImageProvider.getInstance((Activity) mContext);

		listView.setOnScrollListener(new OnScrollListener() {

			public void onScrollStateChanged(AbsListView arg0, int arg1) {
				closeAll();
			}

			public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {

			}
		});
	}

	private List<CheckBox> clist = new ArrayList<CheckBox>();

	@SuppressWarnings("ResourceType")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final View view = super.getView(position, convertView, parent);

		Cursor mCursor = (Cursor) getItem(position);
		// ViewHolderList
		ViewHolderList_v4 viewholder;

		if (view != null) {
			viewholder = new ViewHolderList_v4(view, this);
			holderReference = new WeakReference<ViewHolderList_v4>(viewholder);
			view.setTag(holderReference.get());

		} else {
			viewholder = (ViewHolderList_v4) convertView.getTag();
		}

		boolean isExit = false;
		for (int i = 0; i < clist.size(); i++) {
			if (clist.get(i).equals(viewholder.mMenu)) {
				isExit = true;
				break;
			}
		}
		if (!isExit) {
			clist.add(viewholder.mMenu);
		}

		// Track name
		String trackName = mCursor.getString(Fragment_SongList01.mTitleIndex);
		holderReference.get().mViewHolderLineOne.setText(trackName);

		// Artist name
		String artistName = mCursor.getString(Fragment_SongList01.mArtistIndex);
		holderReference.get().mViewHolderLineTwo.setText(artistName);

		// Album name
		String albumName = mCursor.getString(Fragment_SongList01.mAlbumIndex);

		// Album ID
		String albumId = mCursor.getString(Fragment_SongList01.mAlbumIdIndex);

		ImageInfo mInfo = new ImageInfo();
		mInfo.type = TYPE_ALBUM;
		mInfo.size = SIZE_THUMB;
		mInfo.source = SRC_FIRST_AVAILABLE;
		mInfo.data = new String[] { albumId, artistName, albumName };

		// mImageProvider.loadImage(viewholder.mViewHolderImage, mInfo);
		//searchMusic(trackName, viewholder.mViewHolderImage);
		// holderReference.get().mQuickContext.setVisibility(View.GONE);

		// Now playing indicator
		long currentaudioid = MusicUtils.getCurrentAudioId();
		long audioid = mCursor
				.getLong(CopyOfRecentlyAddedFragment.mMediaIdIndex);
		if (currentaudioid == audioid) {

			holderReference.get().mPeakOne
					.setImageResource(R.anim.peak_meter_1);
			holderReference.get().mPeakTwo
					.setImageResource(R.anim.peak_meter_2);
			holderReference.get().mPeakThree
					.setImageResource(R.anim.peak_meter_3);
			mPeakOneAnimation = (AnimationDrawable) holderReference.get().mPeakOne
					.getDrawable();
			mPeakTwoAnimation = (AnimationDrawable) holderReference.get().mPeakTwo
					.getDrawable();
			mPeakThreeAnimation = (AnimationDrawable) holderReference.get().mPeakThree
					.getDrawable();
			try {
				if (MusicUtils.mService.isPlaying()) {
					mPeakOneAnimation.start();
					mPeakTwoAnimation.start();
					mPeakThreeAnimation.start();
				} else {
					mPeakOneAnimation.stop();
					mPeakTwoAnimation.stop();
					mPeakThreeAnimation.stop();
				}
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		} else {
			holderReference.get().mPeakOne.setImageResource(0);
			holderReference.get().mPeakTwo.setImageResource(0);
			holderReference.get().mPeakThree.setImageResource(0);
		}
		return view;
	}

	public void closeOthers(CheckBox cbox) {
		//Toast.makeText(MyApp.getContext(), "closeOthers", Toast.LENGTH_SHORT)
		//		.show();
		for (int i = 0; i < clist.size(); i++) {
			if (!cbox.equals(clist.get(i))) {
				clist.get(i).setChecked(false);
			}
		}
	}

	public void closeAll() {
		for (int i = 0; i < clist.size(); i++) {
			clist.get(i).setChecked(false);
		}
	}

	public LinearLayout ll_current = null;
	public boolean isRunning = false;
	public boolean isOpen = false;

	/*****************************************************************************************************************/

	/*public void searchMusic(String nameORsinger, final ImageView imView) {

		RequestParams params = new RequestParams();
		params.put("all", nameORsinger);
		params.put("ft", "music");
		params.put("itemset", "web_2013");
		params.put("client", "kt");
		params.put("pn", "1");
		params.put("rn", "1");
		params.put("rformat", "json");
		params.put("encoding", "utf8");
		// ?q=周杰伦&page=1&size=3
		// showToast("参数:"+params);
		MyApp.getNet().getReq(NetConstants.SERVER_KUWO_MUSIC_URL, params,
				new AsyncHttpResponseHandler() {
					@Override
					public void onFailure(int arg0, Header[] arg1, byte[] arg2,
							Throwable arg3) {
						if (arg2 != null) {
							// showToast("失败"*//*:" + data*//*);
						}
					}

					@Override
					public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
						if (arg2 != null) {
							Log.d("CGQ", new String(arg2));
							Result_KuWo root = JSON.parseObject(arg2,
									Result_KuWo.class);
							if (root != null && root.getAbsList() != null
									&& root.getAbsList().size() > 0) {
								int musicNumber = Integer.valueOf(root
										.getAbsList().get(0).getMUSICRID()
										.replace("MUSIC_", ""));
								searchMusicImage(musicNumber, imView);
							}
							*//*
							 * musicList.addAll(root.getData());
							 * sadapter.notifyDataSetChanged();
							 *//*
							// showToast("成功"+root.getRows()+"条");
						}
						// showToast("成功...");
					}
				});
	}*/

	String img_url = "";

	/*public void searchMusicImage(int rid, final ImageView imView) {
		// &rid=[可为空]from=pc&json=[json数据排放方式,一般为1]&version=1&width=[写真宽度]&height=[写真高度]
		// ?type=rid_pic&pictype=url&size=[图片大小,一般为70]&rid=[歌曲id]
		RequestParams params = new RequestParams();
		params.put("type", "rid_pic");
		params.put("pictype", "url");
		params.put("size", 70);
		params.put("rid", rid);
		// ?q=周杰伦&page=1&size=3
		// showToast("参数:"+params);
		MyApp.getNet().getReq(NetConstants.SERVER_KUWO_IMAGE_HEAD_URL, params,
				new AsyncHttpResponseHandler() {
					@Override
					public void onFailure(int arg0, Header[] arg1, byte[] arg2,
							Throwable arg3) {
						if (arg2 != null) {
							// String data = new String(arg2);
							// showToast("失败"*//*:" + data*//*);
						}
						// showToast("失败..." +"state:"+arg0);
					}

					@Override
					public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
						if (arg2 != null) {
							Log.d("CGQ", new String(arg2));
							img_url = new String(arg2);
							MyApp.imageLoader.displayImage(img_url, imView);
							// Toast.makeText(getActivity(), new String(arg2),
							// Toast.LENGTH_SHORT).show();
							// Result_KuWo root = JSON.parseObject(arg2,
							// Result_KuWo.class);
							// Toast.makeText(getActivity(), +"",
							// Toast.LENGTH_SHORT).show();
							// showToast("成功"+root.getAbsList().get(1).getMUSICRID()+"条");
							*//*
							 * musicList.addAll(root.getData());
							 * sadapter.notifyDataSetChanged();
							 *//*
							// showToast("成功"+root.getRows()+"条");
						}
						// showToast("成功...");
					}
				});
	}*/
}
