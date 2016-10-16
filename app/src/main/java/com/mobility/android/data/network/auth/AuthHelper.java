package com.mobility.android.data.network.auth;

import android.support.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AuthHelper {

    public static final String GOOGLE_ID_TOKEN =
            "344692989639-t2q93r2tme7loa1vke8huu8h4r6ng765.apps.googleusercontent.com";

    // ====================================== PREFERENCES ==========================================

    @Nullable
    public static String getUserId() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return user != null ? user.getUid() : null;
    }

    @Nullable
    public static String getUsername() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return user == null ? null : user.getDisplayName();
    }
}
