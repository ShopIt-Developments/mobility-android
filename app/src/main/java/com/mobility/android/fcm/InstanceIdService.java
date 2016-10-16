package com.mobility.android.fcm;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import timber.log.Timber;

public class InstanceIdService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        String token = FirebaseInstanceId.getInstance().getToken();

        Timber.e("Got token: %s", token);

        FcmSettings.setToken(this, token);
        FcmUtils.sendTokenIfNeeded(this);

        Timber.e("FCM registration completed");
    }
}