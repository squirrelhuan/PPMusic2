
package com.example.ppmusic.ui.adapters;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;

import com.example.ppmusic.R;
import com.example.ppmusic.cache.ImageInfo;
import com.example.ppmusic.ui.fragment.grid.PPQuickQueueFragment;
import com.example.ppmusic.ui.fragment.grid.QuickQueueFragment;
import com.example.ppmusic.views.ViewHolderQueue;
import com.squareup.picasso.Picasso;

import java.lang.ref.WeakReference;

import static com.example.ppmusic.constants.Constants.SIZE_THUMB;
import static com.example.ppmusic.constants.Constants.SRC_FIRST_AVAILABLE;
import static com.example.ppmusic.constants.Constants.TYPE_ALBUM;
import static com.example.ppmusic.constants.Constants.TYPE_ARTIST;

/**
 * @author Andrew Neal
 */
public class PPQuickQueueAdapter extends SimpleCursorAdapter {

    private WeakReference<ViewHolderQueue> holderReference;

    private Context mContext;

    public PPQuickQueueAdapter(Context context, int layout, Cursor c, String[] from, int[] to,
                               int flags) {
        super(context, layout, c, from, to, flags);
    	mContext = context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final View view = super.getView(position, convertView, parent);

        Cursor mCursor = (Cursor) getItem(position);
        // ViewHolderQueue
        final ViewHolderQueue viewholder;

        if (view != null) {

            viewholder = new ViewHolderQueue(view);
            holderReference = new WeakReference<ViewHolderQueue>(viewholder);
            view.setTag(holderReference.get());

        } else {
            viewholder = (ViewHolderQueue)convertView.getTag();
        }

        // Artist Name
        String artistName = mCursor.getString(PPQuickQueueFragment.mArtistIndex);

        // Album name
        String albumName = mCursor.getString(PPQuickQueueFragment.mAlbumIndex);

        // Track name
        String trackName = mCursor.getString(PPQuickQueueFragment.mTitleIndex);
        holderReference.get().mTrackName.setText(trackName);

        // Album ID
        String albumId = mCursor.getString(QuickQueueFragment.mAlbumIdIndex);

        ImageInfo mInfo = new ImageInfo();
        mInfo.type = TYPE_ALBUM;
        mInfo.size = SIZE_THUMB;
        mInfo.source = SRC_FIRST_AVAILABLE;
        mInfo.data = new String[]{ albumId , artistName, albumName };        
       // mImageProvider.loadImage( viewholder.mAlbumArt, mInfo );
        Picasso.with(mContext).load(mInfo.type).placeholder(R.drawable.ic_launcher_cd).into(viewholder.mAlbumArt);

        mInfo = new ImageInfo();
        mInfo.type = TYPE_ARTIST;
        mInfo.size = SIZE_THUMB;
        mInfo.source = SRC_FIRST_AVAILABLE;
        mInfo.data = new String[]{ artistName };
        //mImageProvider.loadImage( viewholder.mArtistImage, mInfo );
        Picasso.with(mContext).load(mInfo.source).placeholder(R.drawable.ic_launcher_cd).into(viewholder.mArtistImage);
        return view;
    }
}
