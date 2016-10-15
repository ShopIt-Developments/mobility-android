package com.mobility.android.data.network.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created on 16.10.2016.
 *
 * @author Martin
 */

public class UserModel {

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

}
