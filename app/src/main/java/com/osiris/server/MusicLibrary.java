package com.osiris.server;

import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.util.Log;

import com.osiris.api.ApiConstants;
import com.osiris.model.PlaylistDetailedModel;
import com.osiris.model.SongModel;


import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

class MusicLibrary {
    private static final String TAG = MusicLibrary.class.getName();
    private TreeMap<String, MediaMetadataCompat> mediaItems = new TreeMap<>();

    boolean addSongToMediaItems(SongModel songModel){
        if(mediaItems.get(songModel.getId()) == null){

            MediaMetadataCompat song = new MediaMetadataCompat.Builder()
                    .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, songModel.getId())
                    .putString(MediaMetadataCompat.METADATA_KEY_TITLE, songModel.getTitle())
                    .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, songModel.getAlbum())
                    .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, ApiConstants.GET_SONG_URL(songModel.getFileUrl())).build();

            mediaItems.put(songModel.getId(), song);
            return true;
        }

        return false;
    }

    boolean addPlaylistToMediaItems(PlaylistDetailedModel playlist){
        try {

            mediaItems.clear();

            for (SongModel songModel : playlist.getSongs()) {
                if (mediaItems.get(songModel.getId()) == null) {
                    MediaMetadataCompat song = new MediaMetadataCompat.Builder()
                            .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, songModel.getId())
                            .putString(MediaMetadataCompat.METADATA_KEY_TITLE, songModel.getTitle())
                            .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, songModel.getAlbum())
                            .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, ApiConstants.GET_SONG_URL(songModel.getFileUrl())).build();

                    mediaItems.put(songModel.getId(), song);
                }
            }

            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    MediaBrowserCompat.MediaItem getMediaItem(String id){
        return new MediaBrowserCompat.MediaItem(
                mediaItems.get(id).getDescription(), MediaBrowserCompat.MediaItem.FLAG_PLAYABLE);
    }

    List<MediaBrowserCompat.MediaItem> getMediaItems(){
        Log.i(TAG, "In getMediaItems Size: " + mediaItems.size());
        List<MediaBrowserCompat.MediaItem> result = new ArrayList<>();
        for(MediaMetadataCompat metadata : mediaItems.values()){
            result.add(
                    new MediaBrowserCompat.MediaItem(
                            metadata.getDescription(), MediaBrowserCompat.MediaItem.FLAG_PLAYABLE));
        }
        return result;
    }

    MediaMetadataCompat getMetadata(String mediaId){
        return mediaItems.get(mediaId);
    }
}
