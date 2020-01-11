package com.anguel.dissertation;

import android.app.Application;

import com.anguel.dissertation.serviceengine.ServiceEngine;
import com.jakewharton.threetenabp.AndroidThreeTen;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AndroidThreeTen.init(this);
        ServiceEngine.getInstance(this);
    }
}
