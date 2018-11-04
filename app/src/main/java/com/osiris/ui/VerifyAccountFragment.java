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
import com.osiris.MainActivity;
import com.osiris.R;
import com.osiris.api.RESTClient;
import com.osiris.api.VerifyAccountAsync;
import com.osiris.api.listeners.RESTCallbackListener;
import com.osiris.constants.FragmentConstants;
import com.osiris.utility.CacheManager;

import javax.net.ssl.HttpsURLConnection;

public class VerifyAccountFragment extends Fragment {

    //private static final String TAG = VerifyAccountFragment.class.getName();

    private EditText verificationPin;
    private TextView errorMessage;
    private MainActivity parentActivity;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        parentActivity = (MainActivity) getActivity();
        return inflater.inflate(R.layout.fragment_verify_account,
                container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState){
        verificationPin = view.findViewById(R.id.verification_pin);
        errorMessage = view.findViewById(R.id.error_message);
        view.findViewById(R.id.submit_action).setOnClickListener(submitClickListener);
    }

    View.OnClickListener submitClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String pin = verificationPin.getText().toString();

            errorMessage.setText(null);

            if(!pin.isEmpty()){
                JsonObject verifyJson = new JsonObject();
                verifyJson.addProperty("pin", Integer.parseInt(verificationPin.getText().toString()));
                new VerifyAccountAsync(verifyJson, new RESTCallbackListener() {
                    @Override
                    public void onComplete(RESTClient.RESTResponse response) {

                        JsonParser parser = new JsonParser();
                        JsonObject jsonObject = parser.parse(response.getData()).getAsJsonObject();

                        if(response.getStatus() == HttpsURLConnection.HTTP_OK){
                            CacheManager.getInstance(parentActivity).writeString(getString(R.string.cache_user_id), jsonObject.get("id").getAsString());
                            parentActivity.replaceFragment(FragmentConstants.FRAGMENT_LIBRARY, null);
                        }else{

                            errorMessage.setText(jsonObject.get("error").getAsString());
                        }

                    }
                }).execute();
            }else{
                errorMessage.setText(getString(R.string.specify_pin));
            }

        }
    };
}
