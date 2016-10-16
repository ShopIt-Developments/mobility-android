package com.mobility.android.data.network.api;

import com.mobility.android.data.network.Endpoint;
import com.mobility.android.data.network.model.Trip;
import com.mobility.android.data.network.response.TripsResponse;

import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;

public interface TripsApi {

    @POST(Endpoint.TRIP_ADD)
    Observable<TripsResponse> uploadTrips(@Body Trip trip);
}
