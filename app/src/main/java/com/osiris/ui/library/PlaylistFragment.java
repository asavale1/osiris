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
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.osiris.MainActivity;
import com.osiris.R;
import com.osiris.api.GetUserPlaylistsAsync;
import com.osiris.api.RESTClient;
import com.osiris.api.listeners.RESTCallbackListener;
import com.osiris.constants.FragmentConstants;
import com.osiris.model.ModelParser;
import com.osiris.model.PlaylistModel;
import com.osiris.ui.common.PlaylistRecyclerViewAdapter;
import com.osiris.utility.CacheManager;

import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class PlaylistFragment extends Fragment {

    //private final static String TAG = PlaylistFragment.class.getName();

    private List<PlaylistModel> playlists = new ArrayList<>();
    private View view;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_playlist,
                container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.newPlaylist).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).replaceFragment(FragmentConstants.FRAGMENT_CREATE_PLAYLIST, null);
            }
        });


        boolean reloadPlaylists = CacheManager.getInstance(getActivity()).readBool(getString(R.string.cache_reload_playlists), false);

        if(playlists.size() == 0 || reloadPlaylists){
            playlists.clear();
            new GetUserPlaylistsAsync(CacheManager.getInstance(getActivity()).readString(getString(R.string.cache_user_id), ""), new RESTCallbackListener() {
                @Override
                public void onComplete(RESTClient.RESTResponse response) {
                    if(response.getStatus() == HttpsURLConnection.HTTP_OK){
                        try{
                            JsonParser parser = new JsonParser();
                            JsonArray jsonArray = parser.parse(response.getData()).getAsJsonArray();

                            for(JsonElement elem : jsonArray){
                                playlists.add(ModelParser.parsePlaylistModelJson(elem.getAsJsonObject()));
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
