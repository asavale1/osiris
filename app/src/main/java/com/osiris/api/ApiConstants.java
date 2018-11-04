package com.osiris.api;

public class ApiConstants {

    private static final String API_DOMAIN = "http://192.168.0.11:3000";

    public static String GET_SONG_URL(String fileUrl){ return API_DOMAIN + fileUrl; }
    static String SEARCH_URL(String searchQuery){ return API_DOMAIN + "/search?query=" + searchQuery; }

    static String GET_USER_PLAYLISTS(String userId){ return API_DOMAIN + "/playlists/user/" + userId; }
    static String CREATE_PLAYLIST(){ return API_DOMAIN + "/playlists"; }

    static String VERIFY_ACCOUNT(){ return API_DOMAIN + "/users/verify"; }

    static String ADD_SONG_TO_PLAYLIST(String playlistId){ return API_DOMAIN + "/playlists/" + playlistId + "/song"; }
    static String REMOVE_SONG_FROM_PLAYLIST(String playlistId){ return API_DOMAIN + "/playlists/" + playlistId + "/song"; }

    static String GET_PLAYLIST(String playlistId, boolean detailed){ return API_DOMAIN + "/playlists/"+ playlistId + "?detailed=" + detailed; }
    static String GET_ALBUM(String albumId, boolean detailed){ return API_DOMAIN + "/albums/"+ albumId + "?detailed=" + detailed; }

    static final String METHOD_GET = "GET";
    static final String METHOD_POST = "POST";
    static final String METHOD_PUT = "PUT";
    static final String METHOD_DELETE = "DELETE";
}
