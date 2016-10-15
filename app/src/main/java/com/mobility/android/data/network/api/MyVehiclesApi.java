package com.mobility.android.data.network.api;

import com.google.android.gms.common.data.DataBufferObserver;
import com.mobility.android.data.network.Endpoint;
import com.mobility.android.data.network.response.VehicleResponse;

import retrofit2.http.GET;
import rx.Observable;

/**
 * Created on 15.10.2016.
 *
 * @author Martin
 */

public interface MyVehiclesApi {

    @GET(Endpoint.MY_VEHICLES)
    Observable<VehicleResponse> getMyVehicles();

}
