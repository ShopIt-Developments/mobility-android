package com.mobility.android.data.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.trello.rxlifecycle.internal.Preconditions;

public final class NetUtils {

    private NetUtils() {
    }

    /**
     * Indicates whether network connectivity exists and it is possible to establish
     * connections and pass data.
     *
     * @param context AppApplication context
     * @return {@code true} if network connectivity exists, {@code false} otherwise.
     */
    public static boolean isOnline(Context context) {
        Preconditions.checkNotNull(context, "isOnline() context == null");

        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
        }

        return false;
    }
}
