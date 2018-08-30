package com.osiris.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v7.widget.AppCompatImageButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.osiris.R;

public class PlayerFragment extends Fragment {

    private final static String TAG = PlayerFragment.class.getName();

    private PlayerControllerListener playerControllerListener;
    private AppCompatImageButton playPauseButton;
    private TextView songTitle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_player,
                container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        buildUI(view);
    }

    private void buildUI(View view){
        playPauseButton = view.findViewById(R.id.button_play_pause);
        playPauseButton.setOnClickListener(controllerClickListener);

        AppCompatImageButton skipToNextButton = view.findViewById(R.id.button_next);
        skipToNextButton.setOnClickListener(controllerClickListener);

        AppCompatImageButton skipToPreviousButton = view.findViewById(R.id.button_previous);
        skipToPreviousButton.setOnClickListener(controllerClickListener);

        songTitle = view.findViewById(R.id.song_title);

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof PlayerControllerListener) {
            playerControllerListener = (PlayerControllerListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implemenet PlayerControllerListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        playerControllerListener = null;
    }


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

    public void updateUI(MediaMetadataCompat metadata){
        songTitle.setText(metadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE));
    }

    public void updatePlayPauseButton(boolean showPlay){
        playPauseButton.setPressed(showPlay);
    }

}
