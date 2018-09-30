package com.osiris.api;

import android.os.AsyncTask;
import android.util.Log;

import com.osiris.api.listeners.GetUserPlaylistsAsyncListener;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetUserPlaylistsAsync extends AsyncTask<Void, Void, String> {


    GetUserPlaylistsAsyncListener callbackListener;
    private final static String TAG = GetUserPlaylistsAsync.class.getName();
    private String apiRequestUrl;

    public GetUserPlaylistsAsync(String apiRequestUrl, GetUserPlaylistsAsyncListener callbackListener) {
        this.callbackListener = callbackListener;
        this.apiRequestUrl = apiRequestUrl;
    }

    @Override
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
    }

    @Override
    protected void onPostExecute(String result){
        callbackListener.gotPlaylists(result);
    }

}
