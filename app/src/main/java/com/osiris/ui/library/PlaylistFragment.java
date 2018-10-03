package com.osiris.ui.library;

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
import com.osiris.api.listeners.GetUserPlaylistsAsyncListener;
import com.osiris.constants.FragmentConstants;
import com.osiris.ui.common.PlaylistModel;
import com.osiris.ui.common.PlaylistRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

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
                ((MainActivity) getActivity()).replaceFragment(FragmentConstants.FRAGMENT_CREATE_PLAYLIST);
            }
        });

        new GetUserPlaylistsAsync(ApiConstants.GET_USER_PLAYLISTS("5bb150e937a28b2c670644e2"), new GetUserPlaylistsAsyncListener() {
            @Override
            public void gotPlaylists(String playlistsString) {
                Log.i(TAG, "Playlists response: " + playlistsString);
                try{
                    JsonParser parser = new JsonParser();
                    JsonArray jsonArray = parser.parse(playlistsString).getAsJsonArray();

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

                    buildUI();


                }catch (IllegalStateException e){
                    e.printStackTrace();
                }
            }
        }).execute();

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

        }
    };

}
