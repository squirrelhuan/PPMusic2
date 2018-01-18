package com.example.ppmusic.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ppmusic.R;
import com.example.ppmusic.adapter.EditView_withDeleteButton;
import com.example.ppmusic.adapter.SongListAdapter_search;
import com.example.ppmusic.base.BaseActivity;
import com.example.ppmusic.bean.TTdongting.Data;
import com.example.ppmusic.view.custom.JellyBall;
import com.example.ppmusic.view.custom.PullScrollView;

import java.util.ArrayList;
import java.util.List;

public class SongSheetListActivity extends BaseActivity implements OnClickListener,
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
        setContentView(R.layout.activity_song_sheet);

        initView();
    }

    protected void initView() {

    }

    @Override
    public String getTitleText() {
        return "我的歌单";
    }

    @Override
    public int getBackgroundColor() {
        return getResources().getColor(R.color.transparent);
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

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
