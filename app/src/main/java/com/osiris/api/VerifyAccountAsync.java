package com.osiris.api;

import com.google.gson.JsonObject;
import com.osiris.api.listeners.RESTCallbackListener;

public class VerifyAccountAsync extends RESTClient {

    private RESTCallbackListener callbackListener;

    public VerifyAccountAsync(JsonObject requestBody, RESTCallbackListener callbackListener) {
        super(ApiConstants.VERIFY_ACCOUNT(), ApiConstants.METHOD_POST, requestBody);
        this.callbackListener = callbackListener;
    }

    @Override
    protected void onPostExecute(RESTResponse restResponse)
    {
        super.onPostExecute(restResponse);
        callbackListener.onComplete(restResponse);
    }


}
