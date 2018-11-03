package com.osiris.ui.common;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.osiris.R;

class SongViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    TextView songTitle;
    private SongRecyclerViewAdapter.ItemClickListener itemClickListener;
    //private static final String TAG = SongViewHolder.class.getName();

    SongViewHolder(@NonNull View itemView, SongRecyclerViewAdapter.ItemClickListener itemClickListener) {
        super(itemView);
        this.songTitle = itemView.findViewById(R.id.song_title);
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
