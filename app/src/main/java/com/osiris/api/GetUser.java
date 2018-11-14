package com.osiris.api;

import com.osiris.api.listeners.RESTCallbackListener;
import com.osiris.constants.ApiConstants;

public class GetUser extends RESTClient {

    private RESTCallbackListener callbackListener;

    public GetUser(String userId, RESTCallbackListener callbackListener) {
        super(ApiConstants.GET_USER(userId), ApiConstants.METHOD_GET, null);
        this.callbackListener = callbackListener;
    }

    @Override
    protected void onPostExecute(RESTResponse restResponse)
    {
        super.onPostExecute(restResponse);
        callbackListener.onComplete(restResponse);
    }


}
