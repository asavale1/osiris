package com.osiris.ui.common;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.osiris.R;

public class QueueViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    TextView songTitle, albumTitle;
    private QueueRecyclerViewAdapter.ItemClickListener itemClickListener;
    //private static final String TAG = SongViewHolder.class.getName();

    QueueViewHolder(@NonNull View itemView, QueueRecyclerViewAdapter.ItemClickListener itemClickListener) {
        super(itemView);
        this.songTitle = itemView.findViewById(R.id.song_title);
        this.albumTitle = itemView.findViewById(R.id.album_title);
        this.itemClickListener = itemClickListener;
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(itemClickListener != null){
            itemClickListener.onItemClick(v, getAdapterPosition());
        }
    }
}