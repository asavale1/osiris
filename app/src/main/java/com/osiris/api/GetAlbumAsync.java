package com.osiris.api;

import com.osiris.api.listeners.RESTCallbackListener;

public class GetAlbumAsync extends RESTClient {

    private RESTCallbackListener callbackListener;

    public GetAlbumAsync(String albumId, boolean detailed, RESTCallbackListener callbackListener) {
        super(ApiConstants.GET_ALBUM(albumId, detailed), ApiConstants.METHOD_GET, null);
        this.callbackListener = callbackListener;
    }

    @Override
    protected void onPostExecute(RESTResponse restResponse)
    {
        super.onPostExecute(restResponse);
        callbackListener.onComplete(restResponse);
    }
}
