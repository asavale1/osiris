package com.osiris.ui;

import android.content.Context;
import android.content.SharedPreferences;
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
import com.osiris.MainActivity;
import com.osiris.R;
import com.osiris.api.CreatePlaylistAsync;
import com.osiris.api.RESTClient;
import com.osiris.api.listeners.CreatePlaylistAsyncListener;
import com.osiris.utility.CacheManager;

import javax.net.ssl.HttpsURLConnection;

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
            Log.i(TAG, "Cancel create");
            getFragmentManager().popBackStack();
        }
    };

    View.OnClickListener createClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            JsonObject playlistJson = new JsonObject();
            playlistJson.addProperty("title", playlistTitle.getText().toString());
            playlistJson.addProperty("userId", "5bb150e937a28b2c670644e2");

            new CreatePlaylistAsync(playlistJson, new CreatePlaylistAsyncListener() {
                @Override
                public void onComplete(RESTClient.RESTResponse response) {
                    Log.i(TAG, response.getData());

                    if(response.getStatus() == HttpsURLConnection.HTTP_CREATED){
                        /*SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putBoolean(getString(R.string.cache_reload_playlists), true);
                        editor.commit();*/
                        CacheManager.getInstance(getActivity()).writeBool(getString(R.string.cache_reload_playlists), true);
                        Log.i(TAG, "Success");

                        getFragmentManager().popBackStack();

                    }else{
                        Log.i(TAG, "Failure");
                    }
                }
            }).execute();
        }
    };
}
