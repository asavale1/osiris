package com.osiris.ui.common;

import java.util.List;

public class PlaylistDetailedModel {
    private String id, title, userId;
    private List<SongModel> songs;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public List<SongModel> getSongs() { return songs; }
    public void setSongs(List<SongModel> songs) { this.songs = songs; }


}
