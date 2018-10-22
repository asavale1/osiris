package com.osiris.api.listeners;

import com.osiris.api.RESTClient;

public interface GetPlaylistAsyncListener {
    void onComplete(RESTClient.RESTResponse response);
}
