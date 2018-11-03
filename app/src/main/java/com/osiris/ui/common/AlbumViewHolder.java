package com.osiris.ui.common;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.osiris.R;

public class AlbumViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    TextView albumTitle;
    private AlbumRecyclerViewAdapter.ItemClickListener itemClickListener;
    //private static final String TAG = AlbumViewHolder.class.getName();

    AlbumViewHolder(@NonNull View itemView, AlbumRecyclerViewAdapter.ItemClickListener itemClickListener) {
        super(itemView);
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
