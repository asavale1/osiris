package com.osiris.model;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.osiris.constants.JsonConstants;

import java.util.ArrayList;
import java.util.List;

public class ModelParser {

    private static final String TAG = ModelParser.class.getName();

    public static SongModel parseSongModelJson(JsonObject songJson){
        SongModel songModel = new SongModel();
        songModel.setId(songJson.get(JsonConstants._ID).getAsString());
        songModel.setAlbumId(songJson.get(JsonConstants.ALBUM_ID).getAsString());
        songModel.setAlbumTitle(songJson.get(JsonConstants.ALBUM_TITLE).getAsString());
        songModel.setFileUrl(songJson.get(JsonConstants.FILE_URL).getAsString());
        songModel.setTitle(songJson.get(JsonConstants.TITLE).getAsString());

        return songModel;
    }



    public static AlbumModel parseAlbumModelJson(JsonObject albumJson){
        AlbumModel album = new AlbumModel();
        album.setId(albumJson.get(JsonConstants._ID).getAsString());
        album.setTitle(albumJson.get(JsonConstants.TITLE).getAsString());

        Gson gson = new Gson();
        String [] songs = gson.fromJson(albumJson.get(JsonConstants.SONGS).getAsJsonArray(), String [].class);

        album.setSongs(songs);

        return album;
    }

    public static AlbumDetailedModel parseAlbumDetailedModelJson(JsonObject albumDetailedJson){
        AlbumDetailedModel album = new AlbumDetailedModel();
        album.setId(albumDetailedJson.get(JsonConstants._ID).getAsString());
        album.setTitle(albumDetailedJson.get(JsonConstants.TITLE).getAsString());

        List<SongModel> songs = new ArrayList<>();

        JsonArray songsJsonArray = albumDetailedJson.getAsJsonArray(JsonConstants.SONGS);
        for(JsonElement songJsonElem : songsJsonArray){

            SongModel songModel = ModelParser.parseSongModelJson(songJsonElem.getAsJsonObject());
            songs.add(songModel);

        }
        album.setSongs(songs);
        return album;

    }

    public static PlaylistModel parsePlaylistModelJson(JsonObject playlistJson){
        PlaylistModel playlist = new PlaylistModel();
        playlist.setId(playlistJson.get(JsonConstants._ID).getAsString());
        playlist.setTitle(playlistJson.get(JsonConstants.TITLE).getAsString());
        playlist.setUserId(playlistJson.get(JsonConstants.USER_ID).getAsString());
        playlist.setPrimary(playlistJson.get(JsonConstants.PRIMARY).getAsBoolean());

        Gson gson = new Gson();
        String [] songs = gson.fromJson(playlistJson.get(JsonConstants.SONGS).getAsJsonArray(), String [].class);

        playlist.setSongs(songs);

        return playlist;
    }

    public static PlaylistDetailedModel parsePlaylistDetailedModelJson(JsonObject playlistDetailedJson){
        PlaylistDetailedModel playlist = new PlaylistDetailedModel();
        playlist.setId(playlistDetailedJson.get(JsonConstants._ID).getAsString());
        playlist.setTitle(playlistDetailedJson.get(JsonConstants.TITLE).getAsString());
        playlist.setUserId(playlistDetailedJson.get(JsonConstants.USER_ID).getAsString());
        playlist.setPrimary(playlistDetailedJson.get(JsonConstants.PRIMARY).getAsBoolean());

        List<SongModel> songs = new ArrayList<>();

        JsonArray songsJsonArray = playlistDetailedJson.getAsJsonArray(JsonConstants.SONGS);
        for(JsonElement songJsonElem : songsJsonArray){

            SongModel songModel = ModelParser.parseSongModelJson(songJsonElem.getAsJsonObject());
            songs.add(songModel);

        }
        playlist.setSongs(songs);
        return playlist;
    }
}
