package com.osiris.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.osiris.MainActivity;
import com.osiris.R;

public class PlayerFragment extends Fragment {

    private final static String TAG = PlayerFragment.class.getName();

    private Button playButton, pauseButton;
    private PlayerControllerListener playerControllerListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_player,
                container, false);

        buildUI(view);
        return view;
    }

    private void buildUI(View view){
        playButton = view.findViewById(R.id.play_button);
        playButton.setOnClickListener(controllerClickListener);

        pauseButton = view.findViewById(R.id.pause_button);
        pauseButton.setOnClickListener(controllerClickListener);

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
                case R.id.play_button:
                    playerControllerListener.onPlaySong();
                    break;
                case R.id.pause_button:
                    playerControllerListener.onPauseSong();
                    break;
            }
        }
    };

    public void updateUI(String songTitle){

    }

}
