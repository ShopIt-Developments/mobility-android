package com.mobility.android.beacon.bus;

import java.util.Date;
import java.util.Locale;

public class BusBeacon {

    private static final float POINTS_PER_SECOND = 10F / 60F;

    public String title;

    public final int id;

    private final long startTimeMillis;

    private int points;

    double distance;

    long seenSeconds;

    BusBeacon(int id) {
        this.id = id;

        startTimeMillis = new Date().getTime();

        seen();
    }

    void seen() {
        seenSeconds = (System.currentTimeMillis() - getStartDate().getTime()) / 1000;
        points = (int) (seenSeconds * POINTS_PER_SECOND);
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        int seenMinutes = (int) Math.floor(seenSeconds);

        return String.format(Locale.getDefault(),
                "%d minute in bus, currently %s points", seenMinutes, points);
    }

    Date getStartDate() {
        return new Date(startTimeMillis);
    }
}