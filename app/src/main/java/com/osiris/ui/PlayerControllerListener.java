package com.osiris.ui;

public interface PlayerControllerListener {

    void onPlayPauseSong();
    void onStopSong();
    void onPlaySong();
    void onPauseSong();
    void onSkipToNextSong();
    void onSkipToPreviousSong();
    boolean isMediaPlaying();
}
