package com.mobility.android.data.network.model;

import android.os.Parcel;

import com.google.gson.annotations.SerializedName;

public class BusObject extends MapObject {

    @SerializedName("hydrogen_bus")
    public boolean hydrogenBus;

    @SerializedName("line_id")
    public int line;

    public int variant;

    private BusObject(Parcel in) {
        super(in);

        hydrogenBus = in.readInt() == 1;
        line = in.readInt();
        variant = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);

        dest.writeInt(hydrogenBus ? 1 : 0);
        dest.writeInt(line);
        dest.writeInt(variant);
    }

    @Override
    public int describeContents() {
        return super.describeContents();
    }

    @Override
    public String toString() {
        return "BusObject{" +
                "hydrogenBus=" + hydrogenBus +
                ", line=" + line +
                ", variant=" + variant +
                "} " + super.toString();
    }

    public static final Creator<BusObject> CREATOR = new Creator<BusObject>() {
        @Override
        public BusObject createFromParcel(Parcel in) {
            return new BusObject(in);
        }

        @Override
        public BusObject[] newArray(int size) {
            return new BusObject[size];
        }
    };
}
