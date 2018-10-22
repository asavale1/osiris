package com.osiris.ui.library;

import android.content.Context;
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
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.osiris.R;
import com.osiris.api.GetPlaylistAsync;
import com.osiris.api.RESTClient;
import com.osiris.api.listeners.GetPlaylistAsyncListener;
import com.osiris.ui.LibraryFragmentListener;
import com.osiris.ui.common.PlaylistDetailedModel;
import com.osiris.ui.common.SongModel;
import com.osiris.ui.common.SongRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class ViewPlaylistFragment extends Fragment {
    private static final String TAG = ViewPlaylistFragment.class.getName();
    private View view;
    private String playlistId;
    private PlaylistDetailedModel playlist;
    private LibraryFragmentListener libraryFragmentListener;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_view_playlist,
                container, false);

        playlistId = this.getArguments().getString("playlistId");

        Log.i(TAG, "In onCreateView");
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        new GetPlaylistAsync(playlistId, true, new GetPlaylistAsyncListener() {
            @Override
            public void onComplete(RESTClient.RESTResponse response) {
                Log.i(TAG, "Status: " + response.getStatus());
                Log.i(TAG, "Data: " + response.getData());
                if(response.getStatus() == HttpsURLConnection.HTTP_OK){

                    JsonParser parser = new JsonParser();
                    JsonObject playlistJson = parser.parse(response.getData()).getAsJsonObject();

                    playlist = new PlaylistDetailedModel();
                    playlist.setId(playlistJson.get("_id").getAsString());
                    playlist.setTitle(playlistJson.get("title").getAsString());
                    playlist.setUserId(playlistJson.get("userId").getAsString());
                    List<SongModel> songs = new ArrayList<>();

                    JsonArray songsJsonArray = playlistJson.getAsJsonArray("songs");
                    for(JsonElement songJsonElem : songsJsonArray){

                        JsonObject songJson = songJsonElem.getAsJsonObject();

                        SongModel songModel = new SongModel();
                        songModel.setId(songJson.get("_id").getAsString());
                        songModel.setAlbum(songJson.get("album").getAsString());
                        songModel.setFileUrl(songJson.get("fileUrl").getAsString());
                        songModel.setTitle(songJson.get("title").getAsString());
                        songs.add(songModel);
                    }

                    playlist.setSongs(songs);
                }else{

                }

                buildUI();
            }
        }).execute();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof LibraryFragmentListener) {
            libraryFragmentListener = (LibraryFragmentListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implemenet LibraryFragmentListener");
        }
    }

    private void buildUI(){
        if(playlist != null){
            ((TextView)view.findViewById(R.id.playlist_title)).setText(playlist.getTitle());
            RecyclerView recyclerView = view.findViewById(R.id.songs_recycler_view);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            SongRecyclerViewAdapter adapter = new SongRecyclerViewAdapter(getContext(), playlist.getSongs(), itemClickListener);
            recyclerView.setAdapter(adapter);

            view.findViewById(R.id.queuePlaylist).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(TAG, "Play button clicked");
                    libraryFragmentListener.addPlaylistToQueue(playlist);
                }
            });
        }else{

        }
    }

    private SongRecyclerViewAdapter.ItemClickListener itemClickListener = new SongRecyclerViewAdapter.ItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            PopupMenu popup = new PopupMenu(getActivity(), view);
            popup.inflate(R.menu.options_view_playlist_fragment);

            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.remove_from_playlist:
                            Log.i(TAG, "Remove from playlist");
                            return true;
                        default:
                            return false;
                    }
                }
            });
            popup.show();
        }
    };
}
