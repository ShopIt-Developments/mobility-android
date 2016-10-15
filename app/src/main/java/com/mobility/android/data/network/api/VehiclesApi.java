package com.mobility.android.data.network.api;

import com.mobility.android.data.network.Endpoint;
import com.mobility.android.data.network.model.VehicleObject;
import com.mobility.android.data.network.response.MapResponse;

import java.util.List;

import retrofit2.http.GET;
import rx.Observable;

public interface VehiclesApi {

    @GET(Endpoint.VEHICLES_AVAILABLE)
    Observable<MapResponse> getAvailableVehicles();

    @GET(Endpoint.VEHICLES_MY)
    Observable<List<VehicleObject>> getMyVehicles();
}
