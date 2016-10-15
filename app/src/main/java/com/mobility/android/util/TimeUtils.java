package com.mobility.android.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import timber.log.Timber;

public class TimeUtils {

    private static final SimpleDateFormat ISO_8601 =
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);


    public static String getCurrentIsoTime() {
        return ISO_8601.format(new Date());
    }

    public static String isoToFormat(String isoDate, String newFormat) {
        SimpleDateFormat format = new SimpleDateFormat(newFormat, Locale.getDefault());

        try {
            Date date = ISO_8601.parse(isoDate);
            return format.format(date);
        } catch (ParseException e) {
            Timber.e(e, "Unable to parse date. date=%s, format=%s", isoDate, newFormat);
            e.printStackTrace();
        }

        return "";
    }

}
