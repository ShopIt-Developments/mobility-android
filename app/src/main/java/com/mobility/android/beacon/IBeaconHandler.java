package com.mobility.android.beacon;

import org.altbeacon.beacon.Beacon;

import java.util.Collection;

public interface IBeaconHandler {

    void didRangeBeacons(Collection<Beacon> beacons);

    void validateBeacon(Beacon beacon, int major);

    void stopRanging();
}
