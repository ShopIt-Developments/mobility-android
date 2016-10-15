package com.mobility.android;

import android.app.Application;

import com.mobility.android.data.network.RestClient;

import timber.log.Timber;

public class AppApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Timber.plant(new Timber.DebugTree());

        RestClient.init(this);
    }
}
