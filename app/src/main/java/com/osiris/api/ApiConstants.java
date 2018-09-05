package com.osiris.api;

public class ApiConstants {

    public static final String API_DOMAIN = "http://172.31.98.249:3000";
    public static final String GET_ALL_SONGS = API_DOMAIN + "/songs";
    public static String GET_SONG_URL(String fileUrl){ return API_DOMAIN + fileUrl; }


    public static final String METHOD_GET = "GET";
}
