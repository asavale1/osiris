package com.osiris.ui.library;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
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
import android.widget.Button;
import android.widget.TextView;

import com.osiris.R;
import com.osiris.ui.LibraryFragmentListener;
import com.osiris.ui.PlayerControllerListener;
import com.osiris.ui.common.QueueRecyclerViewAdapter;

import java.util.List;

import static android.support.constraint.Constraints.TAG;

public class QueueFragment extends Fragment {
    private View view;
    //private static final String TAG = QueueFragment.class.getName();
    private LibraryFragmentListener libraryFragmentListener;
    private List<MediaSessionCompat.QueueItem> songs;
    private AppCompatImageButton playPauseButton;
    private Button clearQueueButton;
    private PlayerControllerListener playerControllerListener;
    private TextView songTitle;


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {

        if(libraryFragmentListener != null){
            songs = libraryFragmentListener.getQueue();
            buildUI();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_queue,
                container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(libraryFragmentListener != null){
            songs = libraryFragmentListener.getQueue();
            buildUI();
        }

    }

    private void buildUI(){

        if(songs != null && songs.size() > 0){
            view.findViewById(R.id.linear_layout).setVisibility(View.VISIBLE);
            RecyclerView recyclerView = view.findViewById(R.id.songs_recycler_view);
            recyclerView.setVisibility(View.VISIBLE);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            QueueRecyclerViewAdapter adapter = new QueueRecyclerViewAdapter(getContext(), songs, itemClickListener);
            recyclerView.swapAdapter(adapter, false);

            view.findViewById(R.id.placeholder).setVisibility(View.GONE);

            clearQueueButton = view.findViewById(R.id.clear_queue);
            clearQueueButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    libraryFragmentListener.clearQueue(new ResultReceiver(new Handler()) {
                        @Override
                        protected void onReceiveResult(int resultCode, Bundle resultData) {
                            if(resultCode == 100){
                                songs = libraryFragmentListener.getQueue();
                                if(songs != null) {
                                    buildUI();
                                }
                            }
                        }
                    });
                }
            });

            playPauseButton = view.findViewById(R.id.button_play_pause);
            playPauseButton.setOnClickListener(controllerClickListener);

            view.findViewById(R.id.button_previous).setOnClickListener(controllerClickListener);
            view.findViewById(R.id.button_next).setOnClickListener(controllerClickListener);

            songTitle = view.findViewById(R.id.song_title);

            if(libraryFragmentListener != null){
                MediaMetadataCompat metadata = libraryFragmentListener.getCurrentMediaMetadata();
                if(metadata != null)
                    songTitle.setText(metadata.getDescription().getTitle());

                if(playerControllerListener.isMediaPlaying()){
                    playPauseButton.setImageResource(android.R.drawable.ic_media_pause);
                }else{
                    playPauseButton.setImageResource(android.R.drawable.ic_media_play);
                }

            }
        }else{
            view.findViewById(R.id.linear_layout).setVisibility(View.GONE);
            view.findViewById(R.id.placeholder).setVisibility(View.VISIBLE);
        }
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
        if(state == null)
            return;

        if(PlaybackStateCompat.STATE_PLAYING == state.getState()){
            playPauseButton.setImageResource(android.R.drawable.ic_media_pause);
        }else{
            playPauseButton.setImageResource(android.R.drawable.ic_media_play);
        }
    }
}
