package com.osiris.utility;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class CacheManager {
    private static CacheManager instance = null;

    private SharedPreferences sharedPreferences;

    private CacheManager(Activity activity)
    {
        sharedPreferences = activity.getPreferences(Context.MODE_PRIVATE);
    }

    public void writeBool(String key, boolean value){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public boolean readBool(String key, boolean defaultValue){
       return sharedPreferences.getBoolean(key, defaultValue);
    }

    public void writeString(String key, String value){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public String readString(String key, String defaultValue){
        return sharedPreferences.getString(key, defaultValue);
    }

    public static CacheManager getInstance(Activity activity)
    {
        if (instance == null)
            instance = new CacheManager(activity);

        return instance;
    }
}
