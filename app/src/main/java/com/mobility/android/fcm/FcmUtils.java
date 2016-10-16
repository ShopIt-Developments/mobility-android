package com.mobility.android.fcm;

import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mobility.android.data.network.RestClient;
import com.mobility.android.data.network.api.FcmApi;

import rx.Observer;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class FcmUtils {

    public static void sendTokenIfNeeded(Context context) {
        String token = FcmSettings.getToken(context);
        if (token == null) {
            Timber.w("FCM token doesn't exist");
            return;
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Timber.w("Not sending FCM token because we don't have a user yet");
            return;
        }

        if (!FcmSettings.shouldSendToken(context)) {
            Timber.i("FCM token already sent");
            return;
        }

        Timber.w("Uploading FCM token");

        FcmApi fcmApi = RestClient.ADAPTER.create(FcmApi.class);
        fcmApi.uploadToken(token)
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<Void>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e, "Could not upload FCM token");
                    }

                    @Override
                    public void onNext(Void mapResponse) {
                        Timber.e("Uploaded FCM token");
                        FcmSettings.sentToken(context);
                    }
                });
    }
}
