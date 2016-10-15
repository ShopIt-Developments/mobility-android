package com.mobility.android.fcm;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

public final class FcmSettings {

    private static final String PREF_GCM_TOKEN = "pref_gcm_token";
    private static final String PREF_SHOULD_SEND_TOKEN = "pref_should_send_token";

    private FcmSettings() {
    }

    static void setToken(Context context, String token) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putString(PREF_GCM_TOKEN, token).apply();
    }

    @Nullable
    public static String getToken(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_GCM_TOKEN, null);
    }

    static void sentToken(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putBoolean(PREF_SHOULD_SEND_TOKEN, false).apply();
    }

    @Nullable
    public static boolean shouldSendToken(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(PREF_SHOULD_SEND_TOKEN, true);
    }
}
