package com.mobility.android.data.network.api;

import com.mobility.android.data.network.Endpoint;
import com.mobility.android.data.network.response.MapResponse;

import retrofit2.http.GET;
import rx.Observable;

public interface VehiclesApi {

    @GET(Endpoint.VEHICLES_AVAILABLE)
    Observable<MapResponse> getAvailableVehicles();
}
