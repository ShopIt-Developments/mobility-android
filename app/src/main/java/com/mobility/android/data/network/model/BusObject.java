package com.mobility.android.data.network.model;

import android.os.Parcel;

import com.google.gson.annotations.SerializedName;

public class BusObject extends MapObject {

    @SerializedName("hydrogen_bus")
    public boolean hydrogenBus;

    @SerializedName("line_id")
    public int line;

    public int variant;

    BusObject(Parcel in) {
        id = in.readString();
        name = in.readString();
        description = in.readString();
        hydrogenBus = in.readInt() == 1;
        lat = in.readFloat();
        lng = in.readFloat();
        line = in.readInt();
        variant = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeInt(hydrogenBus ? 1 : 0);
        dest.writeFloat(lat);
        dest.writeFloat(lng);
        dest.writeInt(line);
        dest.writeInt(variant);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MapObject> CREATOR = new Creator<MapObject>() {
        @Override
        public MapObject createFromParcel(Parcel in) {
            return new MapObject(in);
        }

        @Override
        public MapObject[] newArray(int size) {
            return new MapObject[size];
        }
    };

    @Override
    public String toString() {
        return "MapObject{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", hydrogen_bus='" + hydrogenBus + '\'' +
                ", lat=" + lat +
                ", lng=" + lng +
                ", line=" + line +
                ", variant=" + variant +
                '}';
    }

}
