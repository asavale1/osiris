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
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.osiris.MainActivity;
import com.osiris.R;
import com.osiris.api.RESTClient;
import com.osiris.api.SearchSongsAsync;
import com.osiris.api.listeners.RESTCallbackListener;
import com.osiris.constants.BundleConstants;
import com.osiris.constants.FragmentConstants;
import com.osiris.constants.JsonConstants;
import com.osiris.model.AlbumModel;
import com.osiris.model.ModelParser;
import com.osiris.model.PlaylistModel;
import com.osiris.model.SongModel;
import com.osiris.ui.LibraryFragmentListener;
import com.osiris.ui.PlayerControllerListener;
import com.osiris.ui.common.AlbumRecyclerViewAdapter;
import com.osiris.ui.common.PlaylistRecyclerViewAdapter;
import com.osiris.ui.common.SongRecyclerViewAdapter;
import com.osiris.ui.dialog.AddToPlaylistDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.net.ssl.HttpsURLConnection;

public class BrowseFragment extends Fragment {

    private static final String TAG = BrowseFragment.class.getName();

    private List<SongModel> songs = new ArrayList<>();
    private List<AlbumModel> albums = new ArrayList<>();
    private List<PlaylistModel> playlists = new ArrayList<>();
    private LibraryFragmentListener libraryFragmentListener;
    private PlayerControllerListener playerControllerListener;
    private EditText searchEditText;
    private RecyclerView songsRecyclerView, albumsRecyclerView, playlistsRecyclerView;
    private View view;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_browse,
                container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        searchEditText = view.findViewById(R.id.search_query);

        songsRecyclerView = view.findViewById(R.id.songs_recycler_view);
        songsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        albumsRecyclerView = view.findViewById(R.id.albums_recycler_view);
        albumsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        playlistsRecyclerView = view.findViewById(R.id.playlists_recycler_view);
        playlistsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        if(playlists.size() == 0 && albums.size() == 0 && songs.size() == 0) {
            searchLibrary("");
        } else{
            buildUI();
        }

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

        if (context instanceof PlayerControllerListener) {
            playerControllerListener = (PlayerControllerListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implemenet PlayerControllerListener");
        }
    }

    private void buildUI(){

        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    songs.clear();
                    albums.clear();
                    playlists.clear();
                    searchLibrary(searchEditText.getText().toString());
                }
                return false;
            }
        });

        SongRecyclerViewAdapter adapter = new SongRecyclerViewAdapter(getContext(), songs, itemClickListener, songItemLongClickListener);
        songsRecyclerView.swapAdapter(adapter, false);

        if(songs.size() == 0){
            view.findViewById(R.id.songs_layout).setVisibility(View.GONE);
        }else{
            ((TextView) view.findViewById(R.id.songs_layout_title)).setText(getString(R.string.songs));
            songsRecyclerView.setVisibility(View.VISIBLE);
            view.findViewById(R.id.songs_layout).setVisibility(View.VISIBLE);
        }

        AlbumRecyclerViewAdapter albumsAdapter = new AlbumRecyclerViewAdapter(getContext(), albums, albumItemClickListener, null);
        albumsRecyclerView.swapAdapter(albumsAdapter, false);

        if(albums.size() == 0){
            view.findViewById(R.id.albums_layout).setVisibility(View.GONE);
        }else{
            ((TextView) view.findViewById(R.id.albums_layout_title)).setText(R.string.albums);
            albumsRecyclerView.setVisibility(View.VISIBLE);
            view.findViewById(R.id.albums_layout).setVisibility(View.VISIBLE);
        }

        PlaylistRecyclerViewAdapter playlistsAdapter = new PlaylistRecyclerViewAdapter(getContext(), playlists, playlistItemClicklistener, null);
        playlistsRecyclerView.swapAdapter(playlistsAdapter, false);

        if(playlists.size() == 0){
            view.findViewById(R.id.playlists_layout).setVisibility(View.GONE);
        }else{
            ((TextView) view.findViewById(R.id.playlists_layout_title)).setText(getString(R.string.playlists));
            playlistsRecyclerView.setVisibility(View.VISIBLE);
            view.findViewById(R.id.playlists_layout).setVisibility(View.VISIBLE);
        }
    }

    private SongRecyclerViewAdapter.ItemLongClickListener songItemLongClickListener = new SongRecyclerViewAdapter.ItemLongClickListener() {
        @Override
        public void onItemLongClick(View view, int position) {
            PopupMenu popup = new PopupMenu(Objects.requireNonNull(getActivity()), view);
            popup.inflate(R.menu.song_select_options);

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

    private SongRecyclerViewAdapter.ItemClickListener itemClickListener = new SongRecyclerViewAdapter.ItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            libraryFragmentListener.addSongToQueue(songs.get(position));
            playerControllerListener.onPlaySong();
        }
    };

    private AlbumRecyclerViewAdapter.ItemClickListener albumItemClickListener = new AlbumRecyclerViewAdapter.ItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            Bundle bundle = new Bundle();
            bundle.putString(BundleConstants.ALBUM_ID, albums.get(position).getId());
            ((MainActivity) Objects.requireNonNull(getActivity())).replaceFragment(FragmentConstants.FRAGMENT_VIEW_ALBUM, bundle);
        }
    };

    private PlaylistRecyclerViewAdapter.ItemClickListener playlistItemClicklistener = new PlaylistRecyclerViewAdapter.ItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            Bundle bundle = new Bundle();
            bundle.putString(BundleConstants.PLAYLIST_ID, playlists.get(position).getId());
            ((MainActivity) Objects.requireNonNull(getActivity())).replaceFragment(FragmentConstants.FRAGMENT_VIEW_PLAYLIST, bundle);
        }
    };

    private void searchLibrary(String searchQuery){
        view.findViewById(R.id.content_layout).setVisibility(View.GONE);
        view.findViewById(R.id.loading_bar).setVisibility(View.VISIBLE);

        new SearchSongsAsync(searchQuery, new RESTCallbackListener() {
            @Override
            public void onComplete(RESTClient.RESTResponse response) {


                view.findViewById(R.id.content_layout).setVisibility(View.VISIBLE);
                view.findViewById(R.id.loading_bar).setVisibility(View.GONE);

                if(response.getStatus() == HttpsURLConnection.HTTP_OK){
                    try{
                        JsonParser parser = new JsonParser();
                        JsonObject responseJson = parser.parse(response.getData()).getAsJsonObject();

                        JsonArray songsJsonArray = responseJson.get(JsonConstants.SONGS).getAsJsonArray();
                        for(JsonElement elem : songsJsonArray){
                            songs.add(ModelParser.parseSongModelJson(elem.getAsJsonObject()));
                        }

                        JsonArray albumsJsonArray = responseJson.get(JsonConstants.ALBUMS).getAsJsonArray();
                        for(JsonElement elem : albumsJsonArray){
                            albums.add(ModelParser.parseAlbumModelJson(elem.getAsJsonObject()));
                        }

                        JsonArray playlistsJsonArray = responseJson.get(JsonConstants.PLAYLISTS).getAsJsonArray();
                        for(JsonElement elem : playlistsJsonArray){
                            playlists.add(ModelParser.parsePlaylistModelJson(elem.getAsJsonObject()));
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
