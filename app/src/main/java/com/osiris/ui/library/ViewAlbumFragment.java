package com.osiris.ui.library;

import android.app.Dialog;
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
import com.osiris.api.GetAlbumAsync;
import com.osiris.api.RESTClient;
import com.osiris.api.listeners.RESTCallbackListener;
import com.osiris.ui.LibraryFragmentListener;
import com.osiris.ui.common.AlbumDetailedModel;
import com.osiris.ui.common.SongModel;
import com.osiris.ui.common.SongRecyclerViewAdapter;
import com.osiris.ui.dialog.AddToPlaylistDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.net.ssl.HttpsURLConnection;

public class ViewAlbumFragment extends Fragment {
    private static final String TAG = ViewAlbumFragment.class.getName();
    private View view;
    private String albumId;
    private AlbumDetailedModel album;
    private LibraryFragmentListener libraryFragmentListener;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_view_album,
                container, false);

        albumId = this.getArguments().getString("albumId");

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        new GetAlbumAsync(albumId, true, new RESTCallbackListener() {
            @Override
            public void onComplete(RESTClient.RESTResponse response) {
                Log.i(TAG, "Status: " + response.getStatus());
                Log.i(TAG, "Message: " + response.getData());

                if(response.getStatus() == HttpsURLConnection.HTTP_OK){
                    JsonParser parser = new JsonParser();
                    JsonObject albumJson = parser.parse(response.getData()).getAsJsonObject();

                    album = new AlbumDetailedModel();
                    album.setId(albumJson.get("_id").getAsString());
                    album.setTitle(albumJson.get("title").getAsString());
                    List<SongModel> songs = new ArrayList<>();

                    JsonArray songsJsonArray = albumJson.getAsJsonArray("songs");
                    for(JsonElement songJsonElem : songsJsonArray){

                        JsonObject songJson = songJsonElem.getAsJsonObject();

                        SongModel songModel = new SongModel();
                        songModel.setId(songJson.get("_id").getAsString());
                        songModel.setAlbum(songJson.get("albumId").getAsString());
                        songModel.setFileUrl(songJson.get("fileUrl").getAsString());
                        songModel.setTitle(songJson.get("title").getAsString());
                        songs.add(songModel);
                    }

                    album.setSongs(songs);
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
        if(album != null){
            ((TextView)view.findViewById(R.id.album_title)).setText(album.getTitle());
            RecyclerView recyclerView = view.findViewById(R.id.songs_recycler_view);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            SongRecyclerViewAdapter adapter = new SongRecyclerViewAdapter(getContext(), album.getSongs(), itemClickListener);
            recyclerView.setAdapter(adapter);
        }
    }

    private SongRecyclerViewAdapter.ItemClickListener itemClickListener = new SongRecyclerViewAdapter.ItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            Log.i(TAG, "Song clicked");
            PopupMenu popup = new PopupMenu(Objects.requireNonNull(getActivity()), view);
            popup.inflate(R.menu.song_select_options);

            final SongModel selectedSong = album.getSongs().get(position);
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.add_to_playlist:
                            Dialog dialog = new AddToPlaylistDialog(getActivity(), selectedSong.getId());
                            dialog.show();
                            return true;
                        case R.id.add_to_queue:
                            libraryFragmentListener.addSongToQueue(selectedSong);
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
