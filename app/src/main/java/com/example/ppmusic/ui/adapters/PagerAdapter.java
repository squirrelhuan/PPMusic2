/**
 * 
 */

package com.example.ppmusic.ui.adapters;

import java.util.ArrayList;
/*
import android.app.Fragment;
import android.app.FragmentManager;*/




import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.ppmusic.helpers.RefreshableFragment;

/**
 * @author Andrew Neal
 */
public class PagerAdapter extends FragmentPagerAdapter {

    private final ArrayList<Fragment> mFragments = new ArrayList<Fragment>();

    public PagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    public void addFragment(Fragment fragment) {
        mFragments.add(fragment);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

   /* @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }*/

    /**
     * This method update the fragments that extends the {@link RefreshableFragment} class
     */
    public void refresh() {
        for (int i = 0; i < mFragments.size(); i++) {
           /* if( mFragments.get(i) instanceof RefreshableFragment ) {
                ((RefreshableFragment)mFragments.get(i)).refresh();
            }*/
        }
    }

@Override
public android.support.v4.app.Fragment getItem(int arg0) {
	// TODO Auto-generated method stub
	return null;
}

}
