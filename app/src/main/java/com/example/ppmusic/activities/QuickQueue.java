/**
 * 
 */

package com.example.ppmusic.activities;

import android.app.Activity;
import android.media.AudioManager;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.MediaStore.Audio;
import android.view.WindowManager;

import com.example.ppmusic.R;
import com.example.ppmusic.ui.fragment.grid.PPQuickQueueFragment;
import com.example.ppmusic.ui.fragment.grid.QuickQueueFragment;

import static com.example.ppmusic.constants.Constants.MIME_TYPE;
import static com.example.ppmusic.constants.Constants.PLAYLIST_QUEUE;

/**
 * 快速查找
 * @author Andrew Neal
 */
public class QuickQueue extends Activity {

    @Override
    protected void onCreate(Bundle icicle) {
        // This needs to be called first
        super.onCreate(icicle);// 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Control Media volume
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        Bundle bundle = new Bundle();
        bundle.putString(MIME_TYPE, Audio.Playlists.CONTENT_TYPE);
        bundle.putLong(BaseColumns._ID, PLAYLIST_QUEUE);
        getFragmentManager().beginTransaction()
               .replace(android.R.id.content, new PPQuickQueueFragment(bundle)).commit();
    }

    @Override
    public void finish() {
        super.finish();
        //设置切换动画，从右边进入，左边退出
        overridePendingTransition(R.anim.bottom_up, R.anim.bottom_down);
    }

}
