package com.mobility.android.receiver;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.mobility.android.beacon.BeaconHandler;
import com.mobility.android.beacon.BeaconService;
import com.mobility.android.util.Utils;

import timber.log.Timber;

public class BluetoothReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Timber.e("onReceive()");

        if (!Utils.areBeaconsEnabled(context)) {
            Timber.e("Beacon scanning not available or enabled");

            if (BeaconHandler.isListening) {
                BeaconHandler.get(context).stopListening();
            }

            completeWakefulIntent(intent);
        } else {
            Timber.e("Starting beacon service");
            startWakefulService(context, new Intent(context, BeaconService.class));
        }
    }
}