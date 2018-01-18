package com.example.ppmusic.activities;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ppmusic.FormatHelper;
import com.example.ppmusic.MyApp;
import com.example.ppmusic.R;
import com.example.ppmusic.adapter.EditView_withDeleteButton;
import com.example.ppmusic.adapter.SongListAdapter_search;
import com.example.ppmusic.base.BaseActivity;
import com.example.ppmusic.bean.MusicInfo;
import com.example.ppmusic.bean.TTdongting.Data;
import com.example.ppmusic.utils.DBUtils;
import com.example.ppmusic.view.custom.JellyBall;
import com.example.ppmusic.view.custom.PullScrollView;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends BaseActivity implements OnClickListener,
		PullScrollView.OnTurnListener {

	private PullScrollView mScrollView;
	private JellyBall jellyBall;
	private ImageView mHeadImg;
	private TableLayout mMainLayout;

	ListView lv_songs;
	EditView_withDeleteButton et_search;
	Button btn_search;
	String search_str = "周杰伦";
	private SongListAdapter_search sadapter;
	List<Data> musicList = new ArrayList<Data>();

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_music_search);

		initView();
		init();
	}

	protected void initView() {
		mScrollView = (PullScrollView) findViewById(R.id.scroll_view);
		mHeadImg = (ImageView) findViewById(R.id.background_img);

		mMainLayout = (TableLayout) findViewById(R.id.table_layout);
		jellyBall = (JellyBall) findViewById(R.id.cirle);
		mScrollView.setHeader(mHeadImg);
		mScrollView.setOnTurnListener(this);
	}
	 public void showTable() {
	        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(
	                TableRow.LayoutParams.MATCH_PARENT,
	                TableRow.LayoutParams.WRAP_CONTENT);
	        layoutParams.gravity = Gravity.CENTER;
	        layoutParams.leftMargin = 30;
	        layoutParams.bottomMargin = 10;
	        layoutParams.topMargin = 10;

	        for (int i = 0; i < 30; i++) {
	            TableRow tableRow = new TableRow(this);
	            TextView textView = new TextView(this);
	            textView.setText("Test pull down scroll view " + i);
	            textView.setTextSize(20);
	            textView.setPadding(15, 15, 15, 15);

	            tableRow.addView(textView, layoutParams);
	            if (i % 2 != 0) {
	                tableRow.setBackgroundColor(Color.LTGRAY);
	            } else {
	                tableRow.setBackgroundColor(Color.WHITE);
	            }

	            final int n = i;
	            tableRow.setOnClickListener(new View.OnClickListener() {
	                @Override
	                public void onClick(View v) {
	                    Toast.makeText(SearchActivity.this, "Click item " + n, Toast.LENGTH_SHORT).show();
	                }
	            });

	            mMainLayout.addView(tableRow);
	        }
	    }
	@Override
	public String getTitleText() {
		return "个人中心";
	}

	@Override
	public int getBackgroundColor() {
		return getResources().getColor(R.color.transparent);
	}

	private void init() {

		et_search = (EditView_withDeleteButton) findViewById(R.id.met_search);
		btn_search = (Button) findViewById(R.id.btn_search);
		btn_search.setOnClickListener(this);

		ListView lv_songs = (ListView) findViewById(R.id.lv_songs);//
		lv_songs.setVisibility(View.VISIBLE);
		sadapter = new SongListAdapter_search(this, musicList);
		lv_songs.setAdapter(sadapter);
		lv_songs.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				MusicInfo mInfo = new MusicInfo();
				mInfo.setAlbum(musicList.get(position).getAlbum_name());
				mInfo.setArtist(musicList.get(position).getSinger_name());
				mInfo.setDuration(FormatHelper.formatDuration(musicList
						.get(position).getAudition_list().get(0).getDuration()));
				mInfo.setTitle(musicList.get(position).getSong_name());
				mInfo.setUrl(musicList.get(position).getAudition_list().get(0)
						.getUrl());
				DBUtils.insertDB(SearchActivity.this, mInfo);
				MyApp.getNatureBinder().getCurrentMusicList().add(mInfo);
				MyApp.getNatureBinder().startPlay(MyApp.getNatureBinder()
						.getCurrentMusicList().size() - 1, 0);
			}
		});

		// if(NetUtils.isNetworkConnected(this)){
		//searchMusic();
		// }
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

	}

	/*public void searchMusic() {
		musicList.clear();
		sadapter.notifyDataSetChanged();

		if (et_search.getText() != null
				&& !et_search.getText().toString().isEmpty()) {
			search_str = et_search.getText().toString();
		}
		RequestParams params = new RequestParams();
		params.put("q", search_str);
		params.put("page", "1");
		params.put("size", "20");
		// ?q=周杰伦&page=1&size=3
		// showToast("参数:"+params);
		MyApp.getNet().getReq(NetConstants.SERVER_TianTian_URL, params,
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
							Root root = JSON.parseObject(arg2, Root.class);
							musicList.addAll(root.getData());
							sadapter.notifyDataSetChanged();
							// showToast("成功"+root.getRows()+"条");
						}
						// showToast("成功...");
					}
				});
	}*/

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_search:
			//searchMusic();
			break;

		default:
			break;
		}
	}

	// 阻尼回弹
	/**
	 * 下拉
	 *
	 * @param y
	 */
	@Override
	public void onPull(float y) {
		jellyBall.setPullHeight(y);
	}

	/**
	 * 松手
	 *
	 * @param y
	 */
	@Override
	public void onUp(float y) {
		jellyBall.setUpHeight(y);
	}

	@Override
	public void onRefresh() {
		/*
		 * new Handler().postDelayed(new Runnable() {
		 * 
		 * @Override public void run() { //小球回弹停止一段时候后才开始复位
		 * jellyBall.stopRefresh(); mScrollView.stopRefresh(); } }, 500);
		 */
		jellyBall.beginStopRefresh();
		mScrollView.stopRefresh();
	}
}
