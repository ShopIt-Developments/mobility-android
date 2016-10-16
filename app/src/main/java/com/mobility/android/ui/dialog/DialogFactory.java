package com.mobility.android.ui.dialog;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RatingBar;

import com.mobility.android.R;

/**
 * Created on 16.10.2016.
 *
 * @author Martin
 */

public class DialogFactory {

    private DialogFactory() {}

    public static void showRatingDialog(Context context, RateListener listener) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_rate, null);
        RatingBar ratingBar = (RatingBar) view.findViewById(R.id.dialog_rating);

        new AlertDialog.Builder(context)
                .setView(R.layout.dialog_rate)
                .setPositiveButton("Rate", (dialog1, which) -> {
                    listener.onRate(ratingBar.getRating());
                    dialog1.dismiss();
                })
                .setTitle("Please leave a review!")
                .create()
                .show();
    }

    interface RateListener {
        void onRate(float stars);
    }

}
