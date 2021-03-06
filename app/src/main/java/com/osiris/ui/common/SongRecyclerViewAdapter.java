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
    private ItemLongClickListener itemLongClickListener;

    public SongRecyclerViewAdapter(Context context, List<SongModel> songs,
                                   ItemClickListener itemClickListener, ItemLongClickListener itemLongClickListener) {
        this.layoutInflater = LayoutInflater.from(context);
        this.songs = songs;
        this.itemClickListener = itemClickListener;
        this.itemLongClickListener = itemLongClickListener;
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = layoutInflater.inflate(R.layout.recyclerview_item_song, viewGroup, false);
        return new SongViewHolder(view, itemClickListener, itemLongClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder songViewHolder, int i) {
        songViewHolder.songTitle.setText(songs.get(i).getTitle());
        songViewHolder.albumTitle.setText(songs.get(i).getAlbumTitle());
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    public interface ItemClickListener{
        void onItemClick(View view, int position);
    }

    public interface ItemLongClickListener{
        void onItemLongClick(View view, int position);
    }
}
