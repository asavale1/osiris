package com.osiris.api.listeners;

import com.osiris.api.RESTClient;

public interface CreatePlaylistAsyncListener {
    void onComplete(RESTClient.RESTResponse response);
}
