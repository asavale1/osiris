package com.osiris.ui.common;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.osiris.R;

import java.util.List;

public class QueueRecyclerViewAdapter extends RecyclerView.Adapter<QueueViewHolder> {

    private LayoutInflater layoutInflater;
    private List<MediaSessionCompat.QueueItem> songs;
    private ItemClickListener itemClickListener;

    public QueueRecyclerViewAdapter(Context context, List<MediaSessionCompat.QueueItem> songs, ItemClickListener itemClickListener) {
        this.layoutInflater = LayoutInflater.from(context);
        this.songs = songs;
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public QueueViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = layoutInflater.inflate(R.layout.recyclerview_item_song, viewGroup, false);
        return new QueueViewHolder(view, itemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull QueueViewHolder queueViewHolder, int i) {
        queueViewHolder.songTitle.setText(songs.get(i).getDescription().getTitle());
        queueViewHolder.albumTitle.setText(songs.get(i).getDescription().getSubtitle());
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    public interface ItemClickListener{
        void onItemClick(View view, int position);
    }


}