package com.osiris.model;

public class SongModel {
    private String title, album, id, fileUrl;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getFileUrl() { return fileUrl; }
    void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }

    public String getAlbum(){ return album; }
    void setAlbum(String album){ this.album = album; }

}
