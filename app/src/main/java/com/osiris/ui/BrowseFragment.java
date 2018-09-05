package com.osiris.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.osiris.R;

public class BrowseFragment extends Fragment {

    private View view;
    private static final String TAG = BrowseFragment.class.getName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_browse,
                container, false);
        Log.i(TAG, "In onCreateView");
        return view;
    }
}
