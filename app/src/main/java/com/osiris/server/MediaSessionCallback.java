package com.osiris.server;

import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.v4.media.MediaBrowserServiceCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class MediaSessionCallback extends MediaSessionCompat.Callback {

    private MediaPlayerAdapter mediaPlayerAdapter;
    private MediaSessionCompat mediaSession;
    private final List<MediaSessionCompat.QueueItem> playlist = new ArrayList<>();
    private int queueIndex = -1;
    private MediaBrowserServiceCompat mediaBrowserService;

    private static final String TAG = MediaSessionCallback.class.getName();

    public MediaSessionCallback(MediaBrowserServiceCompat mediaBrowserService, MediaSessionCompat mediaSession, MediaPlayerAdapter mediaPlayerAdapter){
        this.mediaSession = mediaSession;
        this.mediaPlayerAdapter = mediaPlayerAdapter;
        this.mediaBrowserService = mediaBrowserService;
    }

    @Override
    public void onPlay() {
        Log.i(TAG, "In onPlay");


        mediaPlayerAdapter.playSong("1");
    }

    @Override
    public void onPause(){
        Log.i(TAG, "In onPause");
        mediaPlayerAdapter.onPause();
    }

    @Override
    public void onAddQueueItem(MediaDescriptionCompat mediaDescription){
        Log.i(TAG, "In onAddQueueItem");
        queueIndex = (queueIndex == -1) ? 0 : queueIndex;
        playlist.add(new MediaSessionCompat.QueueItem(mediaDescription, mediaDescription.hashCode()));
        mediaSession.setQueue(playlist);
    }

    @Override
    public void onPrepare(){
        Log.i(TAG, "In onPrepare");
        if(queueIndex < 0 && playlist.isEmpty()){
            return;
        }

        MediaSessionCompat.QueueItem queueItem = playlist.get(queueIndex);

        if(queueItem.getDescription().getMediaId() == null){
            Log.i(TAG, "media id is null");
        }

        MediaMetadataCompat.Builder builder = new MediaMetadataCompat.Builder();
        builder.putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, queueItem.getDescription().getMediaId());
        builder.putString(MediaMetadataCompat.METADATA_KEY_TITLE, queueItem.getDescription().getTitle().toString());

        mediaSession.setMetadata(builder.build());
    }

    @Override
    public void onCommand(String command, Bundle extras, ResultReceiver cb){

        Log.i(TAG, "In onCommand");
        mediaBrowserService.notifyChildrenChanged("Osiris");

    }

}
