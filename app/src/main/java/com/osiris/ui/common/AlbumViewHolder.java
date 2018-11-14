package com.osiris.ui.common;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.osiris.R;

public class AlbumViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{

    TextView albumTitle;
    private AlbumRecyclerViewAdapter.ItemClickListener itemClickListener;
    private AlbumRecyclerViewAdapter.ItemLongClickListener itemLongClickListener;

    //private static final String TAG = AlbumViewHolder.class.getName();

    AlbumViewHolder(@NonNull View itemView, AlbumRecyclerViewAdapter.ItemClickListener itemClickListener, AlbumRecyclerViewAdapter.ItemLongClickListener itemLongClickListener) {
        super(itemView);
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
