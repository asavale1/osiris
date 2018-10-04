package com.osiris.api;

import com.google.gson.JsonObject;
import com.osiris.api.listeners.CreatePlaylistAsyncListener;

public class CreatePlaylistAsync extends RESTClient {

    private static final String TAG = CreatePlaylistAsync.class.getName();

    private CreatePlaylistAsyncListener callbackListener;

    public CreatePlaylistAsync(JsonObject playlistJson, CreatePlaylistAsyncListener callbackListener) {
        super(ApiConstants.CREATE_PLAYLIST(), "POST", playlistJson);
        this.callbackListener = callbackListener;
    }

    @Override
    protected void onPostExecute(RESTResponse restResponse)
    {
        super.onPostExecute(restResponse);
        callbackListener.onComplete(restResponse);
    }
}
