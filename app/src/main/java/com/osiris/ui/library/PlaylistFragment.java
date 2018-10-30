package com.osiris.ui.library;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.osiris.MainActivity;
import com.osiris.R;
import com.osiris.api.ApiConstants;
import com.osiris.api.GetUserPlaylistsAsync;
import com.osiris.api.RESTClient;
import com.osiris.api.listeners.GetUserPlaylistsAsyncListener;
import com.osiris.api.listeners.RESTCallbackListener;
import com.osiris.constants.FragmentConstants;
import com.osiris.ui.common.PlaylistModel;
import com.osiris.ui.common.PlaylistRecyclerViewAdapter;
import com.osiris.utility.CacheManager;

import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class PlaylistFragment extends Fragment {

    private FloatingActionButton newPlaylistButton;
    private final static String TAG = PlaylistFragment.class.getName();
    private List<PlaylistModel> playlists = new ArrayList<PlaylistModel>();
    private View view;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_playlist,
                container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.newPlaylist).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Add playlist clicked");
                ((MainActivity) getActivity()).replaceFragment(FragmentConstants.FRAGMENT_CREATE_PLAYLIST, null);
            }
        });


        boolean reloadPlaylists = CacheManager.getInstance(getActivity()).readBool(getString(R.string.cache_reload_playlists), false);

        if(playlists.size() == 0 || reloadPlaylists){
            playlists.clear();
            new GetUserPlaylistsAsync("5bb150e937a28b2c670644e2", new RESTCallbackListener() {
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

                            }

                            CacheManager.getInstance(getActivity()).writeBool(getString(R.string.cache_reload_playlists), false);

                            buildUI();

                        }catch (IllegalStateException e){
                            e.printStackTrace();
                        }
                    }

                }

            }).execute();
        }else{
            buildUI();
        }

    }

    private void buildUI(){

        RecyclerView recyclerView = view.findViewById(R.id.playlists_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        PlaylistRecyclerViewAdapter adapter = new PlaylistRecyclerViewAdapter(getContext(), playlists, itemClickListener);
        recyclerView.setAdapter(adapter);

        Log.i(TAG, "Playlists size: " + playlists.size() );
    }

    private PlaylistRecyclerViewAdapter.ItemClickListener itemClickListener = new PlaylistRecyclerViewAdapter.ItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            Bundle bundle = new Bundle();
            bundle.putString("playlistId", playlists.get(position).getId());
            ((MainActivity) getActivity()).replaceFragment(FragmentConstants.FRAGMENT_VIEW_PLAYLIST, bundle);
        }
    };

}
