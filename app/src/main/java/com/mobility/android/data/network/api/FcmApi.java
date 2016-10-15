package com.mobility.android.data.network.api;

import com.mobility.android.data.network.Endpoint;

import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

public interface FcmApi {

    @GET(Endpoint.FCM_TOKEN)
    Observable<Void> uploadToken(@Path("token") String token);
}
