package com.osiris.server;

import android.media.MediaPlayer;
import android.os.SystemClock;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import java.io.IOException;

class MediaPlayerAdapter {

    private final static String TAG = MediaPlayerAdapter.class.getName();

    private MediaPlayer mediaPlayer;
    private MediaPlaybackService.MediaPlaybackListener mediaPlaybackListener;

    private String currentSongId;
    private MediaMetadataCompat currentMediaMetadata;
    private int currentState;

    MediaPlayerAdapter(MediaPlaybackService.MediaPlaybackListener mediaPlaybackListener){
        this.mediaPlaybackListener = mediaPlaybackListener;
    }

    private void initMediaPlayer(){
        Log.i(TAG, "In initMediaPlayer");
        if(mediaPlayer == null){
            Log.i(TAG, "In initMediaPlayer : new media player");
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    setNewState(PlaybackStateCompat.STATE_PAUSED);
                    mediaPlaybackListener.onPlaybackCompleted();
                }
            });
        }
    }

    void playFromMedia(MediaMetadataCompat mediaMetadata){

        Log.i(TAG, "In playFromMedia");

        String mediaId = mediaMetadata.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID);

        boolean mediaChanged = (currentSongId == null || !currentSongId.equals(mediaId));

        if(!mediaChanged){
            Log.i(TAG, "In playFromMedia : media not changed");
            if(!isPlaying()){
                Log.i(TAG, "In playFromMedia : media is not playing");
                onPlay();
                return;
            }
        }else{
            Log.i(TAG, "In playFromMedia : media changed");
            if(mediaPlayer != null){
                Log.i(TAG, "In playFromMedia : meida player release");
                mediaPlayer.release();
                mediaPlayer = null;
            }
        }

        initMediaPlayer();

        currentSongId = mediaId;
        currentMediaMetadata = mediaMetadata;

        try {
            mediaPlayer.setDataSource(mediaMetadata.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI));
            Log.i(TAG, "In playFromMedia : start loading media");
            mediaPlayer.prepare();
            onPlay();
        } catch (IOException e) {
            e.printStackTrace();
            Log.i(TAG, "In playFromMedia : release media player");
            mediaPlayer.release();
            mediaPlayer = null;

        }
    }

    private void onPlay(){
        Log.i(TAG, "In onPlay");
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            Log.i(TAG, "In onPlay : call start");
            mediaPlayer.start();
            setNewState(PlaybackStateCompat.STATE_PLAYING);
        }
    }

    void onPause(){
        Log.i(TAG, "In onPause");

        if(mediaPlayer != null && mediaPlayer.isPlaying()){
            Log.i(TAG, "In onPause : Call pause");
            mediaPlayer.pause();
            setNewState(PlaybackStateCompat.STATE_PAUSED);
        }
    }

    void onStop(){
        Log.i(TAG, "In onStop");
        setNewState(PlaybackStateCompat.STATE_STOPPED);
        release();
    }

    private void release() {
        Log.i(TAG, "In onRelease");
        if (mediaPlayer != null) {
            Log.i(TAG, "In onRelease: release media player");
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    MediaMetadataCompat getCurrentMediaMetadata() {
        return currentMediaMetadata;
    }

    private void setNewState(@PlaybackStateCompat.State int newPlayerState){
        Log.i(TAG, "Set new state");
        currentState = newPlayerState;

        final long playbackPosition = mediaPlayer == null ? 0 : mediaPlayer.getCurrentPosition();

        final PlaybackStateCompat.Builder stateBuilder = new PlaybackStateCompat.Builder();
        stateBuilder.setActions(getAvailableActions());
        stateBuilder.setState(currentState,
                playbackPosition,
                1.0f,
                SystemClock.elapsedRealtime());
        mediaPlaybackListener.onPlaybackStateChanged(stateBuilder.build());
    }

    @PlaybackStateCompat.Actions
    private long getAvailableActions() {
        Log.i(TAG, "In getAvailableActions");
        long actions = PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID
                | PlaybackStateCompat.ACTION_PLAY_FROM_SEARCH
                | PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                | PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS;
        switch (currentState) {
            case PlaybackStateCompat.STATE_STOPPED:
                actions |= PlaybackStateCompat.ACTION_PLAY
                        | PlaybackStateCompat.ACTION_PAUSE;
                break;
            case PlaybackStateCompat.STATE_PLAYING:
                actions |= PlaybackStateCompat.ACTION_STOP
                        | PlaybackStateCompat.ACTION_PAUSE
                        | PlaybackStateCompat.ACTION_SEEK_TO;
                break;
            case PlaybackStateCompat.STATE_PAUSED:
                actions |= PlaybackStateCompat.ACTION_PLAY
                        | PlaybackStateCompat.ACTION_STOP;
                break;
            default:
                actions |= PlaybackStateCompat.ACTION_PLAY
                        | PlaybackStateCompat.ACTION_PLAY_PAUSE
                        | PlaybackStateCompat.ACTION_STOP
                        | PlaybackStateCompat.ACTION_PAUSE;
        }
        return actions;
    }

    boolean isPlaying(){ return mediaPlayer != null && mediaPlayer.isPlaying(); }

}
