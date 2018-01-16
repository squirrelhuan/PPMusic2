package com.example.ppmusic.adapter;

import java.util.ArrayList;
import java.util.List;

import com.example.ppmusic.FormatHelper;
import com.example.ppmusic.R;
import com.example.ppmusic.bean.MusicInfo;
import com.example.ppmusic.interfaces.FilterListener;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

public class MusicAdapter extends BaseAdapter implements Filterable {

	private List<MusicInfo> musicList;
	private Context mContext;
	private MyFilter filter = null;// 创建MyFilter对象
	private FilterListener listener = null;// 接口对象
	
	public MusicAdapter(List<MusicInfo> musicList,Context mContext, FilterListener filterListener){
		this.mContext = mContext;
		this.musicList = musicList;
		this.listener = filterListener;
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

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.music_item, null);
			ImageView pImageView = (ImageView) convertView
					.findViewById(R.id.albumPhoto);
			TextView pTitle = (TextView) convertView
					.findViewById(R.id.title);
			TextView pDuration = (TextView) convertView
					.findViewById(R.id.duration);
			TextView pArtist = (TextView) convertView
					.findViewById(R.id.artist);
			viewHolder = new ViewHolder(pImageView, pTitle, pDuration,
					pArtist);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.imageView.setImageResource(R.drawable.audio);
		viewHolder.title.setText(musicList.get(position).getTitle());
		viewHolder.duration.setText(FormatHelper.formatDuration(musicList
				.get(position).getDuration()));
		viewHolder.artist.setText(musicList.get(position).getArtist());

		return convertView;
	}
	
	class ViewHolder {
		public ViewHolder(ImageView pImageView, TextView pTitle,
				TextView pDuration, TextView pArtist) {
			imageView = pImageView;
			title = pTitle;
			duration = pDuration;
			artist = pArtist;
		}

		ImageView imageView;
		TextView title;
		TextView duration;
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
