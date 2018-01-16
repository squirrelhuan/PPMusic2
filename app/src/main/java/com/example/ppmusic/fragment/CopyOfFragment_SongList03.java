package com.example.ppmusic.fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.example.ppmusic.MusicLoader;
import com.example.ppmusic.MyApp;
import com.example.ppmusic.R;
import com.example.ppmusic.activities.MainActivity;
import com.example.ppmusic.activities.MusicListActivity;
import com.example.ppmusic.adapter.SongListAdapter02;
import com.example.ppmusic.adapter.AlbumListAdapter03;
import com.example.ppmusic.adapter.sortlistview.CharacterParser;
import com.example.ppmusic.adapter.sortlistview.ClearEditText;
import com.example.ppmusic.adapter.sortlistview.GroupMemberBean;
import com.example.ppmusic.adapter.sortlistview.PinyinComparator;
import com.example.ppmusic.adapter.sortlistview.SideBar;
import com.example.ppmusic.adapter.sortlistview.SideBar.OnTouchingLetterChangedListener;
import com.example.ppmusic.adapter.sortlistview.SortGroupMemberAdapter;
import com.example.ppmusic.base.MyBaseFragment;
import com.example.ppmusic.bean.AlbumInfo;
import com.example.ppmusic.bean.MusicInfo;
import com.example.ppmusic.bean.SingerInfo;
import com.example.ppmusic.utils.DBUtils;
import com.example.ppmusic.utils.IntentUtil;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * @author CGQ
 * 
 */
