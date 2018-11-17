package com.osiris.ui.library;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.osiris.MainActivity;
import com.osiris.R;
import com.osiris.api.GetAlbumAsync;
import com.osiris.api.RESTClient;
import com.osiris.api.listeners.RESTCallbackListener;
import com.osiris.constants.BundleConstants;
import com.osiris.constants.FragmentConstants;
import com.osiris.constants.MediaConstants;
import com.osiris.model.AlbumDetailedModel;
import com.osiris.model.ModelParser;
import com.osiris.model.SongModel;
import com.osiris.ui.LibraryFragmentListener;
import com.osiris.ui.PlayerControllerListener;
import com.osiris.ui.common.SongRecyclerViewAdapter;
import com.osiris.ui.dialog.AddToPlaylistDialog;

import java.util.Objects;

import javax.net.ssl.HttpsURLConnection;

public class ViewAlbumFragment extends Fragment {
    //private static final String TAG = ViewAlbumFragment.class.getName();
    private View view;
    private String albumId;
    private AlbumDetailedModel album;
    private LibraryFragmentListener libraryFragmentListener;
    private PlayerControllerListener playerControllerListener;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_view_album,
                container, false);

        assert this.getArguments() != null;
        albumId = this.getArguments().getString(BundleConstants.ALBUM_ID);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View v, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.loading_bar).setVisibility(View.VISIBLE);
        view.findViewById(R.id.content_layout).setVisibility(View.GONE);

        new GetAlbumAsync(albumId, true, new RESTCallbackListener() {
            @Override
            public void onComplete(RESTClient.RESTResponse response) {

                if(response.getStatus() == HttpsURLConnection.HTTP_OK){
                    JsonParser parser = new JsonParser();
                    JsonObject albumJson = parser.parse(response.getData()).getAsJsonObject();

                    album = ModelParser.parseAlbumDetailedModelJson(albumJson);
                }

                view.findViewById(R.id.loading_bar).setVisibility(View.GONE);
                view.findViewById(R.id.content_layout).setVisibility(View.VISIBLE);

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

        if (context instanceof PlayerControllerListener) {
            playerControllerListener = (PlayerControllerListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implemenet PlayerControllerListener");
        }
    }

    private void buildUI(){
        if(album != null){
            ((TextView)view.findViewById(R.id.album_title)).setText(album.getTitle());

            view.findViewById(R.id.queueAlbum).setVisibility(View.VISIBLE);
            view.findViewById(R.id.queueAlbum).setOnClickListener(queueAlbumListener);

            RecyclerView recyclerView = view.findViewById(R.id.songs_recycler_view);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            SongRecyclerViewAdapter adapter = new SongRecyclerViewAdapter(getContext(), album.getSongs(), itemClickListener, itemLongClickListener);
            recyclerView.setAdapter(adapter);
        }
    }

    private View.OnClickListener queueAlbumListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            libraryFragmentListener.addAlbumToQueue(album);
            playerControllerListener.onPlaySong();
            assert getFragmentManager() != null;
            getFragmentManager().popBackStack();
        }
    };

    private SongRecyclerViewAdapter.ItemClickListener itemClickListener = new SongRecyclerViewAdapter.ItemClickListener() {
        @Override
        public void onItemClick(View view, final int position) {
            libraryFragmentListener.clearQueue(new ResultReceiver(new Handler()) {
                @Override
                protected void onReceiveResult(int resultCode, Bundle resultData) {
                    if(resultCode == MediaConstants.RESULT_CODE_CLEAR_QUEUE){
                        libraryFragmentListener.addSongToQueue(album.getSongs().get(position));
                        playerControllerListener.onPlaySong();
                        assert getFragmentManager() != null;
                        getFragmentManager().popBackStack();
                    }
                }
            });
        }
    };

    private SongRecyclerViewAdapter.ItemLongClickListener itemLongClickListener = new SongRecyclerViewAdapter.ItemLongClickListener() {
        @Override
        public void onItemLongClick(View view, int position) {
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
