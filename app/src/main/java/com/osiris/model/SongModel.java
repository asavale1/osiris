package com.osiris.model;

public class SongModel {
    private String title, albumId, albumTitle, id, fileUrl;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getFileUrl() { return fileUrl; }
    void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }

    public String getAlbumId(){ return albumId; }
    void setAlbumId(String albumId){ this.albumId = albumId; }

    public String getAlbumTitle(){ return this.albumTitle; }
    void setAlbumTitle(String albumTitle){ this.albumTitle = albumTitle; }

}
