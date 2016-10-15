package com.mobility.android.data.network.model;

import android.os.Parcel;
import android.os.Parcelable;

public class MapObject implements Parcelable {

    public String name;
    public String id;
    public String description;

    public float lat;
    public float lng;

    protected MapObject(Parcel in) {
        name = in.readString();
        id = in.readString();
        description = in.readString();
        lat = in.readFloat();
        lng = in.readFloat();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(id);
        dest.writeString(description);
        dest.writeFloat(lat);
        dest.writeFloat(lng);
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
}
