package com.example.ppmusic.ui.adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ppmusic.R;
import com.example.ppmusic.bean.MusicCollection;
import com.example.ppmusic.helpers.utils.MusicUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huan on 2017/9/20.
 */

public class Adapter_PhysicalCharacteristics extends PagerAdapter {

    private Context mContext;

    private List<MusicCollection> qiGuanModels = new ArrayList<MusicCollection>();

    public Adapter_PhysicalCharacteristics(Context mContext, List<MusicCollection> list) {
        this.mContext = mContext;
        this.qiGuanModels = list;
    }

    @Override
    public int getCount() {
        return qiGuanModels.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {
        ViewHolder viewHolder = null;
        View view = null;
        viewHolder = new ViewHolder();
        view = LayoutInflater.from(mContext).inflate(R.layout.item_cd_list_01, null);
        view.setTag(viewHolder);

        viewHolder.iv_icon = view.findViewById(R.id.iv_icon);
        viewHolder.tv_name = view.findViewById(R.id.tv_name);
        viewHolder.tv_play_all = view.findViewById(R.id.tv_play_all);
        viewHolder.tv_play_sort = view.findViewById(R.id.tv_play_sort);
        if (qiGuanModels != null && qiGuanModels.get(position) != null) {
            //viewHolder.iv_icon.setImageResource(qiGuanModels.get(position).getResId());
            viewHolder.tv_name.setText(qiGuanModels.get(position).getName());
            if (qiGuanModels.get(position).getId() == -1) {
                viewHolder.tv_play_all.setVisibility(View.GONE);
                viewHolder.tv_play_sort.setVisibility(View.VISIBLE);
            } else {
                viewHolder.tv_play_sort.setVisibility(View.GONE);
                viewHolder.tv_play_all.setVisibility(View.VISIBLE);
                viewHolder.tv_play_all.setTag(qiGuanModels.get(position).getId());
                viewHolder.tv_play_all.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        long id = (long) view.getTag();
                        long[] list = MusicUtils.getSongListForPlaylist(mContext,
                                id);
                        MusicUtils.playAll(mContext, list, 0);
                    }
                });
            }
        }
        // viewHolder.tv_valuestr.setTextColor(mContext.getResources().getColor((!qiGuanModels.get(position).getValueStr().contains("-")) ? R.color.jps_green_01 : R.color.jps_red_01));
        //AnimationUtil.addScaleAnimition(view,null);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    class ViewHolder {
        ImageView iv_icon;
        TextView tv_name, tv_play_all, tv_play_sort;
    }
}
