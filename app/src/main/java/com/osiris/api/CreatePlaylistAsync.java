package com.osiris.api;

import com.google.gson.JsonObject;
import com.osiris.api.listeners.RESTCallbackListener;

public class CreatePlaylistAsync extends RESTClient {

    private static final String TAG = CreatePlaylistAsync.class.getName();

    private RESTCallbackListener callbackListener;

    public CreatePlaylistAsync(JsonObject playlistJson, RESTCallbackListener callbackListener) {
        super(ApiConstants.CREATE_PLAYLIST(), ApiConstants.METHOD_POST, playlistJson);
        this.callbackListener = callbackListener;
    }

    @Override
    protected void onPostExecute(RESTResponse restResponse)
    {
        super.onPostExecute(restResponse);
        callbackListener.onComplete(restResponse);
    }
}
