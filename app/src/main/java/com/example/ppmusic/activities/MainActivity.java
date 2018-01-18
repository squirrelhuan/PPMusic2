package com.example.ppmusic.activities;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.ppmusic.IApolloService;
import com.example.ppmusic.MyApp;
import com.example.ppmusic.NowPlayingCursor;
import com.example.ppmusic.R;
import com.example.ppmusic.adapter.FragmentAdapter;
import com.example.ppmusic.base.BaseActivity;
import com.example.ppmusic.fragment.MainFragment1;
import com.example.ppmusic.fragment.MainFragment2;
import com.example.ppmusic.fragment.MainFragment3;
import com.example.ppmusic.helpers.utils.MusicUtils;
import com.example.ppmusic.service.ApolloService;
import com.example.ppmusic.service.ServiceToken;

import java.util.ArrayList;
import java.util.List;

import static com.example.ppmusic.constants.Constants.Action_WallBackGround;
import static com.example.ppmusic.constants.Constants.Action_WellComeBackGround;

public class MainActivity extends BaseActivity implements
        ServiceConnection {

    private int TIME = 1000;
    private boolean isManual = false;
    private LinearLayout ll_bottom;

    ImageView iv_wallpager;

    ViewPager viewPager;

    private FragmentPagerAdapter fragmentAdapter;
    public RelativeLayout rl_root;

    int window_Width;
    int window_Height;
    WindowManager wm;

    private static MainActivity instance;

    public static MainActivity getInstance() {
        return instance;
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
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_main);
        instance = this;

        // Bind to Service
        mToken = MusicUtils.bindToService(this, this);

        IntentFilter filter = new IntentFilter();
        filter.addAction(ApolloService.META_CHANGED);

        //handler.postDelayed(runnable, TIME); // 每隔1s执行
        rl_root = (RelativeLayout) findViewById(R.id.rl_root);

        //PPSelectCDFragment.getInstance().initView();
        init();

        wm = this.getWindowManager();
        iv_wallpager = (ImageView) findViewById(R.id.iv_wallpager);
        iv_wallpager.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    @SuppressWarnings("deprecation")
                    public void onGlobalLayout() {
                        iv_wallpager.getViewTreeObserver()
                                .removeGlobalOnLayoutListener(this);
                        currentMatrix.set(iv_wallpager.getImageMatrix());
                        Matrix matrix = new Matrix();
                        matrix.set(currentMatrix);// 在没有进行移动之前的位置基础上进行移动

                        window_Width = wm.getDefaultDisplay().getWidth();
                        window_Height = wm.getDefaultDisplay().getHeight();
                        if (MyApp.getPictureDrawable("WallpaperUri", R.drawable.background_wall_001) != null) {
                            //BitmapDrawable bd = (BitmapDrawable) MyApp
                            //		.getPictureDrawable("WallpaperUri",R.drawable.background_wall_001);
                            //Bitmap bitmap = bd.getBitmap();
                            //if (bitmap != null) {
                            Rect rectTemp = iv_wallpager.getDrawable().getBounds();
                            float[] values = new float[9];
                            currentMatrix.getValues(values);
                            float width_c = rectTemp.width() * values[0];
                            float height_c = rectTemp.height() * values[4];
                            scaleX = (window_Width * 1.4f) / width_c;
                            scaleY = window_Height / height_c;
                            matrix.set(currentMatrix);
                            matrix.postScale(scaleX, scaleY, 0, 0);
                            iv_wallpager.setImageMatrix(matrix);
                            //}
                        }
                    }
                });
        Log.i("Tag", "creat...");
    }

    float scaleX;
    float scaleY;

    MainFragment1 mainFragment1;
    public MainFragment2 mainFragment2;
    MainFragment3 mainFragment3;
    //CopyOfFragment_SongList03 mainFragment4;
    private float ll_bottom_Y;
    private float ll_bottom_H;

    private void init() {
        //rl_for_cd = (RelativeLayout) findViewById(R.id.rl_for_cd);
        ll_bottom = (LinearLayout) findViewById(R.id.ll_bottom);
        ll_bottom.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    @SuppressWarnings("deprecation")
                    public void onGlobalLayout() {
                        // 第一次回调通常都是该控件从无到有的过程, 这正是我们本次demo想要跟踪
                        // 的时间节点, 因此只要发生一次, 就证明该Button被绘制过, 我们就得到了
                        // 我们想要的结论, 因此可以取消掉该监听.
                        //
                        // 另外注意一点: removeGlobalOnLayoutListener()方法在 API-1
                        // 就被引入了, 所以可以兼容任何版本的设备,
                        // 尤其是较低版本的设备, 该方法在API-16时被废弃, 从 API-16 开始, 官方建议使用
                        // removeOnGlobalLayoutListener()
                        // 方法来替代该方法, 废弃的原因我猜测就是因为命名不符合常规习惯吧, 因为查看 API-16 甚至
                        // API-23 的源码可知,
                        // 这个从 API-16 开始被废弃的方法的内部其实就是调用取代他的那个新方法的. 所以,
                        // removeOnGlobalLayoutListener()
                        // 方法只能使用在 API-16 及以后版本的设备上. 如果你的 minSdk 版本低于 API-16,
                        // 也就是你的 APP 还需要兼容较低
                        // 版本的设备 (例如: Android 2.3, 4.0), 那么还是要使用
                        // removeGlobalOnLayoutListener()方法, 也就是名称明显
                        // 不符合 Android 系统通常用于取消 Listener 所遵循的命名规则的那个方法.
                        ll_bottom.getViewTreeObserver()
                                .removeGlobalOnLayoutListener(this);
                        // 打印相关尺寸位置信息
                        ll_bottom_Y = ll_bottom.getY();
                        ll_bottom_H = ll_bottom.getHeight();
                    }
                });

        List<Fragment> fragments = new ArrayList<Fragment>();

        mainFragment1 = new MainFragment1();
        mainFragment2 = new MainFragment2();
        mainFragment3 = new MainFragment3();
        //mainFragment3 = new CopyOfFragment_SongList03();

        fragments.add(mainFragment1);
        fragments.add(mainFragment2);
        fragments.add(mainFragment3);

        fragmentAdapter = new FragmentAdapter(getSupportFragmentManager(),
                fragments);
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPager.setAdapter(fragmentAdapter);

        // 设置启动之后展示的fragment
        viewPager.setCurrentItem(1);
        viewPager.setOffscreenPageLimit(4);
        viewPager.addOnPageChangeListener(mOnPageChangeListener);
        viewPager.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                CurrentItem_A = viewPager.getCurrentItem();
                return false;
            }
        });
    }

    public class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Action_WallBackGround.equals(action)) {
                if (MyApp.getPictureDrawable("WallpaperUri", R.drawable.background_wall_001) != null) {
                    iv_wallpager.setImageDrawable(MyApp.getPictureDrawable("WallpaperUri", R.drawable.background_wall_001));
                    iv_wallpager.postInvalidate();
                }
            } else if (Action_WellComeBackGround.equals(action)) {
                System.out.println("sd卡已卸载");
            }
        }
    }

    int CurrentItem_A = 1;
    OnPageChangeListener mOnPageChangeListener = new OnPageChangeListener() {
        @Override
        public void onPageSelected(int arg0) {
            Log.d("CGQ", "滑动当前索引=" + arg0);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            Log.d("CGQ", "CurrentItem= " + viewPager.getCurrentItem()
                    + ",arg0=" + arg0 + ",arg1=" + arg1 + ",arg2=" + arg2);
            if (arg1 != 0) {
                if (arg1 == 1) {
                    CurrentItem_A = viewPager.getCurrentItem();
                }
                if (CurrentItem_A == 1) {
                    if (CurrentItem_A > arg0) {
                        scrollWallPaper(arg1);// 右滑动手指
                        arg1 = 1 - arg1;
                        Log.d("CGQ", "滑动1=" + arg1);
                    } else {
                        Log.d("CGQ", "滑动2=" + (2 - arg1));
                        scrollWallPaper(2 - (1 - arg1));
                    }
                    ll_bottom.setY(ll_bottom_Y + ll_bottom_H * arg1 * 2);
                } else if (arg2 != 0) {
                    // arg1 = 1 - arg1;
                    switch (CurrentItem_A) {
                        case 0:
                            scrollWallPaper(arg1);
                            ll_bottom.setY(ll_bottom_Y + ll_bottom_H * (1 - arg1)
                                    * 2);
                            break;
                        case 2:
                            scrollWallPaper(2 - (1 - arg1));
                            ll_bottom.setY(ll_bottom_Y + ll_bottom_H * arg1 * 2);
                            break;
                        default:
                            break;
                    }
                }
            } else {
                CurrentItem_A = viewPager.getCurrentItem();
            }
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
            // CurrentItem_A = viewPager.getCurrentItem();
            Log.d("CGQ", "滑动状态arg0=" + arg0);
        }
    };

    private Matrix matrix = new Matrix();
    private Matrix currentMatrix = new Matrix();

    private void scrollWallPaper(float scale) {
        float dx = iv_wallpager.getWidth() * (scale / 3) / 2;// 得到在x轴的移动距离
        matrix.set(iv_wallpager.getMatrix());// 在没有进行移动之前的位置基础上进行移动

        matrix.postScale(scaleX, scaleY, 0, 0);
        matrix.postTranslate(-dx, 0);
        iv_wallpager.setImageMatrix(matrix);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (MyApp.getPictureDrawable("WallpaperUri", R.drawable.background_wall_001) != null) {
            iv_wallpager.setImageDrawable(MyApp.getPictureDrawable("WallpaperUri", R.drawable.background_wall_001));
        }
        registerReceiver(new MyReceiver(), new IntentFilter(
                Action_WallBackGround));

        Log.i("Tag", "onResume...");
        Intent intent = getIntent();
        String action = intent.getAction();
        if (intent.ACTION_VIEW.equals(action)) {
            Log.i("Tag", "t——"+intent.getDataString());
            try {
                String str[] = intent.getDataString().split("/");
                int id = Integer.valueOf (str[str.length-1]);
                long[] l =  {id};
                MusicUtils.addToCurrentPlaylist(MainActivity.this,l);
                long[] k = MusicUtils.getQueue();
                int index = 0;
                for(int i=0;i<k.length;i++){
                    if(k[i]==id){
                        index = i;
                    }
                }
                MusicUtils.playAll(MainActivity.this,k,index);
                intent.setAction("none");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        MusicUtils.unbindFromService(mToken);
    }

    private long isExitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (viewPager.getCurrentItem() != 1) {
                viewPager.setCurrentItem(1);
                return true;
            }
            if (System.currentTimeMillis() - isExitTime >= 600) {
                // showToast("再次点击退出程序");
                isExitTime = System.currentTimeMillis();
            } else {
                // stopService(new Intent(this, LocationService.class));
                // super.onBackPressed();
                // 在activity中调用 moveTaskToBack (boolean nonRoot)方法即可将activity
                // 退到后台，注意不是finish()退出。
                // 参数为false代表只有当前activity是task根，指应用启动的第一个activity时，才有效;
                moveTaskToBack(true);
                // finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


}
