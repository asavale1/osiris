package com.osiris.ui.common;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.osiris.R;

class PlaylistViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    TextView playlistTitle;
    private PlaylistRecyclerViewAdapter.ItemClickListener itemClickListener;
    private static final String TAG = PlaylistViewHolder.class.getName();

    PlaylistViewHolder(@NonNull View itemView, PlaylistRecyclerViewAdapter.ItemClickListener itemClickListener) {
        super(itemView);
        this.playlistTitle = (TextView) itemView.findViewById(R.id.playlist_title);
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
