package com.mobility.android.fcm.command;

import android.content.Context;
import android.support.annotation.NonNull;

import com.mobility.android.util.Notifications;

import java.util.Map;

import timber.log.Timber;

public class ActionCommand implements FcmCommand {

    @Override
    public void execute(Context context, @NonNull Map<String, String> data) {
        Timber.e("Received GCM test message: extraData=%s", data);

        String action = data.get("action");

        if (action == null) {
            Timber.e("Called action receiver with a null action");
            return;
        }

        Timber.w("Got command action: %s", action);

        switch (action) {
            case "payment_successful":
                paymentSuccessful();
                break;
            case "payment_initiate":
                initiatePayment(context, data);
                break;
        }
    }

    private void paymentSuccessful() {

    }

    private void initiatePayment(Context context, @NonNull Map<String, String> data) {
        String qrCode = data.get("qr_code");
        String name = data.get("username");

        Timber.w("Got initiate payment command: qrCode=%s, name=%s", qrCode, name);

        Notifications.initiatePayment(context, qrCode, name);
    }
}
