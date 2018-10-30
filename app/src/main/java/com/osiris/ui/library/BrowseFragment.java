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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.osiris.R;
import com.osiris.api.RESTClient;
import com.osiris.api.SearchSongsAsync;
import com.osiris.api.listeners.RESTCallbackListener;
import com.osiris.ui.LibraryFragmentListener;
import com.osiris.ui.common.SongModel;
import com.osiris.ui.common.SongRecyclerViewAdapter;
import com.osiris.ui.dialog.AddToPlaylistDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.net.ssl.HttpsURLConnection;

public class BrowseFragment extends Fragment {

    private static final String TAG = BrowseFragment.class.getName();
    private List<SongModel> songs = new ArrayList<>();
    private LibraryFragmentListener libraryFragmentListener;
    private EditText searchEditText;
    private RecyclerView songsRecyclerView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_browse,
                container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        searchEditText = view.findViewById(R.id.search_query);
        songsRecyclerView = view.findViewById(R.id.songs_recycler_view);
        songsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        buildUI();

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

        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    songs.clear();
                    searchSongs(searchEditText.getText().toString());
                }
                return false;
            }
        });

        SongRecyclerViewAdapter adapter = new SongRecyclerViewAdapter(getContext(), songs, itemClickListener);
        songsRecyclerView.swapAdapter(adapter, false);
    }

    private SongRecyclerViewAdapter.ItemClickListener itemClickListener = new SongRecyclerViewAdapter.ItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            PopupMenu popup = new PopupMenu(Objects.requireNonNull(getActivity()), view);
            popup.inflate(R.menu.options_browse_fragment);

            final SongModel selectedSong = songs.get(position);
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

    private void searchSongs(String searchQuery){
        new SearchSongsAsync(searchQuery, new RESTCallbackListener() {
            @Override
            public void onComplete(RESTClient.RESTResponse response) {
                Log.i(TAG, "Status: " +response.getStatus());
                Log.i(TAG, "Response: " + response.getData());

                if(response.getStatus() == HttpsURLConnection.HTTP_OK){
                    try{
                        JsonParser parser = new JsonParser();
                        JsonArray jsonArray = parser.parse(response.getData()).getAsJsonArray();

                        for(int i = 0; i < jsonArray.size(); i++){
                            SongModel song = new SongModel();
                            song.setTitle(jsonArray.get(i).getAsJsonObject().get("title").getAsString());
                            song.setId(jsonArray.get(i).getAsJsonObject().get("_id").getAsString());
                            song.setAlbum(jsonArray.get(i).getAsJsonObject().get("album").getAsString());
                            song.setFileUrl(jsonArray.get(i).getAsJsonObject().get("fileUrl").getAsString());
                            songs.add(song);
                        }

                        buildUI();
                    }catch (IllegalStateException e){
                        e.printStackTrace();
                    }
                }

            }
        }).execute();
    }
}
