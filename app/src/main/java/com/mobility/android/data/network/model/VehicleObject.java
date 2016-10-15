package com.mobility.android.data.network.model;

import android.os.Parcel;

import com.google.gson.annotations.SerializedName;

public class VehicleObject extends MapObject {

    public String address;
    public String availability;
    public String currency;
    public String image;
    public String owner;

    public String type;

    @SerializedName("user_id")
    public String userId;

    @SerializedName("price_per_hour")
    public float pricePerHour;

    @SerializedName("licence_plate")
    public String licencePlate;

    @SerializedName("qr_code")
    public String qrCode;

    public VehicleObject() {
    }

    private VehicleObject(Parcel in) {
        super(in);

        address = in.readString();
        availability = in.readString();
        currency = in.readString();
        image = in.readString();
        type = in.readString();
        userId = in.readString();
        pricePerHour = in.readFloat();
        licencePlate = in.readString();
        qrCode = in.readString();
        owner = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);

        dest.writeString(address);
        dest.writeString(availability);
        dest.writeString(currency);
        dest.writeString(image);
        dest.writeString(type);
        dest.writeString(userId);
        dest.writeFloat(pricePerHour);
        dest.writeString(licencePlate);
        dest.writeString(qrCode);
        dest.writeString(owner);
    }

    @Override
    public int describeContents() {
        return super.describeContents();
    }

    public static final Creator<VehicleObject> CREATOR = new Creator<VehicleObject>() {
        @Override
        public VehicleObject createFromParcel(Parcel in) {
            return new VehicleObject(in);
        }

        @Override
        public VehicleObject[] newArray(int size) {
            return new VehicleObject[size];
        }
    };
}
