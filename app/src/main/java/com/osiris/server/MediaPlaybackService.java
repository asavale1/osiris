package com.osiris.server;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaBrowserServiceCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import com.osiris.api.GetSongsAsync;
import com.osiris.api.listeners.GetSongsAsyncListener;

import java.util.ArrayList;
import java.util.List;

public class MediaPlaybackService extends MediaBrowserServiceCompat {

    private MediaSessionCompat mediaSession;
    private MusicLibrary musicLibrary;
    private MediaPlayerAdapter mediaPlayerAdapter;
    private final static String MY_MEDIA_ROOT_ID = "Osiris";
    private String apiRequestUrl;

    private static final String TAG = MediaPlaybackService.class.getName();

    @Override
    public void onCreate(){
        super.onCreate();

        Log.i(TAG, "In onCreate");

        musicLibrary = new MusicLibrary();


        mediaSession = new MediaSessionCompat(this, MediaPlaybackService.class.getSimpleName());
        mediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_QUEUE_COMMANDS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        PlaybackStateCompat.Builder stateBuilder = new PlaybackStateCompat.Builder().setActions(
                PlaybackStateCompat.ACTION_PLAY |
                        PlaybackStateCompat.ACTION_PLAY_PAUSE);

        mediaPlayerAdapter = new MediaPlayerAdapter(new MediaPlaybackListener());

        mediaSession.setPlaybackState(stateBuilder.build());
        mediaSession.setCallback(new MediaSessionCallback(MediaPlaybackService.this, mediaSession, mediaPlayerAdapter, musicLibrary));
        setSessionToken(mediaSession.getSessionToken());


    }

    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, Bundle rootHints){
        Log.i(TAG, "In onGetRoot");
        return new BrowserRoot(MY_MEDIA_ROOT_ID, null);
    }

    @Override
    public void onLoadChildren(@NonNull final String parentMediaId, @NonNull final Result<List<MediaBrowserCompat.MediaItem>> result){
        Log.i(TAG, "In onLoadChildren");

        if(apiRequestUrl != null){
            result.detach();

            new GetSongsAsync(apiRequestUrl, new GetSongsAsyncListener() {
                @Override
                public void gotSongs(String songs) {
                    Log.i(TAG, songs);

                    musicLibrary.buildLibrary(songs);

                    result.sendResult(musicLibrary.getMediaItems());
                }
            }).execute();
        }else{
            result.sendResult(null);
        }

    }

    @Override
    public void onTaskRemoved(Intent rootIntent){
        super.onTaskRemoved(rootIntent);
        stopSelf();
    }

    @Override
    public void onDestroy(){

        mediaPlayerAdapter.onStop();
        mediaSession.release();

    }

    public void setApiRequestUrl(String apiRequestUrl){
        this.apiRequestUrl = apiRequestUrl;
    }

    public class MediaPlaybackListener {
        public void onPlaybackStateChanged(PlaybackStateCompat state){
            Log.i(TAG, "In onPlaybackStateChanged");
            mediaSession.setPlaybackState(state);
        }
    }
}
