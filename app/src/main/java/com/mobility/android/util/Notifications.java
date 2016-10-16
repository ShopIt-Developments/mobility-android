package com.mobility.android.util;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.mobility.android.R;
import com.mobility.android.beacon.bus.BusBeacon;
import com.mobility.android.data.network.response.TripsResponse;
import com.mobility.android.ui.MainActivity;
import com.trello.rxlifecycle.internal.Preconditions;

public class Notifications {

    private static final int TRIP_PROGRESS = 1 << 10;
    private static final int TRIP_COMPLETE = 1 << 9;

    public static void tripInProgress(Context context, BusBeacon busBeacon) {
        Preconditions.checkNotNull(context, "context == null");
        Preconditions.checkNotNull(busBeacon, "busBeacon == null");

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_directions_bus_white_24dp)
                .setContentTitle(busBeacon.getTitle())
                .setContentText(busBeacon.getContent())
                .setAutoCancel(false)
                .setOngoing(true)
                .setLights(Color.GREEN, 500, 5000)
                .setColor(ContextCompat.getColor(context, R.color.accent))
                .setCategory(NotificationCompat.CATEGORY_EVENT);

        Intent resultIntent = new Intent(context, MainActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(context,
                TRIP_PROGRESS,
                resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(TRIP_PROGRESS, mBuilder.build());
    }

    public static void tripComplete(Context context, TripsResponse response) {
        Preconditions.checkNotNull(context, "context == null");
        Preconditions.checkNotNull(response, "response == null");

        int minutes = (int) Math.floor(response.duration);

        String title = "Trip complete!";
        String content = String.format("You earned %s points for driving %s' with the bus.",
                response.points, minutes);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_directions_bus_white_24dp)
                .setContentTitle(title)
                .setContentText(content)
                .setAutoCancel(true)
                .setLights(Color.GREEN, 500, 5000)
                .setColor(ContextCompat.getColor(context, R.color.accent))
                .setCategory(NotificationCompat.CATEGORY_EVENT);

        Intent resultIntent = new Intent(context, MainActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(context,
                TRIP_COMPLETE,
                resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(TRIP_COMPLETE, mBuilder.build());
    }

    public static void cancelBus(Context context) {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(TRIP_PROGRESS);
    }
}
