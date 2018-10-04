package com.osiris.api;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.JsonObject;
import com.osiris.api.listeners.CreatePlaylistAsyncListener;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class CreatePlaylistAsync extends AsyncTask<Void, Void, String> {

    private static final String TAG = CreatePlaylistAsync.class.getName();

    CreatePlaylistAsyncListener callbackListener;
    JsonObject playlistJson;

    public CreatePlaylistAsync(JsonObject playlistJson, CreatePlaylistAsyncListener callbackListener) {
        this.callbackListener = callbackListener;
        this.playlistJson = playlistJson;
    }

    @Override
    protected String doInBackground(Void... voids) {
        StringBuilder data = new StringBuilder();
        OutputStreamWriter writer;

        try{
            URL url = new URL(ApiConstants.CREATE_PLAYLIST());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Accept","*/*");
            connection.setRequestMethod("POST");

            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.connect();

            writer = new OutputStreamWriter(connection.getOutputStream());

            writer.write(playlistJson.toString());
            writer.flush();
            writer.close();

            InputStream in;

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
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

        }catch(Exception e){

            e.printStackTrace();
        }

        return data.toString();
    }

    @Override
    protected void onPostExecute(String result){
        callbackListener.createdPlaylist(result);
    }
}
