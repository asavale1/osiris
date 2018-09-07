package com.osiris.server;

import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.v4.media.MediaBrowserServiceCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;


import com.google.gson.Gson;
import com.osiris.ui.common.SongModel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MediaSessionCallback extends MediaSessionCompat.Callback {

    private MediaPlayerAdapter mediaPlayerAdapter;
    private MediaSessionCompat mediaSession;
    private MediaPlaybackService mediaPlaybackService;
    private MusicLibrary musicLibrary;

    private final List<MediaSessionCompat.QueueItem> playlist = new ArrayList<>();
    private int queueIndex = -1;
    private MediaMetadataCompat preparedMedia;


    private static final String TAG = MediaSessionCallback.class.getName();

    public MediaSessionCallback(MediaPlaybackService mediaPlaybackService,
                                MediaSessionCompat mediaSession,
                                MediaPlayerAdapter mediaPlayerAdapter,
                                MusicLibrary musicLibrary){
        this.mediaSession = mediaSession;
        this.mediaPlayerAdapter = mediaPlayerAdapter;
        this.mediaPlaybackService = mediaPlaybackService;
        this.musicLibrary = musicLibrary;
    }

    @Override
    public void onPlay() {
        Log.i(TAG, "In onPlay");


        if(!isReadyToPlay()){
            Log.i(TAG, "Playlist is empty, nothing to play");
            return;
        }

        if(preparedMedia == null){
            onPrepare();
        }

        mediaPlayerAdapter.playFromMedia(preparedMedia);
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

        String mediaId = queueItem.getDescription().getMediaId();
        preparedMedia = musicLibrary.getMetadata(mediaId);
        mediaSession.setMetadata(preparedMedia);

        if(!mediaSession.isActive()){
            mediaSession.setActive(true);
        }
    }

    @Override
    public void onSkipToNext() {
        Log.i(TAG, "In onSkipToNext");
        queueIndex = (++queueIndex % playlist.size());
        preparedMedia = null;

        if(mediaPlayerAdapter.isPlaying()){
            onPlay();
        }else{
            if(!isReadyToPlay()){
                return;
            }

            onPrepare();
            onPause();
        }
    }

    @Override
    public void onSkipToQueueItem (long queueIndex){
        Log.i(TAG, "In onSkipToQueueItem");
    }

    @Override
    public void onSkipToPrevious() {
        Log.i(TAG, "In onSkipToPrevious");

        queueIndex = queueIndex > 0 ? queueIndex - 1 : playlist.size() - 1;
        Log.i(TAG, "Queue index : " + queueIndex);
        preparedMedia = null;

        if(mediaPlayerAdapter.isPlaying()){
            onPlay();
        }else{
            if(!isReadyToPlay()){
                return;
            }

            onPrepare();
            onPause();
        }
    }

    @Override
    public void onStop() {
        Log.i(TAG, "In onStop");
        mediaPlayerAdapter.onStop();
        mediaSession.setActive(false);
    }

    @Override
    public void onCommand(String command, Bundle extras, ResultReceiver cb){

        Log.i(TAG, "In onCommand");
        switch(command){
            case "buildQueue":
                Log.i(TAG, "In onCommand: buildQueue");

                /*String apiRequestUrl = extras.getString("apiRequestUrl");
                int queueIndex = extras.getInt("queueIndex");

                if(apiRequestUrl != null){
                    mediaPlaybackService.setApiRequestUrl(apiRequestUrl);
                    this.queueIndex = queueIndex;

                    mediaPlaybackService.notifyChildrenChanged("Osiris");

                }*/

                break;
            case "addSongToQueue":
                String songModelJson = extras.getString("songModel");
                SongModel songModel = new Gson().fromJson(songModelJson, SongModel.class);
                if(musicLibrary.addSongToMediaItems(songModel))
                    onAddQueueItem(musicLibrary.getMediaItem(songModel.getId()).getDescription());
                break;
        }


    }

    private boolean isReadyToPlay() {
        return (!playlist.isEmpty());
    }

}
