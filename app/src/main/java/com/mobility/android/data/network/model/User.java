package com.mobility.android.data.network.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class User implements Parcelable {

    @SerializedName("user_id")
    public String userId;

    public String name;

    public String address;

    public String telephone;

    public int points;

    @SerializedName("average_rating")
    public float averageRating;

    @SerializedName("ratings_count")
    public int ratingsCount;

    @SerializedName("driven_time")
    public String drivenTime;

    public int emissions;

    @SerializedName("offered_vehicles")
    public int offeredVehicles;

    @SerializedName("used_vehicles")
    public int usedVehicles;

    protected User(Parcel in) {
        userId = in.readString();
        name = in.readString();
        address = in.readString();
        telephone = in.readString();
        points = in.readInt();
        averageRating = in.readFloat();
        ratingsCount = in.readInt();
        drivenTime = in.readString();
        emissions = in.readInt();
        offeredVehicles = in.readInt();
        usedVehicles = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userId);
        dest.writeString(name);
        dest.writeString(address);
        dest.writeString(telephone);
        dest.writeInt(points);
        dest.writeFloat(averageRating);
        dest.writeInt(ratingsCount);
        dest.writeString(drivenTime);
        dest.writeInt(emissions);
        dest.writeInt(offeredVehicles);
        dest.writeInt(usedVehicles);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
