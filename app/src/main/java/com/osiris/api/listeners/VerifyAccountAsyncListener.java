package com.osiris.api.listeners;

import com.osiris.api.RESTClient;

public interface VerifyAccountAsyncListener {
    void onComplete(RESTClient.RESTResponse response);
}
