package com.mobility.android.data.network.api;

import com.mobility.android.data.network.Endpoint;

import retrofit2.http.PATCH;
import retrofit2.http.Path;
import rx.Observable;

public interface FcmApi {

    @PATCH(Endpoint.FCM_TOKEN)
    Observable<Void> uploadToken(@Path("token") String token);
}
