package com.example.ppmusic.adapter.sortlistview;
import java.util.List;

import com.example.ppmusic.FormatHelper;
import com.example.ppmusic.R;
import com.example.ppmusic.bean.MusicInfo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

public class SortGroupMemberAdapter extends BaseAdapter implements SectionIndexer {
	private List<GroupMemberBean> list = null;
	private List<MusicInfo> musicList = null;
	private Context mContext;

	public SortGroupMemberAdapter(Context mContext, List<GroupMemberBean> list) {
		this.mContext = mContext;
		this.list = list;
		this.musicList = musicList;
	}

	/**
	 * 当ListView数据发生变化时,调用此方法来更新ListView
	 * 
	 * @param list
	 */
	public void updateListView(List<GroupMemberBean> list) {
		this.list = list;
		notifyDataSetChanged();
	}

	public int getCount() {
		return this.list.size();
	}

	public Object getItem(int position) {
		return list.get(position);
	}
	
	public MusicInfo getItemAudio(int position) {
		return musicList.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View view, ViewGroup arg2) {
		ViewHolder viewHolder = null;
		final GroupMemberBean mContent = list.get(position);
		if (view == null) {
			viewHolder = new ViewHolder();
			/*view = LayoutInflater.from(mContext).inflate(R.layout.activity_group_member_item, null);*/
			view = LayoutInflater.from(mContext).inflate(R.layout.music_item, null);
			viewHolder.imageView = (ImageView) view.findViewById(R.id.albumPhoto);
			viewHolder.tvTitle = (TextView) view.findViewById(R.id.title);
			viewHolder.artist = (TextView) view.findViewById(R.id.artist);
			//viewHolder.duration = (TextView) view.findViewById(R.id.duration);
			viewHolder.tvLetter = (TextView) view.findViewById(R.id.catalog);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}

		// 根据position获取分类的首字母的Char ascii值
		int section = getSectionForPosition(position);

		// 如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
		if (position == getPositionForSection(section)) {
			viewHolder.tvLetter.setVisibility(View.VISIBLE);
			viewHolder.tvLetter.setText(mContent.getSortLetters());
		} else {
			viewHolder.tvLetter.setVisibility(View.GONE);
		}

		viewHolder.imageView.setImageResource(R.drawable.audio);
		viewHolder.tvTitle.setText(this.list.get(position).getName());
		viewHolder.artist.setText(this.list.get(position).getMusicInfo().getArtist());
		viewHolder.duration.setText(FormatHelper.formatDuration(this.list.get(position).getMusicInfo().getDuration()));

		return view;

	}

	final static class ViewHolder {
		ImageView imageView;
		
		TextView tvLetter;
		TextView tvTitle;
		TextView title;
		TextView duration;
		TextView artist;
	}

	/**
	 * 根据ListView的当前位置获取分类的首字母的Char ascii值
	 */
	public int getSectionForPosition(int position) {
		return list.get(position).getSortLetters().charAt(0);
	}

	/**
	 * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
	 */
	public int getPositionForSection(int section) {
		for (int i = 0; i < getCount(); i++) {
			String sortStr = list.get(i).getSortLetters();
			char firstChar = sortStr.toUpperCase().charAt(0);
			if (firstChar == section) {
				return i;
			}
		}

		return -1;
	}

	/**
	 * 提取英文的首字母，非英文字母用#代替。
	 * 
	 * @param str
	 * @return
	 */
	private String getAlpha(String str) {
		String sortStr = str.trim().substring(0, 1).toUpperCase();
		// 正则表达式，判断首字母是否是英文字母
		if (sortStr.matches("[A-Z]")) {
			return sortStr;
		} else {
			return "#";
		}
	}

	@Override
	public Object[] getSections() {
		return null;
	}
}