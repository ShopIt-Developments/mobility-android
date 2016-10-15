package com.mobility.android.data.network.model;

import com.google.gson.annotations.SerializedName;

public class VehicleObject extends MapObject {

    public String address;
    public String availability;
    public String currency;
    public String image;

    public String type;

    @SerializedName("user_id")
    public String userId;

    @SerializedName("price_per_hour")
    public float pricePerHour;

    @SerializedName("licence_plate")
    public String licencePlate;

    @SerializedName("qr_code")
    public String qrCode;
}
