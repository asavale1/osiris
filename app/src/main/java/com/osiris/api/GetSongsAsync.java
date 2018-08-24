package com.osiris.api;

import android.os.AsyncTask;

import com.osiris.api.ApiConstants;
import com.osiris.api.listeners.GetSongsAsyncListener;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetSongsAsync extends AsyncTask<Void, Void, String> {

    GetSongsAsyncListener callbackListener;

    public GetSongsAsync(GetSongsAsyncListener callbackListener) {
        this.callbackListener = callbackListener;
    }

    @Override
    protected String doInBackground(Void... voids) {
        StringBuilder data = new StringBuilder();


        try{
            URL url = new URL(ApiConstants.GET_ALL_SONGS);
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
        callbackListener.gotSongs(result);
    }
}
