package com.osiris.server;

import android.media.MediaPlayer;
import android.os.SystemClock;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import java.io.IOException;

public class MediaPlayerAdapter {

    private final static String TAG = MediaPlayerAdapter.class.getName();

    private MediaPlayer mediaPlayer;
    private MediaPlaybackService.MediaPlaybackListener mediaPlaybackListener;

    private String currentSongId;

    public MediaPlayerAdapter(MediaPlaybackService.MediaPlaybackListener mediaPlaybackListener){
        this.mediaPlaybackListener = mediaPlaybackListener;
    }


    private void initMediaPlayer(){
        Log.i(TAG, "In initMediaPlayer");
        if(mediaPlayer == null){
            Log.i(TAG, "media player is null");
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    Log.i(TAG, "In onCompletion");
                }
            });
        }
    }

    public void playFromMedia(MediaMetadataCompat mediaMetadata){
        Log.i(TAG, "In playFromMedia");
        Log.i(TAG, mediaMetadata.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI));

        String mediaId = mediaMetadata.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID);

        boolean mediaChanged = (currentSongId == null || !currentSongId.equals(mediaId));

        if(!mediaChanged){
            if(!isPlaying()){
                onPlay();
                return;
            }
        }else{
            if(mediaPlayer != null){
                mediaPlayer.release();
                mediaPlayer = null;
            }
        }

        initMediaPlayer();

        currentSongId = mediaId;

        try {
            mediaPlayer.setDataSource(mediaMetadata.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI));
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        onPlay();
    }

    private void onPlay(){
        Log.i(TAG, "In onPlay");
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            setNewState(PlaybackStateCompat.STATE_PLAYING);
        }
    }

    public void onPause(){
        if(mediaPlayer != null && mediaPlayer.isPlaying()){
            mediaPlayer.pause();
            setNewState(PlaybackStateCompat.STATE_PAUSED);
        }
    }

    private void setNewState(@PlaybackStateCompat.State int newPlayerState){
        Log.i(TAG, "In setNewState");
        final PlaybackStateCompat.Builder stateBuilder = new PlaybackStateCompat.Builder();
        stateBuilder.setActions(PlaybackStateCompat.ACTION_PLAY |
                PlaybackStateCompat.ACTION_PLAY_PAUSE);
        stateBuilder.setState(newPlayerState,
                mediaPlayer.getCurrentPosition(),
                1.0f,
                SystemClock.elapsedRealtime());
        mediaPlaybackListener.onPlaybackStateChanged(stateBuilder.build());
    }

    public boolean isPlaying(){ return mediaPlayer != null && mediaPlayer.isPlaying(); }

}
