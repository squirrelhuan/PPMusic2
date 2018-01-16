package com.example.ppmusic.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ppmusic.MyApp;
import com.example.ppmusic.R;
import com.example.ppmusic.adapter.EditView_withDeleteButton;
import com.example.ppmusic.adapter.LinearLayout_withRaidoButton;
import com.example.ppmusic.adapter.SongListAdapter_search;
import com.example.ppmusic.adapter.ToggleButton;
import com.example.ppmusic.adapter.ToggleButton.OnToggleChanged;
import com.example.ppmusic.base.MyBaseActivity;
import com.example.ppmusic.bean.TTdongting.Data;
import com.example.ppmusic.constants.Constants;
import com.example.ppmusic.utils.FileSizeUtil;
import com.example.ppmusic.utils.ImageUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.example.ppmusic.MyApp.setPicturePath;
import static com.example.ppmusic.constants.Constants.Action_WallBackGround;
import static com.example.ppmusic.constants.Constants.Action_WellComeBackGround;

/**
 * 泡泡音乐设置界面
 * 
 * @author SquirrelNuts
 * 
 */
public class SettingActivity extends MyBaseActivity implements OnClickListener {

	LinearLayout ll_only_wifi;// wifi模式
	LinearLayout ll_net_play;// 是否数据流量播放
	LinearLayout ll_net_download;// 数据流量下载
	LinearLayout ll_music_quality_play;// 播放品质
	LinearLayout ll_music_quality_download;// 下载品质
	LinearLayout ll_download_path;// 存储路径
	LinearLayout ll_custom_wellcomepager;//自定义欢迎页
	LinearLayout ll_custom_wallpager;// 自定义壁纸
	LinearLayout ll_automatic_play;// 自动播放
	LinearLayout ll_automatic_pause;
	LinearLayout ll_help_and_suggest;// 帮助与建议
	LinearLayout ll_about_player;// 关于泡泡音乐

