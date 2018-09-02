package com.osiris.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.osiris.R;
import com.osiris.api.ApiConstants;
import com.osiris.api.GetSongsAsync;
import com.osiris.api.listeners.GetSongsAsyncListener;
import com.osiris.ui.common.SongModel;
import com.osiris.ui.common.SongRecyclerViewAdapter;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.List;

public class LibraryFragment extends Fragment {

    private LibraryFragmentListener libraryFragmentListener;
    private List<SongModel> songs = new ArrayList<>();
    private View view;
    private String apiRequestUrl;

    private static final String TAG = LibraryFragment.class.getName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_library,
                container, false);
        Log.i(TAG, "In onCreateView");
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        apiRequestUrl = ApiConstants.GET_ALL_SONGS;
        new GetSongsAsync(apiRequestUrl, new GetSongsAsyncListener() {
            @Override
            public void gotSongs(String songsString) {
                try {
                    JSONParser parser = new JSONParser();
                    JSONArray songsJson = (JSONArray) parser.parse(songsString);


                    for(Object obj : songsJson){
                        JSONObject jsonObj = (JSONObject) obj;

                        SongModel song = new SongModel();
                        song.setTitle((String) jsonObj.get("name"));
                        song.setId((String) jsonObj.get("_id"));
                        songs.add(song);

                    }

                    buildUI();


                }catch (ParseException e){
                    e.printStackTrace();
                }
            }
        }).execute();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof PlayerControllerListener) {
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
            libraryFragmentListener.buildQueue(apiRequestUrl, position);
        }
    };
}
