package com.mobility.android.fcm;

import android.content.Context;

import com.mobility.android.data.network.RestClient;
import com.mobility.android.data.network.api.FcmApi;

import rx.Observer;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class FcmUtils {

    public static void uploadToken(Context context, String token) {
        Timber.w("Uploading FCM token");

        FcmApi api = RestClient.ADAPTER.create(FcmApi.class);
        api.uploadToken(token)
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
