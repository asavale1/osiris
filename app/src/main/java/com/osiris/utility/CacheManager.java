package com.osiris.utility;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class CacheManager {
    // static variable single_instance of type Singleton
    private static CacheManager instance = null;

    private SharedPreferences sharedPreferences;

    // private constructor restricted to this class itself
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


    // static method to create instance of Singleton class
    public static CacheManager getInstance(Activity activity)
    {
        if (instance == null)
            instance = new CacheManager(activity);

        return instance;
    }
}
