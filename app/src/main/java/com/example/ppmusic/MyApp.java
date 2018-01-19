package com.example.ppmusic;

import android.Manifest.permission;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import com.example.ppmusic.constants.Constants;
import com.example.ppmusic.service.CMusicService;
import com.example.ppmusic.service.NatureBinder;
import com.example.ppmusic.utils.DBUtils;
import com.example.ppmusic.utils.ImageUtils;
import com.example.ppmusic.utils.PreferencesService;
import com.example.ppmusic.utils.ToastUtils;
import com.huan.mylog.MyLog;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;

public class MyApp extends Application {
	private static ToastUtils toastUtils;
	public static MyApp instance;
	private static NatureBinder natureBinder = null;
	//private static NetControl net = null;
	private boolean net_wifi = false;// 0(不允许连接网络)，1（仅wifi连接），2（）*****************************************************/

	private static PreferencesService preferencesService;
	private static Uri wallpaperUri;

	public static ImageLoader imageLoader;

	@Override
	public void onCreate() {
		super.onCreate();

		//MyLog myLog = new MyLog(this);
		//myLog.initialization();
		//myLog.setPrintType(MyLog.PrintType.All);// 设置打印类型
		//myLog.setErrorToast("对不起程序崩溃了");// 设置崩溃提示*/
		// mylog.setErrorCatchedListener(new one);
		//connectToNatureService();
		instance = this;
		toastUtils = new ToastUtils(this);
		//net = new NetControl(getApplicationContext());

		//initDB();

        preferencesService = new PreferencesService(getContext());

		initDir();

		initSetting();
		initImageLoader(this);
		initPicasso();

	}



	private void initSetting() {
		net_wifi = preferencesService.getBoolean("IsNetWifi");
	}

	private void initDB() {
		DBUtils.createDB(this);
	}

	// 创建文件夹
	private void initDir() {
		makeDir(Constants.APP_PATH_DOWNLOAD);
		// makeDir(Constants.APP_PATH_DOWNLOAD_HTML);
		// makeDir(Constants.APP_PATH_PICTURE);
	}

	public void initImageLoader(Context context) {
		// File cacheDir = StorageUtils.getOwnCacheDirectory(getContext(),
		// Constants.APP_PATH_CACHE_DIR);
		// cacheDir.mkdirs();

		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.cacheInMemory(true).cacheOnDisk(true).build();
		ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(
				context);
		config.threadPriority(Thread.NORM_PRIORITY - 2);
		config.denyCacheImageMultipleSizesInMemory();
		config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
		config.defaultDisplayImageOptions(options);
		// config.diskCache(new UnlimitedDiskCache(cacheDir));
		// Logger.d("test", "cacheDir = " + cacheDir.getAbsolutePath());
		config.diskCacheSize(100 * 1024 * 1024); // 100 MiB
		config.tasksProcessingOrder(QueueProcessingType.LIFO);
		config.writeDebugLogs(); // Remove for release app
		ImageLoader.getInstance().init(config.build());
		imageLoader = ImageLoader.getInstance();

	}

	public void initPicasso(){
		Picasso picasso = new Picasso.Builder(this)
				.loggingEnabled(true) .defaultBitmapConfig(Bitmap.Config.RGB_565)
				.build();
		Picasso.setSingletonInstance(picasso);
	}

	@Override
	public void onTrimMemory(int level) {
		super.onTrimMemory(level);
	}

	public static MyApp getInstance() {
		return instance;
	}

	public static Context getContext() {
		return instance.getApplicationContext();
	}

	public static Resources getRes() {
		return getContext().getResources();
	}

	public ToastUtils getToastUtils() {
		return toastUtils;
	}

	private void makeDir(String dirPath) {
		File file = new File(dirPath);
		if (!file.exists()) {
			file.mkdirs();
		}
	}

	public static String getDeviceInfo(Context context) {
		try {
			org.json.JSONObject json = new org.json.JSONObject();
			android.telephony.TelephonyManager tm = (android.telephony.TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			String device_id = null;
			if (checkPermission(context, permission.READ_PHONE_STATE)) {
				device_id = tm.getDeviceId();
			}
			String mac = null;
			FileReader fstream = null;
			try {
				fstream = new FileReader("/sys/class/net/wlan0/address");
			} catch (FileNotFoundException e) {
				fstream = new FileReader("/sys/class/net/eth0/address");
			}
			BufferedReader in = null;
			if (fstream != null) {
				try {
					in = new BufferedReader(fstream, 1024);
					mac = in.readLine();
				} catch (IOException e) {
				} finally {
					if (fstream != null) {
						try {
							fstream.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					if (in != null) {
						try {
							in.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
			json.put("mac", mac);
			if (TextUtils.isEmpty(device_id)) {
				device_id = mac;
			}
			if (TextUtils.isEmpty(device_id)) {
				device_id = android.provider.Settings.Secure.getString(
						context.getContentResolver(),
						android.provider.Settings.Secure.ANDROID_ID);
			}
			json.put("device_id", device_id);
			return json.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static boolean checkPermission(Context context, String permission) {
		boolean result = false;
		if (Build.VERSION.SDK_INT >= 23) {
			try {
				Class<?> clazz = Class.forName("android.content.Context");
				Method method = clazz.getMethod("checkSelfPermission",
						String.class);
				int rest = (Integer) method.invoke(context, permission);
				if (rest == PackageManager.PERMISSION_GRANTED) {
					result = true;
				} else {
					result = false;
				}
			} catch (Exception e) {
				result = false;
			}
		} else {
			PackageManager pm = context.getPackageManager();
			if (pm.checkPermission(permission, context.getPackageName()) == PackageManager.PERMISSION_GRANTED) {
				result = true;
			}
		}
		return result;
	}

	public static PreferencesService getPreferencesService() {
        if(preferencesService==null){
            preferencesService = new PreferencesService(getContext());
        }
		return preferencesService;
	}

	public static NatureBinder getNatureBinder() {
		return natureBinder;
	}

	/*public static NetControl getNet() {
		return net;
	}*/

	/**
	 * 获取图片
	 * @param key “对应的key值”
	 * @param resID  默认资源id
	 * @return
	 */
	public static Drawable getPictureDrawable(String key,int resID) {
		String wallpaperStr = getPreferencesService().getValue(key,
				"");
		if (wallpaperStr == null || wallpaperStr.isEmpty()) {
			return getContext().getResources().getDrawable(
					resID);
		}
		Bitmap photoBmp = ImageUtils.getLoacalBitmap(wallpaperStr);
		Drawable drawable = new BitmapDrawable(photoBmp);
		return drawable;
	}

	/**
	 * 保存key value 值
	 * @param key
	 * @param path
	 */
	public static void setPicturePath(String key ,String path) {
		if (path == null) {
			return;
		}
		getPreferencesService().save(key, path);
	}

}
