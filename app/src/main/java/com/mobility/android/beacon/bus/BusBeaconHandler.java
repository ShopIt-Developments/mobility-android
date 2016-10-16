package com.mobility.android.beacon.bus;

import android.annotation.SuppressLint;
import android.content.Context;

import com.mobility.android.beacon.IBeaconHandler;
import com.mobility.android.data.network.NetUtils;
import com.mobility.android.data.network.RestClient;
import com.mobility.android.data.network.api.TripsApi;
import com.mobility.android.data.network.api.VehicleApi;
import com.mobility.android.data.network.model.BusObject;
import com.mobility.android.data.network.model.Trip;
import com.mobility.android.data.network.response.TripsResponse;
import com.mobility.android.util.Notifications;
import com.mobility.android.util.TimeUtils;

import org.altbeacon.beacon.Beacon;

import java.util.Collection;

import rx.Observer;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public final class BusBeaconHandler implements IBeaconHandler {

    /**
     * The uuid which identifies a bus beacon.
     */
    public static final String UUID = "e923b236-f2b7-4a83-bb74-cfb7fa44cab8";

    /**
     * the identifier used to identify the region the beacon scanner is listening in.
     */
    public static final String IDENTIFIER = "BUS";

    private final Context mContext;

    @SuppressLint("StaticFieldLeak")
    private static BusBeaconHandler sInstance;

    private BusBeacon busBeacon;

    private BusBeaconHandler(Context context) {
        mContext = context;
    }

    public static BusBeaconHandler getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new BusBeaconHandler(context);
        }

        return sInstance;
    }

    @Override
    public void didRangeBeacons(Collection<Beacon> beacons) {
        for (Beacon beacon : beacons) {
            int major = beacon.getId2().toInt();

            validateBeacon(beacon, major);
        }
    }

    @Override
    public void validateBeacon(Beacon beacon, int major) {
        if (busBeacon != null) {
            busBeacon.seen();
            busBeacon.distance = beacon.getDistance();

            Timber.w("Vehicle %d, seen: %d, distance: %f",
                    major, busBeacon.seenSeconds, busBeacon.distance);

            if (busBeacon.title != null) {
                Notifications.tripInProgress(mContext, busBeacon);
            }
        } else {
            busBeacon = new BusBeacon(major);

            Timber.e("Added vehicle %d", major);

            if (NetUtils.isOnline(mContext)) {
                getBusInformation(busBeacon);
            }
        }
    }

    @Override
    public void stopRanging() {
        Notifications.cancelBus(mContext);

        addTrip(busBeacon);
        busBeacon = null;
    }

    private void getBusInformation(BusBeacon beacon) {
        if (!NetUtils.isOnline(mContext)) {
            Timber.w("No internet connection");
            return;
        }

        Timber.e("getBusInformation %d", beacon.id);

        VehicleApi realtimeApi = RestClient.ADAPTER.create(VehicleApi.class);
        realtimeApi.getBus(beacon.id)
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<BusObject>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(BusObject response) {
                        Timber.e("getBusInformation: %d", beacon.id);

                        beacon.title = response.name + " heading to Ospedale";
                    }
                });
    }

    public void addTrip(BusBeacon beacon) {
        Trip trip = new Trip();
        trip.departure = TimeUtils.dateToIso(beacon.getStartDate());
        trip.arrival = TimeUtils.getCurrentIsoTime();

        TripsApi api = RestClient.ADAPTER.create(TripsApi.class);
        api.uploadTrips(trip)
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<TripsResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(TripsResponse response) {
                        Timber.e("Uploaded trip");

                        Notifications.tripComplete(mContext, response);
                    }
                });
    }
}