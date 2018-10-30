package com.osiris.api.listeners;

import com.osiris.api.RESTClient;

public interface RESTCallbackListener {
    void onComplete(RESTClient.RESTResponse response);
}
