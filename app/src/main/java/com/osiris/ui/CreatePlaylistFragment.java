package com.osiris.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.osiris.R;
import com.osiris.api.CreatePlaylistAsync;
import com.osiris.api.RESTClient;
import com.osiris.api.listeners.RESTCallbackListener;
import com.osiris.utility.CacheManager;

import javax.net.ssl.HttpsURLConnection;

public class CreatePlaylistFragment extends Fragment {

    private EditText playlistTitle;
    private TextView errorMessage;

    private static final String TAG = CreatePlaylistFragment.class.getName();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_playlist,
                container, false);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){


        playlistTitle = view.findViewById(R.id.playlist_title);
        errorMessage = view.findViewById(R.id.error_message);
        view.findViewById(R.id.cancel_action).setOnClickListener(cancelClickListener);
        view.findViewById(R.id.create_action).setOnClickListener(createClickListener);
    }

    View.OnClickListener cancelClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            getFragmentManager().popBackStack();
        }
    };

    View.OnClickListener createClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            JsonObject playlistJson = new JsonObject();
            playlistJson.addProperty("title", playlistTitle.getText().toString());
            playlistJson.addProperty("userId", "5bb150e937a28b2c670644e2");

            new CreatePlaylistAsync(playlistJson, new RESTCallbackListener() {
                @Override
                public void onComplete(RESTClient.RESTResponse response) {

                    if(response.getStatus() == HttpsURLConnection.HTTP_CREATED){
                        CacheManager.getInstance(getActivity()).writeBool(getString(R.string.cache_reload_playlists), true);
                        getFragmentManager().popBackStack();

                    }else{
                        JsonParser parser = new JsonParser();
                        JsonObject jsonObject = parser.parse(response.getData()).getAsJsonObject();

                        errorMessage.setText(jsonObject.get("error").getAsString());
                    }
                }
            }).execute();
        }
    };
}
