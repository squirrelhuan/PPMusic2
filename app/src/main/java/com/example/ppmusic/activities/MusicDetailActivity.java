package com.example.ppmusic.activities;

import android.content.ComponentName;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBar;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ppmusic.IApolloService;
import com.example.ppmusic.MusicLoader;
import com.example.ppmusic.R;
import com.example.ppmusic.base.BaseActivity;
import com.example.ppmusic.bean.MusicInfo;
import com.example.ppmusic.helpers.utils.MusicUtils;
import com.example.ppmusic.service.ApolloService;
import com.example.ppmusic.service.ServiceToken;
import com.example.ppmusic.utils.DBUtils;
import com.example.ppmusic.view.custom.JellyBall;
import com.example.ppmusic.view.custom.PullScrollView;

import java.util.ArrayList;
import java.util.List;

public class MusicDetailActivity extends BaseActivity implements
		OnCheckedChangeListener, PullScrollView.OnTurnListener,
		ServiceConnection {

	private PullScrollView mScrollView;
	private JellyBall jellyBall;
	private ImageView mHeadImg;
	private TableLayout mMainLayout;
	private RadioGroup rg_button01;
	private RadioButton main_radio_button_1, main_radio_button_2,
			main_radio_button_3;

	private ImageView img,img_bg;
	private Bundle bundle = new Bundle();
	private String title = "本地音乐";

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_music_detail);

		bundle = getIntent().getExtras();
		if (bundle != null && bundle.containsKey("Title")) {
			title = bundle.getString("Title");
		}
		initView();
		showTable();
		init();
	}

	protected void initView(){
		mScrollView = (PullScrollView) findViewById(R.id.scroll_view);
		mHeadImg = (ImageView) findViewById(R.id.background_img);
		img_bg = (ImageView) findViewById(R.id.background_img);
		//searchMusicImage(title);
		mMainLayout = (TableLayout) findViewById(R.id.table_layout);
		jellyBall = (JellyBall) findViewById(R.id.cirle);
		mScrollView.setHeader(mHeadImg);
		mScrollView.setOnTurnListener(this);
	}

	public void showTable(){
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
					Toast.makeText(MusicDetailActivity.this, "Click item " + n,
							Toast.LENGTH_SHORT).show();
				}
			});
			mMainLayout.addView(tableRow);
		}
	}

	@Override
	public String getTitleText() {
		bundle = getIntent().getExtras();
		if (bundle != null && bundle.containsKey("Title")) {
			title = bundle.getString("Title");
		}
		return title;
	}

	@Override
	public int getBackgroundColor() {
		return getResources().getColor(R.color.transparent);
	}

	// CopyOfRecentlyAddedFragment mainFragment4;
	List<MusicInfo> musicList = new ArrayList<MusicInfo>();
	public static List<MusicInfo> musicList_current = new ArrayList<MusicInfo>();

	private void init() {

		MusicLoader musicLoader = MusicLoader.instance(getContentResolver());
		musicList = musicLoader.getMusicList();
		musicList_current.clear();
		switch (title) {
		case "本地歌曲":
			musicList_current.addAll(musicList);
			break;
		case "我喜欢":
			musicList_current.addAll(DBUtils.queryAllFavouriteList(this, 1));
			break;

		default:
			break;
		}

		img = (ImageView) findViewById(R.id.iv_head);
		// DampView view = (DampView) findViewById(R.id.dampview);
		// view.setImageView(img);

		rg_button01 = (RadioGroup) findViewById(R.id.rg_button01);
		rg_button01.setOnCheckedChangeListener(this);
		main_radio_button_1 = (RadioButton) findViewById(R.id.main_radio_button_1);
		main_radio_button_2 = (RadioButton) findViewById(R.id.main_radio_button_2);
		main_radio_button_3 = (RadioButton) findViewById(R.id.main_radio_button_3);

		main_radio_button_1.setChecked(true);

	}

	@Override
	protected void onResume() {
		super.onResume();
		//searchMusicImage(title);
	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder obj) {
		MusicUtils.mService = IApolloService.Stub.asInterface(obj);
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		MusicUtils.mService = null;
	}

	private ServiceToken mToken;

	@Override
	protected void onStart() {

		// Bind to Service
		mToken = MusicUtils.bindToService(this, this);

		IntentFilter filter = new IntentFilter();
		filter.addAction(ApolloService.META_CHANGED);
		// Toast.makeText(MainActivity.this,
		// "META_CHANGED="+ApolloService.META_CHANGED.toString(),
		// Toast.LENGTH_SHORT).show();
		super.onStart();
	}

	@Override
	protected void onStop() {
		// Unbind
		if (MusicUtils.mService != null)
			MusicUtils.unbindFromService(mToken);
		/*Toast.makeText(MusicDetailActivity.this, "stop", Toast.LENGTH_SHORT)
				.show();*/
		// TODO: clear image cache

		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// unbindService(conn);
	}

	OnPageChangeListener mOnPageChangeListener = new OnPageChangeListener() {

		@Override
		public void onPageSelected(int arg0) {
			switch (arg0) {
			case 0:
				main_radio_button_1.setChecked(true);
				// rel_01.startAnimation(AnimationUtils.loadAnimation(MainActivity.this,
				// R.anim.bottom_down));
				// rel_01.setVisibility(View.GONE);
				break;
			case 1:
				main_radio_button_2.setChecked(true);
				// rel_01.startAnimation(AnimationUtils.loadAnimation(MainActivity.this,
				// R.anim.bottom_up));
				// rel_01.setVisibility(View.VISIBLE);
				break;
			case 2:
				main_radio_button_3.setChecked(true);
				// rel_01.startAnimation(AnimationUtils.loadAnimation(MainActivity.this,
				// R.anim.bottom_down));
				// rel_01.setVisibility(View.GONE);
				break;

			default:
				break;
			}

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub

		}
	};

	@Override
	public void onCheckedChanged(RadioGroup arg0, int arg1) {
		// showToast(""+arg1);
		switch (arg1) {
		case R.id.main_radio_button_1:
			// viewPager.setCurrentItem(0);
			break;
		case R.id.main_radio_button_2:
			// viewPager.setCurrentItem(1);
			break;
		case R.id.main_radio_button_3:
			// viewPager.setCurrentItem(2);
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
    
   /* public void searchMusicImage(String singer_name) {
    	//type=big_artist_pic&pictype=url&content=list&&id=0&name=[歌手名]&rid=[可为空]from=pc&json=[json数据排放方式,一般为1]&version=1&width=[写真宽度]&height=[写真高度]
		RequestParams params = new RequestParams();
		params.put("type", "big_artist_pic");
		params.put("pictype", "url");
		params.put("content", "list");
		params.put("id", 0);
		params.put("name", singer_name);
		params.put("rid", "");
		params.put("from", "pc");
		params.put("json", 1);
		params.put("version", 1);
		params.put("width", 320);
		params.put("height", 240);
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
							if(new String(arg2).equals("NO_PIC")){
								return;
							}
							//img_url = new String(arg2);
							Result_KuWo_Singer_bg root = JSON.parseObject(arg2, Result_KuWo_Singer_bg.class);
							
							String url_img = root.getArray().get(0).get("wpurl");
							if(url_img==null||url_img.equals("")){
								url_img = root.getArray().get(0).get("bkurl");
							}
					        MyApp.imageLoader.displayImage(url_img,img_bg);
							//Toast.makeText(getActivity(), new String(arg2), Toast.LENGTH_SHORT).show();
						}
						// showToast("成功...");
					}
				});
	}*/
}
