package com.example.ppmusic.adapter;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.RemoteException;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ppmusic.FormatHelper;
import com.example.ppmusic.R;
import com.example.ppmusic.activities.MusicQuickList;
import com.example.ppmusic.bean.MusicInfo;
import com.example.ppmusic.helpers.utils.MusicUtils;
import com.example.ppmusic.interfaces.FilterListener;
import com.example.ppmusic.ui.fragment.grid.PPQuickQueueFragment;

import java.util.ArrayList;
import java.util.List;

public class MusicFormAdapter extends BaseAdapter implements Filterable {
	// 帧动画
	private AnimationDrawable mPeakOneAnimation, mPeakTwoAnimation,
			mPeakThreeAnimation;
	private List<MusicInfo> musicList;
	private Context mContext;
	private MyFilter filter = null;// 创建MyFilter对象
	private FilterListener listener = null;// 接口对象
	private MusicQuickList mPPQuickQueueFragment;
	private int songSheetId = MusicQuickList.SongSheet_Current_Tag;

	public int getSongSheetId() {
		return songSheetId;
	}

	public void setSongSheetId(int songSheetId) {
		this.songSheetId = songSheetId;
		this.notifyDataSetChanged();
	}

	public MusicFormAdapter(List<MusicInfo> musicList, Context mContext, MusicQuickList ppQuickQueueFragment, FilterListener filterListener){
		this.mContext = mContext;
		this.musicList = musicList;
		this.listener = filterListener;
		this.mPPQuickQueueFragment = ppQuickQueueFragment;
	}
	
	@Override
	public int getCount() {
		return musicList.size();
	}

	@Override
	public Object getItem(int position) {
		return musicList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return musicList.get(position).getId();
	}
	@SuppressWarnings("ResourceType")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.music_item_small_01, null);
			ImageView pImageView = (ImageView) convertView
					.findViewById(R.id.albumPhoto);
			TextView pTitle = (TextView) convertView
					.findViewById(R.id.title);
			ImageView pRemove = (ImageView) convertView
					.findViewById(R.id.iv_remove_from_list);
			TextView pArtist = (TextView) convertView
					.findViewById(R.id.artist);

			ImageView mPeakOne = convertView.findViewById(R.id.peak_one);
			ImageView mPeakTwo = convertView.findViewById(R.id.peak_two);
			ImageView mPeakThree = convertView.findViewById(R.id.peak_three);
			viewHolder = new ViewHolder(pImageView, pTitle, pRemove,
					pArtist,mPeakOne,mPeakTwo,mPeakThree);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.imageView.setImageResource(R.drawable.audio);
		viewHolder.title.setText(musicList.get(position).getTitle());
		viewHolder.mRemove.setTag(musicList.get(position).getId());
		viewHolder.mRemove.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				mPPQuickQueueFragment.removePlaylistItem(getSongSheetId(),(long) view.getTag());
			}
		});
		viewHolder.artist.setText(musicList.get(position).getArtist());

		long currentaudioid = MusicUtils.getCurrentAudioId();
		long audioid = musicList.get(position).getId();
		if (currentaudioid == audioid) {

			viewHolder.mPeakOne
					.setImageResource(R.anim.peak_meter_1);
			viewHolder.mPeakTwo
					.setImageResource(R.anim.peak_meter_2);
			viewHolder.mPeakThree
					.setImageResource(R.anim.peak_meter_3);
			mPeakOneAnimation = (AnimationDrawable) viewHolder.mPeakOne
					.getDrawable();
			mPeakTwoAnimation = (AnimationDrawable) viewHolder.mPeakTwo
					.getDrawable();
			mPeakThreeAnimation = (AnimationDrawable) viewHolder.mPeakThree
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
			viewHolder.mPeakOne.setImageResource(0);
			viewHolder.mPeakTwo.setImageResource(0);
			viewHolder.mPeakThree.setImageResource(0);
		}

		return convertView;
	}
	
	class ViewHolder {
		public ViewHolder(ImageView pImageView, TextView pTitle,
				ImageView piv_remove_from_list, TextView pArtist,ImageView PeakOne,ImageView PeakTwo,ImageView PeakThree) {
			imageView = pImageView;
			title = pTitle;
			mRemove = piv_remove_from_list;
			artist = pArtist;
			mPeakOne = PeakOne;
			mPeakTwo = PeakTwo;
			mPeakThree = PeakThree;
		}

		ImageView imageView,mPeakOne,mPeakTwo,mPeakThree,mRemove;
		TextView title;
		TextView artist;
	}


	@Override
	public Filter getFilter() {
		// 如果MyFilter对象为空，那么重写创建一个
		if (filter == null) {
			filter = new MyFilter(musicList);
		}
		return filter;
	}


	/**
	 * 创建内部类MyFilter继承Filter类，并重写相关方法，实现数据的过滤
	 * @author 邹奇
	 *
	 */
	class MyFilter extends Filter {

		// 创建集合保存原始数据
		private List<MusicInfo> original = new ArrayList<MusicInfo>();

		public MyFilter(List<MusicInfo> list) {
			this.original = list;
		}

		/**
		 * 该方法返回搜索过滤后的数据
		 */
		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			// 创建FilterResults对象
			FilterResults results = new FilterResults();

			/**
			 * 没有搜索内容的话就还是给results赋值原始数据的值和大小
			 * 执行了搜索的话，根据搜索的规则过滤即可，最后把过滤后的数据的值和大小赋值给results
			 *
			 */
			if(TextUtils.isEmpty(constraint)){
				results.values = original;
				results.count = original.size();
			}else {
				// 创建集合保存过滤后的数据
				List<MusicInfo> mList = new ArrayList<MusicInfo>();
				// 遍历原始数据集合，根据搜索的规则过滤数据
				for(MusicInfo s: original){
					// 这里就是过滤规则的具体实现【规则有很多，大家可以自己决定怎么实现】
					if(s.getTitle().trim().toLowerCase().contains(constraint.toString().trim().toLowerCase())){
						// 规则匹配的话就往集合中添加该数据
						mList.add(s);
					}
				}
				results.values = mList;
				results.count = mList.size();
			}

			// 返回FilterResults对象
			return results;
		}

		/**
		 * 该方法用来刷新用户界面，根据过滤后的数据重新展示列表
		 */
		@Override
		protected void publishResults(CharSequence constraint,
									  FilterResults results) {
			// 获取过滤后的数据
			musicList = (List<MusicInfo>) results.values;
			// 如果接口对象不为空，那么调用接口中的方法获取过滤后的数据，具体的实现在new这个接口的时候重写的方法里执行
			if(listener != null){
				listener.getFilterData(musicList);
			}
			// 刷新数据源显示
			notifyDataSetChanged();
		}
	}

}
