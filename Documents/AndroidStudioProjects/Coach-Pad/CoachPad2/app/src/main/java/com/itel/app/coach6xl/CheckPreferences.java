package com.itel.app.coach6xl;

import android.content.SharedPreferences;
import android.util.Log;


public class CheckPreferences {
    private static final int PREFERENCE_MODE_PRIVATE = 0;
    private static final String ITEL_PREF_FILE = "APP_PREF_FILE";

    public SharedPreferences SharedPreferenceSettings;
    private SharedPreferences.Editor preferenceEditor;

    public CheckPreferences(SharedPreferences preferenceSettings) {
        this.SharedPreferenceSettings = preferenceSettings;
    }

    public void set(String userName) {

        //preferenceSettings = getSharedPreferences(ITEL_PREF_FILE, PREFERENCE_MODE_PRIVATE);
        preferenceEditor = SharedPreferenceSettings.edit();

        preferenceEditor.putString("userName", userName);
        preferenceEditor.apply();

        String username = SharedPreferenceSettings.getString("userName", "No user name");
        Log.d("SP User Name", username);


    }

    public void check(String userName) {
        String username = SharedPreferenceSettings.getString("userName", "None");
        Log.d("SP User Name", username);

        if (username.equals("None"))
        {
            preferenceEditor = SharedPreferenceSettings.edit();
            preferenceEditor.putString("userName", userName);
            preferenceEditor.apply();
            Log.d("New User Name", userName);
        }
        else if (!username.equals(userName)) {
            Log.d("Incorrect Reg Name", username);
        }



    }



}
