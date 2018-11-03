package com.osiris.api;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.JsonObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class RESTClient extends AsyncTask<Void, Void, RESTClient.RESTResponse> {

    private static final String TAG = RESTClient.class.getName();

    private JsonObject requestBody;
    private String method;
    private String restUrl;

    public RESTClient(String restUrl, String method, JsonObject requestBody){
        this.restUrl = restUrl;
        this.method = method;
        this.requestBody = requestBody;
    }

    @Override
    protected RESTResponse doInBackground(Void... p) {
        RESTResponse restResponse;
        StringBuilder data = new StringBuilder();
        OutputStreamWriter writer;

        try{
            URL url = new URL(restUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method);
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

            if(requestBody != null){
                connection.setDoOutput(true);
                connection.connect();

                writer = new OutputStreamWriter(connection.getOutputStream());

                Log.i(TAG, requestBody.toString());
                writer.write(requestBody.toString());
                writer.flush();
                writer.close();

            }else{
                connection.connect();
            }


            InputStream inputStream;

            if (connection.getResponseCode() < 400) {
                inputStream = new BufferedInputStream(connection.getInputStream());
            }else{
                inputStream = new BufferedInputStream(connection.getErrorStream());
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while((line = reader.readLine()) != null){
                data.append(line);
            }
            inputStream.close();
            reader.close();

            restResponse = new RESTResponse(connection.getResponseCode(), data.toString());

            connection.disconnect();


        }catch(Exception e){

            e.printStackTrace();
            restResponse = new RESTResponse(500, e.getMessage());
        }

        return restResponse;
    }


    public class RESTResponse{
        private String data;
        private int status;

        RESTResponse(int status, String data){
            this.status = status;
            this.data = data;
        }

        public String getData(){ return this.data; }
        public int getStatus(){ return this.status; }

    }

}
