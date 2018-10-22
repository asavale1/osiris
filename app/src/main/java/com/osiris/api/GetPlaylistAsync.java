package com.osiris.api;

import com.google.gson.JsonObject;
import com.osiris.api.listeners.GetPlaylistAsyncListener;

public class GetPlaylistAsync extends RESTClient {

    private GetPlaylistAsyncListener callbackListener;

    public GetPlaylistAsync(String playlistId, boolean detailed, GetPlaylistAsyncListener callbackListener) {
        super(ApiConstants.GET_PLAYLIST(playlistId, detailed), ApiConstants.METHOD_GET, null);
        this.callbackListener = callbackListener;
    }

    @Override
    protected void onPostExecute(RESTResponse restResponse)
    {
        super.onPostExecute(restResponse);
        callbackListener.onComplete(restResponse);
    }
}
