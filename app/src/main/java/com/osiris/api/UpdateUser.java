package com.osiris.api;

import com.google.gson.JsonObject;
import com.osiris.api.listeners.RESTCallbackListener;
import com.osiris.constants.ApiConstants;

public class UpdateUser extends RESTClient {
    private RESTCallbackListener callbackListener;

    public UpdateUser(String userId, JsonObject userJson, RESTCallbackListener callbackListener) {
        super(ApiConstants.UPDATE_USER(userId), ApiConstants.METHOD_PUT, userJson);
        this.callbackListener = callbackListener;
    }

    @Override
    protected void onPostExecute(RESTResponse restResponse)
    {
        super.onPostExecute(restResponse);
        callbackListener.onComplete(restResponse);
    }
}
