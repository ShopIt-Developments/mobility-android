package com.mobility.android.fcm.command;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;

import com.mobility.android.ui.payment.deliverer.ShowQrCodeActivity;
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
                paymentSuccessful(context, data);
                break;
            case "payment_initiate":
                initiatePayment(context, data);
                break;
        }
    }

    private void paymentSuccessful(Context context, @NonNull Map<String, String> data) {
        Intent filter = new Intent(ShowQrCodeActivity.ACTION_PAYMENT_COMPLETE);
        filter.putExtra(ShowQrCodeActivity.EXTRA_PRICE, data.get("price"));

        LocalBroadcastManager.getInstance(context).sendBroadcast(filter);
    }

    private void initiatePayment(Context context, @NonNull Map<String, String> data) {
        String qrCode = data.get("qr_code");
        String name = data.get("username");

        Timber.w("Got initiate payment command: qrCode=%s, name=%s", qrCode, name);

        Notifications.initiatePayment(context, qrCode, name);
    }
}
