
package com.example.ppmusic.ui.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.AnimationDrawable;
import android.os.RemoteException;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;

import com.example.ppmusic.R;
import com.example.ppmusic.fragment.MainFragment3;
import com.example.ppmusic.helpers.utils.MusicUtils;
import com.example.ppmusic.views.ViewHolderList;

import java.lang.ref.WeakReference;

/**
 * @author Andrew Neal
 */
public class TrackAdapter extends SimpleCursorAdapter {

    private AnimationDrawable mPeakOneAnimation, mPeakTwoAnimation;

    private WeakReference<ViewHolderList> holderReference;

    public TrackAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
    }

    /**
     * Used to quickly our the ContextMenu
     */
    private final View.OnClickListener showContextMenu = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            v.showContextMenu();
        }
    };
    @SuppressWarnings("ResourceType")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View view = super.getView(position, convertView, parent);

        Cursor mCursor = (Cursor) getItem(position);
        // ViewHolderList
        ViewHolderList viewholder;

        if (view != null) {

            viewholder = new ViewHolderList(view);
            holderReference = new WeakReference<ViewHolderList>(viewholder);
            view.setTag(holderReference.get());

        } else {
            viewholder = (ViewHolderList)convertView.getTag();
        }

        // Track name
        String trackName = mCursor.getString(MainFragment3.mTitleIndex);
        viewholder.mViewHolderLineOne.setText(trackName);

        // Artist name
        String artistName = mCursor.getString(MainFragment3.mArtistIndex);
        holderReference.get().mViewHolderLineTwo.setText(artistName);

        // Hide the album art
        holderReference.get().mViewHolderImage.setVisibility(View.GONE);

        holderReference.get().mQuickContext.setOnClickListener(showContextMenu);

        // Now playing indicator
        long currentaudioid = MusicUtils.getCurrentAudioId();
        long audioid = mCursor.getLong(MainFragment3.mMediaIdIndex);
        if (currentaudioid == audioid) {
            holderReference.get().mPeakOne.setImageResource(R.anim.peak_meter_1);
            holderReference.get().mPeakTwo.setImageResource(R.anim.peak_meter_2);
            mPeakOneAnimation = (AnimationDrawable)holderReference.get().mPeakOne.getDrawable();
            mPeakTwoAnimation = (AnimationDrawable)holderReference.get().mPeakTwo.getDrawable();
            try {
                if (MusicUtils.mService.isPlaying()) {
                    mPeakOneAnimation.start();
                    mPeakTwoAnimation.start();
                } else {
                    mPeakOneAnimation.stop();
                    mPeakTwoAnimation.stop();
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else {
            holderReference.get().mPeakOne.setImageResource(0);
            holderReference.get().mPeakTwo.setImageResource(0);
        }
        return view;
    }
}