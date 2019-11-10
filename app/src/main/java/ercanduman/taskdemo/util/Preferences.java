package ercanduman.taskdemo.util;

import android.content.Context;
import android.content.SharedPreferences;

public class Preferences {
    private static final String SHARED_PREFS = "ercanduman.taskdemo.SHARED_PREFS";
    private static final String SAVED_TIME = "ercanduman.taskdemo.SAVED_TIME";

    private SharedPreferences preferences;

    public Preferences() {
    }

    public Preferences(Context context) {
        preferences = context.getApplicationContext().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
    }

    public void setSavedTime(String savedTime) {
        preferences.edit().putString(SAVED_TIME, savedTime).apply();
    }

    public String getSavedTime() {
        return preferences.getString(SAVED_TIME, String.valueOf(System.currentTimeMillis()));
    }
}
