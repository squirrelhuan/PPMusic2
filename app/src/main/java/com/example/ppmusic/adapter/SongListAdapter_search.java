package com.example.ppmusic.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ppmusic.R;
import com.example.ppmusic.bean.SingerInfo;
import com.example.ppmusic.bean.TTdongting.Data;

public class SongListAdapter_search extends BaseAdapter {

	List<Data> mList;
	Context mContext;
	LayoutInflater mInflater;

	public SongListAdapter_search(Context mContext, List<Data> mList) {
		this.mContext = mContext;
		this.mInflater = LayoutInflater.from(mContext);
		this.mList = mList;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.item_song_list_search, null);

			viewHolder.iv_head = (ImageView) convertView.findViewById(R.id.iv_head);
			viewHolder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
			viewHolder.tv_album = (TextView) convertView.findViewById(R.id.tv_album);
			viewHolder.tv_artist = (TextView) convertView.findViewById(R.id.tv_artist);
			viewHolder.tv_duration = (TextView) convertView.findViewById(R.id.tv_duration);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		Data eModel = mList.get(position);
		
		viewHolder.tv_title.setText(eModel.getSong_name());
		viewHolder.tv_album.setText(eModel.getAlbum_name());
		viewHolder.tv_artist.setText(eModel.getSinger_name());
		viewHolder.tv_duration.setText(eModel.getAudition_list().get(0).getDuration());
		
		return convertView;
	}

	class ViewHolder {
		ImageView iv_head;
		TextView tv_title,tv_artist,tv_album,tv_duration;
	}

}
