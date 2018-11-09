package com.osiris.model;

public class PlaylistModel {
    private String id, title, userId, userUsername;
    private String [] songs;
    private boolean isPrimary;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String[] getSongs() { return songs; }
    public void setSongs(String[] songs) { this.songs = songs; }

    public boolean isPrimary() { return isPrimary; }
    public void setPrimary(boolean primary) { isPrimary = primary; }

    public String getUserUsername() { return userUsername; }
    public void setUserUsername(String userUsername) { this.userUsername = userUsername; }
}
