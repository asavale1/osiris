package com.osiris.ui.common;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.osiris.R;

class PlaylistViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{

    TextView playlistTitle;
    private PlaylistRecyclerViewAdapter.ItemClickListener itemClickListener;
    private PlaylistRecyclerViewAdapter.ItemLongClickListener itemLongClickListener;
    //private static final String TAG = PlaylistViewHolder.class.getName();

    PlaylistViewHolder(@NonNull View itemView,
                       PlaylistRecyclerViewAdapter.ItemClickListener itemClickListener,
                       PlaylistRecyclerViewAdapter.ItemLongClickListener itemLongClickListener) {
        super(itemView);
        this.playlistTitle = itemView.findViewById(R.id.playlist_title);
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
