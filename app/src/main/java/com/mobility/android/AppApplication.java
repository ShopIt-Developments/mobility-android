package com.mobility.android;

import android.app.Application;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.mobility.android.data.network.RestClient;

import timber.log.Timber;

public class AppApplication extends Application {

    private GoogleApiClient mApiClient;

    private GoogleSignInOptions signInOptions;

    private static final String GOOGLE_ID_TOKEN =
            "344692989639-t2q93r2tme7loa1vke8huu8h4r6ng765.apps.googleusercontent.com";

    @Override
    public void onCreate() {
        super.onCreate();

        Timber.plant(new Timber.DebugTree());

        RestClient.init(this);

        signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(GOOGLE_ID_TOKEN)
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
                .addApi(Auth.GOOGLE_SIGN_IN_API, signInOptions)
                .build();

        mApiClient.connect();
    }

    public GoogleApiClient getGoogleApiClient() {
        return mApiClient;
    }

    public GoogleSignInOptions getSignInOptions() {
        return signInOptions;
    }
}
