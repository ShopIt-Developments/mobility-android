package com.mobility.android.beacon;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.RemoteException;

import com.mobility.android.beacon.bus.BusBeaconHandler;
import com.mobility.android.util.DeviceUtils;
import com.mobility.android.util.Utils;

import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.startup.BootstrapNotifier;
import org.altbeacon.beacon.startup.RegionBootstrap;

import timber.log.Timber;

/**
 * Beacon handler which scans for beacons in range. This scanner will be automatically stated
 * by the {@code AltBeacon} library as soon as it detects either a bus or bus stop beacon.
 *
 * @author Alex Lardschneider
 */
public final class BeaconHandler implements BeaconConsumer, BootstrapNotifier {

    private final Context mContext;

    private BeaconManager mBeaconManager;

    private Region mRegionBus;

    private final BusBeaconHandler mBusBeaconHandler;

    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private RegionBootstrap mRegionBusBootstrap;

    @SuppressLint("StaticFieldLeak")
    private static BeaconHandler sInstance;

    private boolean isBeaconHandlerBound;

    public static boolean isListening;

    /**
     * Creates a new instance of {@code BeaconHandler} used to listen for beacons and display
     * bus and station information in the app
     *
     * @param context application context
     */
    private BeaconHandler(Context context) {
        Timber.e("Creating beacon handlers");

        mContext = context.getApplicationContext();

        mBusBeaconHandler = BusBeaconHandler.getInstance(context);
    }

    /**
     * Returns the current {@code BeaconHandler} instance. If there is no instance yet,
     * create a new instance and start listening
     *
     * @param context AppApplication context
     * @return current instance in use, or {@code null} if the scanner has no location permission.
     */
    public static BeaconHandler get(Context context) {
        if (sInstance == null) {
            sInstance = new BeaconHandler(context);
        }

        return sInstance;
    }

    @Override
    public void onBeaconServiceConnect() {
        Timber.e("onBeaconServiceConnect()");

        mBeaconManager.setRangeNotifier((beacons, region) -> {
            if (!isListening) {
                Timber.e("didRangeBeaconsInRegion: not listening");
                return;
            }

            if (region.getUniqueId().equals(BusBeaconHandler.IDENTIFIER)) {
                mBusBeaconHandler.didRangeBeacons(beacons);
            }
        });
    }

    @Override
    public Context getApplicationContext() {
        return mContext;
    }

    @Override
    public void didDetermineStateForRegion(int i, Region region) {
    }

    @Override
    public void didEnterRegion(Region region) {
        startRangingBeacon(region);
    }

    @Override
    public void didExitRegion(Region region) {
        stopRangingBeacon(region);
    }

    @Override
    public boolean bindService(Intent service, ServiceConnection connection, int flags) {
        return mContext.bindService(service, connection, flags);
    }

    @Override
    public void unbindService(ServiceConnection conn) {
        mContext.unbindService(conn);
    }

    /**
     * Starts getting detailed beacon information
     */
    private void startRangingBeacon(Region region) {
        Timber.e("startRanging() %s", region.getUniqueId());

        try {
            if (region.getUniqueId().equals(BusBeaconHandler.IDENTIFIER)) {
                mBeaconManager.startRangingBeaconsInRegion(mRegionBus);
            }
        } catch (RemoteException e) {
            Utils.logException(e);
        }
    }

    /**
     * Stops getting detailed beacon information
     */
    private void stopRangingBeacon(Region region) {
        Timber.e("stopRanging() %s", region.getUniqueId());

        try {
            if (region.getUniqueId().equals(BusBeaconHandler.IDENTIFIER)) {
                mBusBeaconHandler.stopRanging();
                mBeaconManager.stopRangingBeaconsInRegion(mRegionBus);
            }
        } catch (RemoteException e) {
            Utils.logException(e);
        }
    }

    /**
     * Starts listening for available beacons
     */
    void startListening() {
        Timber.e("startListening()");

        if (isListening) {
            Timber.w("Already listening for beacons");
            return;
        }

        if (!DeviceUtils.hasPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)) {
            Timber.e("Missing location permission");
            return;
        }

        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter == null) {
            Timber.e("Unable to find a valid bluetooth adapter");
            return;
        }

        if (!adapter.isEnabled()) {
            Timber.e("Bluetooth adapter is disabled");
            return;
        }

        if (adapter.getState() == BluetoothAdapter.STATE_TURNING_OFF) {
            Timber.e("Bluetooth adapter is turning off");
            return;
        }

        if (adapter.getState() != BluetoothAdapter.STATE_ON) {
            Timber.e("Bluetooth adapter is not in state STATE_ON");
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            mBeaconManager = BeaconManager.getInstanceForApplication(mContext);

            mBeaconManager.getBeaconParsers().add(new BeaconParser()
                    .setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));

            mBeaconManager.setForegroundScanPeriod(3000);
            mBeaconManager.setForegroundBetweenScanPeriod(0);

            mBeaconManager.setBackgroundScanPeriod(3000);
            mBeaconManager.setBackgroundBetweenScanPeriod(0);

            mRegionBus = new Region(BusBeaconHandler.IDENTIFIER, Identifier.parse(BusBeaconHandler.UUID), null, null);
            mRegionBusBootstrap = new RegionBootstrap(this, mRegionBus);

            mBeaconManager.bind(this);

            isBeaconHandlerBound = true;
            isListening = true;
        }
    }

    /**
     * Stops listening for beacons and cancels all currently displayed notifications.
     */
    public void stopListening() {
        if (!isListening) {
            Timber.e("Not listening, call to stopListening() will be ignored");
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            if (mBeaconManager != null && isBeaconHandlerBound) {
                isBeaconHandlerBound = false;
                mBeaconManager.unbind(this);
            }

            isListening = false;
            sInstance = null;
        }
    }
}