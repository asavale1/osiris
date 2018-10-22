package com.osiris.ui.library;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.osiris.R;
import com.osiris.ui.LibraryFragmentListener;
import com.osiris.ui.PlayerControllerListener;
import com.osiris.ui.common.QueueRecyclerViewAdapter;
import com.osiris.ui.common.SongRecyclerViewAdapter;

import java.util.List;

public class QueueFragment extends Fragment {
    private View view;
    private static final String TAG = QueueFragment.class.getName();
    private LibraryFragmentListener libraryFragmentListener;
    private List<MediaSessionCompat.QueueItem> songs;
    private AppCompatImageButton playPauseButton;
    private PlayerControllerListener playerControllerListener;
    private TextView songTitle;


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        Log.i(TAG, "Is visible to user: " + isVisibleToUser);
        if(libraryFragmentListener != null){
            songs = libraryFragmentListener.getQueue();
            if(songs != null){
                Log.i(TAG, "Songs size: " + songs.size());
                buildUI();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "In onCreateView");

        view = inflater.inflate(R.layout.fragment_queue,
                container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(TAG, "In onViewCreated");

    }

    private void buildUI(){

        view.findViewById(R.id.linear_layout).setVisibility(View.VISIBLE);
        RecyclerView recyclerView = view.findViewById(R.id.songs_recycler_view);
        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        Log.i(TAG, "In build UI: " + songs.size());
        QueueRecyclerViewAdapter adapter = new QueueRecyclerViewAdapter(getContext(), songs, itemClickListener);
        recyclerView.setAdapter(adapter);

        view.findViewById(R.id.placeholder).setVisibility(View.GONE);

        playPauseButton = view.findViewById(R.id.button_play_pause);
        playPauseButton.setOnClickListener(controllerClickListener);

        view.findViewById(R.id.button_previous).setOnClickListener(controllerClickListener);
        view.findViewById(R.id.button_next).setOnClickListener(controllerClickListener);

        songTitle = view.findViewById(R.id.song_title);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof LibraryFragmentListener) {
            libraryFragmentListener = (LibraryFragmentListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implemenet LibraryFragmentListener");
        }

        if (context instanceof PlayerControllerListener) {
            playerControllerListener = (PlayerControllerListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implemenet PlayerControllerListener");
        }
    }

    private QueueRecyclerViewAdapter.ItemClickListener itemClickListener = new QueueRecyclerViewAdapter.ItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            libraryFragmentListener.playSongAt(position);
        }
    };

    View.OnClickListener controllerClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.button_play_pause:
                    playerControllerListener.onPlayPauseSong();
                    break;
                case R.id.button_next:
                    playerControllerListener.onSkipToNextSong();
                    break;
                case R.id.button_previous:
                    playerControllerListener.onSkipToPreviousSong();
                    break;
            }
        }
    };

    public void onMetadataChanged(MediaMetadataCompat metadata){
        songTitle.setText(metadata.getDescription().getTitle());
        songTitle.setSelected(true);
    }

    public void onPlaybackStateChanged(PlaybackStateCompat state){
        Log.i(TAG, "In onPlaybackStateChanged");
        if(state == null)
            return;

        Log.i(TAG, "playbackState is not null");

        playPauseButton.setPressed((state.getState() == PlaybackStateCompat.STATE_PLAYING));
    }
}
