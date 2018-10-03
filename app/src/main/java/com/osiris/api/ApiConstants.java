package com.osiris.api;

public class ApiConstants {

    public static final String API_DOMAIN = "http://192.168.0.6:3000";

    public static final String GET_ALL_SONGS = API_DOMAIN + "/songs";
    public static String GET_SONG_URL(String fileUrl){ return API_DOMAIN + fileUrl; }

    public static String GET_USER_PLAYLISTS(String userId){ return API_DOMAIN + "/playlists/user/" + userId; }


    public static final String METHOD_GET = "GET";
}
