package com.osiris.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.osiris.R;
import com.osiris.ui.library.QueueFragment;

public class LibraryFragment extends Fragment {

    private ViewPager viewPager;

    private static final String TAG = LibraryFragment.class.getName();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_library,
                container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewPager = view.findViewById(R.id.viewpager);
        LibraryFragmentPagerAdapter adapter = new LibraryFragmentPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(adapter);
        TabLayout tabLayout = view.findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if ( !(context instanceof PlayerControllerListener) ) {
            throw new ClassCastException(context.toString()
                    + " must implemenet LibraryFragmentListener");
        }
    }


    public void onMetadataChanged(MediaMetadataCompat metadata){
        Log.i(TAG, "In onSongMetadataChanged");
        int itemId = viewPager.getCurrentItem();
        switch (itemId){
            case 2:
                QueueFragment queueFragment = (QueueFragment) getChildFragmentManager().findFragmentById(R.id.viewpager);
                assert queueFragment != null;
                queueFragment.onMetadataChanged(metadata);
                break;

        }

    }

    public void onPlaybackStateChanged(PlaybackStateCompat state){

        int itemId = viewPager.getCurrentItem();
        switch (itemId){
            case 2:
                QueueFragment queueFragment = (QueueFragment) getChildFragmentManager().findFragmentById(R.id.viewpager);
                assert queueFragment != null;
                queueFragment.onPlaybackStateChanged(state);
                break;

        }
    }

}