	ListView lv_songs;
	EditView_withDeleteButton et_search;
	ToggleButton button_only_wifi, button_net_play, button_net_download;
	TextView tv_only_wifi, tv_net_play, tv_net_download, tv_wallpage_path,
			tv_welcomepage_path,tv_download_storage;
	String search_str = "周杰伦";
	private SongListAdapter_search sadapter;
	List<Data> musicList = new ArrayList<Data>();

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_music_setting);

		if (Build.VERSION.SDK_INT >= 21) {
			View decorView = getWindow().getDecorView();
			int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
					| View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
			decorView.setSystemUiVisibility(option);
			getWindow().setStatusBarColor(Color.TRANSPARENT);
		}
		ActionBar actionBar = getSupportActionBar();
		actionBar.hide();

		init();
	}

	@Override
	public String getTitleText() {
		return "设置";
	}

	@Override
	public int getBackgroundColor() {
		return getResources().getColor(R.color.transparent);
	}

	private void init() {

		tv_only_wifi = (TextView) findViewById(R.id.tv_only_wifi);
		tv_net_play = (TextView) findViewById(R.id.tv_net_play);
		tv_net_download = (TextView) findViewById(R.id.tv_net_download);

		ll_only_wifi = (LinearLayout) findViewById(R.id.ll_only_wifi);
		ll_only_wifi.setOnClickListener(this);
		button_only_wifi = (ToggleButton) findViewById(R.id.button_only_wifi);
		button_only_wifi.toggle(MyApp.getPreferencesService().getBoolean(
				"IsNetWifi"));

		button_only_wifi.setOnToggleChanged(new OnToggleChanged() {
			@Override
			public void onToggle(boolean on) {
				MyApp.getPreferencesService().save("IsNetWifi", on);
				if (on) {
					tv_net_play.setTextColor(SettingActivity.this
							.getResources().getColor(R.color.gray));
					tv_net_download.setTextColor(SettingActivity.this
							.getResources().getColor(R.color.gray));
					ll_net_play.setClickable(false);
					ll_net_download.setClickable(false);
				} else {
					tv_net_play.setTextColor(SettingActivity.this
							.getResources().getColor(R.color.black));
					tv_net_download.setTextColor(SettingActivity.this
							.getResources().getColor(R.color.black));
					ll_net_play.setClickable(true);
					ll_net_download.setClickable(true);
				}
				Toast.makeText(SettingActivity.this,
						"only wifi " + (on ? "open" : "close"), Toast.LENGTH_SHORT).show();
			}
		});
		ll_net_play = (LinearLayout) findViewById(R.id.ll_net_play);
		ll_net_play.setOnClickListener(this);
		button_net_play = (ToggleButton) findViewById(R.id.button_net_play);
		button_net_play.toggle(MyApp.getPreferencesService().getBoolean(
				"UseNetPlay"));
		button_net_play.setOnToggleChanged(new OnToggleChanged() {
			@Override
			public void onToggle(boolean on) {
				MyApp.getPreferencesService().save("UseNetPlay", on);
				Toast.makeText(SettingActivity.this,
						"only wifi " + (on ? "open" : "close"), Toast.LENGTH_SHORT).show();
			}
		});
		ll_net_download = (LinearLayout) findViewById(R.id.ll_net_download);
		ll_net_download.setOnClickListener(this);
		button_net_download = (ToggleButton) findViewById(R.id.button_net_download);
		button_net_download.toggle(MyApp.getPreferencesService().getBoolean(
				"UseNetDown"));
		button_net_download.setOnToggleChanged(new OnToggleChanged() {
			@Override
			public void onToggle(boolean on) {
				MyApp.getPreferencesService().save("UseNetDown", on);
				Toast.makeText(SettingActivity.this,
						"only wifi " + (on ? "open" : "close"), Toast.LENGTH_SHORT).show();
			}
		});

		if (MyApp.getPreferencesService().getBoolean("IsNetWifi")) {
			tv_net_play.setTextColor(SettingActivity.this.getResources()
					.getColor(R.color.gray));
			tv_net_download.setTextColor(SettingActivity.this.getResources()
					.getColor(R.color.gray));
			ll_net_play.setClickable(false);
			ll_net_download.setClickable(false);
			button_net_play.setClickable(false);
			button_net_download.setClickable(false);
		} else {
			tv_net_play.setTextColor(SettingActivity.this.getResources()
					.getColor(R.color.black));
			tv_net_download.setTextColor(SettingActivity.this.getResources()
					.getColor(R.color.black));
			ll_net_play.setClickable(true);
			ll_net_download.setClickable(true);
			button_net_play.setClickable(false);
			button_net_download.setClickable(false);
		}
		ll_music_quality_play = (LinearLayout) findViewById(R.id.ll_music_quality_play);
		ll_music_quality_play.setOnClickListener(this);
		ll_music_quality_download = (LinearLayout) findViewById(R.id.ll_music_quality_download);
		ll_music_quality_download.setOnClickListener(this);
		ll_download_path = (LinearLayout) findViewById(R.id.ll_download_path);
		ll_download_path.setOnClickListener(this);
		ll_custom_wallpager = (LinearLayout) findViewById(R.id.ll_custom_wallpager);
		ll_custom_wallpager.setOnClickListener(this);
		ll_custom_wellcomepager = (LinearLayout) findViewById(R.id.ll_custom_welcomepager);
		ll_custom_wellcomepager.setOnClickListener(this);
		ll_automatic_play = (LinearLayout) findViewById(R.id.ll_automatic_play);
		ll_automatic_play.setOnClickListener(this);
		ll_automatic_pause = (LinearLayout) findViewById(R.id.ll_automatic_pause);
		ll_automatic_pause.setOnClickListener(this);
		ll_help_and_suggest = (LinearLayout) findViewById(R.id.ll_help_and_suggest);
		ll_help_and_suggest.setOnClickListener(this);
		ll_about_player = (LinearLayout) findViewById(R.id.ll_about_player);
		ll_about_player.setOnClickListener(this);

		boolean network_permission = MyApp.getPreferencesService().getBoolean(
				"IsNetWifi");
		if (network_permission) {
			ll_net_play.setClickable(false);
			ll_net_download.setClickable(false);
		}

		tv_welcomepage_path = (TextView) findViewById(R.id.tv_welcomepage_path);
		tv_welcomepage_path.setText(MyApp.getPreferencesService().getValue(
				"WelcomePagePath", "默认"));
		tv_wallpage_path = (TextView) findViewById(R.id.tv_wallpage_path);
		tv_wallpage_path.setText(MyApp.getPreferencesService().getValue(
				"WallPagerPath", "默认"));
		tv_download_storage = (TextView) findViewById(R.id.tv_download_storage);
		tv_download_storage.setText(MyApp.getPreferencesService().getInt(
				"DownLoadStorage", 0) == 0 ? "手机存储" : "SD卡存储");

		initDriDialog();
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
		case R.id.ll_only_wifi:
			button_only_wifi.performClick();
			break;
		case R.id.ll_net_play:
			button_net_play.performClick();
			break;
		case R.id.ll_net_download:
			button_net_download.performClick();
			break;
		case R.id.ll_custom_welcomepager://自定义欢迎页
			SelectPhotoPopupWindow("WelcomePagePath");
			break;
		case R.id.ll_custom_wallpager://自定义背景
			SelectPhotoPopupWindow("WallPagerPath");
			break;
		case R.id.ll_download_path:
			mChooseMachineTypeDialog.show();
			break;
		case R.id.ll_music_quality_play:// 播放品质
			SelectQualityPopupWindow(0);
			break;
		case R.id.ll_music_quality_download:// 下载品质
			SelectQualityPopupWindow(1);
			break;
		default:
			break;
		}
	}

	// 选择图片dialog
	private PopupWindow pop = null;
	private LinearLayout ll_popup;
	private Uri fileUri;
	public static final int MEDIA_TYPE_IMAGE = 1;
	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	private static final int GALLERY_Photo_REQUEST_CODE = 300;
	private static final int CROP_SMALL_PICTURE = 5;
	private static final String LOG_TAG = "CGQ";
    private static String operationType = "WallPagerPath";

	// 选择照片的popupWindow
	private void SelectPhotoPopupWindow(String operationType) {
		this.operationType = operationType;
		pop = new PopupWindow(this);
		View view = getLayoutInflater().inflate(R.layout.item_popupwindows_ios,
				null);

		ll_popup = (LinearLayout) view.findViewById(R.id.ll_popup);
		pop.setWidth(LayoutParams.MATCH_PARENT);
		pop.setHeight(LayoutParams.WRAP_CONTENT);
		pop.setBackgroundDrawable(new BitmapDrawable());
		pop.setFocusable(true);
		pop.setOutsideTouchable(true);
		pop.setContentView(view);

		RelativeLayout parent = (RelativeLayout) view.findViewById(R.id.parent);
		Button btn_camera = (Button) view
				.findViewById(R.id.item_popupwindows_camera);
		Button btn_file = (Button) view
				.findViewById(R.id.item_popupwindows_Photo);
		Button btn_default = (Button) view
				.findViewById(R.id.item_popupwindows_Default);
		Button btn_cancle = (Button) view
				.findViewById(R.id.item_popupwindows_cancel);
		parent.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				pop.dismiss();
				ll_popup.startAnimation(AnimationUtils.loadAnimation(
						SettingActivity.this, R.anim.scaling_big));
			}
		});
		// 拍照
		btn_camera.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				takePicture();
			}
		});
		// 文件选取
		btn_file.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
				// getPictureByGallery(SettingActivity.this);
				getImageFromPhoto(SettingActivity.this,
						GALLERY_Photo_REQUEST_CODE);
			}
		});
		// 恢复默认
		btn_default.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				switch (SettingActivity.operationType){
					case "WallPagerPath":
						setPicturePath("WallpaperUri","");
						MyApp.getPreferencesService().save("WallPagerPath", "默认");
						tv_wallpage_path.setText(MyApp.getPreferencesService()
								.getValue("WallPagerPath", "默认"));
						break;
					case "WelcomePagePath":
						setPicturePath("WelcomepaperUri","");
						MyApp.getPreferencesService().save("WelcomePagerPath", "默认");
						tv_wallpage_path.setText(MyApp.getPreferencesService()
								.getValue("WelcomePagerPath", "默认"));
						break;
					default:
						break;
				}

				pop.dismiss();
			}
		});
		// 取消
		btn_cancle.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				pop.dismiss();
			}
		});
		pop.showAtLocation(view, Gravity.BOTTOM, 0, 0);
		ll_popup.startAnimation(AnimationUtils.loadAnimation(this,
				R.anim.bottom_up));
	}

	/**
	 * 选择品质的popupWindow
	 * 
	 * @param type  0 播放品质 1下载品质
	 */
	private void SelectQualityPopupWindow(int type) {
		pop = new PopupWindow(this);
		View view = getLayoutInflater().inflate(
				R.layout.item_popupwindows_selected_quality_ios, null);

		ll_popup = (LinearLayout) view.findViewById(R.id.ll_popup);
		pop.setWidth(LayoutParams.MATCH_PARENT);
		pop.setHeight(LayoutParams.WRAP_CONTENT);
		pop.setBackgroundDrawable(new BitmapDrawable());
		pop.setFocusable(true);
		pop.setOutsideTouchable(true);
		pop.setContentView(view);

		RelativeLayout parent = (RelativeLayout) view.findViewById(R.id.parent);
		parent.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				pop.dismiss();
				ll_popup.startAnimation(AnimationUtils.loadAnimation(
						SettingActivity.this, R.anim.scaling_big));
			}
		});
		List<RadioButton> list = new ArrayList<RadioButton>();
		LinearLayout_withRaidoButton ll_btn_01 = (LinearLayout_withRaidoButton) view
				.findViewById(R.id.item_popupwindows_ll_btn_01);
		RadioButton item_popupwindows_rb_001 = (RadioButton) view.findViewById(R.id.item_popupwindows_rb_001);
		item_popupwindows_rb_001.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				
			}
		});
		ll_btn_01.setList(list);
		ll_btn_01.setRadioButton(item_popupwindows_rb_001);
		
		LinearLayout_withRaidoButton ll_btn_02 = (LinearLayout_withRaidoButton) view
				.findViewById(R.id.item_popupwindows_ll_btn_02);
		RadioButton item_popupwindows_rb_002 = (RadioButton) view.findViewById(R.id.item_popupwindows_rb_002);
		item_popupwindows_rb_002.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				
			}
		});
		ll_btn_02.setList(list);
		ll_btn_02.setRadioButton(item_popupwindows_rb_002);
		
		LinearLayout_withRaidoButton ll_btn_03 = (LinearLayout_withRaidoButton) view
				.findViewById(R.id.item_popupwindows_ll_btn_03);
		RadioButton item_popupwindows_rb_003 = (RadioButton) view.findViewById(R.id.item_popupwindows_rb_003);
		item_popupwindows_rb_003.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				
			}
		});
		ll_btn_03.setList(list);
		ll_btn_03.setRadioButton(item_popupwindows_rb_003);
		
		LinearLayout_withRaidoButton ll_btn_04 = (LinearLayout_withRaidoButton) view
				.findViewById(R.id.item_popupwindows_ll_btn_04);
		RadioButton item_popupwindows_rb_004 = (RadioButton) view.findViewById(R.id.item_popupwindows_rb_004);
		item_popupwindows_rb_004.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				
			}
		});
		ll_btn_04.setList(list);
		ll_btn_04.setRadioButton(item_popupwindows_rb_004);
		
		LinearLayout_withRaidoButton ll_btn_05 = (LinearLayout_withRaidoButton) view
				.findViewById(R.id.item_popupwindows_ll_btn_05);
		RadioButton item_popupwindows_rb_005 = (RadioButton) view.findViewById(R.id.item_popupwindows_rb_005);
		item_popupwindows_rb_005.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				
			}
		});
		ll_btn_05.setList(list);
		ll_btn_05.setRadioButton(item_popupwindows_rb_005);
		
		pop.showAtLocation(view, Gravity.BOTTOM, 0, 0);
		ll_popup.startAnimation(AnimationUtils.loadAnimation(this,
				R.anim.bottom_up));
	}

	Dialog mChooseMachineTypeDialog;
	TextView tv_storage_phone, tv_storage_extra;
	RadioButton rb_phone, rb_extra;
	LinearLayout ll_no_sdcard, ll_has_sdcard;

	/** 选择存储Dialog */
	private void initDriDialog() {
		View mChooseTypeDialogView = this.getLayoutInflater().inflate(
				R.layout.choose_download_path_item, null);
		tv_storage_phone = (TextView) mChooseTypeDialogView
				.findViewById(R.id.tv_storage_phone);
		tv_storage_phone.setText(FileSizeUtil.formatFileSize(
				FileSizeUtil.getAvailableInternalMemorySize(), true)
				+ "可用，共"
				+ FileSizeUtil.formatFileSize(
						FileSizeUtil.getTotalInternalMemorySize(), true));
		ll_has_sdcard = (LinearLayout) mChooseTypeDialogView
				.findViewById(R.id.ll_has_sdcard);
		ll_no_sdcard = (LinearLayout) mChooseTypeDialogView
				.findViewById(R.id.ll_no_sdcard);
		if (!FileSizeUtil.externalMemoryAvailable()){
			ll_has_sdcard.setVisibility(View.GONE);
			ll_no_sdcard.setVisibility(View.VISIBLE);
		} else {
			tv_storage_extra = (TextView) mChooseTypeDialogView
					.findViewById(R.id.tv_storage_extra);
			tv_storage_extra.setText(FileSizeUtil.formatFileSize(
					FileSizeUtil.getAvailableExternalMemorySize(), true)
					+ "可用，共"
					+ FileSizeUtil.formatFileSize(
							FileSizeUtil.getTotalExternalMemorySize(), true));
		}
		rb_phone = (RadioButton) mChooseTypeDialogView
				.findViewById(R.id.rb_phone);
		rb_phone.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				if (arg1) {
					rb_extra.setChecked(false);
					MyApp.getPreferencesService().save("DownLoadStorage", 0);
					tv_download_storage.setText(MyApp.getPreferencesService()
							.getInt("DownLoadStorage", 0) == 0 ? "手机存储"
							: "SD卡存储");
				}
			}
		});
		rb_extra = (RadioButton) mChooseTypeDialogView
				.findViewById(R.id.rb_extra);
		rb_extra.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				if (arg1) {
					rb_phone.setChecked(false);
					MyApp.getPreferencesService().save("DownLoadStorage", 1);
					tv_download_storage.setText(MyApp.getPreferencesService()
							.getInt("DownLoadStorage", 0) == 0 ? "手机存储"
							: "SD卡存储");
				}
			}
		});
		mChooseMachineTypeDialog = new Dialog(this);
		mChooseMachineTypeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		mChooseMachineTypeDialog.setCanceledOnTouchOutside(true);
		mChooseMachineTypeDialog.setContentView(mChooseTypeDialogView);
	}

	/** 调用系统拍照 */
	private void takePicture() {
		// 利用系统自带的相机应用:拍照
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		// create a file to save the image
		fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
		// 此处这句intent的值设置关系到后面的onActivityResult中会进入那个分支，即关系到data是否为null，如果此处指定，则后来的data为null
		// set the image file name
		intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
		startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
	}

	public static void getImageFromPhoto(Context context, int REQUE_CODE_PHOTO) {
		Intent intent = new Intent(Intent.ACTION_PICK, null);
		intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
				"image/*");
		((Activity) context).startActivityForResult(intent, REQUE_CODE_PHOTO);
	}

	/**
	 * 调用图库获取图片
	 * 
	 * @param context
	 *            必须是Activity
	 */
	public void getPictureByGallery(Activity context) {
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_PICK);
		intent.setType("image/*");
		context.startActivityForResult(intent, GALLERY_Photo_REQUEST_CODE);
	}

	/** Create a file Uri for saving an image or video */
	private static Uri getOutputMediaFileUri(int type) {
		return Uri.fromFile(getOutputMediaFile(type));
	}

	/** Create a File for saving an image or video */
	private static File getOutputMediaFile(int type) {
		// To be safe, you should check that the SDCard is mounted
		// using Environment.getExternalStorageState() before doing this.

		File mediaStorageDir = null;
		try {
			// This location works best if you want the created images to be
			// shared
			// between applications and persist after your app has been
			// uninstalled.
			mediaStorageDir = new File(
					Environment
							.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
					"MyCameraApp");

			Log.d(LOG_TAG, "Successfully created mediaStorageDir: "
					+ mediaStorageDir);

		} catch (Exception e) {
			e.printStackTrace();
			Log.d(LOG_TAG, "Error in Creating mediaStorageDir: "
					+ mediaStorageDir);
		}
		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				// 在SD卡上创建文件夹需要权限：
				// <uses-permission
				// android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
				Log.d(LOG_TAG,
						"failed to create directory, check if you have the WRITE_EXTERNAL_STORAGE permission");
				return null;
			}
		}
		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
				.format(new Date());
		File mediaFile;
		if (type == MEDIA_TYPE_IMAGE) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator
					+ "IMG_" + timeStamp + ".jpg");
		} else {
			return null;
		}
		return mediaFile;
	}

	// 获取返回结果
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (pop != null) {
			pop.dismiss();
		}
		Log.d(LOG_TAG, "onActivityResult: requestCode: " + requestCode
				+ ", resultCode: " + requestCode + ", data: " + data
				+ ",resultCode:" + resultCode);
		switch (requestCode) {
		// 如果是拍照
		case CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE:
			Log.d(LOG_TAG, "CAPTURE_IMAGE");
			if (RESULT_OK == resultCode) {
				Log.d(LOG_TAG, "RESULT_OK");
				// Check if the result includes a thumbnail Bitmap
				if (data != null) {
					// 没有指定特定存储路径的时候
					Log.d(LOG_TAG,
							"data is NOT null, file on default position.");
					// 指定了存储路径的时候（intent.putExtra(MediaStore.EXTRA_OUTPUT,fileUri);）
					// Image captured and saved to fileUri specified in the
					// Intent
					if (data.hasExtra("data")) {
						// Bitmap thumbnail = data.getParcelableExtra("data");
					}
				} else {
					switch (operationType){
						case "WallPagerPath":
							ImageUtils.startPhotoZoom(SettingActivity.this, fileUri,
									CROP_SMALL_PICTURE);
							break;
						case "WelcomePagePath":
							ImageUtils.startPhotoZoomScale(SettingActivity.this, fileUri,
									CROP_SMALL_PICTURE,1,1.78f);
							break;
						default:
							break;
					}
				}
			} else if (resultCode == RESULT_CANCELED) {
				// User cancelled the image capture
			} else {
				// Image capture failed, advise user
			}
			break;
		// 从相册选取
		case GALLERY_Photo_REQUEST_CODE:
			Log.d(LOG_TAG, "GALLERY");
			if (resultCode == RESULT_OK) {
				if (data != null) {
					// MyApp.setWallpaperUri(data.getData());
					fileUri = data.getData();
					switch (operationType){
						case "WallPagerPath":
							ImageUtils.startPhotoZoom(SettingActivity.this, fileUri,
									CROP_SMALL_PICTURE);
							break;
						case "WelcomePagePath":
							ImageUtils.startPhotoZoomScale(SettingActivity.this, fileUri,
									CROP_SMALL_PICTURE,1,1.78f);
							break;
						default:
							break;
					}

				}
			} else if (resultCode == RESULT_CANCELED) {
			}
			break;
		case CROP_SMALL_PICTURE:
			if (data != null) {
				if (fileUri == null) {
					break;
				}
				Bitmap photo = ImageUtils.getBitmapFromUri(fileUri,
						SettingActivity.this);
				String path ="";
				Intent intent = new Intent();
				switch (operationType){
					case "WallPagerPath":
						path = ImageUtils.savePhoto(photo,
								Constants.APP_PATH_PICTURE, "wallpaper");
						Toast.makeText(this, "背景图片设置成功", Toast.LENGTH_LONG).show();
						MyApp.setPicturePath("WallpaperUri",path);
						MyApp.getPreferencesService().save("WallPagerPath", path);
						tv_wallpage_path.setText(MyApp.getPreferencesService()
								.getValue("WallPagerPath", "默认"));
						intent.setAction(Action_WallBackGround);
						break;
					case "WelcomePagePath":
						path = ImageUtils.savePhoto(photo,
								Constants.APP_PATH_PICTURE, "welcomepaper");
						Toast.makeText(this, "欢迎页面设置成功", Toast.LENGTH_LONG).show();
						MyApp.setPicturePath("WelcomepaperUri",path);
						MyApp.getPreferencesService().save("WelcomePagePath", path);
						tv_welcomepage_path.setText(MyApp.getPreferencesService()
								.getValue("WelcomePagePath", "默认"));
						intent.setAction(Action_WellComeBackGround);
						break;
					default:
						break;
				}
				sendBroadcast(intent);//发送普通广播
			}
			/*
			 * if (data != null) { setImageToView(data); }
			 */
		default:
			break;
		}
	}

	protected void setImageToView(Intent data) {
		Bundle extras = data.getExtras();
		if (extras != null) {
			Bitmap photo = extras.getParcelable("data");
			String path = ImageUtils.savePhoto(photo,
					Constants.APP_PATH_PICTURE, "wallpaper");
			Toast.makeText(this, "设置成功", Toast.LENGTH_LONG).show();
			MyApp.setPicturePath("WallpaperUri","path");
			// photo = ImageUtils.toRoundBitmap(photo, fileUri); //
			// ���ʱ���ͼƬ�Ѿ��������Բ�ε���
			// iv_personal_icon.setImageBitmap(photo);
			// uploadPic(photo);
		}
	}

	protected void startPhotoZoom1(Uri uri) {
		if (uri == null) {
			Log.i("tag", "The uri is not exist.");
		}
		int dp = 500;
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
		intent.putExtra("crop", "true");
		intent.putExtra("scale", true);// 去黑边
		intent.putExtra("scaleUpIfNeeded", true);// 去黑边
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1.45);
		// outputX outputY 是裁剪图片宽高，切忌不要再改动下列数字，会卡死
		intent.putExtra("outputX", dp);
		intent.putExtra("outputY", dp);
		intent.putExtra("return-data", false);// 不返回数据
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("noFaceDetection", true);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		startActivityForResult(intent, CROP_SMALL_PICTURE);
	}

	// 缩放图片
	public static Bitmap zoomImg(Bitmap bm, int newWidth, int newHeight) {
		// 获得图片的宽高
		int width = bm.getWidth();
		int height = bm.getHeight();
		// 计算缩放比例
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		// 取得想要缩放的matrix参数
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		// 得到新的图片
		Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix,
				true);
		return newbm;
	}

}
