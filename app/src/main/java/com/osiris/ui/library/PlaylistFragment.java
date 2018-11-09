package com.osiris.ui.library;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.osiris.MainActivity;
import com.osiris.R;
import com.osiris.api.DeletePlaylist;
import com.osiris.api.GetUserPlaylistsAsync;
import com.osiris.api.RESTClient;
import com.osiris.api.listeners.RESTCallbackListener;
import com.osiris.constants.BundleConstants;
import com.osiris.constants.FragmentConstants;
import com.osiris.model.ModelParser;
import com.osiris.model.PlaylistModel;
import com.osiris.ui.common.PlaylistRecyclerViewAdapter;
import com.osiris.utility.CacheManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.net.ssl.HttpsURLConnection;

public class PlaylistFragment extends Fragment {

    private final static String TAG = PlaylistFragment.class.getName();

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
                ((MainActivity) Objects.requireNonNull(getActivity())).replaceFragment(FragmentConstants.FRAGMENT_CREATE_PLAYLIST, null);
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
        PlaylistRecyclerViewAdapter adapter = new PlaylistRecyclerViewAdapter(getContext(), playlists, itemClickListener, itemLongClickListener);
        recyclerView.swapAdapter(adapter, false);

    }

    private PlaylistRecyclerViewAdapter.ItemClickListener itemClickListener = new PlaylistRecyclerViewAdapter.ItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            Bundle bundle = new Bundle();
            bundle.putString(BundleConstants.PLAYLIST_ID, playlists.get(position).getId());
            ((MainActivity) Objects.requireNonNull(getActivity())).replaceFragment(FragmentConstants.FRAGMENT_VIEW_PLAYLIST, bundle);
        }
    };

    private PlaylistRecyclerViewAdapter.ItemLongClickListener itemLongClickListener = new PlaylistRecyclerViewAdapter.ItemLongClickListener() {
        @Override
        public void onItemLongClick(View view, final int position) {
            if(!playlists.get(position).isPrimary()){
                PopupMenu popup = new PopupMenu(Objects.requireNonNull(getActivity()), view);
                popup.inflate(R.menu.playlist_fragment_options);

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.delete_playlist:
                                new DeletePlaylist(playlists.get(position).getId(), new RESTCallbackListener() {
                                    @Override
                                    public void onComplete(RESTClient.RESTResponse response) {
                                        if(response.getStatus() == HttpsURLConnection.HTTP_OK){
                                            playlists.remove(position);
                                            buildUI();
                                        }else{
                                            Toast.makeText(getActivity(), "Failed to delete playlist", Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                }).execute();
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                popup.show();
            }else{
                Toast.makeText(getActivity(), "Can't delete your library", Toast.LENGTH_SHORT).show();

            }
        }
    };

}
