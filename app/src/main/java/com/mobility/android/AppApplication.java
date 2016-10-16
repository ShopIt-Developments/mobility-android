package com.mobility.android;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.mobility.android.data.network.RestClient;
import com.mobility.android.data.network.auth.AuthHelper;
import com.mobility.android.fcm.FcmUtils;
import com.mobility.android.receiver.BluetoothReceiver;

import timber.log.Timber;

public class AppApplication extends Application {

    private GoogleApiClient mApiClient;

    @Override
    public void onCreate() {
        super.onCreate();

        Timber.plant(new Timber.DebugTree());
        RestClient.init(this);

        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(AuthHelper.GOOGLE_ID_TOKEN)
                .requestProfile()
                .requestEmail()
                .build();

        //noinspection CodeBlock2Expr
        mApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        Timber.i("Connected to google api");
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        Timber.e("Connection to google api suspended");

                    }
                })
                .addOnConnectionFailedListener(connectionResult -> {
                    Timber.e("Connection to google api failed: %s", connectionResult);
                })
                .addApi(LocationServices.API)
                .addApi(Auth.GOOGLE_SIGN_IN_API, options)
                .build();

        mApiClient.connect();

        FcmUtils.sendTokenIfNeeded(this);

        startBeacons();
    }

    private void startBeacons() {
        Timber.e("Starting beacons");
        sendBroadcast(new Intent(this, BluetoothReceiver.class));
    }

    public GoogleApiClient getGoogleApiClient() {
        return mApiClient;
    }
}
