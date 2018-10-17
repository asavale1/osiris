package com.osiris.api.listeners;

import com.osiris.api.RESTClient;

public interface AddSongToPlaylistAsyncListener {
    void onComplete(RESTClient.RESTResponse response);
}
