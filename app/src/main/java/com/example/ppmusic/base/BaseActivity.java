package com.example.ppmusic.base;

import com.example.ppmusic.MyApp;
import com.example.ppmusic.R;
import com.example.ppmusic.interfaces.TitleInterface;
import com.example.ppmusic.utils.StatusBarUtil;
import com.example.ppmusic.utils.ToastUtils;
import com.lidroid.xutils.ViewUtils;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


public abstract class BaseActivity extends AppCompatActivity implements TitleInterface {

	protected static final String TAG = BaseActivity.class.getName();
	public ProgressDialog progressDialog;
	MyApp app;
	ToastUtils toastUtils;

	// public ImageLoader imageLoader;

	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);
		StatusBarUtil.transparencyBar(this);
		StatusBarUtil.StatusBarLightMode(this);
		ViewUtils.inject(this);
		app = (MyApp) MyApp.getInstance();
		toastUtils = app.getToastUtils();
		initTitle();
	};

	public TextView title_txt_right;
	public TextView title_txt;

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	public void initTitle() {
		LinearLayout common_title_layout = (LinearLayout) findViewById(R.id.common_title_layout);
		if (common_title_layout != null) {
			if (getTitleText() == null) {
				common_title_layout.setVisibility(View.GONE);
				return;
			}
			common_title_layout.setBackgroundColor(getBackgroundColor());
			title_txt = (TextView) findViewById(R.id.common_title);
			title_txt.setText(getTitleText());
			ImageView common_title_left = (ImageView) findViewById(R.id.common_title_left);
			common_title_left.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					finishUI();
				}
			});
			if (getTitleLeftHide()) {
				common_title_left.setVisibility(View.GONE);
			}
			title_txt_right = (TextView) findViewById(R.id.common_title_right);
			title_txt_right.setText("" + getTitleRightText());
			if (getTitleRightText().isEmpty()) {
				title_txt_right.setVisibility(View.GONE);
			}
			title_txt_right.setOnClickListener(getTitleRightClick());
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
	}

	@Override
	public void finishUI() {
		finish();
	}

	@Override
	public void showToast(String str) {
		toastUtils.showToast(str);
	}

	@Override
	public void showProgress(String msg) {
		if (progressDialog == null) {
			progressDialog = new ProgressDialog(this);
		}
		progressDialog.setMessage(msg);
		progressDialog.setCancelable(true);
		progressDialog.show();
	}

	@Override
	public void showProgress(String msg, boolean flag) {
		if (progressDialog == null) {
			progressDialog = new ProgressDialog(this);
		}
		progressDialog.setMessage(msg);
		progressDialog.setCancelable(flag);
		progressDialog.setCanceledOnTouchOutside(flag);
		progressDialog.show();
	}

	@Override
	public void hideProgress() {
		if (progressDialog != null && progressDialog.isShowing())
			progressDialog.hide();
	}

	@Override
	public int getBackgroundColor() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public String getTitleText() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean getTitleLeftHide() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getTitleRightText() {
		return "";
	}

	@Override
	public OnClickListener getTitleRightClick() {
		return null;

	}

	/**
	 * 隐藏键盘
	 * 
	 * @param editText
	 */
	protected void hideInputMethod(EditText editText) {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		if (null != imm) {
			imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
		}
	}

}
