package com.osiris.model;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class ModelParser {

    public static SongModel parseSongModelJson(JsonObject songJson){
        SongModel songModel = new SongModel();
        songModel.setId(songJson.get("_id").getAsString());
        songModel.setAlbumId(songJson.get("albumId").getAsString());
        songModel.setAlbumTitle(songJson.get("albumTitle").getAsString());
        songModel.setFileUrl(songJson.get("fileUrl").getAsString());
        songModel.setTitle(songJson.get("title").getAsString());

        return songModel;
    }



    public static AlbumModel parseAlbumModelJson(JsonObject albumJson){
        AlbumModel album = new AlbumModel();
        album.setId(albumJson.get("_id").getAsString());
        album.setTitle(albumJson.get("title").getAsString());

        Gson gson = new Gson();
        String [] songs = gson.fromJson(albumJson.get("songs").getAsJsonArray(), String [].class);

        album.setSongs(songs);

        return album;
    }

    public static AlbumDetailedModel parseAlbumDetailedModelJson(JsonObject albumDetailedJson){
        AlbumDetailedModel album = new AlbumDetailedModel();
        album.setId(albumDetailedJson.get("_id").getAsString());
        album.setTitle(albumDetailedJson.get("title").getAsString());

        List<SongModel> songs = new ArrayList<>();

        JsonArray songsJsonArray = albumDetailedJson.getAsJsonArray("songs");
        for(JsonElement songJsonElem : songsJsonArray){

            SongModel songModel = ModelParser.parseSongModelJson(songJsonElem.getAsJsonObject());
            songs.add(songModel);

        }
        album.setSongs(songs);
        return album;

    }

    public static PlaylistModel parsePlaylistModelJson(JsonObject playlistJson){
        PlaylistModel playlist = new PlaylistModel();
        playlist.setId(playlistJson.get("_id").getAsString());
        playlist.setTitle(playlistJson.get("title").getAsString());
        playlist.setUserId(playlistJson.get("userId").getAsString());

        Gson gson = new Gson();
        String [] songs = gson.fromJson(playlistJson.get("songs").getAsJsonArray(), String [].class);

        playlist.setSongs(songs);

        return playlist;
    }

    public static PlaylistDetailedModel parsePlaylistDetailedModelJson(JsonObject playlistDetailedJson){
        PlaylistDetailedModel playlist = new PlaylistDetailedModel();
        playlist.setId(playlistDetailedJson.get("_id").getAsString());
        playlist.setTitle(playlistDetailedJson.get("title").getAsString());
        playlist.setUserId(playlistDetailedJson.get("userId").getAsString());
        List<SongModel> songs = new ArrayList<>();

        JsonArray songsJsonArray = playlistDetailedJson.getAsJsonArray("songs");
        for(JsonElement songJsonElem : songsJsonArray){

            SongModel songModel = ModelParser.parseSongModelJson(songJsonElem.getAsJsonObject());
            songs.add(songModel);

        }
        playlist.setSongs(songs);
        return playlist;
    }
}
