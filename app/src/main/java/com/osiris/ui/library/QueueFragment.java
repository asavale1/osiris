package com.osiris.ui.library;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.osiris.R;
import com.osiris.ui.LibraryFragmentListener;
import com.osiris.ui.common.QueueRecyclerViewAdapter;
import com.osiris.ui.common.SongRecyclerViewAdapter;

import java.util.List;

public class QueueFragment extends Fragment {
    private View view;
    private static final String TAG = QueueFragment.class.getName();
    private LibraryFragmentListener libraryFragmentListener;
    private List<MediaSessionCompat.QueueItem> songs;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        Log.i(TAG, "Is visible to user: " + isVisibleToUser);
        if(libraryFragmentListener != null){
            songs = libraryFragmentListener.getQueue();
            if(songs != null){
                buildUI();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "In onCreateView");

        view = inflater.inflate(R.layout.fragment_queue,
                container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(TAG, "In onViewCreated");

    }

    private void buildUI(){
        RecyclerView recyclerView = view.findViewById(R.id.songs_recycler_view);
        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        QueueRecyclerViewAdapter adapter = new QueueRecyclerViewAdapter(getContext(), songs, itemClickListener);
        recyclerView.setAdapter(adapter);

        view.findViewById(R.id.placeholder).setVisibility(View.GONE);
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

    private QueueRecyclerViewAdapter.ItemClickListener itemClickListener = new QueueRecyclerViewAdapter.ItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            Log.i(TAG, "On item clicked");
            Log.i(TAG, songs.get(position).getDescription().getTitle().toString());
            //libraryFragmentListener.playSongAt(position);
            //libraryFragmentListener.addSongToQueue(songs.get(position));

        }
    };
}
