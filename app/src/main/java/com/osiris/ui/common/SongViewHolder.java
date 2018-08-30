package com.osiris.ui.common;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.osiris.R;

class SongViewHolder extends RecyclerView.ViewHolder {

    TextView songTitle;

    SongViewHolder(@NonNull View itemView) {
        super(itemView);
        songTitle = (TextView) itemView.findViewById(R.id.song_title);
    }

}
