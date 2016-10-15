package com.mobility.android.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.mobility.android.R;

import timber.log.Timber;

public final class UIUtils {

    private static Bitmap sIcon;

    private UIUtils() {
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void styleRecentTasksEntry(Activity activity, int color) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return;
        }

        Resources resources = activity.getResources();
        String label = resources.getString(activity.getApplicationInfo().labelRes);

        if (sIcon == null) {
            // Cache to avoid decoding the same bitmap on every Activity change
            sIcon = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher);
        }

        activity.setTaskDescription(new ActivityManager.TaskDescription(label, sIcon, color));
    }

    public static void hideKeyboard(Activity activity) {
        View focus = activity.getCurrentFocus();

        if (focus == null) {
            Timber.e("Tried to hide keyboard but there is no focused window");
            return;
        }

        InputMethodManager inputMethodManager = (InputMethodManager)
                activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(focus.getWindowToken(), 0);
    }

    // ====================================== DIALOGS ==============================================

    public static void okDialog(Context context, @StringRes int title, @StringRes int message) {
        okDialog(context, title, message, (dialogInterface, i) -> dialogInterface.dismiss());
    }

    public static void okDialog(Context context, @StringRes int title, @StringRes int message,
                                DialogInterface.OnClickListener listener) {
        new AlertDialog.Builder(context, R.style.DialogStyle)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, listener)
                .create()
                .show();
    }

    public static void okDialog(Context context, String title, String message) {
        okDialog(context, title, message, (dialogInterface, i) -> dialogInterface.dismiss());
    }

    public static void okDialog(Context context, String title, String message,
                                DialogInterface.OnClickListener listener) {
        new AlertDialog.Builder(context, R.style.DialogStyle)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, listener)
                .create()
                .show();
    }
}
