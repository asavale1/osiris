package com.osiris.api;

import com.osiris.api.listeners.RESTCallbackListener;

public class SearchSongsAsync extends RESTClient {
    private RESTCallbackListener callbackListener;

    public SearchSongsAsync(String searchQuery, RESTCallbackListener callbackListener){
        super(ApiConstants.SEARCH_URL(searchQuery), ApiConstants.METHOD_GET, null);
        this.callbackListener = callbackListener;
    }

    @Override
    protected void onPostExecute(RESTResponse restResponse)
    {
        super.onPostExecute(restResponse);
        callbackListener.onComplete(restResponse);
    }
}
