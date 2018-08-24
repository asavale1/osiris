package com.osiris.server;

import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.util.Log;

import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.List;

public class MusicLibrary {
    private static final String TAG = MusicLibrary.class.getName();
    private List<MediaBrowserCompat.MediaItem> mediaItems = new ArrayList<>();

    public void buildLibrary(String songsString){
        try {
            JSONParser parser = new JSONParser();
            JSONArray songsJson = (JSONArray) parser.parse(songsString);

            for(Object obj : songsJson){
                JSONObject jsonObj = (JSONObject) obj;

                MediaMetadataCompat song = new MediaMetadataCompat.Builder()
                        .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, (String) jsonObj.get("_id"))
                        .putString(MediaMetadataCompat.METADATA_KEY_TITLE, (String) jsonObj.get("name")).build();

                mediaItems.add(new MediaBrowserCompat.MediaItem(
                        song.getDescription(), MediaBrowserCompat.MediaItem.FLAG_PLAYABLE));
            }

        }catch (ParseException e){
            e.printStackTrace();
        }
    }

    public List<MediaBrowserCompat.MediaItem> getMediaItems(){
        return this.mediaItems;
    }
}
