package com.osiris.ui.library;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.osiris.R;
import com.osiris.api.ApiConstants;
import com.osiris.api.GetSongsAsync;
import com.osiris.api.listeners.GetSongsAsyncListener;
import com.osiris.ui.LibraryFragmentListener;
import com.osiris.ui.common.SongModel;
import com.osiris.ui.common.SongRecyclerViewAdapter;


import java.util.ArrayList;
import java.util.List;

public class BrowseFragment extends Fragment {

    private View view;
    private static final String TAG = BrowseFragment.class.getName();
    private List<SongModel> songs = new ArrayList<>();
    private LibraryFragmentListener libraryFragmentListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_browse,
                container, false);
        Log.i(TAG, "In onCreateView");
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        new GetSongsAsync(ApiConstants.GET_ALL_SONGS, new GetSongsAsyncListener() {
            @Override
            public void gotSongs(String songsString) {

                try{
                    JsonParser parser = new JsonParser();
                    JsonArray jsonArray = parser.parse(songsString).getAsJsonArray();

                    for(int i = 0; i < jsonArray.size(); i++){
                        SongModel song = new SongModel();
                        song.setTitle(jsonArray.get(i).getAsJsonObject().get("name").getAsString());
                        song.setId(jsonArray.get(i).getAsJsonObject().get("_id").getAsString());
                        song.setFileUrl(jsonArray.get(i).getAsJsonObject().get("fileUrl").getAsString());
                        songs.add(song);
                    }

                    buildUI();
                }catch (IllegalStateException e){
                    e.printStackTrace();
                }

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

        RecyclerView recyclerView = view.findViewById(R.id.songs_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        SongRecyclerViewAdapter adapter = new SongRecyclerViewAdapter(getContext(), songs, itemClickListener);
        recyclerView.setAdapter(adapter);
    }

    private SongRecyclerViewAdapter.ItemClickListener itemClickListener = new SongRecyclerViewAdapter.ItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            Log.i(TAG, "On item clicked");
            Log.i(TAG, songs.get(position).getTitle());
            //libraryFragmentListener.playSongAt(position);
            libraryFragmentListener.addSongToQueue(songs.get(position));
        }
    };
}