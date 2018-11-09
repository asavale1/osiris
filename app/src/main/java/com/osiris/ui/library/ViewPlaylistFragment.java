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

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.osiris.R;
import com.osiris.api.GetPlaylistAsync;
import com.osiris.api.RESTClient;
import com.osiris.api.RemoveSongFromPlaylistAsync;
import com.osiris.api.listeners.RESTCallbackListener;
import com.osiris.constants.BundleConstants;
import com.osiris.constants.JsonConstants;
import com.osiris.model.ModelParser;
import com.osiris.model.PlaylistDetailedModel;
import com.osiris.ui.LibraryFragmentListener;
import com.osiris.ui.common.SongRecyclerViewAdapter;

import java.util.Objects;

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

        assert this.getArguments() != null;
        playlistId = this.getArguments().getString(BundleConstants.PLAYLIST_ID);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getPlaylist();
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
            SongRecyclerViewAdapter adapter = new SongRecyclerViewAdapter(getContext(), playlist.getSongs(), itemClickListener, null);
            recyclerView.swapAdapter(adapter, false);

            if(playlist.getSongs().size() > 0){
                view.findViewById(R.id.empty_playlist).setVisibility(View.GONE);
                view.findViewById(R.id.queuePlaylist).setVisibility(View.VISIBLE);
                view.findViewById(R.id.queuePlaylist).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        libraryFragmentListener.addPlaylistToQueue(playlist);
                        assert getFragmentManager() != null;
                        getFragmentManager().popBackStack();
                    }
                });
            }else{
                view.findViewById(R.id.empty_playlist).setVisibility(View.VISIBLE);
                view.findViewById(R.id.queuePlaylist).setVisibility(View.GONE);
            }

        }
    }

    private void getPlaylist(){
        new GetPlaylistAsync(playlistId, true, new RESTCallbackListener() {
            @Override
            public void onComplete(RESTClient.RESTResponse response) {
                if(response.getStatus() == HttpsURLConnection.HTTP_OK){

                    JsonParser parser = new JsonParser();
                    JsonObject playlistJson = parser.parse(response.getData()).getAsJsonObject();
                    playlist = ModelParser.parsePlaylistDetailedModelJson(playlistJson);

                }

                buildUI();
            }
        }).execute();
    }

    private void removeSongFromPlaylist(String songId){
        JsonObject requestJson = new JsonObject();
        requestJson.addProperty(JsonConstants.SONG_ID, songId);
        new RemoveSongFromPlaylistAsync(playlistId, requestJson, new RESTCallbackListener() {
            @Override
            public void onComplete(RESTClient.RESTResponse response) {
                if(response.getStatus() == HttpsURLConnection.HTTP_OK){
                    getPlaylist();
                }
            }
        }).execute();
    }

    private SongRecyclerViewAdapter.ItemClickListener itemClickListener = new SongRecyclerViewAdapter.ItemClickListener() {
        @Override
        public void onItemClick(View view, final int position) {
            PopupMenu popup = new PopupMenu(Objects.requireNonNull(getActivity()), view);
            popup.inflate(R.menu.options_view_playlist_fragment);

            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.remove_from_playlist:
                            removeSongFromPlaylist(playlist.getSongs().get(position).getId());
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
