package com.osiris.server;

import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;


import com.google.gson.Gson;
import com.osiris.constants.BundleConstants;
import com.osiris.constants.MediaConstants;
import com.osiris.model.AlbumDetailedModel;
import com.osiris.model.PlaylistDetailedModel;
import com.osiris.model.SongModel;

import java.util.ArrayList;
import java.util.List;

public class MediaSessionCallback extends MediaSessionCompat.Callback {

    private MediaPlayerAdapter mediaPlayerAdapter;
    private MediaSessionCompat mediaSession;
    private MusicLibrary musicLibrary;

    private final List<MediaSessionCompat.QueueItem> playlist = new ArrayList<>();
    private int queueIndex = -1;
    private MediaMetadataCompat preparedMedia;


    private static final String TAG = MediaSessionCallback.class.getName();

    MediaSessionCallback(MediaSessionCompat mediaSession,
                         MediaPlayerAdapter mediaPlayerAdapter,
                         MusicLibrary musicLibrary){
        this.mediaSession = mediaSession;
        this.mediaPlayerAdapter = mediaPlayerAdapter;
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
            Log.i(TAG, "Prepared media is null");
            onPrepare();
        }

        Log.i(TAG, "Play from media");

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
            if(!isReadyToPlay()){ return; }

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
            if(!isReadyToPlay()){ return; }

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

        switch(command){
            case MediaConstants.COMMAND_PLAY_SONG_AT:
                this.queueIndex = extras.getInt(BundleConstants.QUEUE_INDEX);
                onPause();
                preparedMedia = null;
                onPlay();

                break;
            case MediaConstants.COMMAND_ADD_SONG_TO_QUEUE:

                String songModelJson = extras.getString(BundleConstants.SONG_MODEL);
                SongModel songModel = new Gson().fromJson(songModelJson, SongModel.class);
                if(musicLibrary.addSongToMediaItems(songModel))
                    onAddQueueItem(musicLibrary.getMediaItem(songModel.getId()).getDescription());
                break;
            case MediaConstants.COMMAND_ADD_PLAYLIST_TO_QUEUE:
                Log.i(TAG, "Add playlist to queue");
                String playlistJson = extras.getString(BundleConstants.PLAYLIST_MODEL);
                PlaylistDetailedModel playlistModel = new Gson().fromJson(playlistJson, PlaylistDetailedModel.class);

                if(musicLibrary.addPlaylistToMediaItems(playlistModel)){

                    onPause();

                    playlist.clear();
                    queueIndex = -1;

                    for(SongModel song : playlistModel.getSongs()){
                        onAddQueueItem(musicLibrary.getMediaItem(song.getId()).getDescription());
                    }
                }

                break;
            case MediaConstants.COMMAND_ADD_ALBUM_TO_QUEUE:
                String albumJson = extras.getString(BundleConstants.ALBUM_MODEL);
                AlbumDetailedModel albumModel = new Gson().fromJson(albumJson, AlbumDetailedModel.class);
                if(musicLibrary.addAlbumToMediaItems(albumModel)){
                    onPause();
                    playlist.clear();
                    queueIndex = -1;
                    for(SongModel song : albumModel.getSongs()){
                        onAddQueueItem(musicLibrary.getMediaItem(song.getId()).getDescription());
                    }
                }
                break;
            case MediaConstants.COMMAND_CLEAR_QUEUE:
                onStop();
                preparedMedia = null;
                queueIndex = -1;

                musicLibrary.clearQueue();
                playlist.clear();
                mediaSession.setQueue(playlist);
                if(cb != null)
                    cb.send(MediaConstants.RESULT_CODE_CLEAR_QUEUE, null);

                break;
        }
    }

    private boolean isReadyToPlay() {
        return (!playlist.isEmpty());
    }

}
