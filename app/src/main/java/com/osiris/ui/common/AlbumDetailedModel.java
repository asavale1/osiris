package com.osiris.ui.common;

import java.util.List;

public class AlbumDetailedModel {

    private String id;
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    private String title;
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    private List<SongModel> songs;
    public List<SongModel> getSongs() { return songs; }
    public void setSongs(List<SongModel> songs) { this.songs = songs; }

}
