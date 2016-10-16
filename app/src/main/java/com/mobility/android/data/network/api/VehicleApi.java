package com.mobility.android.data.network.api;

import com.mobility.android.data.network.Endpoint;
import com.mobility.android.data.network.model.BusObject;
import com.mobility.android.data.network.model.VehicleObject;
import com.mobility.android.data.network.response.MapResponse;

import java.util.List;

import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;

public interface VehicleApi {

    @GET(Endpoint.VEHICLES_AVAILABLE)
    Observable<MapResponse> getAvailableVehicles();

    @GET(Endpoint.VEHICLES_MY)
    Observable<List<VehicleObject>> getMyVehicles();

    @GET(Endpoint.VEHICLES_BOOKED)
    Observable<List<VehicleObject>> getBookedVehicles();

    @POST(Endpoint.VEHICLE_ADD)
    Observable<VehicleObject> addVehicle(@Body VehicleObject object);

    @DELETE(Endpoint.VEHICLE_DELETE)
    Observable<Void> deleteCar(@Path("vehicle_id") String vehicleId);

    @POST(Endpoint.VEHICLE_ORDER)
    Observable<Void> order(@Path("vehicle_id") String vehicleId);

    @GET(Endpoint.BUS)
    Observable<BusObject> getBus(@Path("id") int vehicle);
}
