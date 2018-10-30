package com.osiris.api;

import com.osiris.api.listeners.RESTCallbackListener;

public class GetUserPlaylistsAsync extends RESTClient {


    private RESTCallbackListener callbackListener;
    private final static String TAG = GetUserPlaylistsAsync.class.getName();

    public GetUserPlaylistsAsync(String userId, RESTCallbackListener callbackListener) {
        super(ApiConstants.GET_USER_PLAYLISTS(userId), ApiConstants.METHOD_GET, null);
        this.callbackListener = callbackListener;
    }

    /*@Override
    protected String doInBackground(Void... voids) {
        StringBuilder data = new StringBuilder();


        try{
            URL url = new URL(apiRequestUrl);
            Log.i(TAG, apiRequestUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(ApiConstants.METHOD_GET);
            connection.setInstanceFollowRedirects(false);
            connection.connect();

            InputStream in;
            if (connection.getResponseCode() < HttpURLConnection.HTTP_BAD_REQUEST) {
                in = new BufferedInputStream(connection.getInputStream());
            }else{
                in = new BufferedInputStream(connection.getErrorStream());
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            String line;
            while((line = reader.readLine()) != null){
                data.append(line);
            }
            in.close();
            reader.close();

            connection.disconnect();


        }catch (Exception e){
            e.printStackTrace();
        }

        return data.toString();
    }*/

    @Override
    protected void onPostExecute(RESTResponse restResponse)
    {
        super.onPostExecute(restResponse);
        callbackListener.onComplete(restResponse);
    }

}
