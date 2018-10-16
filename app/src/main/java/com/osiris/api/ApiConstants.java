package com.osiris.api;

public class ApiConstants {

    public static final String API_DOMAIN = "http://172.31.98.249:3000";

    public static final String GET_ALL_SONGS = API_DOMAIN + "/songs";
    public static String GET_SONG_URL(String fileUrl){ return API_DOMAIN + fileUrl; }

    public static String GET_USER_PLAYLISTS(String userId){ return API_DOMAIN + "/playlists/user/" + userId; }
    public static String CREATE_PLAYLIST(){ return API_DOMAIN + "/playlists"; }

    public static String VERIFY_ACCOUNT(){ return API_DOMAIN + "/users/verify"; }


    public static final String METHOD_GET = "GET";
    static final String METHOD_POST = "POST";
}
