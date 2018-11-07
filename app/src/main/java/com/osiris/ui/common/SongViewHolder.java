package com.osiris.ui.common;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.osiris.R;

class SongViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{

    TextView songTitle, albumTitle;
    private SongRecyclerViewAdapter.ItemClickListener itemClickListener;
    private SongRecyclerViewAdapter.ItemLongClickListener itemLongClickListener;
    private static final String TAG = SongViewHolder.class.getName();

    SongViewHolder(@NonNull View itemView,
                   SongRecyclerViewAdapter.ItemClickListener itemClickListener,
                   SongRecyclerViewAdapter.ItemLongClickListener itemLongClickListener) {
        super(itemView);
        this.songTitle = itemView.findViewById(R.id.song_title);
        this.albumTitle = itemView.findViewById(R.id.album_title);
        this.itemClickListener = itemClickListener;
        this.itemLongClickListener = itemLongClickListener;
        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(itemClickListener != null){
            itemClickListener.onItemClick(v, getAdapterPosition());
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if(itemLongClickListener != null){
            itemLongClickListener.onItemLongClick(v, getAdapterPosition());
        }
        return true;
    }
}
