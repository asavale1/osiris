package com.osiris.server;

import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.util.Log;

import com.osiris.api.ApiConstants;
import com.osiris.ui.common.SongModel;


import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class MusicLibrary {
    private static final String TAG = MusicLibrary.class.getName();
    private TreeMap<String, MediaMetadataCompat> mediaItems = new TreeMap<>();

    public boolean addSongToMediaItems(SongModel songModel){
        if(mediaItems.get(songModel.getId()) == null){
            MediaMetadataCompat song = new MediaMetadataCompat.Builder()
                    .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, songModel.getId())
                    .putString(MediaMetadataCompat.METADATA_KEY_TITLE, songModel.getTitle())
                    .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, ApiConstants.GET_SONG_URL(songModel.getFileUrl())).build();

            mediaItems.put(songModel.getId(), song);
            return true;
        }

        return false;
    }

    /*public void buildLibrary(String songsString){
        try {
            JSONParser parser = new JSONParser();
            JSONArray songsJson = (JSONArray) parser.parse(songsString);


            for(Object obj : songsJson){
                JSONObject jsonObj = (JSONObject) obj;

                MediaMetadataCompat song = new MediaMetadataCompat.Builder()
                        .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, (String) jsonObj.get("_id"))
                        .putString(MediaMetadataCompat.METADATA_KEY_TITLE, (String) jsonObj.get("name"))
                        .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, ApiConstants.GET_SONG_URL((String) jsonObj.get("fileUrl"))).build();

                mediaItems.put((String) jsonObj.get("_id"), song);
            }

        }catch (ParseException e){
            e.printStackTrace();
        }
    }*/

    public List<MediaBrowserCompat.MediaItem> getMediaItems(){
        List<MediaBrowserCompat.MediaItem> result = new ArrayList<>();
        for(MediaMetadataCompat metadata : mediaItems.values()){
            result.add(
                    new MediaBrowserCompat.MediaItem(
                            metadata.getDescription(), MediaBrowserCompat.MediaItem.FLAG_PLAYABLE));
        }
        return result;
    }

    public MediaMetadataCompat getMetadata(String mediaId){
        return mediaItems.get(mediaId);
    }
}
