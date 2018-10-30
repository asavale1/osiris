package com.osiris.api;

import com.osiris.api.listeners.RESTCallbackListener;

public class GetPlaylistAsync extends RESTClient {

    private RESTCallbackListener callbackListener;

    public GetPlaylistAsync(String playlistId, boolean detailed, RESTCallbackListener callbackListener) {
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
