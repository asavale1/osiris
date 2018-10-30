package com.osiris.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.osiris.R;
import com.osiris.api.AddSongToPlaylistAsync;
import com.osiris.api.GetUserPlaylistsAsync;
import com.osiris.api.RESTClient;
import com.osiris.api.listeners.RESTCallbackListener;
import com.osiris.ui.common.PlaylistModel;
import com.osiris.ui.common.PlaylistRecyclerViewAdapter;
import com.osiris.utility.CacheManager;

import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class AddToPlaylistDialog extends Dialog {

    private final static String TAG = AddToPlaylistDialog.class.getName();

    private List<PlaylistModel> playlists = new ArrayList<>();
    private String songId, userId;
    private Activity activity;

    public AddToPlaylistDialog(Activity activity, String songId){
        super(activity);
        setContentView(R.layout.dialog_select_playlist);
        this.songId = songId;
        this.activity = activity;
        this.userId = CacheManager.getInstance(activity).readString(activity.getString(R.string.cache_user_id), "");

        preprocessUI();
    }

    private void preprocessUI(){

        new GetUserPlaylistsAsync(userId, new RESTCallbackListener() {
            @Override
            public void onComplete(RESTClient.RESTResponse response) {
                if(response.getStatus() == HttpsURLConnection.HTTP_OK){
                    try{
                        JsonParser parser = new JsonParser();
                        JsonArray jsonArray = parser.parse(response.getData()).getAsJsonArray();

                        Gson gson = new Gson();

                        for(int i = 0; i < jsonArray.size(); i++){
                            PlaylistModel playlist = new PlaylistModel();
                            playlist.setId(jsonArray.get(i).getAsJsonObject().get("_id").getAsString());
                            playlist.setTitle(jsonArray.get(i).getAsJsonObject().get("title").getAsString());
                            playlist.setUserId(jsonArray.get(i).getAsJsonObject().get("userId").getAsString());

                            String [] songs = gson.fromJson(jsonArray.get(i).getAsJsonObject().get("songs").getAsJsonArray(), String [].class);

                            playlist.setSongs(songs);
                            playlists.add(playlist);

                            setupUI();

                        }

                    }catch (IllegalStateException e){
                        e.printStackTrace();
                    }
                }
            }
        }).execute();
    }

    private void setupUI(){
        RecyclerView recyclerView = findViewById(R.id.playlists_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        PlaylistRecyclerViewAdapter adapter = new PlaylistRecyclerViewAdapter(getContext(), playlists, itemClickListener);
        recyclerView.setAdapter(adapter);
    }


    private PlaylistRecyclerViewAdapter.ItemClickListener itemClickListener = new PlaylistRecyclerViewAdapter.ItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            String playlistId = playlists.get(position).getId();

            JsonObject requestBody = new JsonObject();
            requestBody.addProperty("userId", userId);
            requestBody.addProperty("songId", songId);

            new AddSongToPlaylistAsync(playlistId, requestBody, new RESTCallbackListener() {
                @Override
                public void onComplete(RESTClient.RESTResponse response) {
                    Log.i(TAG, "Status: " + response.getStatus());
                    Log.i(TAG, "Body: " + response.getData());

                    JsonParser parser = new JsonParser();

                    if(response.getStatus() == HttpsURLConnection.HTTP_OK){
                        JsonObject jsonObject = parser.parse(response.getData()).getAsJsonObject();
                        Toast.makeText(activity, jsonObject.get("message").getAsString(), Toast.LENGTH_LONG).show();
                        dismiss();
                    }else{
                        JsonObject jsonObject = parser.parse(response.getData()).getAsJsonObject();
                        Toast.makeText(activity, jsonObject.get("error").getAsString(), Toast.LENGTH_LONG).show();
                    }

                }
            }).execute();
        }
    };
}
