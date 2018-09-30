package com.osiris.ui;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.osiris.ui.library.BrowseFragment;
import com.osiris.ui.library.PlaylistFragment;
import com.osiris.ui.library.QueueFragment;

public class LibraryFragmentPagerAdapter extends FragmentPagerAdapter {

    private static final String TAG = LibraryFragmentPagerAdapter.class.getName();

    LibraryFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Log.i(TAG, "Position: " + position);
        switch (position){
            case 0:
                return new PlaylistFragment();
            case 1:
                return new BrowseFragment();
            case 2:
                return new QueueFragment();
            default:
                return new BrowseFragment();
        }
    }


    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        switch (position) {
            case 0:
                return "Your playlists";
            case 1:
                return "Browse";
            case 2:
                return "On Deck";
            default:
                return "Playlists";
        }
    }
}
