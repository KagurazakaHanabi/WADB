package com.yaerin.wadb;

import android.app.Application;

import com.yaerin.support.util.Crashlytics;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Thread.setDefaultUncaughtExceptionHandler(new Crashlytics(this));
    }
}
