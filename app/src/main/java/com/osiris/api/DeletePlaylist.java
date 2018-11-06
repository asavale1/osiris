package com.osiris.api;

import com.osiris.api.listeners.RESTCallbackListener;

public class DeletePlaylist extends RESTClient {
    private RESTCallbackListener callbackListener;

    public DeletePlaylist(String playlistId, RESTCallbackListener callbackListener) {
        super(ApiConstants.DELETE_PLAYLIST(playlistId), ApiConstants.METHOD_DELETE, null);
        this.callbackListener = callbackListener;
    }

    @Override
    protected void onPostExecute(RESTResponse restResponse)
    {
        super.onPostExecute(restResponse);
        callbackListener.onComplete(restResponse);
    }
}
