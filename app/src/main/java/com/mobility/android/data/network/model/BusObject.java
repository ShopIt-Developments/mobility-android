package com.mobility.android.data.network.model;

import android.os.Parcel;

public class BusObject extends MapObject {

    public int lineId;
    public int variant;
    public int trip;

    protected BusObject(Parcel in) {
        super(in);
    }
}
