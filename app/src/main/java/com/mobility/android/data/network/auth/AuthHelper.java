package com.mobility.android.data.network.auth;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import timber.log.Timber;

public class AuthHelper {

    private static final String PREF_AUTH_TOKEN = "pref_auth_token";
    private static final String PREF_USER_ID = "pref_user_id";

    @SuppressLint("StaticFieldLeak")
    private static Context sContext;

    // ====================================== PREFERENCES ==========================================

    @Nullable
    public static String getUserId(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREF_USER_ID, null);
    }

    private static void setUserId(Context context, String userId) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putString(PREF_USER_ID, userId).apply();
    }

    @Nullable
    private static String getAuthToken(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREF_AUTH_TOKEN, null);
    }

    private static void setAuthToken(Context context, String token) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putString(PREF_AUTH_TOKEN, token).apply();
    }


    // =================================== TOKEN VERIFICATION ======================================

    public static void init(Context context) {
        sContext = context;
    }

    public static String getTokenIfValid() {
        if (isLoggedIn()) {
            return getAuthToken(sContext);
        }

        return null;
    }

    public static void setInitialToken() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        if (user == null) {
            Timber.e("We do not have a logged in user. " +
                    "Did you forget to call signInWithEmailAndPassword() first?");
            return;
        }

        user.getToken(true).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Timber.w("User token fetch successful");

                String token = task.getResult().getToken();
                setAuthToken(sContext, token);
            } else {
                Timber.e(task.getException(), "Could not fetch user token");
            }
        });
    }

    public static boolean isLoggedIn() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        return auth.getCurrentUser() != null;
    }
}