public class CopyOfFragment_SongList03 extends MyBaseFragment implements
		SectionIndexer, OnClickListener, OnItemClickListener {

	MainActivity mainActivity;

	ImageView main_image;
	GridView gv_singer;
	List<MusicInfo> musicList_current = new ArrayList<MusicInfo>();
	List<AlbumInfo> albumList = new ArrayList<AlbumInfo>();

	private AlbumListAdapter03 sadapter;

	/**************************************************************************************************/

	private ListView sortListView;
	private SideBar sideBar;
	private TextView dialog;
	public static SortGroupMemberAdapter adapter;
	private ClearEditText mClearEditText;

	private LinearLayout titleLayout;
	private TextView title;
	private TextView tvNofriends;
	/**
	 * 上次第一个可见元素，用于滚动时记录标识。
	 */
	private int lastFirstVisibleItem = -1;
	/**
	 * 汉字转换成拼音的类
	 */
	private CharacterParser characterParser;
	private List<GroupMemberBean> SourceDateList;

	/**
	 * 根据拼音来排列ListView里面的数据类
	 */
	private PinyinComparator pinyinComparator;

	@Override
	public View setContentUI(LayoutInflater inflater, ViewGroup container) {
		return inflater
				.inflate(R.layout.activity_add_friends, container, false);
	}

	@Override
	public void init() {
		//initViews();
	}

	@Override
	public void onResume() {
		//initViews();
		super.onResume();
	}
	
	private void initViews() {
		/*MusicLoader musicLoader = MusicLoader.instance(getActivity()
				.getContentResolver());
		List<MusicInfo> musicList = musicLoader.getMusicList();*/

		//DBUtils.clearTable(getActivity());
		/*for (int i = 0; i < musicList.size(); i++) {
			DBUtils.insertDB(getActivity(),musicList.get(i));
		}*/
		//DBUtils.insertAllDB(getActivity(), musicList);
		musicList_current.clear();
		musicList_current.addAll(DBUtils.queryAllDB(getActivity()));
		ArrayList<String> strs = new ArrayList<String>();

		if(musicList_current.size()<1){
			Toast.makeText(getActivity(), "songlist", Toast.LENGTH_SHORT).show();
		};
		
		for (int i = 0; i < musicList_current.size(); i++) {
			strs.add(musicList_current.get(i).getTitle());
		}
		final String[] sss = strs.toArray(new String[strs.size()]);

		titleLayout = (LinearLayout) findViewById(R.id.title_layout);
		title = (TextView) this.findViewById(R.id.title_layout_catalog);
		tvNofriends = (TextView) this
				.findViewById(R.id.title_layout_no_friends);
		// 实例化汉字转拼音类
		characterParser = CharacterParser.getInstance();

		pinyinComparator = new PinyinComparator();

		sideBar = (SideBar) findViewById(R.id.sidrbar);
		dialog = (TextView) findViewById(R.id.dialog);
		sideBar.setTextView(dialog);

		// 设置右侧触摸监听
		sideBar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {

			@Override
			public void onTouchingLetterChanged(String s) {
				// 该字母首次出现的位置
				int position = adapter.getPositionForSection(s.charAt(0));
				if (position != -1) {
					sortListView.setSelection(position);
				}
			}
		});

		sortListView = (ListView) findViewById(R.id.lvSongs);
		sortListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// 这里要利用adapter.getItem(position)来获取当前position所对应的对象
				/*Toast.makeText(
						getActivity(),
						((GroupMemberBean) adapter.getItem(position)).getName(),
						Toast.LENGTH_SHORT).show();*/
			
				MyApp.getNatureBinder().startPlay(((GroupMemberBean) adapter.getItem(position)).getMusicInfo());
				//MyApp.natureBinder.startPlay(musicList_current,position,0);
				//adapter.notifyDataSetChanged();
			}
		});

		SourceDateList = filledData(sss,musicList_current);

		// 根据a-z进行排序源数据
		Collections.sort(SourceDateList, pinyinComparator);
		/* adapter = new SortGroupMemberAdapter(getActivity(), SourceDateList); */
		adapter = new SortGroupMemberAdapter(getActivity(), SourceDateList);
		sortListView.setAdapter(adapter);
		sortListView.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				int section = getSectionForPosition(firstVisibleItem);
				int nextSection = getSectionForPosition(firstVisibleItem + 1);
				int nextSecPosition = getPositionForSection(+nextSection);
				if (firstVisibleItem != lastFirstVisibleItem) {
					MarginLayoutParams params = (MarginLayoutParams) titleLayout
							.getLayoutParams();
					params.topMargin = 0;
					titleLayout.setLayoutParams(params);
					if(SourceDateList.size()>section){
						title.setText(SourceDateList.get(
								getPositionForSection(section)).getSortLetters());
					}else{
						title.setText("A");
					}
				}
				if (nextSecPosition == firstVisibleItem + 1) {
					View childView = view.getChildAt(0);
					if (childView != null) {
						int titleHeight = titleLayout.getHeight();
						int bottom = childView.getBottom();
						MarginLayoutParams params = (MarginLayoutParams) titleLayout
								.getLayoutParams();
						if (bottom < titleHeight) {
							float pushedDistance = bottom - titleHeight;
							params.topMargin = (int) pushedDistance;
							titleLayout.setLayoutParams(params);
						} else {
							if (params.topMargin != 0) {
								params.topMargin = 0;
								titleLayout.setLayoutParams(params);
							}
						}
					}
				}
				lastFirstVisibleItem = firstVisibleItem;
			}
		});
		mClearEditText = (ClearEditText) findViewById(R.id.filter_edit);

		// 根据输入框输入值的改变来过滤搜索
		mClearEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// 这个时候不需要挤压效果 就把他隐藏掉
				titleLayout.setVisibility(View.GONE);
				// 当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
				filterData(s.toString());
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
	}

	/**
	 * 为ListView填充数据
	 * 
	 * @param date
	 * @param musicList_current2 
	 * @return
	 */
	private List<GroupMemberBean> filledData(String[] date, List<MusicInfo> musicList_current2) {
		List<GroupMemberBean> mSortList = new ArrayList<GroupMemberBean>();

		for (int i = 0; i < date.length; i++) {
			GroupMemberBean sortModel = new GroupMemberBean();
			sortModel.setName(date[i]);
			// 汉字转换成拼音
			String pinyin = characterParser.getSelling(date[i]);
			String sortString = pinyin.substring(0, 1).toUpperCase();

			// 正则表达式，判断首字母是否是英文字母
			if (sortString.matches("[A-Z]")) {
				sortModel.setSortLetters(sortString.toUpperCase());
			} else {
				sortModel.setSortLetters("#");
			}
			sortModel.setMusicInfo(musicList_current2.get(i));
			mSortList.add(sortModel);
		}
		return mSortList;

	}

	/**
	 * 根据输入框中的值来过滤数据并更新ListView
	 * 
	 * @param filterStr
	 */
	private void filterData(String filterStr) {
		List<GroupMemberBean> filterDateList = new ArrayList<GroupMemberBean>();

		if (TextUtils.isEmpty(filterStr)) {
			filterDateList = SourceDateList;
			tvNofriends.setVisibility(View.GONE);
		} else {
			filterDateList.clear();
			for (GroupMemberBean sortModel : SourceDateList) {
				String name = sortModel.getName();
				if (name.indexOf(filterStr.toString()) != -1
						|| characterParser.getSelling(name).startsWith(
								filterStr.toString())) {
					filterDateList.add(sortModel);
				}
			}
		}

		// 根据a-z进行排序
		Collections.sort(filterDateList, pinyinComparator);
		adapter.updateListView(filterDateList);
		if (filterDateList.size() == 0) {
			tvNofriends.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public Object[] getSections() {
		return null;
	}

	/**
	 * 根据ListView的当前位置获取分类的首字母的Char ascii值
	 */
	public int getSectionForPosition(int position) {
		if(position== 0 && SourceDateList.size()<1 ||position >= SourceDateList.size()){
			return 0;
		}
		return SourceDateList.get(position).getSortLetters().charAt(0);
	}

	/**
	 * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
	 */
	public int getPositionForSection(int section) {
		if(SourceDateList.size()==0){
			return -1;
		}
		for (int i = 0; i < SourceDateList.size(); i++) {
			String sortStr = SourceDateList.get(i).getSortLetters();
			char firstChar = sortStr.toUpperCase().charAt(0);
			if (firstChar == section) {
				return i;
			}
		}
		return -1;
	}

	int test = 0;
	long testTime = System.currentTimeMillis();

	@Override
	public void onClick(View v) {
		Bundle bundle = new Bundle();

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		IntentUtil.jump(getActivity(), MusicListActivity.class, null);

	}

	public static void main(String[] args) {
	}
}
