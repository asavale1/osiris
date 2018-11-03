package com.osiris.api;

import com.google.gson.JsonObject;
import com.osiris.api.listeners.RESTCallbackListener;

public class RemoveSongFromPlaylistAsync extends RESTClient {

    private RESTCallbackListener callbackListener;

    public RemoveSongFromPlaylistAsync(String playlistId, JsonObject requestBody, RESTCallbackListener callbackListener) {
        super(ApiConstants.REMOVE_SONG_FROM_PLAYLIST(playlistId), ApiConstants.METHOD_DELETE, requestBody);
        this.callbackListener = callbackListener;
    }

    @Override
    protected void onPostExecute(RESTResponse restResponse)
    {
        super.onPostExecute(restResponse);
        callbackListener.onComplete(restResponse);
    }
}
