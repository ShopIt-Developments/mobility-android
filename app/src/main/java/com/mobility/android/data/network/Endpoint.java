package com.mobility.android.data.network;

public final class Endpoint {

    public static final String API = "https://shopit-application.appspot.com/v1/";
    public static final String API_DATA = "https://sasa-bus-data.appspot.com/v1/";

    public static final String MAP_TILES = "map/coordinates/%d/%d/%d/line/%d/variant/%d";
    public static final String MAP_TILES_ALL = "map/coordinates/%d/%d/%d";

    public static final String FCM_TOKEN = "fcm/token/{token}";

    private Endpoint() {
    }
}
