package ercanduman.taskdemo;

import android.app.Application;

import ercanduman.taskdemo.util.Preferences;

public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Preferences preferences = new Preferences(this);
        preferences.setSavedTime(String.valueOf(System.currentTimeMillis()));
    }
}
