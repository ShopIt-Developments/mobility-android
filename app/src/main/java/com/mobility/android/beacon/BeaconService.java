package com.mobility.android.beacon;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import timber.log.Timber;

/**
 * Service which runs in the background and keeps the beacon handlers alive.
 *
 * @author Alex Lardschneider
 */
public class BeaconService extends Service {

    private BeaconHandler beaconHandler;

    @Override
    public void onCreate() {
        super.onCreate();

        Timber.e("onCreate()");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Timber.e("onStartCommand()");

        beaconHandler = BeaconHandler.get(getApplication());
        beaconHandler.startListening();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Timber.e("onDestroy()");

        if (beaconHandler != null) {
            beaconHandler.stopListening();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}