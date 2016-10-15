package com.mobility.android.data.network.api;

import com.mobility.android.data.network.Endpoint;
import com.mobility.android.data.network.response.MapResponse;
import com.mobility.android.data.network.response.VehicleResponse;

import retrofit2.http.GET;
import rx.Observable;

public interface VehiclesApi {

    @GET(Endpoint.VEHICLES_AVAILABLE)
    Observable<MapResponse> getAvailableVehicles();

    @GET(Endpoint.VEHICLES_MY)
    Observable<VehicleResponse> getMyVehicles();
}
