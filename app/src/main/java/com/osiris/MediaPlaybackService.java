package com.osiris;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaBrowserServiceCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class MediaPlaybackService extends MediaBrowserServiceCompat {

    private MediaSessionCompat mediaSession;
    private PlaybackStateCompat.Builder stateBuilder;
    private MediaPlayerAdapter mediaPlayerAdapter;
    private final static String MY_MEDIA_ROOT_ID = "OsirisSimple";

    private static final String TAG = MediaPlaybackService.class.getName();

    @Override
    public void onCreate(){
        super.onCreate();

        Log.i(TAG, "In onCreate");

        mediaSession = new MediaSessionCompat(this, MediaPlaybackService.class.getSimpleName());
        mediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_QUEUE_COMMANDS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        stateBuilder = new PlaybackStateCompat.Builder().setActions(
                PlaybackStateCompat.ACTION_PLAY |
                        PlaybackStateCompat.ACTION_PLAY_PAUSE);

        mediaPlayerAdapter = new MediaPlayerAdapter(new MediaPlaybackListener());
        mediaSession.setPlaybackState(stateBuilder.build());
        mediaSession.setCallback(new MediaSessionCallback(MediaPlaybackService.this, mediaSession, mediaPlayerAdapter));
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
        List<MediaBrowserCompat.MediaItem> mediaItems = new ArrayList<>();
        MediaMetadataCompat song1 = new MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, "1")
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, "Song1")
                .build();
        MediaMetadataCompat song2 = new MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, "2")
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, "Song2")
                .build();

        mediaItems.add(new MediaBrowserCompat.MediaItem(
                song1.getDescription(), MediaBrowserCompat.MediaItem.FLAG_PLAYABLE));
        mediaItems.add(new MediaBrowserCompat.MediaItem(
                song2.getDescription(), MediaBrowserCompat.MediaItem.FLAG_PLAYABLE));

        result.sendResult(mediaItems);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent){
        super.onTaskRemoved(rootIntent);
        stopSelf();
    }

    @Override
    public void onDestroy(){
        mediaSession.release();
    }

    public class MediaPlaybackListener {
        public void onPlaybackStateChanged(PlaybackStateCompat state){
            Log.i(TAG, "In onPlaybackStateChanged");
            mediaSession.setPlaybackState(state);
        }
    }
}
