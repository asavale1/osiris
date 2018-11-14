package com.osiris.ui.library;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.osiris.R;
import com.osiris.api.GetUser;
import com.osiris.api.RESTClient;
import com.osiris.api.UpdateUser;
import com.osiris.api.listeners.RESTCallbackListener;
import com.osiris.constants.JsonConstants;
import com.osiris.model.ModelParser;
import com.osiris.model.UserModel;
import com.osiris.utility.CacheManager;

import javax.net.ssl.HttpsURLConnection;

public class UserSettingsFragment extends Fragment {

    private static final String TAG = UserSettingsFragment.class.getName();
    private UserModel user;
    private EditText username;
    private String userId;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        userId = CacheManager.getInstance(getActivity()).readString(getString(R.string.cache_user_id), "");
        return inflater.inflate(R.layout.fragment_user_settings,
                container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        username = view.findViewById(R.id.username);
        view.findViewById(R.id.submit_action).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUser();
            }
        });

        getUser();

    }

    private void getUser(){
        new GetUser(
                userId,
                new RESTCallbackListener() {
                    @Override
                    public void onComplete(RESTClient.RESTResponse response) {
                        if(response.getStatus() == HttpsURLConnection.HTTP_OK){
                            JsonParser parser = new JsonParser();
                            JsonObject userJson = parser.parse(response.getData()).getAsJsonObject();
                            user = ModelParser.parseUserModelJson(userJson);
                            buildUI();
                        }
                    }
                }).execute();
    }

    private void updateUser(){

        JsonObject userJson = new JsonObject();
        userJson.addProperty(JsonConstants.USERNAME, username.getText().toString());

        new UpdateUser(userId, userJson, new RESTCallbackListener() {
            @Override
            public void onComplete(RESTClient.RESTResponse response) {

                if(response.getStatus() == HttpsURLConnection.HTTP_OK){
                    Toast.makeText(getContext(), "Info updated", Toast.LENGTH_SHORT).show();
                    CacheManager.getInstance(getActivity()).writeString(getString(R.string.cache_user_username), username.getText().toString());
                }else{
                    JsonParser parser = new JsonParser();
                    JsonObject errorJson = parser.parse(response.getData()).getAsJsonObject();
                    Toast.makeText(getContext(), errorJson.get(JsonConstants.ERROR).getAsString(), Toast.LENGTH_SHORT).show();
                }
            }
        }).execute();
    }

    private void buildUI(){
        if(!user.getUsername().isEmpty()){
            username.setText(user.getUsername());
        }
    }

}
