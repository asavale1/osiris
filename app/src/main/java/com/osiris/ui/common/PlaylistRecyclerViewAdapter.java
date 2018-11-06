package com.osiris.ui.common;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.osiris.R;
import com.osiris.model.PlaylistModel;

import java.util.List;

public class PlaylistRecyclerViewAdapter extends RecyclerView.Adapter<PlaylistViewHolder> {

    private LayoutInflater layoutInflater;
    private List<PlaylistModel> playlists;
    private ItemClickListener itemClickListener;
    private ItemLongClickListener itemLongClickListener;

    //private final static String TAG = PlaylistRecyclerViewAdapter.class.getName();

    public PlaylistRecyclerViewAdapter(Context context, List<PlaylistModel> playlists,
                                       ItemClickListener itemClickListener, ItemLongClickListener itemLongClickListener) {
        this.layoutInflater = LayoutInflater.from(context);
        this.playlists = playlists;
        this.itemClickListener = itemClickListener;
        this.itemLongClickListener = itemLongClickListener;
    }

    @NonNull
    @Override
    public PlaylistViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = layoutInflater.inflate(R.layout.recyclerview_item_playlist, viewGroup, false);
        return new PlaylistViewHolder(view, itemClickListener, itemLongClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistViewHolder playlistViewHolder, int i) {
        playlistViewHolder.playlistTitle.setText(playlists.get(i).getTitle());
    }

    @Override
    public int getItemCount() {
        return playlists.size();
    }

    public interface ItemClickListener{
        void onItemClick(View view, int position);
    }

    public interface ItemLongClickListener{
        void onItemLongClick(View view, int position);
    }


}
