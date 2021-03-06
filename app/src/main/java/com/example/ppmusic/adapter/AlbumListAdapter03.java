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
import com.example.ppmusic.bean.AlbumInfo;
import com.example.ppmusic.bean.SingerInfo;

public class AlbumListAdapter03 extends BaseAdapter {

	List<AlbumInfo> mList;
	Context mContext;
	LayoutInflater mInflater;

	public AlbumListAdapter03(Context mContext, List<AlbumInfo> mList) {
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
			convertView = mInflater.inflate(R.layout.item_album_list03, null);

			viewHolder.iv_head = (ImageView) convertView.findViewById(R.id.iv_head);
			viewHolder.tv_count = (TextView) convertView.findViewById(R.id.tv_count);
			viewHolder.tv_artist = (TextView) convertView.findViewById(R.id.tv_artist);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		AlbumInfo eModel = mList.get(position);
		
		//viewHolder.iv_head
		viewHolder.tv_count.setText(eModel.getCount()+"");
		viewHolder.tv_artist.setText("专辑："+eModel.getName());
		
		return convertView;
	}

	class ViewHolder {
		ImageView iv_head;
		TextView tv_artist,tv_count;
	}

}
