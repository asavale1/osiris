package com.osiris.ui;

import android.content.Context;
import android.os.Bundle;
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
import com.osiris.ui.library.BrowseFragment;
import com.osiris.ui.library.QueueFragment;

public class LibraryFragment extends Fragment {

    private LibraryFragmentListener libraryFragmentListener;
    //private List<SongModel> songs = new ArrayList<>();
    private View view;
    private String apiRequestUrl;
    private ViewPager viewPager;

    private static final String TAG = LibraryFragment.class.getName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_library,
                container, false);
        Log.i(TAG, "In onCreateView");
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        LibraryFragmentPagerAdapter adapter = new LibraryFragmentPagerAdapter(getActivity(), getChildFragmentManager());
        viewPager.setAdapter(adapter);
        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof PlayerControllerListener) {
            libraryFragmentListener = (LibraryFragmentListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implemenet LibraryFragmentListener");
        }
    }


    public void onMetadataChanged(MediaMetadataCompat metadata){
        Log.i(TAG, "In onSongMetadataChanged");
        int itemId = viewPager.getCurrentItem();
        switch (itemId){
            case 0:
                BrowseFragment browseFragment = (BrowseFragment) getChildFragmentManager().findFragmentById(R.id.viewpager);
                break;
            case 1:
                QueueFragment queueFragment = (QueueFragment) getChildFragmentManager().findFragmentById(R.id.viewpager);
                queueFragment.onMetadataChanged(metadata);
                break;

        }

    }

    public void onPlaybackStateChanged(PlaybackStateCompat state){

        int itemId = viewPager.getCurrentItem();
        switch (itemId){
            case 0:
                BrowseFragment browseFragment = (BrowseFragment) getChildFragmentManager().findFragmentById(R.id.viewpager);
                break;
            case 1:
                QueueFragment queueFragment = (QueueFragment) getChildFragmentManager().findFragmentById(R.id.viewpager);
                queueFragment.onPlaybackStateChanged(state);
                break;

        }
    }

    /*private void buildUI(){

        RecyclerView recyclerView = view.findViewById(R.id.songs_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        SongRecyclerViewAdapter adapter = new SongRecyclerViewAdapter(getContext(), songs, itemClickListener);
        recyclerView.setAdapter(adapter);
    }

    private SongRecyclerViewAdapter.ItemClickListener itemClickListener = new SongRecyclerViewAdapter.ItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            Log.i(TAG, "On item clicked");
            Log.i(TAG, songs.get(position).getTitle());
            //libraryFragmentListener.playSongAt(position);
            libraryFragmentListener.buildQueue(apiRequestUrl, position);
        }
    };*/
}
