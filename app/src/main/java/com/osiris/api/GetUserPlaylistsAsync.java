package com.osiris.api;

import com.osiris.api.listeners.RESTCallbackListener;
import com.osiris.constants.ApiConstants;

public class GetUserPlaylistsAsync extends RESTClient {

    private RESTCallbackListener callbackListener;

    public GetUserPlaylistsAsync(String userId, RESTCallbackListener callbackListener) {
        super(ApiConstants.GET_USER_PLAYLISTS(userId), ApiConstants.METHOD_GET, null);
        this.callbackListener = callbackListener;
    }

    @Override
    protected void onPostExecute(RESTResponse restResponse)
    {
        super.onPostExecute(restResponse);
        callbackListener.onComplete(restResponse);
    }

}
