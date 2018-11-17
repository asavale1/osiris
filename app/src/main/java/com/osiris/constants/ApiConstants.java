package com.osiris.constants;

public abstract class ApiConstants {

    private static final String API_DOMAIN = "http://172.31.98.249:3000";

    public static String GET_SONG_URL(String fileUrl){ return API_DOMAIN + fileUrl; }
    public static String SEARCH_URL(String searchQuery){ return API_DOMAIN + "/search?query=" + searchQuery; }

    public static String GET_USER_PLAYLISTS(String userId){ return API_DOMAIN + "/playlists/user/" + userId; }
    public static String CREATE_PLAYLIST(){ return API_DOMAIN + "/playlists"; }

    public static String VERIFY_ACCOUNT(){ return API_DOMAIN + "/users/verify"; }
    public static String GET_USER(String userId){ return API_DOMAIN + "/users/" + userId; }
    public static String UPDATE_USER(String userId){ return API_DOMAIN + "/users/" + userId; }

    public static String ADD_SONG_TO_PLAYLIST(String playlistId){ return API_DOMAIN + "/playlists/" + playlistId + "/song"; }
    public static String REMOVE_SONG_FROM_PLAYLIST(String playlistId){ return API_DOMAIN + "/playlists/" + playlistId + "/song"; }

    public static String GET_PLAYLIST(String playlistId, boolean detailed){ return API_DOMAIN + "/playlists/"+ playlistId + "?detailed=" + detailed; }
    public static String GET_ALBUM(String albumId, boolean detailed){ return API_DOMAIN + "/albums/"+ albumId + "?detailed=" + detailed; }
    public static String DELETE_PLAYLIST(String playlistId){ return API_DOMAIN + "/playlists/" + playlistId; }

    public static final String METHOD_GET = "GET";
    public static final String METHOD_POST = "POST";
    public static final String METHOD_PUT = "PUT";
    public static final String METHOD_DELETE = "DELETE";

}
