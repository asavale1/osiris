package com.osiris.api;

import com.google.gson.JsonObject;
import com.osiris.api.listeners.AddSongToPlaylistAsyncListener;

public class AddSongToPlaylistAsync extends RESTClient {

    private AddSongToPlaylistAsyncListener callbackListener;

    public AddSongToPlaylistAsync(String playlistId, JsonObject requestBody, AddSongToPlaylistAsyncListener callbackListener) {
        super(ApiConstants.ADD_SONG_TO_PLAYLIST(playlistId), ApiConstants.METHOD_PUT, requestBody);
        this.callbackListener = callbackListener;
    }

    @Override
    protected void onPostExecute(RESTResponse restResponse)
    {
        super.onPostExecute(restResponse);
        callbackListener.onComplete(restResponse);
    }
}