package com.osiris.ui;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.osiris.ui.library.BrowseFragment;
import com.osiris.ui.library.QueueFragment;

public class LibraryFragmentPagerAdapter extends FragmentPagerAdapter {

    private Context context;
    private static final String TAG = LibraryFragmentPagerAdapter.class.getName();

    public LibraryFragmentPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        Log.i(TAG, "Position: " + position);
        switch (position){
            case 0:
                return new BrowseFragment();
            case 1:
                return new QueueFragment();
            default:
                return new BrowseFragment();
        }
    }


    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        switch (position) {
            case 0:
                return "Browse";
            case 1:
                return "On Deck";
            default:
                return "Browse";
        }
    }
}
