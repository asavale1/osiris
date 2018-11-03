package com.osiris.ui.common;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.osiris.R;

import java.util.List;

public class AlbumRecyclerViewAdapter extends RecyclerView.Adapter<AlbumViewHolder> {

    private LayoutInflater layoutInflater;
    private List<AlbumModel> albums;
    private ItemClickListener itemClickListener;

    // data is passed into the constructor
    public AlbumRecyclerViewAdapter(Context context, List<AlbumModel> albums, ItemClickListener itemClickListener) {
        this.layoutInflater = LayoutInflater.from(context);
        this.albums = albums;
        this.itemClickListener = itemClickListener;
    }


    @NonNull
    @Override
    public AlbumViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = layoutInflater.inflate(R.layout.recyclerview_item_album, viewGroup, false);
        return new AlbumViewHolder(view, itemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumViewHolder albumViewHolder, int i) {
        albumViewHolder.albumTitle.setText(albums.get(i).getTitle());
    }

    @Override
    public int getItemCount() {
        return albums.size();
    }

    public interface ItemClickListener{
        void onItemClick(View view, int position);
    }

}
