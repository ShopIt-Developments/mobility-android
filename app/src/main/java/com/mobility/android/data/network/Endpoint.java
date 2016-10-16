package com.mobility.android.data.network;

public final class Endpoint {

    public static final String API = "https://south-tyrol.appspot.com/mobility/";
    public static final String API_DATA = "https://sasa-bus-data.appspot.com/v1/";

    public static final String MAP_TILES = "map/coordinates/%d/%d/%d/line/%d/variant/%d";

    public static final String VEHICLES_AVAILABLE = "vehicles/available";
    public static final String VEHICLES_MY = "vehicles/my";
    public static final String VEHICLES_BOOKED = "vehicles/booked";
    public static final String BUS = "bus/{id}";

    public static final String VEHICLE_ORDER = "order/{vehicle_id}";
    public static final String VEHICLE_ADD = "vehicle";
    public static final String VEHICLE_DELETE = "vehicle/{vehicle_id}";

    public static final String PAYMENT_SCAN = "payment/scan/{order_id}";
    public static final String PAYMENT_ACCEPT = "payment/accept/{order_id}";
    public static final String PAYMENT_INITIATE = "payment/notify/{order_id}";

    public static final String FCM_TOKEN = "token/{token}";
    public static final String USER = "user";

    public static final String TRIP_ADD = "trip";
    public static final String LEADERBOARD = "leaderboard";

    private Endpoint() {
    }
}
