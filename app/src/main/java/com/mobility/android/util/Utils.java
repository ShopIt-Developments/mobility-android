package com.mobility.android.util;

import android.content.Context;
import android.os.Build;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.mobility.android.BuildConfig;
import com.trello.rxlifecycle.internal.Preconditions;

/**
 * Utility class which holds various methods to help with things like logging exceptions.
 *
 * @author Alex Lardschneider
 */
public final class Utils {

    private Utils() {
    }

    public static String locale(Context context) {
        Preconditions.checkNotNull(context, "context == null");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return context.getResources().getConfiguration().getLocales().get(0).getLanguage();
        }

        //noinspection deprecation
        return context.getResources().getConfiguration().locale.getLanguage();
    }

    public static String localeDeIt(Context context) {
        Preconditions.checkNotNull(context, "context == null");

        String locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = context.getResources().getConfiguration().getLocales().get(0).getLanguage();
        } else {
            //noinspection deprecation
            locale = context.getResources().getConfiguration().locale.getLanguage();
        }

        if (locale.equals("de")) {
            return locale;
        } else {
            return "it";
        }
    }

    /**
     * Logs a {@link Throwable}.
     *
     * @param t the {@link Throwable} to log.
     */
    public static void logException(Throwable t) {
        if (BuildConfig.DEBUG) {
            t.printStackTrace();
        }
    }

    /**
     * Returns the play services connection status.
     *
     * @param context Context to access Google Play apis.
     * @return an {@link Integer} representing the connection status.
     * @see ConnectionResult#SUCCESS
     * @see ConnectionResult#API_UNAVAILABLE
     */
    public static int getPlayServicesStatus(Context context) {
        return GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context);
    }

    public static boolean isBrokenSamsungDevice() {
        return Build.MANUFACTURER.equalsIgnoreCase("samsung")
                && isBetweenAndroidVersions(
                Build.VERSION_CODES.LOLLIPOP,
                Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    private static boolean isBetweenAndroidVersions(int min, int max) {
        return Build.VERSION.SDK_INT >= min && Build.VERSION.SDK_INT <= max;
    }

    public static int getToolbarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
