package com.mobility.android.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.preference.PreferenceManager;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.mobility.android.AppApplication;

import timber.log.Timber;

public final class MapUtils {

    private MapUtils() {
    }

    // ===================================== PREFERENCES ===========================================

    private static final String PREF_DEFAULT_MAP_POSITION = "pref_default_map_position";

    private static final String PREF_MAP_OVERLAY = "pref_map_overlay";

    private static final String PREF_MAP_TYPE = "pref_map_type";


    // ======================================== CAMERA =============================================

    public static CameraUpdate defaultCamera() {
        return CameraUpdateFactory.newLatLngZoom(new LatLng(46.58, 11.25), 10);
    }

    private static CameraUpdate cameraBz() {
        return CameraUpdateFactory.newLatLngZoom(new LatLng(46.486, 11.333), 12);
    }

    private static CameraUpdate cameraMe() {
        return CameraUpdateFactory.newLatLngZoom(new LatLng(46.6532, 11.1606), 12);
    }

    public static CameraUpdate getDefaultPosition(Context context) {
        int position = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREF_DEFAULT_MAP_POSITION, "1"));

        switch (position) {
            case 2:
                return cameraBz();
            case 3:
                return cameraMe();
            default:
                return defaultCamera();
        }
    }


    // ======================================== CAMERA =============================================

    private static final String MAP_STYLE_DEFAULT = "[]";

    public static MapStyleOptions getStyle(Context context) {
        return new MapStyleOptions(MAP_STYLE_DEFAULT);
    }


    // ======================================= SETTINGS ============================================

    /**
     * Returns an integer representing the map type to use. These match with the constants
     * in {@link com.google.android.gms.maps.GoogleMap}.
     *
     * @param context Context to be used to lookup the {@link SharedPreferences}.
     * @return an integer representing the map type.
     */
    public static int getType(Context context) {
        return Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREF_MAP_TYPE, "1"));
    }

    public static boolean areOverlaysEnabled(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(PREF_MAP_OVERLAY, true);
    }


    // ======================================== VARIOUS ============================================

    public static Location getLastKnownLocation(Activity activity) {
        if (!DeviceUtils.hasPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)) {
            Timber.e("Missing location permission");
            return null;
        }

        GoogleApiClient client = ((AppApplication) activity.getApplication()).getGoogleApiClient();
        if (!client.isConnected()) {
            Timber.e("Client not yet connected, returning null");
            return null;
        }

        //noinspection MissingPermission
        return LocationServices.FusedLocationApi.getLastLocation(client);
    }

    public static float distance(double lat1, double lon1, double lat2, double lon2) {
        Location l1 = new Location("");
        l1.setLatitude(lat1);
        l1.setLongitude(lon1);

        Location l2 = new Location("");
        l2.setLatitude(lat2);
        l2.setLongitude(lon2);

        return l1.distanceTo(l2);
    }

    public static float distance(LatLng start, LatLng stop) {
        return distance(start.latitude, start.longitude, stop.latitude, stop.longitude);
    }
}
