package com.osiris.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.JsonObject;
import com.osiris.R;
import com.osiris.api.CreatePlaylistAsync;
import com.osiris.api.listeners.CreatePlaylistAsyncListener;

public class CreatePlaylistFragment extends Fragment {

    private View view;
    private EditText playlistTitle;
    private Button cancel, create;

    private static final String TAG = CreatePlaylistFragment.class.getName();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_create_playlist,
                container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        playlistTitle = view.findViewById(R.id.playlist_title);
        cancel = view.findViewById(R.id.cancel_action);
        cancel.setOnClickListener(cancelClickListener);
        create = view.findViewById(R.id.create_action);
        create.setOnClickListener(createClickListener);
    }

    View.OnClickListener cancelClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };

    View.OnClickListener createClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            JsonObject playlistJson = new JsonObject();
            playlistJson.addProperty("title", playlistTitle.getText().toString());
            //playlistJson.addProperty("userId", "5bb150e937a28b2c6706c4e2");

            new CreatePlaylistAsync(playlistJson, new CreatePlaylistAsyncListener() {
                @Override
                public void createdPlaylist(String result) {
                    Log.i(TAG, result);
                }
            }).execute();
        }
    };
}
