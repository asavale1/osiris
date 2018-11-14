package com.osiris.ui.common;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.osiris.R;
import com.osiris.model.AlbumModel;

import java.util.List;

public class AlbumRecyclerViewAdapter extends RecyclerView.Adapter<AlbumViewHolder> {

    private LayoutInflater layoutInflater;
    private List<AlbumModel> albums;
    private ItemClickListener itemClickListener;
    private ItemLongClickListener itemLongClickListener;

    public AlbumRecyclerViewAdapter(Context context, List<AlbumModel> albums, ItemClickListener itemClickListener, ItemLongClickListener itemLongClickListener) {
        this.layoutInflater = LayoutInflater.from(context);
        this.albums = albums;
        this.itemClickListener = itemClickListener;
        this.itemLongClickListener = itemLongClickListener;
    }

    @NonNull
    @Override
    public AlbumViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = layoutInflater.inflate(R.layout.recyclerview_item_album, viewGroup, false);
        return new AlbumViewHolder(view, itemClickListener, itemLongClickListener);
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

    public interface ItemLongClickListener{
        void onItemLongClick(View view, int position);
    }

}
