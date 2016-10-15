package com.mobility.android.fcm;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessaging;

import timber.log.Timber;

public class InstanceIdService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        String token = FirebaseInstanceId.getInstance().getToken();

        Timber.e("Got token: %s", token);

        FcmSettings.setToken(this, token);

        FirebaseMessaging.getInstance().subscribeToTopic("general");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && FcmSettings.shouldSendToken(this)) {
            FcmUtils.uploadToken(this, token);
        }

        Timber.e("Registration completed");
    }
}