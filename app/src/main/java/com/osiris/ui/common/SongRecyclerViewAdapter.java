package com.osiris.ui.common;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.osiris.R;
import com.osiris.model.SongModel;

import java.util.List;

public class SongRecyclerViewAdapter extends RecyclerView.Adapter<SongViewHolder> {

    private LayoutInflater layoutInflater;
    private List<SongModel> songs;
    private ItemClickListener itemClickListener;

    // data is passed into the constructor
    public SongRecyclerViewAdapter(Context context, List<SongModel> songs, ItemClickListener itemClickListener) {
        this.layoutInflater = LayoutInflater.from(context);
        this.songs = songs;
        this.itemClickListener = itemClickListener;
    }


    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = layoutInflater.inflate(R.layout.recyclerview_item_song, viewGroup, false);
        return new SongViewHolder(view, itemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder songViewHolder, int i) {
        songViewHolder.songTitle.setText(songs.get(i).getTitle());
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    public interface ItemClickListener{
        void onItemClick(View view, int position);
    }


}
